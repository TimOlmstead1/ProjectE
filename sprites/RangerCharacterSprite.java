import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class RangerCharacterSprite implements DisplayableSprite, MovableSprite{
	
	private final double ACCCELERATION_X = 15;		//PIXELS PER SECOND PER SECOND
	private final double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND
	private final double MAX_VELOCITY_X = 600;	//PIXELS PER SECOND
	private final double MAX_VELOCITY_Y = 600;
	private final double FRICTION_FACTOR_X = 0.90; 
	private final double INITIAL_JUMP_VELOCITY = 320; //pixels / second
	
	private boolean isJumping = false;
	
	private double velocityY = 0;
	private double velocityX = 0;

	private static BufferedImage[] rangerSprites;
	private double animationCount = 0;
	private boolean wasTravelingRight = false;
	
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 26;
	private double height = 32;
	private int cols = 8;
	private int rows = 10;
	
	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	TwoDimensionBounce bounce;
	
	public RangerCharacterSprite(double centerX, double centerY, double height, double width) {
		this(centerX,centerY);
		this.height = height;
		this.width = width;
	}
	
	public RangerCharacterSprite(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		collisionDetection = new CollisionDetection();
		bounce = new TwoDimensionBounce();
		collisionDetection.setBounceFactorX(0);
		collisionDetection.setBounceFactorY(0);
		
		Image image = null;
		
		try {
			image = ImageIO.read(new File("res/NobleRanger(32x26).png"));
		}
		catch (IOException e) {
			System.out.print(e.toString());
		}
		//https://stackoverflow.com/questions/10604824/reading-images-from-a-sprite-sheet-java
		
		rangerSprites = new BufferedImage[rows * cols]; 

		for (int i = 0; i < rows; i++){ 
			
		    for (int j = 0; j < cols; j++){ 
		    	
		    	rangerSprites[(i * cols) + j] = ((BufferedImage) image).getSubimage( 
		    		i * (int)width, 
		    		j *(int)height, 
		            (int)width, 
		            (int)height 
		        ); 
		    } 
		} 
	}

	public Image getImage() {
		if (velocityY > 1){
			if (wasTravelingRight){
				return rangerSprites[76];
			}
			else {
				return rangerSprites[0];
			}
		}
		else if (velocityY < -1){
			if (wasTravelingRight){
				return rangerSprites[68];
			}
			else {
				return rangerSprites[8];
			}
		}
		else if (velocityX > 1){
			wasTravelingRight = true;
			if (animationCount <= 10) {
				animationCount++;
				return rangerSprites[4];
			}else if (animationCount <= 20){
				animationCount++;
				return rangerSprites[12];
			}else if (animationCount <= 30){
				animationCount++;
				return rangerSprites[20];
			}else if (animationCount <= 40){
				animationCount++;
				return rangerSprites[28];
			}else if (animationCount <= 50){
				animationCount++;
				return rangerSprites[36];
			}else if (animationCount <= 60){
				animationCount++;
				return rangerSprites[44];
			}else if (animationCount <= 70){
				animationCount = 0;
				return rangerSprites[52];
			}
		}
		
		else if (velocityX < -1){
			wasTravelingRight = false;
			if (animationCount <= 10) {
				animationCount++;
				return rangerSprites[24];
			}else if (animationCount <= 20){
				animationCount++;
				return rangerSprites[32];
			}else if (animationCount <= 30){
				animationCount++;
				return rangerSprites[40];
			}else if (animationCount <= 40){
				animationCount++;
				return rangerSprites[48];
			}else if (animationCount <= 50){
				animationCount++;
				return rangerSprites[56];
			}else if (animationCount <= 60){
				animationCount++;
				return rangerSprites[64];
			}else if (animationCount <= 70){
				animationCount = 0;
				return rangerSprites[72];
			}
		}
		else if (wasTravelingRight) {
			return rangerSprites[5];
		}
		return rangerSprites[73];
	}

	public boolean getVisible() {
		return visible;
	}

	public double getMinX() {
		return centerX - (width/2);
	}

	public double getMaxX() {
		return centerX + (width/2);
	}

	public double getMinY() {
		return centerY - (height/2);
	}

	public double getMaxY() {
		return centerY + (height/2);
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
		if (onGround) {
			if (keyboard.keyDown(38)) {
				isJumping = true;
				this.velocityY -= INITIAL_JUMP_VELOCITY;
				onGround = false;
			}
		}
		// RIGHT
		if (keyboard.keyDown(39)) {
			velocityX += ACCCELERATION_X;
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
			if (keyboard.keyDown(40)){
				collisionDetection.calculate2DBounce(bounce, this, universe.getBarriers(), velocityX, velocityY, actual_delta_time);
			}
			else {
				collisionDetection.calculate2DBounce(bounce, this, universe.getOneWayBarriers(), velocityX, velocityY, actual_delta_time);
			}
		}
		this.centerX = bounce.newX + (width / 2);
		this.centerY = bounce.newY + (height / 2);
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
		onGround = isOnGround(universe);
		
	}
	
	private boolean checkOverlap(Universe sprites, String targetSprite) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite.getClass().toString().contains(targetSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					overlap = true;
					break;					
				}
			}
		}		
		return overlap;		
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
