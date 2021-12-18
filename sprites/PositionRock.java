
import java.awt.Image;

import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class PositionRock implements EnemyProjectile{
	
	private final double RESULTANT = 40;
	private final double RESULTANT_VELOCITY = 65;
	private final double INCREASE_VELOCITY = 5;
	private final double MAXIMUM_VELOCITY = 150;
	
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
	private int position;
	private int angleOffset;

	public PositionRock(int angleOffset, int position) {
		this.position = position;
		this.angleOffset = angleOffset;
		
		health = 4;
		
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
		if (health == 4) {
			return orbitRocks[0]; 
		}
		else if(health == 3) {
			return orbitRocks[1]; 
		}
		else if(health == 2) {
			return orbitRocks[2]; 
		}
		else if(health == 1) {
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

		else if (health >= 3) {
			double locationAroundBossX = RESULTANT * Math.cos((position*(Math.PI/4)) - (Math.PI/angleOffset));
			double locationAroundBossY = RESULTANT * Math.sin((position*(Math.PI/4)) - (Math.PI/angleOffset));
			centerX = thisUniverse.getBoss().getCenterX() + locationAroundBossX;
			centerY = thisUniverse.getBoss().getCenterY() + locationAroundBossY;
		}
		else if (health >= 1) {
			
			velocityX = velocityX + Math.cos(playerAngle(universe))*(INCREASE_VELOCITY) + Math.cos(playerAngle(universe))*(RESULTANT/distanceBetweenPlayer(universe));
			velocityY = velocityY + Math.sin(playerAngle(universe))*(INCREASE_VELOCITY) + Math.sin(playerAngle(universe))*(RESULTANT/distanceBetweenPlayer(universe));
			if (velocityX > MAXIMUM_VELOCITY) {
				velocityX = MAXIMUM_VELOCITY;
			}
			if (velocityY > MAXIMUM_VELOCITY) {
				velocityY = MAXIMUM_VELOCITY;
			}
			
			double movement_x = (this.velocityX * actual_delta_time * 0.001);
			double movement_y = (this.velocityY * actual_delta_time * 0.001);
			    
			this.centerX += movement_x;
			this.centerY += movement_y;
		}
		checkOverlap(thisUniverse, "ArrowSprite");
	}
	
	public void setDispose() {
		this.dispose = true;
	}

	public int getCollisionDamage() {
		return 1;
	}
	
	private boolean checkOverlap(Universe sprites, String targetSprite) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite.getClass().toString().contains(targetSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					if (targetSprite.equals("ArrowSprite")) {
						health = health - ((Projectile) sprite).getDamageGiven();
						((Projectile) sprite).setDispose();
						if (health == 2) {
							velocityX = Math.cos(playerAngle(sprites))*(RESULTANT_VELOCITY);
							velocityY = Math.sin(playerAngle(sprites))*(RESULTANT_VELOCITY);
						}
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
}
