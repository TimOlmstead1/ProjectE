import java.awt.Image;

public class Projectile implements DisplayableSprite {
	
	private double velocityX;
	private static Image image;
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	private double width;
	private double height;
	private boolean dispose = false;
	
	public Projectile(double minX, double minY, double maxX, double maxY, boolean visible, double angle, String typeOfProjectile) {
		//TODO
	}

	public Image getImage() {
		return image;
	}

	public boolean getVisible() {
		return visible;
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

	@Override
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

	
	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		//TODO
	}

}
