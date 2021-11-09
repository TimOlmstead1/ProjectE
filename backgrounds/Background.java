import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public interface Background {

	public Tile getTile(int col, int row);
	
	public int getCol(int x);
	
	public int getRow(int y);

	public ArrayList<DisplayableSprite> getOneWayBarriers();

	public ArrayList<DisplayableSprite> getBarriers();

	public ArrayList<DisplayableSprite> getMappedSprites();
	
}
