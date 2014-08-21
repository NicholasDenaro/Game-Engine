package denaro.nick.core.timer;

public abstract class Timer
{
	/**
	 * Creates a new instance of this timer and calls init
	 * @param time - the amount of time this should last for
	 * @param active - true if it should perform the action at every tick
	 */
	public Timer(int time, boolean active)
	{
		this.time=time;
		this.active=active;
		init();
	}
	
	/**
	 * Is called when the timer is first created
	 */
	public void init()
	{
		
	}
	
	/**
	 * The action to be performed at the end of the timer, or if active at every tick
	 */
	public abstract void action();
	
	/**
	 * The accessor method for active
	 * @return - true if the Timer is an active timer
	 */
	public boolean active()
	{
		return(active);
	}
	
	private boolean active;
	protected int time;
}
