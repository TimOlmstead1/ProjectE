import java.util.ArrayList;

public interface Universe {

	//STATIC VARIABLES
	
	//INSTANCE VARIABLES
	
	

	public double getScale();

	public ArrayList<DisplayableSprite> getBarriers();
	public ArrayList<DisplayableSprite> getOneWayBarriers();

	public double getXCenter();
	public double getYCenter();
	
	public void setXCenter(double xCenter);
	public void setYCenter(double yCenter);
	
	public boolean isComplete();
	public void setComplete(boolean complete);
	
	public Background getBackground();
	public Background getForeground();
	
	public DisplayableSprite getPlayer1();
	public boolean centerOnPlayer();

	public ArrayList<DisplayableSprite> getSprites();	

	public void update(KeyboardInput keyboard, MouseInput mouse, long actual_delta_time);
    
	
}
