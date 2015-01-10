package denaro.nick.core;

import java.util.ArrayList;

public abstract class EngineType extends Thread
{
	public abstract ArrayList<String> information();
	
	@Override
	public void start()
	{
		running=true;
		super.start();
	}
	
	public void setEngine(GameEngine engine)
	{
		this.engine=engine;
	}
	
	protected GameEngine engine()
	{
		return(engine);
	}
	
	protected boolean running()
	{
		return(running);
	}
	
	protected void kill()
	{
		kill=true;
	}
	
	public boolean isKilled()
	{
		return(kill);
	}
	
	protected void running(boolean running)
	{
		this.running=running;
	}
	
	private boolean kill;
	private boolean running;
	private GameEngine engine;
}
