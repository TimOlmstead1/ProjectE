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
	private double timeHit = 0;
	private boolean shielded = false;
	private double timeAlive = 0;
	private boolean regularBatAdded = false;
	
	private boolean isSummoning = false;
	private double timeSummoning = 0;
	private boolean summoned = false;

	public VampireBoss(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		health = 5;
		
		collisionDetection = new CollisionDetection();
		bounce = new TwoDimensionBounce();
		collisionDetection.setBounceFactorX(0);
		collisionDetection.setBounceFactorY(0);
	
		try {
			vampireSprites = new Image[5];
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
		if (shielded) {
			return vampireSprites[3];
		}
		else if (isSummoning) {
			return vampireSprites[4];
		}
		return vampireSprites[0];
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
		
		if (health == 0) {
			this.dispose = true;
			((FightingUniverse) universe).setIsFightStarted(false);
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
			universe.getSprites().add(new FollowerBatEnemy(centerX, centerY));
		}
		else if ((timeAlive%50 > 1)&&(timeAlive%50 < 2)){
			regularBatAdded = false;
		}
		
		if ((!(timeHit == 0))&&(timeAlive >= timeHit+100)){
			shielded = false;
			if (health < 5) {
				isSummoning = true;
				timeSummoning = timeAlive;
			}
			timeHit = 0;
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
		}
		
		
		if (beenHit && (health > 0)) {
			move(universe);
			universe.getSprites().add(new FollowerBatEnemy(StandardLevelLayout.TILE_WIDTH * 2, StandardLevelLayout.TILE_HEIGHT * 1));
			universe.getSprites().add(new FollowerBatEnemy(StandardLevelLayout.TILE_WIDTH * 2, StandardLevelLayout.TILE_HEIGHT * 48));
			universe.getSprites().add(new FollowerBatEnemy(StandardLevelLayout.TILE_WIDTH * 62, StandardLevelLayout.TILE_HEIGHT * 48));
			universe.getSprites().add(new FollowerBatEnemy(StandardLevelLayout.TILE_WIDTH * 62, StandardLevelLayout.TILE_HEIGHT * 1));
			beenHit = false;
		}
	}
	
	private void checkCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if (sprite instanceof ArrowSprite) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
					
					((ArrowSprite) sprite).setDispose();
					if (!(shielded)) {
						health = health - ((Projectile) sprite).getDamageGiven();
						beenHit = true;
						shielded = true;
						timeHit = timeAlive;
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
	public void move(Universe universe) {
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
	}
}
