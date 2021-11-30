import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FollowerBatEnemy implements EnemySprite, MovableSprite{
	
	private static final int WIDTH = 32;
	private static final int HEIGHT = 32;
	private static final double RESULTANT_VELOCITY = 80;
	
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private boolean dispose = false;
	
	private double velocityX;
	private double velocityY;
	
	private static BufferedImage[] imageList = null;
	private double animationCount = 0;
	
	private static int rows = 4;
	private static int cols = 4;
	
	private int damage = 1;
	private int health = 1;
	
	public FollowerBatEnemy(double centerX, double centerY) {
		
		this.centerX = centerX;
		this.centerY = centerY;
		
		this.height = HEIGHT;
		this.width = WIDTH;
		
		Image image = null;
		try {
			image = ImageIO.read(new File("res/32x32-bat-sprite.png"));
		}
		catch (IOException e) {
			System.out.print(e.toString());
		}
		if (imageList == null) {
		
			imageList = new BufferedImage[rows * cols]; 
	
			for (int i = 0; i < rows; i++){ 
				
			    for (int j = 0; j < cols; j++){ 
			    	
			    	imageList[(i * cols) + j] = ((BufferedImage) image).getSubimage( 
			    		i * (int)width, 
			    		j *(int)height, 
			            (int)width, 
			            (int)height 
			        ); 
			    } 
			} 
		}
	}

	public Image getImage() {
		if (velocityX > 0) {
			if (animationCount <= 20) {
				animationCount++;
				return imageList[1];
			}
			else if (animationCount <= 60) {
				animationCount++;
				return imageList[5];
			}
			else if (animationCount <= 100) {
				animationCount = 0;
				return imageList[9];
			}
			
		}
		else if (velocityX < 0) {
			if (animationCount <= 20) {
				animationCount++;
				return imageList[3];
			}
			else if (animationCount <= 60) {
				animationCount++;
				return imageList[7];
			}
			else if (animationCount <= 100) {
				animationCount = 0;
				return imageList[11];
			}
		}
		else if (velocityX == 0) {
			if (animationCount <= 20) {
				animationCount++;
				return imageList[4];
			}
			else if (animationCount <= 60) {
				animationCount++;
				return imageList[8];
			}
			else if (animationCount <= 100) {
				animationCount = 0;
				return imageList[12];
			}
		}
		return imageList[8];
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
		if ((health <= 0)||((FightingUniverse) universe).getIsFightStarted() == false) {
			this.dispose = true;
		}
		
		double targetX = universe.getPlayer1().getCenterX();
		double targetY = universe.getPlayer1().getCenterY();
		
		double angleRadians = Math.atan(Math.abs((centerY - targetY)/(centerX - targetX)));
		velocityX = Math.cos(angleRadians)*RESULTANT_VELOCITY;
		velocityY = Math.sin(angleRadians)*RESULTANT_VELOCITY;	
		
		if(targetX < centerX) {
			velocityX = -velocityX;
		}
		
		if(targetY < centerY) {
			velocityY = -velocityY;
		}
		double movement_x = (velocityX * actual_delta_time * 0.001);
		double movement_y = (velocityY * actual_delta_time * 0.001);
		
		this.centerX += movement_x;
		this.centerY += movement_y;
		
		checkCollision(universe);
	}

	private void checkCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if ((sprite instanceof ArrowSprite)) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
					
					if (sprite instanceof ArrowSprite) {
						health = health - ((Projectile) sprite).getDamageGiven();
						((ArrowSprite) sprite).setDispose();
					}
					break;		
				}
			}
		}
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;		
	}

	public void moveX(double pixelsPerSecond) {
		velocityX = velocityX*pixelsPerSecond;		
	}

	public void moveY(double pixelsPerSecond) {
		velocityY = velocityY*pixelsPerSecond;	
	}

	public void stop() {
		velocityY = 0;
		velocityX = 0;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getCollisionDamage() {
		return 1;
	}
}
