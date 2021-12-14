import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ArrowSprite implements Projectile{
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 8;
	
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private boolean dispose = false;
	
	private double velocityX;
	private double velocityY;
	
	private final double RESULTANT_VELOCITY = 280;
	
	private CollisionDetection collisionDetection;
	
	private double angle; //in radians
	//private static Image[] rotatedImages = new Image[360];
	private static Image image = null;
	
	private int damage = 1;
	
	public ArrowSprite(double centerX, double centerY, double angle) {
		
		collisionDetection = new CollisionDetection();
		
		this.centerX = centerX;
		this.centerY = centerY;
		this.angle = angle;
		
		if (image == null) {
			try {
				image = ImageIO.read(new File("res/ArrowBall.png"));
			}
			catch (IOException e) {
				System.out.print(e.toString());
			}
			
//			if (image != null) {
//				for (int i = 0; i < 360; i++) {
//					rotatedImages[i] = ImageRotator.rotate(image, i);			
//				}
//			}
		}
		this.width =  WIDTH;
		this.height = HEIGHT;
		
		velocityX = Math.cos(angle)*(RESULTANT_VELOCITY);
		velocityY = Math.sin(angle)*(RESULTANT_VELOCITY);
	}

	public Image getImage() {
//		if ((int)Math.toDegrees(angle) >= 0) {
//			return rotatedImages[((int)Math.toDegrees(angle))];
//		}
//		else {
//			return rotatedImages[(360+((int)Math.toDegrees(angle)))];
//		}
		return image;
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
