import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Turtle implements EnemySprite, MovableSprite{
	
	private final double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND

	private double velocityY = 0;
	private double velocityX = 0;

	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	private double speed = 0;
	
	private double width = 41;  
	private double height = 21; 

	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	TwoDimensionBounce bounce;
	
	private static Image[] turtleImages = new Image[8];

	private int health;
	private double animationCount = 0;
	private DisplayableSprite overlappingSprite;
	
	private boolean travellingRight = false;

	public Turtle(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = 1000;  //needs to be an enemy but essentially cannot be killed
		
		collisionDetection = new CollisionDetection();
		bounce = new TwoDimensionBounce();
		collisionDetection.setBounceFactorX(0);
		collisionDetection.setBounceFactorY(0);
	
		if (turtleImages[0] == null) {
			try {
				for (int i = 1; i <= turtleImages.length; i++) {
					String path = String.format("res/turtleEnemy/turtle%d.png", i);
					turtleImages[i-1] = ImageIO.read(new File(path));
				}
			}
			catch (IOException e) {
				System.out.print(e.toString());
			} 
		}
	}

	public Image getImage() {
		if (travellingRight) {
			if (animationCount <= 30) {
				animationCount++;
				return turtleImages[0];
			}else if (animationCount <= 60){
				animationCount++;
				return turtleImages[1];
			}else if (animationCount <= 90){
				animationCount++;
				return turtleImages[2];
			}else if (animationCount <= 120){
				animationCount++;
				return turtleImages[3];
			}
			else {
				animationCount = 0;
				return turtleImages[3];
			}
		}
		else {
			if (animationCount <= 30) {
				animationCount++;
				return turtleImages[4];
			}else if (animationCount <= 60){
				animationCount++;
				return turtleImages[5];
			}else if (animationCount <= 90){
				animationCount++;
				return turtleImages[6];
			}else if (animationCount <= 120){
				animationCount++;
				return turtleImages[7];
			}
			else {
				animationCount = 0;
				return turtleImages[7];
			}
		}
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
		
		animationCount++;
		
		if ((health <= 0)||((FightingUniverse) universe).getIsFightStarted() == false) {
			this.dispose = true;
		}
		
		if (checkOverlap(universe)) {
			try {
				checkPixelCollision(universe, overlappingSprite);
			}
			catch(Exception ImageOutOfBoundsException){
			//if at first you don't succeed try try try again   //actually figured out why this didn't work, the arrows needed to have their own collision detection as well 
			}
		}
		
		boolean onGround = isOnGround(universe);
		
		if (velocityY < 0) {
			collisionDetection.calculate2DBounce(bounce, this, universe.getBarriers(), velocityX, velocityY, actual_delta_time);
		}
		else if(velocityY >= 0) {
			collisionDetection.calculate2DBounce(bounce, this, universe.getOneWayBarriers(), velocityX, velocityY, actual_delta_time);
		}
		this.centerY = bounce.newY + (height / 2);
		this.velocityY = bounce.newVelocityY;

		if (onGround == true) {
			this.velocityY = 0;
		} else {
			this.velocityY = this.velocityY + ACCCELERATION_Y * 0.001 * actual_delta_time;
		}
		onGround = isOnGround(universe);
		
		//
		
		if (checkOverlap(universe)) {
			if (overlappingSprite instanceof BarrierSprite) {
				if (travellingRight) {
					travellingRight = false;
				}
				else {
					travellingRight = true;
				}
			}
		}
		if ((universe.getPlayer1().getCenterY() <= centerY+15)&&(universe.getPlayer1().getCenterY() >= centerY-15)) {
			speed = 3;
		}
		else {
			speed = 2;
		}
		
		if (travellingRight) {
			centerX = centerX + speed;
		}
		else {
			centerX = centerX - speed;
		}
	}
	private boolean checkOverlap(Universe sprites) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if ((sprite instanceof ArrowSprite)||(sprite instanceof BarrierSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					overlap = true;
					overlappingSprite = sprite;
					break;					
				}
			}
		}		
		return overlap;		
	}	
	
	private void checkPixelCollision(Universe universe, DisplayableSprite sprite) {
		if (sprite instanceof ArrowSprite) {
			
			if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
				
				((ArrowSprite) sprite).setDispose();
				health = health - ((Projectile) sprite).getDamageGiven();
			}
		}				
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

	public void setDispose() {
		this.dispose = true;
	}

	public int getCollisionDamage() { //running into a cultist does not harm you
		return 1;
	}
}
