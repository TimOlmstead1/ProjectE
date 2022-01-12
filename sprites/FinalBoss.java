
import java.awt.Image;

import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class FinalBoss implements EnemySprite, MovableSprite{
	
	private final double RESULTANT_VELOCITY = 70;
	
	private double velocityY = 0;
	private double velocityX = 0;

	private double animationCount = 0;
	
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 38;
	private double height = 48;
	
	private boolean dispose = false;

	private Image[] snakeSprites;
	
	private CollisionDetection collisionDetection;
	
	private int health;
	private boolean beenHit = false;
	
	private double floatingAnimationCount = 0;
	
	private DisplayableSprite overlappingSprite;
	
	private double timeAlive = 0;
	
	private boolean shootingAnimation = false;
	private int deathAnimation = 0; //0 is not started, 1 is started, and 2 is complete
	private double deathAnimationCounter = 0;
	
	private boolean playerIsToTheRight = false;
	
	private EnemyProjectile[] rockShield;
	private boolean rocksAdded;

	private double timeLastShot = 0;
	private double timeHit = 0;
	private boolean angry = false;
	private boolean cosmic = false;
	private int cosmicCount = 0;
	
	public FinalBoss(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		rockShield = new EnemyProjectile[16];
		for (int i = 0; i < rockShield.length; i++) {
			if (i > 7) {
				rockShield[i] = new PositionRock(8, i-8);
			}
			else {
				rockShield[i] = new PositionRock(4, i);
			}
		}
		rocksAdded = false;
		
		collisionDetection = new CollisionDetection();
		
		health = 4;
		
		try {
			snakeSprites = new Image[12];
			for (int i = 1; i <= snakeSprites.length; i++) {
				String path = String.format("res/greenBoss/snakeBoss%d.png", i);
				snakeSprites[i-1] = ImageIO.read(new File(path));
			}
		}
		catch (IOException e) {
			System.out.print(e.toString());
		} 
	}

	public Image getImage() {
		if (deathAnimation == 1) {
			if (deathAnimationCounter <= 100) {
				deathAnimationCounter++;
				return snakeSprites[3];
			}
			else if (deathAnimationCounter <= 150) {
				deathAnimationCounter++;
				return snakeSprites[8];
			}
			else if (deathAnimationCounter <= 200) {
				deathAnimationCounter++;
				return snakeSprites[9];
			}
			else if (deathAnimationCounter <= 250) {
				deathAnimationCounter++;
				return snakeSprites[10];
			}
			else if (deathAnimationCounter <= 300) {
				deathAnimationCounter++;
				return snakeSprites[11];
			}
			else {
				deathAnimation = 2;
			}
		}
		else if (deathAnimation == 2) {
			return snakeSprites[11];
		}	
		else if (playerIsToTheRight){
			if (beenHit) {
				return snakeSprites[2];
			}
			else if (shootingAnimation) {
				return snakeSprites[1];
			}
			else {
				if (animationCount <= 30) {
					animationCount++;
					return snakeSprites[0];
				}
				else if (animationCount <= 60){
					animationCount++;
					return snakeSprites[3];
				}
				else {
					animationCount = 0;
					return snakeSprites[0]; 
				}	
			}
		}
		else {
			if (beenHit) {
				return snakeSprites[6];
			}
			else if (shootingAnimation) {
				return snakeSprites[5];
			}
			else {
				if (animationCount <= 30) {
					animationCount++;
					return snakeSprites[4];
				}
				else if (animationCount <= 60){
					animationCount++;
					return snakeSprites[7];
				}
				else {
					animationCount = 0;
					return snakeSprites[4]; 
				}
			}
		}
		return snakeSprites[11]; 
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
		
		if (deathAnimation == 2) {
			((FightingUniverse) universe).setIsFightOver(true);
			this.dispose = true;
		}
		
		if (health <= 0) {
			deathAnimation = 1;
			((FightingUniverse) universe).setIsFightStarted(false);
		}
		else {
			
			if (!(rocksAdded)) {
				for(int i = 0; i < rockShield.length; i++) {
					if (!(rockShield[i] == null)) {
						universe.getSprites().add(rockShield[i]);
					}
				}
				rocksAdded = true;
			}
			for (int i = 0; i < rockShield.length; i++) {
				if (!(rockShield[i] == null)) {
					if (((PositionRock) rockShield[i]).getHealth() <= 2){
						rockShield[i] = null;
					}
				}
			}
			
			if (universe.getPlayer1().getCenterX() > centerX) {
				playerIsToTheRight = true;
			}
			else {
				playerIsToTheRight = false;
			}
			
			timeAlive = timeAlive + (actual_delta_time*0.01);
			
			//
		
			if (checkOverlapArrows(universe)) {
				try {
					checkPixelCollision(universe, overlappingSprite);
				}
				catch(Exception ImageOutOfBoundsException){
				//actually figured out why this didn't work, the arrows needed to have their own collision detection as well 
				}
			}
			
			floatingAnimationCount++;
			if (floatingAnimationCount <= 20) {
				centerY = centerY + 0.5;
			}
			else if (floatingAnimationCount <= 40){
				centerY = centerY - 0.5;
			}
			else {
				floatingAnimationCount = 0;
			}
			

			
			double travelAngle = (playerAngle(universe));
			if ((distanceBetweenPlayer(universe) <= 200)&&(distanceBetweenPlayer(universe) > 170)){
				velocityX = 0;
				velocityY = 0;
				
				double targetX = universe.getPlayer1().getCenterX();
				double targetY = universe.getPlayer1().getCenterY();	
				double angleRadians = Math.atan(Math.abs((centerY - targetY)/(centerX - targetX)));
				
				if ((targetX > centerX)&&(targetY > centerY)) {
				}
				else if ((targetX < centerX)&&(targetY > centerY)) {
					angleRadians = Math.PI - angleRadians;
				}
				else if ((targetX < centerX)&&(targetY < centerY)) {
					angleRadians = Math.PI + angleRadians;
				}
				else {
					angleRadians = 2*(Math.PI) - angleRadians;
				}
				angleRadians = 2*(Math.PI) - angleRadians;
				velocityX = Math.sin(angleRadians)*(RESULTANT_VELOCITY);
				velocityY = Math.cos(angleRadians)*(RESULTANT_VELOCITY);
				
			}
			else if (distanceBetweenPlayer(universe) >= 170) {
				velocityX = Math.cos(travelAngle)*(RESULTANT_VELOCITY);
				velocityY = Math.sin(travelAngle)*(RESULTANT_VELOCITY);
			}
			
			else {
				velocityX = Math.cos(travelAngle)*(RESULTANT_VELOCITY)*(-1);
				velocityY = Math.sin(travelAngle)*(RESULTANT_VELOCITY)*(-1);
			}
			
			double movement_x = (this.velocityX * actual_delta_time * 0.001);
			double movement_y = (this.velocityY * actual_delta_time * 0.001);
			    
			this.centerX += movement_x;
			this.centerY += movement_y;
			
			//
			if (((cosmic)&&(timeAlive >= timeLastShot + 5)&&(timeAlive >= timeHit + 15))) {
				beenHit = false;
				cosmicShot(universe);
				timeLastShot = timeAlive;
			}
			if ((cosmic)&&(timeAlive >= timeHit + 80)){
				cosmic = false;
				timeHit = timeAlive;
				timeLastShot = timeAlive;
				angry = true;
			}
			
			
			if (((angry)&&(timeAlive >= timeLastShot + 3)&&(timeAlive >= timeHit + 15))) {
				beenHit = false;
				shootingAnimation = true;
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 1));
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) + Math.PI/48, 1)); // 3.25 degrees more
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) - + Math.PI/48, 1)); // same but less
				timeLastShot = timeAlive;
			}
			if ((angry)&&(timeAlive >= timeHit + 65)){
				angry = false;
				shootingAnimation = false;
			}
			
			
			if (timeAlive >= timeLastShot + 57) {
				shootingAnimation = true;
			}
			if (timeAlive >= timeLastShot + 60) {
				shootingAnimation = false;
				timeLastShot = timeAlive;
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 1));
			}
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
				if (!(beenHit)) {
					((ArrowSprite) sprite).setDispose();
					health = health - ((Projectile) sprite).getDamageGiven();
					beenHit = true;
					timeHit = timeAlive;
					for(int i = 0; i < rockShield.length; i++) {
						if (rockShield[i] == null) {
							if (i > 7) {
								rockShield[i] = new PositionRock(8, i-8);
							}
							else {
								rockShield[i] = new PositionRock(4, i);
							}
							universe.getSprites().add(rockShield[i]);
						}
					}
					if (health > 1) {
						angry = true;
					}
					else {
						cosmic = true;
					}
				}
			}
		}				
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
	
	private double distanceBetweenPlayer(Universe universe) {
		double diagonalDistance;
		double targetX = universe.getPlayer1().getCenterX();
		double targetY = universe.getPlayer1().getCenterY();
		diagonalDistance = Math.sqrt(Math.pow((targetX - centerX),2) + Math.pow((targetY - centerY),2));
		return diagonalDistance;
	}
	
	private void cosmicShot(Universe universe) {
		if (cosmicCount == 0) {
			for(int i = 0; i < 20; i++) {
				universe.getSprites().add(new BloodProjectile(24, 24, 0 + i*(Math.PI/10), 1));
			}
			cosmicCount++;
		}
		else if (cosmicCount == 1) {
			for(int i = 0; i < 20; i++) {
				universe.getSprites().add(new BloodProjectile(24, 24, Math.PI/20 + i*(Math.PI/10), 1));
			}
			cosmicCount--;
		}
	}
}
