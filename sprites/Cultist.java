import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Cultist implements EnemySprite, MovableSprite{
	
	private final double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND

	private double velocityY = 0;
	private double velocityX = 0;

	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 24;  //59
	private double height = 35; //87
	
	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	TwoDimensionBounce bounce;
	
	private static Image cultist = null;
	private static Image cultistSpawning = null;
	
	private int health;
	private boolean isSpawning = false;
	private double animationCount = Math.random()*(399);
	private boolean regularBatAdded = false;
	private DisplayableSprite overlappingSprite;

	public Cultist(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = 1;
		
		collisionDetection = new CollisionDetection();
		bounce = new TwoDimensionBounce();
		collisionDetection.setBounceFactorX(0);
		collisionDetection.setBounceFactorY(0);
	
		if (cultist == null) {
			try {
				cultist = ImageIO.read(new File("res/cultistEnemy/cultist2.png"));
				cultistSpawning = ImageIO.read(new File("res/cultistEnemy/cultist1.png"));
			}
			catch (IOException e) {
				System.out.print(e.toString());
			} 
		}
	}

	public Image getImage() {
		if (isSpawning) {
			return cultistSpawning;
		}
		return cultist;
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
	
		if (checkOverlapArrows(universe)) {
			try {
				checkPixelCollision(universe, overlappingSprite);
			}
			catch(Exception ImageOutOfBoundsException){
			//if at first you don't succeed try try try again
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
		
		if ((animationCount >= 400)&&(!(regularBatAdded))) {
			regularBatAdded = true;
			universe.getSprites().add(new FollowerBatEnemy(centerX, centerY));
			isSpawning = true;
		}	
		else if (animationCount >= 500){
			animationCount = 0;
			isSpawning = false;
			regularBatAdded = false;
		}
	}
	private boolean checkOverlapArrows(Universe sprites) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite instanceof ArrowSprite) {
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

	public int getCollisionDamage() {
		return 1;
	}
}
