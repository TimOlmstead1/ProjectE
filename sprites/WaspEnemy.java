import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WaspEnemy implements EnemySprite, MovableSprite{
	
	private static final int WIDTH = 37;
	private static final int HEIGHT = 34;
	
	
	private double resultantVelocity = 86;
	
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private boolean dispose = false;
	
	private double velocityX;
	private double velocityY;
	
	private static Image[] imageList = null;
	private double animationCount = 0;
	private boolean playerIsToTheRight = false;
	
	private double timeLastShot = Math.random()* 35;
	private double timeAlive = 0;
		
	private boolean avoid = false;
	private boolean clockwiseOrbit = false;
	
	private int damage = 1;
	private int health = 1;
	
	public WaspEnemy(double centerX, double centerY) {
		
		double random = (double) (Math.random() * (100) + 60);
		resultantVelocity = random;
				
		double random2 = (Math.random() * 2);
		if (random2 >= 1) {
			clockwiseOrbit = true;
		}
		
		this.centerX = centerX;
		this.centerY = centerY;
		
		this.height = HEIGHT;
		this.width = WIDTH;
	
		if (imageList == null) {
			try {
				imageList = new Image[4];
				for (int i = 1; i <= imageList.length; i++) {
					String path = String.format("res/Wasp/Wasp%d.png", i);
					imageList[i-1] = ImageIO.read(new File(path));
				}
			}
			catch (IOException e) {
				System.out.print(e.toString());
			} 
		}
	}

	public Image getImage() {
		if (playerIsToTheRight) {
			if (animationCount <= 20) {
				animationCount++;
				return imageList[0];
			}
			else if (animationCount <= 60) {
				animationCount++;
				return imageList[2];
			}
			else if (animationCount <= 100) {
				animationCount = 0;
				return imageList[0];
			}
		}
		else {
			if (animationCount <= 20) {
				animationCount++;
				return imageList[1];
			}
			else if (animationCount <= 60) {
				animationCount++;
				return imageList[3];
			}
			else if (animationCount <= 100) {
				animationCount = 0;
				return imageList[1];
			}
		}
		return imageList [1];
	}

	public boolean getVisible() {
		return true;
	}

	public double getMinX() {
		return centerX - (width / 2);
	}

	public double getMaxX() {
		return centerX + (width / 2);
	}

	public double getMinY() {
		return centerY - (height / 2);
	}

	public double getMaxY() {
		return centerY + (height / 2);
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
	
	public int getDamageGiven() {
		return damage;
	}

	public void setDispose() {
		dispose = true;
	}

	public void update(Universe universe, KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {
		if ((health <= 0)||((FightingUniverse) universe).getIsFightStarted() == false) {
			this.dispose = true;
		}
		
		timeAlive = timeAlive + (actual_delta_time*0.01);
		
		if (universe.getPlayer1().getCenterX() > centerX) {
			playerIsToTheRight = true;
		}
		else {
			playerIsToTheRight = false;
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
			if (clockwiseOrbit) {
				velocityX = Math.sin(angleRadians)*(-resultantVelocity);
				velocityY = Math.cos(angleRadians)*(-resultantVelocity);
			}
			else {
				velocityX = Math.sin(angleRadians)*(resultantVelocity);
				velocityY = Math.cos(angleRadians)*(resultantVelocity);
			}
			
		}
		else if (distanceBetweenPlayer(universe) >= 170) {
			velocityX = Math.cos(travelAngle)*(resultantVelocity);
			velocityY = Math.sin(travelAngle)*(resultantVelocity);
		}
		
		else {
			velocityX = Math.cos(travelAngle)*(resultantVelocity)*(-1);
			velocityY = Math.sin(travelAngle)*(resultantVelocity)*(-1);
		}
		
		double movement_x = (this.velocityX * actual_delta_time * 0.001);
		double movement_y = (this.velocityY * actual_delta_time * 0.001);
		    
		this.centerX += movement_x;
		this.centerY += movement_y;
		
		checkCollision(universe);
		
		if (timeAlive >= timeLastShot + 35) {
			timeLastShot = timeAlive;
			universe.getSprites().add(new BloodProjectile(centerX, centerY, playerAngle(universe), 0));
		}
	}

	private void checkCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if ((sprite instanceof ArrowSprite)||(sprite instanceof ToggleBullet)) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
					
					if (sprite instanceof ArrowSprite) {
						((ArrowSprite) sprite).setDispose();
					}
					if (sprite instanceof ToggleBullet) {
						health = 0;
					}
					break;		
				}
			}
		}
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;		
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

	public int getCollisionDamage() {
		return 0;
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
