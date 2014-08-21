package denaro.nick.core.timer;


public abstract class TickingTimer extends Timer
{

	public TickingTimer(int time, boolean active)
	{
		super(time,active);
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
			return(true);
		}
		else if(active())
			action();
		return(false);
	}
}
