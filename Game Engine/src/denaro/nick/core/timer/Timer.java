package denaro.nick.core.timer;

public abstract class Timer extends Thread
{
	/**
	 * Creates a new instance of this timer and calls init
	 * @param time - the amount of time (in milliseconds) this should wait before performing the action
	 * @param active - true if it should perform the action at every tick
	 */
	public Timer(int time)
	{
		this.time=time;
		finished=false;
		init();
	}
	
	/**
	 * Is called when the timer is first created
	 */
	public void init()
	{
		
	}
	
	/**
	 * Check if the Timer is finished
	 * @return true if the timer is finished
	 */
	public boolean isFinished()
	{
		return(finished);
	}
	
	/**
	 * Sets the finished field to true
	 */
	protected void finish()
	{
		finished=true;
	}
	
	@Override
	public void start()
	{
		startTime=System.currentTimeMillis();
		super.start();
	}
	
	@Override
	public void run()
	{
		while(System.currentTimeMillis()-startTime<time)
		{
			try
			{
				sleep((time-(System.currentTimeMillis()-startTime))*4/5);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		action();
		finished=true;
	}
	
	/**
	 * The action to be performed at the end of the timer
	 */
	public abstract void action();
	
	protected int time;
	private boolean finished;
	private long startTime;
}
