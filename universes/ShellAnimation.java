
public class ShellAnimation implements Animation {
	
	private final int TOTAL_UNIVERSES = 4;

	private static int universeCount = 0;
	
	public int getUniverseCount() {
		return universeCount;
	}

	public void setUniverseCount(int count) {
		ShellAnimation.universeCount = count;
	}

	public Universe getNextUniverse() {

		universeCount++;
		
		if (universeCount > TOTAL_UNIVERSES) {
			universeCount = 1;
		}
		
		return getUniverse(universeCount);
	}

	public Universe getUniverse(int universeNumber) {
		if(universeCount == 1) {
			return new StartUniverse();
		}
		
		else if (universeCount == 2) {
			return new ControlsUniverse();
		}
		
		else if (universeCount == 3) {
			return new FirstUniverse();
		}
		else if (universeCount == 4) {
			return new SecondUniverse();
		}
		else {
			return null;
		}
	}
}
