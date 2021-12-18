import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BloodProjectile implements EnemyProjectile{
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 8;
	
	private double centerX;
	private double centerY;
	private double width = 8;
	private double height = 8;
	private boolean dispose = false;
	
	private double velocityX;
	private double velocityY;
	
	private final double RESULTANT_VELOCITY = 180;
	
	private double angle; //in radians
	private int type = 0; //0 is blood 1 is ice
	private static Image image = null;
	private static Image iceBall = null;
	
	private int damage = 1;

	
	
	public BloodProjectile(double centerX, double centerY, double angle, int type) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.angle = angle;
		this.type = type;
		
		if (image == null) {
			try {
				image = ImageIO.read(new File("res/BloodBall.png"));
				iceBall = ImageIO.read(new File("res/BlueFireBall.png"));
			}
			catch (IOException e) {
				System.out.print(e.toString());
			}
		}
		this.width = WIDTH;
		this.height = HEIGHT;
		
		velocityX = Math.cos(angle)*(RESULTANT_VELOCITY);
		velocityY = Math.sin(angle)*(RESULTANT_VELOCITY);
	}

	public Image getImage() {
		if (type == 0) {
			return image;
		}
		return iceBall;
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
		
		double movement_x = (this.velocityX * actual_delta_time * 0.001);
		double movement_y = (this.velocityY * actual_delta_time * 0.001);
		    
		this.centerX += movement_x;
		this.centerY += movement_y;
		
		checkOverlap(universe, "BarrierSprite");
	}
	
	private boolean checkOverlap(Universe sprites, String targetSprite) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite.getClass().toString().contains(targetSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					if (targetSprite.equals("BarrierSprite")) {
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
