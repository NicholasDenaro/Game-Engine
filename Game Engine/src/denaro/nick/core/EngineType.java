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
	
	public void stopRunning()
	{
		running=false;
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
	
	protected void running(boolean running)
	{
		this.running=running;
	}
	
	private boolean running;
	private GameEngine engine;
}
