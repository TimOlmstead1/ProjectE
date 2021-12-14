import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class TitleBackground implements Background{
	
	private int backgroundWidth = 0;
 	private int backgroundHeight = 0;

    private Image image = null;
	
    public TitleBackground() {
    	try {
    		this.image = ImageIO.read(new File("res/MapElements/titleScreen.png"));
    	}
    	catch (IOException e) {
    		System.out.println(e.toString());
    	}
    	backgroundWidth = image.getWidth(null);
		backgroundHeight = image.getHeight(null);
    }
    
	public Tile getTile(int col, int row) {
		int x = (col * backgroundWidth);
		int y = (row * backgroundHeight);
		Tile newTile = null;
		
		newTile = new Tile(image, x, y, backgroundWidth, backgroundHeight, false);

		return newTile;
	}

	
	public int getCol(int x) {
		int col = 0;
		
		if (backgroundWidth != 0) {
			col = (x / backgroundWidth);
			if (x < 0) {
				return col - 1;
			}
			else {
				return col;
			}
		}
		else {
			return 0;
		}
	}
	

	
	public int getRow(int y) {
		int row = 0;
		
		if (backgroundHeight != 0) {
			row = (y / backgroundHeight);
			if (y < 0) {
				return row - 1;
			}
			else {
				return row;
			}
		}
		else {
			return 0;
		}
	}

	public ArrayList<DisplayableSprite> getOneWayBarriers() {
		return null;
	}

	public ArrayList<DisplayableSprite> getBarriers() {
		return null;
	}

	public ArrayList<DisplayableSprite> getMappedSprites() {
		return null;
	}

}
