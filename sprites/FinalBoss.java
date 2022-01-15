
import java.awt.Image;

import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class FinalBoss implements EnemySprite, MovableSprite{
	
	private final double RESULTANT_VELOCITY = 70;
	private final double COSMIC_DURATION = 100;
	
	private double velocityY = 0;
	private double velocityX = 0;

	private double animationCount = 0;
	
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 88; //44
	private double height = 90; //45
	
	private boolean dispose = false;

	private Image[] flameSprites;
	
	private int bossStage = -1; // Increases to change boss behavior
	
	private double floatingAnimationCount = 0;
	
	private CollisionDetection collisionDetection;
	
	private int health;
	private boolean beenHit = false;
	
	private DisplayableSprite overlappingSprite;
	
	private double timeAlive = 0;
	
	private boolean shootingAnimation = false;
	private int deathAnimation = 0; //0 is not started, 1 is started, and 2 is complete
	private double deathAnimationCounter = 0;
	
	private boolean playerIsToTheRight = false;
	
	private double timeLastShot = 0;
	private double timeHit = 0;
	private int cosmicCount = 0;
	private boolean cosmicShooting = false;
	private boolean cosmicShootingSimul = false;
	private double cosmicAnimation = 0;
	private boolean waspSummoned = false;
	
	public FinalBoss(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		collisionDetection = new CollisionDetection();
		
		health = 9;
		
		try {
			flameSprites = new Image[11];
			for (int i = 1; i <= flameSprites.length; i++) {
				String path = String.format("res/FlameBoss/BlueFlameBoss%d.png", i);
				flameSprites[i-1] = ImageIO.read(new File(path));
			}
		}
		catch (IOException e) {
			System.out.print(e.toString());
		} 
	}

	public Image getImage() {
		if (bossStage < 2) {
			if (playerIsToTheRight) {
				return flameSprites[10]; 
			}
			else {
				return flameSprites[4]; 
			}
		}
		return flameSprites[10]; 
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
		/////
		
		if (universe.getPlayer1().getCenterX() > centerX) {
			playerIsToTheRight = true;
		}
		else {
			playerIsToTheRight = false;
		}
		
		timeAlive = timeAlive + (actual_delta_time*0.01);
		
		if ((health < 9)&&(health >= 7)) {
			bossStage = 0;
			if (beenHit) {
				cosmicAnimation = timeAlive;
				cosmicCount = 0;
				centerX = -100;
				centerY = 100; 
				beenHit = false;
			}
		}
		else if ((health < 7)&&(health > 4)) {
			bossStage = 1;
			if (beenHit) {
				waspSummoned = false;
				centerX = -100;
				centerY = 100; 
				beenHit = false;
			}
		}
		
		if (timeHit + 15 <= timeAlive) {
			beenHit = false;
		}
		//
	
		if (checkOverlapProjectiles(universe)) {
			try {
				checkPixelCollision(universe, overlappingSprite);
			}
			catch(Exception ImageOutOfBoundsException){
			//actually figured out why this didn't work, the arrows needed to have their own collision detection as well 
			}
		}
		
		if (bossStage < 2) {
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
		}
		if (bossStage == 0) {
			if (health == 8) {
				if (cosmicAnimation + 5 >= timeAlive) {
					centerX = -100;
					centerY = 100;    //puts the boss way off screen
				}
				else if (cosmicAnimation + COSMIC_DURATION >= timeAlive) {
					if ((timeAlive%6 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicShot(universe, 1);
					}
					else if ((timeAlive%6 > 1)&&(timeAlive%6 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation + COSMIC_DURATION*2 >= timeAlive) {
					if ((timeAlive%6 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicShot(universe, 2);
					}
					else if ((timeAlive%6 > 1)&&(timeAlive%6 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation + COSMIC_DURATION*3 >= timeAlive) {
					if ((timeAlive%5 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicShot(universe, 3);
					}
					else if ((timeAlive%5 > 1)&&(timeAlive%5 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation + COSMIC_DURATION*4 >= timeAlive) {
					if ((timeAlive%5 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicShot(universe, 4);
					}
					else if ((timeAlive%5 > 1)&&(timeAlive%5 < 2)){
						cosmicShooting = false;
					}
				}
				else{
					centerX = StandardLevelLayout.TILE_WIDTH * 32;
					centerY = StandardLevelLayout.TILE_HEIGHT * 47; 
				}
			}
			
			///////////////////////////////////////////////////////////////////////////////////
			else if (health == 7) {
				if (cosmicAnimation + 0.05 >= timeAlive) {
					centerX = -100;
					centerY = 100;    //puts the boss way off screen
				}
				else if (cosmicAnimation + COSMIC_DURATION*0.4 >= timeAlive) {
					if ((timeAlive%1.1 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicWall(universe, 2);
					}
					else if ((timeAlive%1.2 > 1)&&(timeAlive%1.2 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation + COSMIC_DURATION*0.4 + 0.5 >= timeAlive) {
					cosmicCount = 0;
				}
				else if (cosmicAnimation + COSMIC_DURATION*0.8 >= timeAlive) {
					if ((timeAlive%1.2 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicWall(universe, 1);
					}
					else if ((timeAlive%1.2 > 1)&&(timeAlive%1.2 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation + COSMIC_DURATION*0.8 + 0.5 >= timeAlive) {
					cosmicCount = 0;
				}
				else if (cosmicAnimation + COSMIC_DURATION*1.28 >= timeAlive) {
					if ((timeAlive%1.1 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicWall(universe, 4);
						cosmicCount--;
						cosmicWall(universe, 3);
					}
					else if ((timeAlive%1.2 > 1)&&(timeAlive%1.2 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation +  COSMIC_DURATION*1.28 + 0.5 >= timeAlive) {
					cosmicCount = 0;
					cosmicShooting = false;
				}
				else if (cosmicAnimation + COSMIC_DURATION*1.76 >= timeAlive) {
					if ((timeAlive%1.2 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicWall(universe, 5);
						cosmicCount--;
						cosmicWall(universe, 6);
					}
					else if ((timeAlive%1.2 > 1)&&(timeAlive%1.2 < 2)){
						cosmicShooting = false;
					}
				}
				else if (cosmicAnimation +  COSMIC_DURATION*1.76 + 0.5 >= timeAlive) {
					cosmicCount = 0;
					cosmicShooting = false;
				}
				else if (cosmicAnimation + COSMIC_DURATION*2.16 >= timeAlive) {
					if ((timeAlive%1.2 < 1)&&(!(cosmicShooting))) {
						cosmicShooting = true;
						cosmicWall(universe, 2);
					}
					else if ((timeAlive%1.2 > 1)&&(timeAlive%1.2 < 2)){
						cosmicShooting = false;
					}
				}
				else{
					centerX = StandardLevelLayout.TILE_WIDTH * 32;
					centerY = StandardLevelLayout.TILE_HEIGHT * 47; 
				}
			}
		}
		////////////////////////////////////////////////////////////////////////////
		else if (bossStage == 1) {
			if (health == 6) {
				if (!(waspSummoned)) {
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 4, StandardLevelLayout.TILE_HEIGHT * 4));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 4, StandardLevelLayout.TILE_HEIGHT * 48));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 48));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 4));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 30, StandardLevelLayout.TILE_HEIGHT * 48));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 30, StandardLevelLayout.TILE_HEIGHT * 4));
					waspSummoned = true;
				}
				if (checkForWasp(universe)) {
					centerX = -100;
					centerY = 100;    //puts the boss way off screen
				}
				else {
					centerX = StandardLevelLayout.TILE_WIDTH * 32;
					centerY = StandardLevelLayout.TILE_HEIGHT * 5; 
				}
			}
			else if (health == 5) {
				if (!(waspSummoned)) {
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 4, StandardLevelLayout.TILE_HEIGHT * 4));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 4, StandardLevelLayout.TILE_HEIGHT * 48));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 48));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 4));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 30, StandardLevelLayout.TILE_HEIGHT * 48));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 30, StandardLevelLayout.TILE_HEIGHT * 4));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 22));
					universe.getSprites().add(new WaspEnemy(StandardLevelLayout.TILE_WIDTH * 60, StandardLevelLayout.TILE_HEIGHT * 22));
					waspSummoned = true;
				}
				if (checkForWasp(universe)) {
					centerX = -100;
					centerY = 100;    //puts the boss way off screen
				}
				else {
					centerX = StandardLevelLayout.TILE_WIDTH * 32;
					centerY = StandardLevelLayout.TILE_HEIGHT * 5; 
				}
			}
		}
		///////////////////////////////////////////////////////////////////////////
		else if (bossStage == 2) {
			
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
	
	private boolean checkOverlapProjectiles(Universe sprites) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if ((sprite instanceof ArrowSprite)||(sprite instanceof ToggleBullet)) {
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
		if ((sprite instanceof ArrowSprite)||(sprite instanceof ToggleBullet)) {
			
			if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
				
				if (!(beenHit)) {
					((Projectile) sprite).setDispose();
					if (sprite instanceof ArrowSprite) {
						if (bossStage < 2) {
							health = health - ((Projectile) sprite).getDamageGiven();
							beenHit = true;
							timeHit = timeAlive;
						}
					}
					else if (sprite instanceof ToggleBullet){
						health = health - ((Projectile) sprite).getDamageGiven();
						beenHit = true;
						timeHit = timeAlive;
					}					
				}
			}
		}				
	}
	
	public void setDispose() {
		this.dispose = true;
	}

	public int getCollisionDamage() {
		return 2;
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
	
	private void cosmicShot(Universe universe, int type) {
		if (type == 1) {
			if (cosmicCount == 0) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(24, 25, 0 + i*(Math.PI/10), 1));
				}
				cosmicCount++;
			}
			else if (cosmicCount == 1) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(24, 25, Math.PI/20 + i*(Math.PI/10), 1));
				}
				cosmicCount--;
			}
		}

		else if (type == 2) {
			if (cosmicCount == 0) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(StandardLevelLayout.TILE_WIDTH * 61, StandardLevelLayout.TILE_HEIGHT * 49, 0 + i*(Math.PI/10), 1));
				}
				cosmicCount++;
			}
			else if (cosmicCount == 1) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(StandardLevelLayout.TILE_WIDTH * 61, StandardLevelLayout.TILE_HEIGHT * 49, Math.PI/20 + i*(Math.PI/10), 1));
				}
				cosmicCount--;
			}
		}
		else if (type == 3) {
			if (cosmicCount == 0) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(StandardLevelLayout.TILE_WIDTH * 61, 25, 0 + i*(Math.PI/10), 1));
				}
				cosmicCount++;
			}
			else if (cosmicCount == 1) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(StandardLevelLayout.TILE_WIDTH * 61, 25, Math.PI/20 + i*(Math.PI/10), 1));
				}
				cosmicCount--;
			}
		}
		else if (type == 4) {
			if (cosmicCount == 0) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(24, StandardLevelLayout.TILE_HEIGHT * 49, 0 + i*(Math.PI/10), 1));
				}
				cosmicCount++;
			}
			else if (cosmicCount == 1) {
				for(int i = 0; i < 20; i++) {
					universe.getSprites().add(new BloodProjectile(24, StandardLevelLayout.TILE_HEIGHT * 49, Math.PI/20 + i*(Math.PI/10), 1));
				}
				cosmicCount--;
			}
		}
		else if (type == 5) {
			if (cosmicCount == 0) {
				for(int i = 0; i < 30; i++) {
					universe.getSprites().add(new BloodProjectile(StandardLevelLayout.TILE_WIDTH * 30, 25, 0 + i*(Math.PI/15), 1));
				}
				cosmicCount++;
			}
			else if (cosmicCount == 1) {
				for(int i = 0; i < 30; i++) {
					universe.getSprites().add(new BloodProjectile(StandardLevelLayout.TILE_WIDTH * 30, 25, Math.PI/30 + i*(Math.PI/15), 1));
				}
				cosmicCount--;
			}
		}
	}
	
	private void cosmicWall(Universe universe, int type) {
		if (type == 1) {
			universe.getSprites().add(new BloodProjectile(24 + (12*cosmicCount), 25, -3*(Math.PI/2), 1));
			cosmicCount++;
		}
		else if (type == 2) {
			universe.getSprites().add(new BloodProjectile((StandardLevelLayout.TILE_WIDTH * 61) - (cosmicCount*12), 25, -3*(Math.PI/2), 1));
			cosmicCount++;
		}
		else if (type == 3) {
			if (!((cosmicCount > 15)&&(cosmicCount < 19))) {
				universe.getSprites().add(new BloodProjectile((StandardLevelLayout.TILE_WIDTH * 61) - (cosmicCount*12), 25, -3*(Math.PI/2), 1));
			}
			cosmicCount++;
		}
		else if (type == 4) {
			if (!((cosmicCount > 30)&&(cosmicCount < 34))) {
				universe.getSprites().add(new BloodProjectile(24 + (12*cosmicCount), 25, -3*(Math.PI/2), 1));
			}
			cosmicCount++;
		}
		else if (type == 5) {
			if (!((cosmicCount > 11)&&(cosmicCount < 15))) {
				universe.getSprites().add(new BloodProjectile((StandardLevelLayout.TILE_WIDTH * 61) - (cosmicCount*12), 25, -3*(Math.PI/2), 1));
			}
			cosmicCount++;
		}
		else if (type == 6) {
			if (!((cosmicCount > 22)&&(cosmicCount < 26))) {
				universe.getSprites().add(new BloodProjectile(24 + (12*cosmicCount), 25, -3*(Math.PI/2), 1));
			}
			cosmicCount++;
		}
	}
	private boolean checkForWasp(Universe universe) {

		boolean copyFound = false;

		for (DisplayableSprite sprite : universe.getSprites()) {
			if (sprite instanceof WaspEnemy) {
				copyFound = true;
				break;
			}
		}
		return copyFound;
	}
}
