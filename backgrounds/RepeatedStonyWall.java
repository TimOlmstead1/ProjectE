import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class RepeatedStonyWall implements Background{
	
	private int backgroundWidth = 0;
 	private int backgroundHeight = 0;

    private Image wall =null;
	
    public RepeatedStonyWall(int wallType) { //1 is Stony, 2 is Viney
    	Image stonyWall = null;
    	Image vineyWall = null;
    	try {
    		stonyWall = ImageIO.read(new File("res/MapElements/Stony Wall(64x64).png"));
    		vineyWall = ImageIO.read(new File("res/MapElements/Vine Wall(64x64).png"));
    	}
    	catch (IOException e) {
    		System.out.println(e.toString());
    	}
    	if (wallType == 1) {
    		wall = stonyWall;
    	}
    	else {
    		wall = vineyWall;
    	}
    	backgroundWidth = wall.getWidth(null);
		backgroundHeight = wall.getHeight(null);
    }
    
	public Tile getTile(int col, int row) {
		int x = (col * backgroundWidth);
		int y = (row * backgroundHeight);
		Tile newTile = null;
		
		newTile = new Tile(wall, x, y, backgroundWidth, backgroundHeight, false);

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
