import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ToggleBullet implements EnemyProjectile{
	
	private static final int WIDTH = 24;
	private static final int HEIGHT = 16;
	
	private double centerX;
	private double centerY;
	private double width = 24;
	private double height = 16;
	private boolean dispose = false;
	
	private final double RESULTANT_VELOCITY_X = 180;
	
	private static Image imageRight = null;
	private static Image imageLeft = null;
	
	private boolean toTheRight;
	
	private int damage = 1;

	
	
	public ToggleBullet(double centerX, double centerY, boolean toTheRight) {
		this.toTheRight = toTheRight;
		if (toTheRight) {
			this.centerX = centerX + 25;
		}
		else {
			this.centerX = centerX - 25;
		}
		this.centerY = centerY + 8; // adds 8 to make it more in line with the real center of the toggle boxes
		
		if (imageRight == null) {
			try {
				imageRight = ImageIO.read(new File("res/box/toggleBullet2.png"));
				imageLeft = ImageIO.read(new File("res/box/toggleBullet1.png"));
			}
			catch (IOException e) {
				System.out.print(e.toString());
			}
		}
		this.width = WIDTH;
		this.height = HEIGHT;
	}

	public Image getImage() {
		if (toTheRight) {
			return imageRight;
		}
		return imageLeft;
	}

	public boolean getVisible() {
		return true;
	}

	public double getMinX() {
		return centerX - (width / 2);
	}

	public double getMaxX() {
		return centerX + (width / 2);
	}

	public double getMinY() {
		return centerY - (height / 2);
	}

	public double getMaxY() {
		return centerY + (height / 2);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}
	
	
	public boolean getDispose() {
		return dispose;
	}
	
	public int getDamageGiven() {
		return damage;
	}

	public void setDispose() {
		dispose = true;
	}

	public void update(Universe universe, KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {
		if (((FightingUniverse) universe).getIsFightStarted() == false) {
			this.dispose = true;
		}
		
		double movement_x = (RESULTANT_VELOCITY_X * actual_delta_time * 0.001);
		    
		if (toTheRight) {
			this.centerX += movement_x;
		}
		else {
			this.centerX -= movement_x;
		}
		
		checkOverlap(universe, "BarrierSprite");
		//checkOverlap(universe, "ToggleBullet");
	}
	
	private boolean checkOverlap(Universe sprites, String targetSprite) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite.getClass().toString().contains(targetSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					if (targetSprite.equals("BarrierSprite")) {
						this.dispose = true;
					}
					if (targetSprite.equals("ToggleBullet")) {
						this.dispose = true;
					}
					overlap = true;
					break;					
				}
			}
		}		
		return overlap;		
	}
}
