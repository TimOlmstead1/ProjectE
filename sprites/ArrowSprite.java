import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ArrowSprite implements Projectile{
	
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private boolean dispose = false;
	
	private double velocityX;
	private double velocityY;
	
	private final double RESULTANT_VELOCITY = 320;
	
	private double angle;
	private static Image[] rotatedImages = new Image[360];
	
	private int damage = 1;
	
	public ArrowSprite(double centerX, double centerY, double angle) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.angle = angle;
		
		Image image = null;
		
		if (image == null) {
			try {
				image = ImageIO.read(new File("res/Arrow.png"));
			}
			catch (IOException e) {
				System.out.print(e.toString());
			}
			
			if (image != null) {
				for (int i = 0; i < 360; i++) {
					rotatedImages[i] = ImageRotator.rotate(image, i);			
				}
				this.height = image.getHeight(null);
				this.width = image.getWidth(null);
			}
		}
		
		velocityX = Math.cos(angle)*(RESULTANT_VELOCITY);
		velocityY = Math.sin(angle)*(RESULTANT_VELOCITY);
	}

	public Image getImage() {
		return rotatedImages[(int)angle];
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

	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		double movement_x = (this.velocityX * actual_delta_time * 0.001);
		double movement_y = (this.velocityY * actual_delta_time * 0.001);
		    
		this.centerX += movement_x;
		this.centerY += movement_y;
		
		checkWallCollision(universe);
	}

	private void checkWallCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getBarriers().get(i);
			
			if (sprite instanceof BarrierSprite) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite))
				{
					if (sprite instanceof BarrierSprite) {
						//dispose of arrow
						this.dispose = true;
					}
					break;
				}			
			}
		}		
	}
}
