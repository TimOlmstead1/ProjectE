import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MageBoss implements EnemySprite, MovableSprite{
	
	private final double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND
	private final double SPEED = 1;

	private double velocityY = 0;
	private double velocityX = 0;

	private double animationCount = 0;
	
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 32;
	private double height = 32;
	
	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	TwoDimensionBounce bounce;
	
	private Image[] mageSprites;
	
	private int health;
	private boolean beenHit = false;
	private double timeHit = 0;
	
	private double timeAlive = 0;
	private boolean eyeAdded = false;
	private boolean shot = false;
	private int castType = 0; // 0 is none, 1 is blue, 2 is red

	private int deathAnimation = 0; //0 is not started, 1 is started, and 2 is complete
	private double deathAnimationCounter = 0;
	
	private DisplayableSprite overlappingSprite;
	private boolean travellingRight = false;
	private boolean playerIsToTheRight = false;

	public MageBoss(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = 4;
		
		collisionDetection = new CollisionDetection();
		bounce = new TwoDimensionBounce();
		collisionDetection.setBounceFactorX(0);
		collisionDetection.setBounceFactorY(0);
	
		try {
			mageSprites = new Image[11];
			for (int i = 1; i <= mageSprites.length; i++) {
				String path = String.format("res/EvilMage/Mage%d.png", i);
				mageSprites[i-1] = ImageIO.read(new File(path));
			}
		}
		catch (IOException e) {
			System.out.print(e.toString());
		} 
	}

	public Image getImage() {
		if (deathAnimation == 1) {
			if (playerIsToTheRight) {
				if (deathAnimationCounter <= 40) {
					deathAnimationCounter++;
					return mageSprites[0];
				}
				else if (deathAnimationCounter <= 80) {
					deathAnimationCounter++;
					return mageSprites[3];
				}
				else if (deathAnimationCounter <= 120) {
					deathAnimationCounter++;
					return mageSprites[5];
				}
				else if (deathAnimationCounter <= 480) {
					deathAnimationCounter++;
					return mageSprites[8];
				}
				else {
					deathAnimation = 2;
					return mageSprites[8];
				}
			}
			else {
				if (deathAnimationCounter <= 40) {
					deathAnimationCounter++;
					return mageSprites[1];
				}
				else if (deathAnimationCounter <= 80) {
					deathAnimationCounter++;
					return mageSprites[2];
				}
				else if (deathAnimationCounter <= 120) {
					deathAnimationCounter++;
					return mageSprites[4];
				}
				else if (deathAnimationCounter <= 480) {
					deathAnimationCounter++;
					return mageSprites[8];
				}
				else {
					deathAnimation = 2;
					return mageSprites[8];
				}	
			}
		}
		else if (deathAnimation == 2) {
			return mageSprites[8];
		}
		if (playerIsToTheRight) {
			if (castType == 1) {
				return mageSprites[9];
			}
			else if (castType == 2) {
				return mageSprites[6];
			}
			return mageSprites[0];
		}
		else {
			if (castType == 1) {
				return mageSprites[10];
			}
			else if (castType == 2) {
				return mageSprites[7];
			}
			return mageSprites[1];
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
		
		timeAlive = timeAlive + (actual_delta_time*0.01);
		
		if (deathAnimation == 2) {
			((FightingUniverse) universe).setIsFightOver(true);
			this.dispose = true;
		}
		
		if (health <= 0) {
			((FightingUniverse) universe).setIsFightStarted(false);
			deathAnimation = 1;
		}
		if (!(visible)) {
			centerX = 0;
			centerY = 0;
			if (!(checkForCopy(universe))) {
				visible = true;	
				beenHit = false;
				centerX = randomX(universe);
				centerY = randomY(universe);	
			}
		}
		else if ((visible)&&(!(deathAnimation == 1))) {
			
			if (beenHit) {
				visible = false;
			}
			
			if (universe.getPlayer1().getCenterX() > centerX) {
				playerIsToTheRight = true;
			}
			else {
				playerIsToTheRight = false;
			}

			//
		
			checkCollision(universe);
			
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
			
			if (travellingRight) {
				centerX = centerX + SPEED;
			}
			else {
				centerX = centerX - SPEED;
			}
			
			//
			if ((timeAlive%68 > 1)&&(timeAlive%68 < 8)){
				castType = 2;
			}
			else if ((timeAlive%48 > 1)&&(timeAlive%48 < 8)){
				castType = 1;
			}
			else {
				castType = 0;
			}
			
			if ((timeAlive%70 < 1)&&(!(eyeAdded))) {
				eyeAdded = true;
				universe.getSprites().add(new EyeEnemy(centerX, centerY));
			}
			else if ((timeAlive%70 > 1)&&(timeAlive%70 < 2)){
				eyeAdded = false;
			}
			
			if ((timeAlive%50 < 1)&&(!(shot))) {
				shot = true;
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 1));
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) + Math.PI/22, 1));
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) - + Math.PI/22, 1));
			}
			else if ((timeAlive%50 > 1)&&(timeAlive%50 < 2)){
				shot = false;
			}
		}
	}
	
	private void checkCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if (sprite instanceof ArrowSprite) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
					
					if (!(beenHit)) {
						((ArrowSprite) sprite).setDispose();
						for (int j = 0; j <= 4 - (health-1); j ++) {
							splitSelf(universe);
						}
						health = health - ((Projectile) sprite).getDamageGiven();
						if (!(health == 0)) {
							beenHit = true;
							timeHit = timeAlive;
						}
						break;
					}
				}			
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
	
	
	private double playerAngle(Universe universe) { //finds the angle between the player and the boss
		
		double targetX = universe.getPlayer1().getCenterX();
		double targetY = universe.getPlayer1().getCenterY();	
		double angleRadians = Math.atan((centerY - targetY)/(centerX - targetX));
		
		if (angleRadians < 0) { //negative radians (since CAST)
			if (targetX < centerX) {
				angleRadians = angleRadians - Math.PI;
			}
		}
		else {
			if (targetY < centerY) {
				angleRadians = angleRadians + Math.PI;
			}
		}
		
		return angleRadians;
	}
	
	private boolean checkOverlap(Universe sprites) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if ((sprite instanceof ArrowSprite)||(sprite instanceof BarrierSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY()-5, this.getMaxX(), this.getMaxY()-5, sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					overlap = true;
					overlappingSprite = sprite;
					break;					
				}
			}
		}		
		return overlap;		
	}	
	
	private void splitSelf(Universe universe) {
		double locationX = randomX(universe);
		double locationY = randomY(universe);
		universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 1));
		universe.getSprites().add(new MageCopy(locationX, locationY, 2));
	}
	
	private double randomX(Universe universe) {
		double locationX;
		int random = (int) ((Math.random() * (3)));
		if (random == 1) {
			locationX = StandardLevelLayout.TILE_WIDTH * 5;
		}
		else if (random == 2) {
			locationX = StandardLevelLayout.TILE_WIDTH * 32;
		}
		else {
			locationX = StandardLevelLayout.TILE_WIDTH * 59;
		}
		if ((locationX < universe.getPlayer1().getCenterX()+32)&&(locationX > universe.getPlayer1().getCenterX()-32)) {
			return randomY(universe);
		}
		return locationX;
	}
	
	private double randomY(Universe universe) {
		double locationY;
		int random = (int) ((Math.random() * (5)));
		if (random == 1) {
			locationY = StandardLevelLayout.TILE_HEIGHT * 5;
		}
		else if (random == 2) {
			locationY = StandardLevelLayout.TILE_WIDTH * 15;
		}
		else if (random == 3) {
			locationY = StandardLevelLayout.TILE_WIDTH * 26;
		}
		else if (random == 4) {
			locationY = StandardLevelLayout.TILE_WIDTH * 37;
		}
		else {
			locationY = StandardLevelLayout.TILE_WIDTH * 48;
		}
		if ((locationY < universe.getPlayer1().getCenterY()+32)&&(locationY > universe.getPlayer1().getCenterY()-32)) {
			return randomY(universe);
		}
		return locationY;
	}
	private boolean checkForCopy(Universe universe) {

		boolean copyFound = false;

		for (DisplayableSprite sprite : universe.getSprites()) {
			if (sprite instanceof MageCopy) {
				copyFound = true;
				break;
			}
		}
		return copyFound;
	}
}
