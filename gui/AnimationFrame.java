import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class AnimationFrame extends JFrame {

	final public static int FRAMES_PER_SECOND = 60;
	final public static int SCREEN_HEIGHT = 700;
	final public static int SCREEN_WIDTH = 1000;

	private int xpCenter = SCREEN_WIDTH / 2;
	private int ypCenter = SCREEN_HEIGHT / 2;

	private double scale = 1;
	//point in universe on which the screen will center
	private double xpFrameCenter = 0;		
	private double ypFrameCenter = 0;
	
	private JLabel[] healthBar = new JLabel[5];
	private JLabel[] bossBar = new JLabel[10];
	
	private int lastCheckedBossHealth = 0;
	private int lastCheckedPlayerHealth = 0;

	private JPanel panel = null;
	private JButton btnPauseRun;
	private JLabel lblTimeLabel;
	private JLabel lblTime;
	private JLabel lblLevelLabel;
	private JLabel lblLevel;
	private JLabel lblStatus;;

	private static boolean stop = false;

	private long current_time = 0;								//MILLISECONDS
	private long next_refresh_time = 0;							//MILLISECONDS
	private long last_refresh_time = 0;
	private long minimum_delta_time = 1000 / FRAMES_PER_SECOND;	//MILLISECONDS
	private long actual_delta_time = 0;							//MILLISECONDS
	private long elapsed_time = 0;
	private boolean isPaused = false;

	private KeyboardInput keyboard = new KeyboardInput();
	private MouseInput mouse;
	
	private Universe universe = null;

	//local (and direct references to various objects in universe ... should reduce lag by avoiding dynamic lookup
	private Animation animation = null;
	private DisplayableSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = null;
	private Background background = null;
	private Background foreground = null;
	boolean centreOnPlayer = false;
	int universeLevel = 0;
	
	public AnimationFrame(Animation animation)
	{
		super("");
	
		this.animation = animation;
		this.setVisible(true);		
		this.setFocusable(true);
		this.setSize(SCREEN_WIDTH + 20, SCREEN_HEIGHT + 36);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				keyboard.keyPressed(arg0);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				keyboard.keyReleased(arg0);
			}
		});
			
		Container cp = getContentPane();
		cp.setBackground(Color.BLACK);
		cp.setLayout(null);

		panel = new DrawPanel();
		panel.setLayout(null);
		panel.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		btnPauseRun = new JButton("||");
		btnPauseRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnPauseRun_mouseClicked(arg0);
			}
		});
		
		
		ImageIcon heartIcon = new ImageIcon("res/hearts/heart.png"); // load the image
		
		ImageIcon bossHeartIcon = new ImageIcon("res/hearts/evilHeart.png"); // load the image

		for (int i = 0; i < healthBar.length; i++) {
			healthBar[i] = new JLabel(heartIcon);
			healthBar[i].setBounds(15, (i*40) + 75, 42, 40);
			getContentPane().add(healthBar[i]);
			getContentPane().setComponentZOrder(healthBar[i], 0);
			healthBar[i].setVisible(false);
		}
		for (int i = 0; i < bossBar.length; i++) {
			bossBar[i] = new JLabel(bossHeartIcon);	
			bossBar[i].setBounds(SCREEN_WIDTH-15-42, (i*40) + 75, 42, 40);
			getContentPane().add(bossBar[i]);
			getContentPane().setComponentZOrder(bossBar[i], 0);
			bossBar[i].setVisible(false);
		}

		btnPauseRun.setFont(new Font("Optima", Font.BOLD, 12));
		btnPauseRun.setBounds(15, 20, 48, 32);
		btnPauseRun.setFocusable(false);
		getContentPane().add(btnPauseRun);
		getContentPane().setComponentZOrder(btnPauseRun, 0);

		lblTimeLabel = new JLabel("Time: ");
		lblTimeLabel.setForeground(Color.YELLOW);
		lblTimeLabel.setFont(new Font("Optima", Font.BOLD, 30));
		lblTimeLabel.setBounds(80, 22, 96, 30);
		getContentPane().add(lblTimeLabel);
		getContentPane().setComponentZOrder(lblTimeLabel, 0);

		lblTime = new JLabel("000");
		lblTime.setForeground(Color.YELLOW);
		lblTime.setFont(new Font("Optima", Font.BOLD, 30));
		lblTime.setBounds(192, 22, 320, 30);
		getContentPane().add(lblTime);
		getContentPane().setComponentZOrder(lblTime, 0);

		lblLevelLabel = new JLabel("Level: ");
		lblLevelLabel.setForeground(Color.YELLOW);
		lblLevelLabel.setFont(new Font("Optima", Font.BOLD, 30));
		lblLevelLabel.setBounds(528, 22, 128, 30);
		getContentPane().add(lblLevelLabel);
		getContentPane().setComponentZOrder(lblLevelLabel, 0);

		lblLevel = new JLabel("1");
		lblLevel.setForeground(Color.YELLOW);
		lblLevel.setFont(new Font("Optima", Font.BOLD, 30));
		lblLevel.setBounds(672, 22, 48, 30);
		getContentPane().add(lblLevel);
		getContentPane().setComponentZOrder(lblLevel, 0);

		lblStatus = new JLabel("Status");
		lblStatus.setForeground(Color.WHITE);
		lblStatus.setFont(new Font("Optima", Font.BOLD, 30));
		lblStatus.setBounds(0, SCREEN_HEIGHT - 30 - 16, SCREEN_WIDTH, 36);
		getContentPane().add(lblStatus);
		getContentPane().setComponentZOrder(lblStatus, 0);
		
		mouse = new MouseInput(panel);
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
		panel.addMouseListener( mouse );
		panel.addMouseMotionListener( mouse );
		mouse.setCenter(new Point(xpCenter, ypCenter));
	}

	public void start()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				animationLoop();
				System.out.println("run() complete");
			}
		};

		thread.start();
		System.out.println("main() complete");

	}	
	private void animationLoop() {

		universe = animation.getNextUniverse();
		universeLevel++;

		while (stop == false && universe != null) {

			sprites = universe.getSprites();
			player1 = universe.getPlayer1();
			background = universe.getBackground();
			foreground = universe.getForeground();
			centreOnPlayer = universe.centerOnPlayer();
			this.scale = universe.getScale();
			this.xpFrameCenter = universe.getXCenter();
			this.ypFrameCenter = universe.getYCenter();

			// main game loop
			while (stop == false && universe.isComplete() == false) {

				//adapted from http://www.java-gaming.org/index.php?topic=24220.0
				last_refresh_time = System.currentTimeMillis();
				next_refresh_time = current_time + minimum_delta_time;

				//sleep until the next refresh time
				while (current_time < next_refresh_time)
				{
					//allow other threads (i.e. the Swing thread) to do its work
					Thread.yield();

					try {
						Thread.sleep(1);
					}
					catch(Exception e) {    					
					} 

					//track current time
					current_time = System.currentTimeMillis();
				}

				//read input
				keyboard.poll();
				handleKeyboardInput();
				
				mouse.poll();
				
				//UPDATE STATE
				
				updateTime();
				universe.update(keyboard, mouse, actual_delta_time);
				updateControls();

				//REFRESH
				this.repaint();
			}

			universe = animation.getNextUniverse();

		}

		System.out.println("animation complete");
		AudioPlayer.setStopAll(true);
		dispose();	

	}

	private void updateControls() {
		this.lblTime.setText(Long.toString(elapsed_time));
		this.lblLevel.setText(Integer.toString(universeLevel));
		if (universe != null) {
			this.lblStatus.setText(universe.toString());
		}
		
		if (universe instanceof FightingUniverse) {
			int health = ((RangerCharacterSprite)universe.getPlayer1()).getHealth();
			if (!(health == lastCheckedPlayerHealth)) {
			
				lastCheckedPlayerHealth = health;
						
				for (int i = 0; i < healthBar.length; i++) {
					healthBar[i].setVisible(false);
				}
				for (int i = 0; i < health; i++) {
					healthBar[i].setVisible(true);
				}
			}
			
			if (((FightingUniverse) universe).getIsFightStarted()) {
				try {
					int bossHealth = ((FightingUniverse) universe).getBoss().getHealth();
					if (!(bossHealth == lastCheckedBossHealth)) {
						
						lastCheckedBossHealth = bossHealth;
						
						for (int i = 0; i < bossBar.length; i++) {
							bossBar[i].setVisible(false);
						}
						for (int i = 0; i < bossHealth; i++) {
							bossBar[i].setVisible(true);
						}
					}
				}
				catch (NullPointerException e){ //for when the boss reaches 0 health and is disposed of
				}			
			}
		}
	}

	private void updateTime() {

		current_time = System.currentTimeMillis();
		actual_delta_time = (isPaused ? 0 : current_time - last_refresh_time);
		last_refresh_time = current_time;
		elapsed_time += actual_delta_time;

	}

	protected void btnPauseRun_mouseClicked(MouseEvent arg0) {
		if (isPaused) {
			isPaused = false;
			this.btnPauseRun.setText("||");
		}
		else {
			isPaused = true;
			this.btnPauseRun.setText(">");
		}
	}

	private void handleKeyboardInput() {
		if (keyboard.keyDown(80) && ! isPaused) {
			btnPauseRun_mouseClicked(null);	
		}
		if (keyboard.keyDown(79) && isPaused ) {
			btnPauseRun_mouseClicked(null);
		}
		if (keyboard.keyDownOnce(27)) {
			mouse.setRelative(false);
		}
	}

	class DrawPanel extends JPanel {

		public void paintComponent(Graphics g)
		{	
			if (universe == null) {
				return;
			}

			if (player1 != null && centreOnPlayer) {
				xpFrameCenter = player1.getCenterX();
				ypFrameCenter = player1.getCenterY();     
			}

			paintBackground(g, background);
			paintBackground(g, foreground);

			for (DisplayableSprite activeSprite : sprites) {
				DisplayableSprite sprite = activeSprite;
				if (sprite.getVisible()) {
					if (sprite.getImage() != null) {
						g.drawImage(sprite.getImage(), translatePixelToLogicalX(sprite.getMinX()), translatePixelToLogicalY(sprite.getMinY()), scaleX(sprite.getWidth()), scaleY(sprite.getHeight()), null);
					}
					else {
						g.setColor(Color.BLUE);
						g.fillRect(translatePixelToLogicalX(scale * (sprite.getMinX())), translatePixelToLogicalY(sprite.getMinY()), scaleX(sprite.getWidth()), scaleY(sprite.getHeight()));					
					}
				}

			}

		}
		
		private int translatePixelToLogicalX(double x) {
			return xpCenter + scaleX(x - xpFrameCenter);
		}
				
		private int scaleX(double x) {
			return (int) Math.round(scale * x);
		}
		
		
//		xpCenter + scaleX(x - xCenter)
//		(int) Math.round(scale * (x - xcenter) );
//		
//		xLogical = xpCenter + (int) Math.round(scale * (x - xcenter));
//
//			
//		private double convertX(double mousePixelPositionX) {
//			
//			double logicalMousePosition = xpCenter + Math.round(scale * (mousePixelPositionX - xpFrameCenter));
//					
//			return logicalMousePosition;
//		}
//		
//		private double convertY(double mousePixelPositionY) {
//			
//			double logicalMousePosition = ypCenter + Math.round(scale * (mousePixelPositionY - yCenter));
//					
//			return logicalMousePosition;
//		}
		
		private int translatePixelToLogicalY(double y) {
			return ypCenter + scaleY(y - ypFrameCenter);
		}		
		private int scaleY(double y) {
			return (int) Math.round(scale * y);
		}

		private void paintBackground(Graphics g, Background background) {

			if ((g == null) || (background == null)) {
				return;
			}
			
			//what tile covers the top-left corner?
			double xTopLeft = ( xpFrameCenter - (xpCenter / scale));
			double yTopLeft =  (ypFrameCenter - (ypCenter / scale)) ;
			
			int row = background.getRow((int)yTopLeft);
			int col = background.getCol((int)xTopLeft);
			Tile tile = null;

			boolean rowDrawn = false;
			boolean screenDrawn = false;
			while (screenDrawn == false) {
				while (rowDrawn == false) {
					tile = background.getTile(col, row);
					if (tile.getWidth() <= 0 || tile.getHeight() <= 0) {
						//no increase in width; will cause an infinite loop, so consider this screen to be done
						g.setColor(Color.GRAY);
						g.fillRect(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);					
						rowDrawn = true;
						screenDrawn = true;						
					}
					else {
						Tile nextTile = background.getTile(col+1, row+1);
						int pwidth = translatePixelToLogicalX(nextTile.getMinX()) - translatePixelToLogicalX(tile.getMinX());
						int pheight = translatePixelToLogicalY(nextTile.getMinY()) - translatePixelToLogicalY(tile.getMinY());
						g.drawImage(tile.getImage(), translatePixelToLogicalX(tile.getMinX()), translatePixelToLogicalY(tile.getMinY()), pwidth, pheight, null);
					}					
					//does the RHE of this tile extend past the RHE of the visible area?
					if (translatePixelToLogicalX(tile.getMinX() + tile.getWidth()) > SCREEN_WIDTH || tile.isOutOfBounds()) {
						rowDrawn = true;
					}
					else {
						col++;
					}
				}
				//does the bottom edge of this tile extend past the bottom edge of the visible area?
				if (translatePixelToLogicalY(tile.getMinY() + tile.getHeight()) > SCREEN_HEIGHT || tile.isOutOfBounds()) {
					screenDrawn = true;
				}
				else {
					//TODO - should be passing in a double, as this represents a universe coordinate
					col = background.getCol((int)xTopLeft);
					row++;
					rowDrawn = false;
				}
			}
		}				
	}
	protected void this_windowClosing(WindowEvent e) {
		System.out.println("windowClosing()");
		stop = true;
		dispose();	
	}
}
