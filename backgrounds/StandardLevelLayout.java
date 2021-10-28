import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class StandardLevelLayout implements Background{
	
	protected static int TILE_WIDTH = 8;
    protected static int TILE_HEIGHT = 8;
    
    private Image wall;
    private Image woodenPlatform;
    
    private int maxCols = 0;
    private int maxRows = 0;    

    private int map[][] = new int[][] { 
		{4,4,4,4},
		{4,4,4,4},
		};
    
	public StandardLevelLayout() {
		try {
			this.wall = ImageIO.read(new File("res/MapElements/DarkLevelWall(16x16).png"));
			this.woodenPlatform = ImageIO.read(new File("res/MapElements/DarkLevelWall(16x16).png"));
		}	
		catch(IOException e) {
    		System.out.println(e.toString());
		}
		
		map = CSVReader.importFromCSV("res/CSVMaps/BaseLevel.csv");
		
    	
    	maxRows = map.length - 1;
    	maxCols = map[0].length - 1;
	}

	@Override
	public Tile getTile(int col, int row) {
		Image image = null;
		
		if (row < 0 || row > maxRows || col < 0 || col > maxCols) {
			image = null;
		}
		else if ((map[row][col] == 1)||(map[row][col] == 2)) {
			image = wall;
		}
		else if (map[row][col] == 3) {
			image = woodenPlatform;
		}
		
		int x = (col * TILE_WIDTH);
		int y = (row * TILE_HEIGHT);
		
		Tile newTile = new Tile(image, x, y, TILE_WIDTH, TILE_HEIGHT, false);
		
		return newTile;
	}

	@Override
	public int getCol(int x) {
		int col = 0;
		if (TILE_WIDTH != 0) {
			col = (x / TILE_WIDTH);
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

	@Override
	public int getRow(int y) {
		int row = 0;
		
		if (TILE_HEIGHT != 0) {
			row = (y / TILE_HEIGHT);
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
		ArrayList<DisplayableSprite> oneWayBarriers = new ArrayList<DisplayableSprite>();
		for (int row = 0; row < map[0].length; row++) {
			for (int col = 0; col < map.length; col++) {
				if (map[col][row] == 3) {
					oneWayBarriers.add(new BarrierOneWay(row * TILE_WIDTH, col * TILE_HEIGHT, (row + 1) * TILE_WIDTH, (col + 1) * TILE_HEIGHT, true));
				}
			}
		}
		return oneWayBarriers;
	}

	public ArrayList<DisplayableSprite> getBarriers() {
		ArrayList<DisplayableSprite> barriers = new ArrayList<DisplayableSprite>();
		for (int row = 0; row < map[0].length; row++) {
			for (int col = 0; col < map.length; col++) {
				if ((map[col][row] == 1)) {
					barriers.add(new BarrierSprite(row * TILE_WIDTH, col * TILE_HEIGHT, (row + 1) * TILE_WIDTH, (col + 1) * TILE_HEIGHT, true));
				}
			}
		}
		return barriers;
	}
}
