
public interface FightingUniverse extends Universe{
	
	public void startFight();
	
	public boolean getIsFightStarted();
	
	public void setIsFightStarted(boolean isStarted);
	
	public EnemySprite getBoss();
	
	public boolean getIsPlayer1Dead();

	public void setIsFightOver(boolean isOver);

	public boolean getIsFightOver();
}
