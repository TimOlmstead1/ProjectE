import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class RangerCharacterSprite implements DisplayableSprite, MovableSprite{
	
	private final double ACCCELERATION_X = 15;		//PIXELS PER SECOND PER SECOND
	private final double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND
	private final double MAX_VELOCITY_X = 600;	//PIXELS PER SECOND
	private final double MAX_VELOCITY_Y = 400;
	private final double FRICTION_FACTOR_X = 0.90; 
	private final double INITIAL_JUMP_VELOCITY = 320; //pixels / second
	
	private final double RELOAD_TIME = 500;
	
	private Universe currentUniverse = null;
	
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
	
	private double convertedMouseX;
	private double convertedMouseY;
	private double reloadTime = 0;
	private boolean shooting = false;
	
	private int health;
	
	public RangerCharacterSprite(double centerX, double centerY, double height, double width) {
		this(centerX,centerY, 0);
		this.height = height;
		this.width = width;
	}
	
	public RangerCharacterSprite(double centerX, double centerY, int startingHelath) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = startingHelath;
		
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
		if (shooting) {
			if (((centerX - currentUniverse.getXCenter()) < convertedMouseX)){
				if (animationCount <= 10) {
					animationCount++;
					return rangerSprites[6];
				}else if (animationCount <= 20){
					animationCount++;
					return rangerSprites[14];
				}else if (animationCount <= 30){
					animationCount++;
					return rangerSprites[22];
				}else if (animationCount <= 40){
					animationCount++;
					return rangerSprites[30];
				}else if (animationCount <= 50){
					animationCount++;
					return rangerSprites[38];
				}else if (animationCount <= 60){
					animationCount++;
					return rangerSprites[46];
				}else if (animationCount <= 70){
					animationCount++;
					return rangerSprites[54];
				}else if (animationCount <= 80){
					animationCount++;
					shoot(currentUniverse, convertedMouseX, convertedMouseY);
					return rangerSprites[62];
				}else if (animationCount <= 90){
					animationCount++;
					return rangerSprites[70];
				}else if (animationCount <= 100){
					animationCount = 0;
					shooting = false;
					return rangerSprites[78];
				}		
				
			}
			else {
				if (animationCount <= 10) {
					animationCount++;
					return rangerSprites[74];
				}else if (animationCount <= 20){
					animationCount++;
					return rangerSprites[66];
				}else if (animationCount <= 30){
					animationCount++;
					return rangerSprites[58];
				}else if (animationCount <= 40){
					animationCount++;
					return rangerSprites[50];
				}else if (animationCount <= 50){
					animationCount++;
					return rangerSprites[42];
				}else if (animationCount <= 60){
					animationCount++;
					return rangerSprites[34];
				}else if (animationCount <= 70){
					animationCount++;
					return rangerSprites[26];
				}else if (animationCount <= 80){
					animationCount++;
					shoot(currentUniverse, convertedMouseX, convertedMouseY);
					return rangerSprites[18];
				}else if (animationCount <= 90){
					animationCount++;
					return rangerSprites[10];
				}else if (animationCount <= 100){
					animationCount = 0;
					shooting = false;
					return rangerSprites[2];
				}
				
			}
		}
		
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
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public void update(Universe universe, KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {
		
		if (currentUniverse == null) {
			currentUniverse = universe;
		}
		
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
		
		if (mouse.buttonDown(1)) {
			
			shooting = true;
			
			double mouseX = mouse.getPosition().getX();
			double mouseY = mouse.getPosition().getY();
			//System.out.println(String.format("mouse position: %7.2f, %7.2f", mouseX, mouseY));
			
			convertedMouseX = ((mouseX - 64)/ universe.getScale()) - (universe.getXCenter());   // the 64 comes from the way the frame is drawn rectangularly so the origin is actually 64 pixels (the width of a stony wall tile) further to the right with this particular universe scale 
			convertedMouseY = ((mouseY / universe.getScale()) - universe.getYCenter());
			
			//System.out.println(String.format("mouse Converted position: %7.2f, %7.2f", convertedMouseX, convertedMouseY));
		}		
		
		reloadTime -= actual_delta_time;
		
	}
	
	public void shoot(Universe universe, double mouseCenterX, double mouseCenterY) {
		
		if (reloadTime <= 0) {
		
			double relativeScreenCenterX = ((centerX - universe.getXCenter()) - mouseCenterX);
			double relativeScreenCenterY = ((centerY - universe.getYCenter()) - mouseCenterY);
			
			double angleRadians = Math.atan((relativeScreenCenterY)/(relativeScreenCenterX));
		
			if ((centerX - universe.getXCenter()) > mouseCenterX) {
				if (angleRadians < 0) {
					angleRadians = -1*((Math.PI) - angleRadians);
				}
				else {
					angleRadians = (Math.PI) + angleRadians;
				}
			}
		
			//System.out.println(String.format("%7.2f", Math.toDegrees(angleRadians))); //for testing angles
		
			Projectile arrow = new ArrowSprite(centerX, centerY, angleRadians);
			universe.getSprites().add(arrow);
			reloadTime = RELOAD_TIME;
		}
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
