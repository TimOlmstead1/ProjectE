import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class VampireBoss implements EnemySprite, MovableSprite{
	
	private final double ACCCELERATION_Y = 600; 	//PIXELS PER SECOND PER SECOND

	private double velocityY = 0;
	private double velocityX = 0;

	private double animationCount = 0;
	
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 59;  //211
	private double height = 55; //195
	
	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	TwoDimensionBounce bounce;
	
	private Image[] vampireSprites;
	
	private int health;
	private boolean beenHit = false;
	private double timeShielded = 0;
	private boolean shielded = false;
	private double shieldDuration = 100;
	
	private double timeAlive = 0;
	private boolean regularBatAdded = false;
	
	private boolean isSummoning = false;
	private double timeSummoning = 0;
	private boolean summoned = false;
	
	private double timeLastMoved = 0;
	
	private boolean bloodRageStarted = false;
	private boolean bloodRageOver = false;
	private boolean bloodRage = false;
	private double bloodRageAnimation = 0;
	private double bloodRageTimer = 0;
	private boolean bloodRageSummon;
	
	private int deathAnimation = 0; //0 is not started, 1 is started, and 2 is complete
	private double deathAnimationCounter = 0;

	public VampireBoss(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = 5;
		
		collisionDetection = new CollisionDetection();
		bounce = new TwoDimensionBounce();
		collisionDetection.setBounceFactorX(0);
		collisionDetection.setBounceFactorY(0);
	
		try {
			vampireSprites = new Image[9];
			for (int i = 1; i <= vampireSprites.length; i++) {
				String path = String.format("res/vampireBoss/vampire%d.png", i);
				vampireSprites[i-1] = ImageIO.read(new File(path));
			}
		}
		catch (IOException e) {
			System.out.print(e.toString());
		} 
	}

	public Image getImage() {
		if (deathAnimation == 1) {
			if (deathAnimationCounter <= 80) {
				deathAnimationCounter++;
				return vampireSprites[5];
			}
			else if (deathAnimationCounter <= 160) {
				deathAnimationCounter++;
				return vampireSprites[6];
			}
			else if (deathAnimationCounter <= 320) {
				deathAnimationCounter++;
				return vampireSprites[7];
			}
			else if (deathAnimationCounter <= 480) {
				deathAnimationCounter++;
				return vampireSprites[8];
			}
			else {
				deathAnimation = 2;
			}
		}
		else if (deathAnimation == 2) {
			return vampireSprites[8];
		}
		else {
			if (shielded) {
				return vampireSprites[3];
			}
			else if (isSummoning) {
				return vampireSprites[4];
			}
			else if ((bloodRageStarted)&&(!(bloodRageOver))){
				if (bloodRageAnimation < 100){
					bloodRageAnimation++;
					return vampireSprites[1];
				}
				else if (bloodRageAnimation >= 100){
					return vampireSprites[2];
				}
			}
			else {
				return vampireSprites[0];
			}
		}
		return vampireSprites[8];
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
			((FightingUniverse) universe).setIsFightStarted(false);
			deathAnimation = 1;
		}
		else {
			
			timeAlive = timeAlive + (actual_delta_time*0.01);
			
			if (bloodRage) {
				bloodRageTimer = bloodRageTimer - (actual_delta_time*0.01);
				if (bloodRageTimer < 0) {
					bloodRage = false;
					bloodRageOver = true;
				}
			}
			
		
			if (health > 3) {
				shieldDuration = 85;
			}
			else if (health < 3) {
				shieldDuration = 180;
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
			
			if ((timeAlive%50 < 1)&&(!(regularBatAdded))) {
				regularBatAdded = true;
				universe.getSprites().add(new followerBatEnemy(centerX, centerY));
				fireInCircle(universe);
			}
			else if ((timeAlive%50 > 1)&&(timeAlive%50 < 2)){
				regularBatAdded = false;
			}
			
			if ((!(timeShielded == 0))&&(timeAlive >= timeShielded + shieldDuration)){  
				if ((bloodRageStarted)&&(!(bloodRageOver))) {
					shielded = false;
					timeShielded = 0;
				}
				else if (health < 3) {
					isSummoning = true; 
					timeSummoning = timeAlive;
					shielded = false;
					timeShielded = 0;
				}
				else if (health >= 3) {
					shielded = false;
					timeShielded = 0;
				}
			}
		
			if ((isSummoning)&&(!(summoned))){
				universe.getSprites().add(new Cultist(StandardLevelLayout.TILE_WIDTH * 4, StandardLevelLayout.TILE_HEIGHT * 4));
				universe.getSprites().add(new Cultist(StandardLevelLayout.TILE_WIDTH * 4, StandardLevelLayout.TILE_HEIGHT * 48));
				universe.getSprites().add(new Cultist(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 48));
				universe.getSprites().add(new Cultist(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 4));
				summoned = true;
			}
			else if ((summoned)&&(timeAlive >= timeSummoning+20)&&isSummoning){ 
				timeSummoning = 0;
				isSummoning = false;
				summoned = false;
				if (health == 1) {  //when at 1 health re-shield after finishing the cultist summoning
					shielded = true;
					timeShielded = timeAlive;
				}
			}
			if (bloodRage) {
				if ((bloodRageTimer % 50 < 1)&&(!(bloodRageSummon))) {
					move(universe);
					fireInCircle(universe);
					universe.getSprites().add(new Cultist(centerX, centerY));
					universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 0));
					universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) + Math.PI/48, 0)); //3.75 degrees
					universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) - + Math.PI/48, 0));
					bloodRageSummon = true;
				}
				else if ((bloodRageTimer % 50 > 1)&&((bloodRageSummon))){
					bloodRageSummon = false;
				}
			}
			
			if (beenHit && (health > 0)) {
				move(universe);
				int random = (int) ((Math.random() * (4 - 1)) + 1);
				if (!(random == 1)) {
					universe.getSprites().add(new followerBatEnemy(StandardLevelLayout.TILE_WIDTH * 2, StandardLevelLayout.TILE_HEIGHT * 1));
				}
				if (!(random == 2)) {
					universe.getSprites().add(new followerBatEnemy(StandardLevelLayout.TILE_WIDTH * 2, StandardLevelLayout.TILE_HEIGHT * 48));
				}
				if (!(random == 3)) {
					universe.getSprites().add(new followerBatEnemy(StandardLevelLayout.TILE_WIDTH * 62, StandardLevelLayout.TILE_HEIGHT * 48));
				}
				if (!(random == 4)) {
					universe.getSprites().add(new followerBatEnemy(StandardLevelLayout.TILE_WIDTH * 62, StandardLevelLayout.TILE_HEIGHT * 1));
				}
				beenHit = false;
			}
			else if ((timeAlive >= timeLastMoved + 90)){
				move(universe);
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 0));
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) + Math.PI/24, 0)); // 7.5 degrees more
				universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe) - + Math.PI/24, 0)); // same but less
			}
			
			if ((health == 1)&&(!(bloodRageStarted))) {
				bloodRageStarted = true;
				bloodRage = true;
				bloodRageTimer = 200;
				shielded = false;
				timeShielded = 0;
			}
		}
	}
	
	private void checkCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if (sprite instanceof ArrowSprite) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
					
					((ArrowSprite) sprite).setDispose();
					if ((!(shielded))&&(!(bloodRage))) {
						health = health - ((Projectile) sprite).getDamageGiven();
						beenHit = true;
						shielded = true;
						timeShielded = timeAlive;
					}
					break;
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
		health = health + 1;
		return 1;
	}
	private void move(Universe universe) {
		//calculates best position for the boss to teleport to now
		double playerX = universe.getPlayer1().getCenterX();
		double playerY = universe.getPlayer1().getCenterY();
		
		double newCenterX = 0;
		double newCenterY = 0;
		
		if (playerY > (StandardLevelLayout.TILE_HEIGHT * 23)) { //higher than middle
			newCenterY = StandardLevelLayout.TILE_HEIGHT * 4;
		}
		else { //lower than middle
			newCenterY = StandardLevelLayout.TILE_HEIGHT * 45;
		}
		
		if ((playerX > (StandardLevelLayout.TILE_WIDTH * 22))&&(playerX < (StandardLevelLayout.TILE_WIDTH * 42))) { //near middle
			newCenterX = StandardLevelLayout.TILE_WIDTH * 32;
		}
		else if (playerX < (StandardLevelLayout.TILE_WIDTH * 22)) { //left side
			newCenterX = StandardLevelLayout.TILE_WIDTH * 58;     //64-6 since 32 is half
		}
		else { //right side
			newCenterX = StandardLevelLayout.TILE_WIDTH * 6;  
		}
		
		this.centerX = newCenterX;
		this.centerY = newCenterY;
		timeLastMoved = timeAlive;
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
	
	private void fireInCircle(Universe universe) {
		for (int i = 0; i < 24; i++) {
			universe.getSprites().add(new BloodProjectile(centerX, centerY, 0 + i*(Math.PI/12), 0));
		}
	}
}
