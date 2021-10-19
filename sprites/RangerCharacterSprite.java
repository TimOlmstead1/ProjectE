import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RangerCharacterSprite implements DisplayableSprite {

	private static BufferedImage[] rangerSprites = null;
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	
	final private int width = 32;
	final private int height = 32;
	final private int cols = 5;
	final private int rows = 10;
	
	private boolean dispose = false;
	
	private CollisionDetection collisionDetection;
	private TwoDimensionBounce bounce;
	
	public RangerCharacterSprite(int centerX, int centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		
		collisionDetection = new CollisionDetection();
		collisionDetection.setBounceFactorX(0.5);
		collisionDetection.setBounceFactorY(0);
		bounce = new TwoDimensionBounce();
		
		Image image = null;
		
		try {
			image = ImageIO.read(new File("res/NobleRanger(Edit).png"));
		}
		catch (IOException e) {
			System.out.print(e.toString());
		}
		//https://stackoverflow.com/questions/10604824/reading-images-from-a-sprite-sheet-java
		
		rangerSprites = new BufferedImage[rows * cols]; 

		for (int i = 0; i < rows; i++){ 
			
		    for (int j = 0; j < cols; j++){ 
		    	
		    	rangerSprites[(i * cols) + j] = ((BufferedImage) image).getSubimage( 
		            i * width, 
		            j * height, 
		            width, 
		            height 
		        ); 
		    } 
		} 
	}

	public Image getImage() {
		return rangerSprites[49];
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

	}

}
