
import java.awt.Image;

import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class OrbitRock implements EnemyProjectile{
	
	private double orbitVelocity = 180;
	
	private final double RESULTANT_VELOCITY = 110;
	
	private double velocityY = 0;
	private double velocityX = 0;

	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	private double width = 24;
	private double height = 24;
	
	private boolean dispose = false;

	private static Image[] orbitRocks;
	
	private CollisionDetection collisionDetection;
	
	private int health;
	private boolean beenHit = false;

	public OrbitRock(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = 3;
		
		collisionDetection = new CollisionDetection();
		
		if (orbitRocks == null) {
			try {
				orbitRocks = new Image[4];
				for (int i = 1; i <= orbitRocks.length; i++) {
					String path = String.format("res/rock/rock%d.png", i);
					orbitRocks[i-1] = ImageIO.read(new File(path));
				}
			}
			catch (IOException e) {
				System.out.print(e.toString());
			} 
		}
	}

	public Image getImage() {
		if (health == 3) {
			return orbitRocks[0]; 
		}
		else if(health == 2) {
			return orbitRocks[1]; 
		}
		else if(health == 1) {
			return orbitRocks[2]; 
		}
		else if(health == 0) {
			return orbitRocks[3]; 
		}
		return orbitRocks[3]; 
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
	
	public void setOrbitVelocity(double newVelocity) {
		orbitVelocity = newVelocity;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public void update(Universe universe, KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {
		
		if (((FightingUniverse) universe).getIsFightStarted() == false) {
			this.dispose = true;
		}
		FightingUniverse thisUniverse = (FightingUniverse) universe;
		
		if (health <= 0) {
			this.dispose = true;
		}

		else if (health >= 2) {
			double travelAngle = (bossAngle(thisUniverse));
			
			velocityX = 0;
			velocityY = 0;
			
			double targetX = thisUniverse.getBoss().getCenterX();
			double targetY = thisUniverse.getBoss().getCenterY();	
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
			velocityX = Math.sin(angleRadians)*(orbitVelocity);
			velocityY = Math.cos(angleRadians)*(orbitVelocity);
		
			if (distanceBetweenBoss(thisUniverse) >= 50) {
				velocityX = velocityX + Math.cos(travelAngle)*(orbitVelocity);
				velocityY = velocityY + Math.sin(travelAngle)*(orbitVelocity);
			}
			
			else if (distanceBetweenBoss(thisUniverse) <= 20){
				velocityX = velocityX + Math.cos(travelAngle)*(orbitVelocity)*(-1);
				velocityY = velocityY + Math.sin(travelAngle)*(orbitVelocity)*(-1);
			}
			double movement_x = (this.velocityX * actual_delta_time * 0.001);
			double movement_y = (this.velocityY * actual_delta_time * 0.001);
			    
			this.centerX += movement_x;
			this.centerY += movement_y;
			
			checkOverlap(universe, "ArrowSprite");
			if (checkOverlap(universe, "OrbitRock")) {
				orbitVelocity = 250;
			}
			else {
				orbitVelocity = 180;
			}
		}
		else {
			double movement_x = (this.velocityX * actual_delta_time * 0.001);
			double movement_y = (this.velocityY * actual_delta_time * 0.001);
			    
			this.centerX += movement_x;
			this.centerY += movement_y;
			
			checkOverlap(universe, "ArrowSprite");
			if (checkOverlap(universe, "OrbitRock")) {
				orbitVelocity = 250;
			}
			else {
				orbitVelocity = 180;
			}
		}
	}
	
	public void setDispose() {
		this.dispose = true;
	}

	public int getCollisionDamage() {
		return 1;
	}
	
	private double bossAngle(FightingUniverse universe) { //finds the angle between the player and the boss
		
		double targetX = universe.getBoss().getCenterX();
		double targetY = universe.getBoss().getCenterY();	
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
	
	private double distanceBetweenBoss(FightingUniverse universe) {
		double diagonalDistance;
		double targetX = universe.getBoss().getCenterX();
		double targetY = universe.getBoss().getCenterY();
		diagonalDistance = Math.sqrt(Math.pow((targetX - centerX),2) + Math.pow((targetY - centerY),2));
		return diagonalDistance;
	}
	
	private boolean checkOverlap(Universe sprites, String targetSprite) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite.getClass().toString().contains(targetSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					if (targetSprite.equals("ArrowSprite")) {
						((Projectile) sprite).setDispose();
						health--;
					}
					if (targetSprite.equals("OrbitRock")) {
						((OrbitRock) sprite).setOrbitVelocity(100);
					}
					overlap = true;
					break;					
				}
			}
		}		
		return overlap;		
	}

	public int getDamageGiven() {
		return 1;
	}
}
