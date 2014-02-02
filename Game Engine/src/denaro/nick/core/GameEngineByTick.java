package denaro.nick.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GameEngineByTick extends GameEngine
{
	/**
	 * Creates a new default GameEngine with 0 ticksPerSecond, 0 framesPerSecond and no view
	 */
	private GameEngineByTick()
	{
		engine=this;
		ticksPerSecond=0;
		framesPerSecond=0;
		currentView=null;
		
		running=false;
		entityAddQueue=new ArrayList<Pair<Entity,Location>>();

		entityRemoveQueue=new ArrayList<Pair<Entity,Location>>();
		
		keyEventQueue=new ArrayList<Pair<Focusable, KeyEvent>>();
	}
	
	/**
	 * Adds an entity to the specified location
	 * @param entity - the entity to add
	 * @param location - the location at which to add the entity
	 */
	@Override
	public void removeEntity(Entity entity,Location location)
	{
		entityRemoveQueue.add(new Pair(entity,location));
	}
	
	/**
	 * Adds an entity to the specified location
	 * @param entity - the entity to add
	 * @param location - the location at which to add the entity
	 */
	@Override
	public void addEntity(Entity entity,Location location)
	{
		entityAddQueue.add(new Pair(entity,location));
	}
	
	/**
	 * Changes the framesPerSecond to frames
	 * @param frames - the current rate to set the framesPerSecond to.
	 */
	public void setFramesPerSecond(int frames)
	{
		framesPerSecond=frames;
	}
	
	/**
	 * Changes the ticksPerSecond to ticks
	 * @param ticks - the current rate to set the ticksPerSecond to.
	 */
	public void setTicksPerSecond(int ticks)
	{
		ticksPerSecond=ticks;
	}
	
	/** 
	 * The loop the game runs in
	 */
	@Override
	public void run()
	{
		long time=0;
		long timeTick=0;
		long timeFrame=0;
		
		running=true;
		try
		{
			while(running)
			{
				time=System.nanoTime();
				sleep(0,1);
				time=System.nanoTime()-time;
				elapsedTime+=time;
				timeTick+=time;
				timeFrame+=time;
				time=System.nanoTime();
				while(timeTick>=NANOSECOND/ticksPerSecond)
				{
					timeTick-=NANOSECOND/ticksPerSecond;
					tick();
					totalTicks++;
				}
				if(timeFrame>=NANOSECOND/framesPerSecond)
				{
					timeFrame-=NANOSECOND/framesPerSecond;
					redraw();
					totalFrames++;
				}
				time=System.nanoTime()-time;
				elapsedTime+=time;
				timeTick+=time;
				timeFrame+=time;
				if(elapsedTime>NANOSECOND*2)
				{
					elapsedTime-=NANOSECOND;
					totalTicks-=ticksPerSecond;
					totalFrames-=framesPerSecond;
				}
			}
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			running=false;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent event)
	{
		if(currentFocus() instanceof KeyListener)
		{
			keyEventQueue.add(new Pair<Focusable, KeyEvent>(currentFocus(),event));
		}
			//((KeyListener)currentFocus()).keyPressed(event);
		keyPressed(event.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		if(currentFocus() instanceof KeyListener)
			keyEventQueue.add(new Pair<Focusable, KeyEvent>(currentFocus(),event));
			//((KeyListener)currentFocus()).keyReleased(event);
		keyReleased(event.getKeyCode());
	}

	/** 
	 * progresses the game 1 tick
	 */
	private void tick()
	{
		//ticks
		if(currentLocation!=null)
			currentLocation.tick();
		
		//key presses
		if(!keyEventQueue.isEmpty())
		{
			for(int i=0;i<keyEventQueue.size();i++)
			{
				Pair<Focusable, KeyEvent> pair=keyEventQueue.get(i);
				if(pair.second().getID()==KeyEvent.KEY_PRESSED)
				{
					((KeyListener)pair.first()).keyPressed(pair.second());
				}
				if(pair.second().getID()==KeyEvent.KEY_RELEASED)
				{
					((KeyListener)pair.first()).keyReleased(pair.second());
				}
			}
			keyEventQueue.clear();
		}
		
		//perform the EngineActions!
		while(hasActions())
		{
			EngineAction action=peekAction();
			action.callFunction();
			if(action.shouldEnd())
				popAction();
		}
		
		//entity adding/deleting
		if(!entityAddQueue.isEmpty())
		{
			ArrayList<Pair<Entity,Location>> clone=(ArrayList<Pair<Entity,Location>>)entityAddQueue.clone();
			for(Pair<Entity,Location> pair:clone)
			{
				try
				{
					pair.second().addEntity(pair.first());
				}
				catch(LocationAddEntityException ex)
				{
					//ex.printStackTrace();
					//System.out.println("*ERROR*: Entity already exists at location");
				}
			}
			entityAddQueue.clear();
		}
		
		if(!entityRemoveQueue.isEmpty())
		{
			ArrayList<Pair<Entity,Location>> clone=(ArrayList<Pair<Entity,Location>>)entityRemoveQueue.clone();
			for(Pair<Entity,Location> pair:clone)
			{
				pair.second().removeEntity(pair.first());
			}
			entityRemoveQueue.clear();
		}
	}
	
	/** 
	 * Tells the current View to redraw itself
	 */
	private void redraw()
	{
		if(currentView!=null)
		{
			currentView.redraw();
			currentView.repaint();
		}
	}
	
	/**
	 * Returns information about the engine
	 * @return - the information of the engine
	 */
	public ArrayList<String> information()
	{
		ArrayList<String> string=new ArrayList<String>();
		string.add("tps: "+(int)(0.5+totalTicks*1.0/(elapsedTime*1.0/this.NANOSECOND)));
		string.add("fps: "+(int)(0.5+totalFrames*1.0/(elapsedTime*1.0/this.NANOSECOND)));
		
		return (string);
	}
	
	/**
	 * The accessor method for the static member engine, lazily instantiates if null.
	 */
	public static GameEngine instance()
	{
		if(engine==null)
			engine=new GameEngineByTick();
		return(engine);
	}
	
	private static GameEngine engine;
	
	private ArrayList<Pair<Entity,Location>> entityAddQueue;
	private ArrayList<Pair<Entity,Location>> entityRemoveQueue;
	
	private ArrayList<Pair<Focusable,KeyEvent>> keyEventQueue;
	
	public static final long NANOSECOND=1000000000;
	public static final long MILLISECOND=1000;
	private int ticksPerSecond;
	private int framesPerSecond;
	private boolean running;
	
	private int totalFrames;
	private int totalTicks;
	private long elapsedTime;
}
