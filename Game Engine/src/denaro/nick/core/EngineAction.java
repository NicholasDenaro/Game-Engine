package denaro.nick.core;

public abstract class EngineAction
{
	public EngineAction()
	{
		init();
	}
	
	/**
	 * Called when the object is created
	 */
	public abstract void init();
	
	/**
	 * The code that is to be called later
	 */
	public abstract void callFunction();
	
	/**
	 * Test case whether to dispose of the action yet
	 * @return - true if to be disposed
	 */
	public abstract boolean shouldEnd();
}
