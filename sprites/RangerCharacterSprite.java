import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RangerCharacterSprite implements DisplayableSprite, MovableSprite{
	
	private double ACCCELERATION_X = 10;		//PIXELS PER SECOND PER SECOND
	private double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND
	private double MAX_VELOCITY_X = 400;	//PIXELS PER SECOND
	private double MAX_VELOCITY_Y = 600;
	private double FRICTION_FACTOR_X = 0.92; 
	
	private boolean isJumping = false;
	private final double INITIAL_JUMP_VELOCITY = 200; //pixels / second
	private final double CONTINUE_JUMP_VELOCITY = 22;
	
	private double velocityY = 0;
	private double velocityX = 0;

	private static BufferedImage[] rangerSprites = null;
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	final private int width = 32;
	final private int height = 32;
	final private int cols = 5;
	final private int rows = 10;
	
	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	TwoDimensionBounce bounce;
	
	public RangerCharacterSprite(int centerX, int centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		collisionDetection = new CollisionDetection();
		collisionDetection.setBounceFactorX(0.5);
		collisionDetection.setBounceFactorY(0);
		bounce = new TwoDimensionBounce();
		
		Image image = null;
		
		try {
			image = ImageIO.read(new File("res/NobleRanger(Edit).png"));
		}
		catch (IOException e) {
			System.out.print(e.toString());
		}
		//https://stackoverflow.com/questions/10604824/reading-images-from-a-sprite-sheet-java
		
		rangerSprites = new BufferedImage[rows * cols]; 

		for (int i = 0; i < rows; i++){ 
			
		    for (int j = 0; j < cols; j++){ 
		    	
		    	rangerSprites[(i * cols) + j] = ((BufferedImage) image).getSubimage( 
		            i * width, 
		            j * height, 
		            width, 
		            height 
		        ); 
		    } 
		} 
	}

	public Image getImage() {
		return rangerSprites[32];
	}

	public boolean getVisible() {
		return visible;
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

	public void setCenterX(double newCenterX) {
		centerX = newCenterX;		
	}

	public void setCenterY(double newCenterY) {
		centerY = newCenterY;
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
	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		
		boolean onGround = isOnGround(universe);
		
		// Jump
		if (keyboard.keyDown(38)) {
			isJumping = true;
			this.velocityY -= INITIAL_JUMP_VELOCITY;
			onGround = false;
		}
		// RIGHT
		if (keyboard.keyDown(39)) {
			velocityX += + ACCCELERATION_X;
			if (velocityX > MAX_VELOCITY_X) {
				velocityX = MAX_VELOCITY_X;
			}
		}
		// LEFT
		if (keyboard.keyDown(37)) {
			velocityX -= ACCCELERATION_X;
			if (velocityX < - MAX_VELOCITY_X) {
				velocityX = - MAX_VELOCITY_X;
			}
		}
		// DOWN
		if (keyboard.keyDown(40)) {
			velocityY += ACCCELERATION_Y/60;
			if (velocityY < - MAX_VELOCITY_Y) {
				velocityY = - MAX_VELOCITY_Y;
			}
		}
		
		this.velocityX = this.velocityX * FRICTION_FACTOR_X;
		
		if (velocityY < 0) {
			collisionDetection.calculate2DBounce(bounce, this, universe.getBarriers(), velocityX, velocityY, actual_delta_time);
		}
		else if(velocityY >= 0) {
			if (keyboard.keyDown(40)) {
				collisionDetection.calculate2DBounce(bounce, this, universe.getBarriers(), velocityX, velocityY, actual_delta_time);
			}
			else {
				collisionDetection.calculate2DBounce(bounce, this, universe.getOneWayBarriers(), velocityX, velocityY, actual_delta_time);
			}
		}
		this.centerX = bounce.newX + (width / 2);
		this.centerY = bounce.newY + (width / 2);
		this.velocityX = bounce.newVelocityX;
		this.velocityY = bounce.newVelocityY;

		if (onGround == true) {
			this.velocityY = 0;
		} else {
			this.velocityY = this.velocityY + ACCCELERATION_Y * 0.001 * actual_delta_time;
			if (this.velocityY < -MAX_VELOCITY_Y)
				this.velocityY = -MAX_VELOCITY_Y;
			if (this.velocityY > MAX_VELOCITY_Y)
				this.velocityY = MAX_VELOCITY_Y;
		}
			

		setCenterX(centerX);
		setCenterY(centerY);
	}
	
	private boolean isOnGround(Universe universe) {
		boolean onGround = false;
		for (DisplayableSprite sprite: universe.getSprites()) {
			boolean bottomColiding = this.getMaxY() >= (sprite.getMinY()) && this.getMaxY() <= sprite.getMinY();
			boolean withinRange = this.getMaxX() > sprite.getMinX() && this.getMinX() < sprite.getMaxX();
			if (bottomColiding && withinRange) {
				onGround = true;
				break;
			}
		}
		return onGround;
	}
}
