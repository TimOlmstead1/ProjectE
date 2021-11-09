import java.awt.MouseInfo;
import java.util.ArrayList;

public class FirstUniverse implements FightingUniverse {

	private boolean complete = false;	
	private Background background = null;	
	private Background foreground = null;	
	private DisplayableSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = new ArrayList<DisplayableSprite>();
	private ArrayList<DisplayableSprite> barriers = new ArrayList<DisplayableSprite>();
	private ArrayList<DisplayableSprite> oneWayBarriers = new ArrayList<DisplayableSprite>();
	private ArrayList<DisplayableSprite> disposalList = new ArrayList<DisplayableSprite>();
	
	private double XCenter = 0;
	private double YCenter = 0;
	
	private double universeScale = 1.7;
	
	private boolean fightStarted = false;
	
	public FirstUniverse () {

		this.setXCenter(256);
		this.setYCenter(208);
		
		background = new RepeatedStonyWall();
		foreground = new StandardLevelLayout();
		
		ArrayList<DisplayableSprite>  mappedSprites = foreground.getMappedSprites();
		
		oneWayBarriers = foreground.getOneWayBarriers();
		barriers = foreground.getBarriers();
		
		foreground.getOneWayBarriers();
		foreground.getBarriers();
		foreground.getMappedSprites();
		
		player1 = new RangerCharacterSprite(StandardLevelLayout.TILE_HEIGHT * 16, StandardLevelLayout.TILE_WIDTH * 12);
		sprites.addAll(barriers);
		sprites.addAll(oneWayBarriers);
		oneWayBarriers.addAll(barriers);
		sprites.addAll(mappedSprites);
		sprites.add(player1);
	}

	public double getScale() {
		return universeScale;
	}

	public double getXCenter() {
		return XCenter;
	}

	public double getYCenter() {
		return YCenter;
	}

	public void setXCenter(double newXCenter) {
		XCenter = newXCenter;
	}

	public void setYCenter(double newYCenter) {
		YCenter = newYCenter;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		complete = true;
	}

	public Background getBackground() {
		return background;
	}
	
	public Background getForeground() {
		return foreground;
	}

	public DisplayableSprite getPlayer1() {
		return player1;
	}

	public ArrayList<DisplayableSprite> getSprites() {
		return sprites;
	}

	public boolean centerOnPlayer() {
		return false;
	}		
	
	public ArrayList<DisplayableSprite> getBarriers() {
		return barriers;
	}

	public ArrayList<DisplayableSprite> getOneWayBarriers() {
		return oneWayBarriers;
	}	

	public void update(KeyboardInput keyboard, MouseInput mouse, long actual_delta_time) {

		if (keyboard.keyDownOnce(27)) {
			complete = true;
		}
		
		for (int i = 0; i < sprites.size(); i++) {
			DisplayableSprite sprite = sprites.get(i);
			sprite.update(this, keyboard, mouse, actual_delta_time);
    	} 
		
		disposeSprites();
		
	}

	protected void disposeSprites() {
        
    	//collect a list of sprites to dispose
    	//this is done in a temporary list to avoid a concurrent modification exception
		for (int i = 0; i < sprites.size(); i++) {
			DisplayableSprite sprite = sprites.get(i);
    		if (sprite.getDispose() == true) {
    			disposalList.add(sprite);
    		}
    	}
		
		//go through the list of sprites to dispose
		//note that the sprites are being removed from the original list
		for (int i = 0; i < disposalList.size(); i++) {
			DisplayableSprite sprite = disposalList.get(i);
			sprites.remove(sprite);
			System.out.println("Remove: " + sprite.toString());
    	}
		
		//clear disposal list if necessary
    	if (disposalList.size() > 0) {
    		disposalList.clear();
    	}
    }

	public String toString() {
		return "";
	}

	public void startFight() {
		fightStarted = true;
	}
}
