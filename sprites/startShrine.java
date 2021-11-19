import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class StartShrine implements DisplayableSprite {

	private static Image normalImage;
	private static Image hitImage;
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	private double width = 32;
	private double height = 32;
	private boolean dispose = false;
	
	private boolean hasBeenHit = false;
	private double waitCounter = 50;
	
	public StartShrine(double minX, double minY, double maxX, double maxY, boolean visible) {
		
		if (normalImage == null) {
			try {
				normalImage = ImageIO.read(new File("res/MapElements/shrine(32x32).png"));
				hitImage = ImageIO.read(new File("res/MapElements/shrineRed(32x32).png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}		
		}	
		this.centerX = (minX + maxX) / 2;
		this.centerY = (minY + (minY - (height/2))) / 2;
		this.visible = visible;
	}
	

	public Image getImage() {
		if (hasBeenHit) {
			return hitImage;
		}
		else {
			return normalImage;
		}
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	//DISPLAYABLE
	
	public boolean getVisible() {
		return this.visible;
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
	};

	public double getCenterY() {
		return centerY;
	};
	
	
	public boolean getDispose() {
		return dispose;
	}

	public void update(Universe universe, KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {
		checkCollision(universe);
		
		if (hasBeenHit) {
			waitCounter--;
			if (waitCounter <= 0) {
				((FightingUniverse) universe).startFight();
				dispose = true;
			}
		}		
	}
	
	private void checkCollision(Universe universe) {
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if (sprite instanceof ArrowSprite) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)){
					
					((ArrowSprite) sprite).setDispose();
					hasBeenHit = true;
					break;
				}			
			}
		}		
	}

}
