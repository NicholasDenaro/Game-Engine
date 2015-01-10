package denaro.nick.core.timer;


public abstract class TickingTimer extends Timer
{

	public TickingTimer(int time, boolean active)
	{
		super(time);
		this.active=active;
	}
	
	/**
	 * Ticks the timer
	 * @return - true if the timer is finished
	 */
	public boolean tick()
	{
		if(time--==0)
		{
			action();
			finish();
			return(true);
		}
		else if(active())
			action();
		return(false);
	}
	
	/**
	 * The accessor method for active
	 * @return - true if the Timer is an active timer
	 */
	public boolean active()
	{
		return(active);
	}
	
	/**
	 * The action to be performed at the end of the timer, or if active at every tick
	 */
	public abstract void action();
	
	/** If true, the action is performed every tick.*/
	private boolean active;
}
