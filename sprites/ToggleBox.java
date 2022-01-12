import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ToggleBox implements DisplayableSprite {

	private static Image activeImage;
	private static Image unactiveImage;
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	private double width = 16;
	private double height = 16;
	private boolean dispose = false;
	
	private boolean active = false;
	
	public ToggleBox(double minX, double minY, double maxX, double maxY, boolean visible) {
		
		if (activeImage == null) {
			try {
				activeImage = ImageIO.read(new File("res/box/greenToggle.png"));
				unactiveImage = ImageIO.read(new File("res/box/redToggle.png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}		
		}
		
		this.centerX = (minX + maxX) / 2;
		this.centerY = (minY + maxY) / 2;
		this.width = maxX - minX;
		this.height = maxY - minY;
		this.visible = visible;
		
	}
	

	public Image getImage() {
		if (active) {
			return activeImage;
		}
		else {
			return unactiveImage;
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

	public boolean getActivity() {
		return active;
	}
	public void resetActivity() {
		active = false;
	}
	
	public void update(Universe universe, KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {
			checkOverlap(universe);
			
	}

	
	private boolean checkOverlap(Universe sprites) {

		boolean overlap = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if ((sprite instanceof ArrowSprite)) {
				if (CollisionDetection.overlaps(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY(), sprite.getMinX(),sprite.getMinY(), sprite.getMaxX(), sprite.getMaxY())) {
					overlap = true;
					((ArrowSprite) sprite).setDispose();
					active = true;
					break;					
				}
			}
		}		
		return overlap;		
	}
}
