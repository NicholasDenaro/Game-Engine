package denaro.nick.core;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.controllertest.XBoxControllerListener;
import denaro.nick.controllertest.XBoxButtonEvent;

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
		
		inputEventQueue=new ArrayList<Pair<ControllerListener, ControllerEvent>>();
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		//TODO make this work =)
		if(currentFocus()!=null)
		if(currentFocus() instanceof ControllerListener)
		{
			inputEventQueue.add(new Pair<ControllerListener,ControllerEvent>((ControllerListener)this.currentFocus(),event));
		}
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
		long startSecond=0;
		long timeTick=0;
		long timeFrame=0;
		long time=0;
		long wait=0;
		long leftover=0;
		tpsAVG=1;
		
		running=true;
		try
		{
			//startSecond=System.currentTimeMillis();
			/*while(running)
			{
				//ticks and frames
				elapsedTime=0;
				time=System.nanoTime();
				tick();
				totalTicks++;
				redraw();
				totalFrames++;
				elapsedTime+=System.nanoTime()-time;
				
				//wait if there is still time left
				time=System.nanoTime();
				wait=NANOSECOND/ticksPerSecond-elapsedTime;
				while(wait>0)
				{
					long waiter=System.nanoTime();
					sleep((long)(wait/(NANOSECOND/MILLISECOND)),(int)(wait%(NANOSECOND/MILLISECOND)));
					//calculate if we waited too long or too little, adding it to the beginning
					leftover=-(System.nanoTime()-waiter-wait);
					wait=leftover;
				}
				
				//if it has been a second, calculate the number of ticks that happened
				if(System.currentTimeMillis()-startSecond>MILLISECOND)
				{
					startSecond=System.currentTimeMillis();
					//System.out.println("tps: "+totalTicks);
					totalTicks=0;
				}
				
				//try to average the ticks per second
				elapsedTime+=System.nanoTime()-time;
				tpsAVG=tpsAVG*0.9+elapsedTime*0.1;
			}*/
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
			e.printStackTrace();
			running=false;
		}
	}
	
	/*@Override
	public void keyPressed(KeyEvent event)
	{
		if(currentFocus() instanceof KeyListener)
		{
			inputEventQueue.add(new Pair<Focusable, ControllerEvent>(currentFocus(),event));
		}
			//((KeyListener)currentFocus()).keyPressed(event);
		//keyPressed(event.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		if(currentFocus() instanceof KeyListener)
			inputEventQueue.add(new Pair<Focusable, ControllerEvent>(currentFocus(),event));
			//((KeyListener)currentFocus()).keyReleased(event);
		//keyReleased(event.getKeyCode());
	}
	
	public void buttonPressed(XBoxEvent event)
	{
		if(currentFocus() instanceof XBoxControllerListener)
			inputEventQueue.add(new Pair<Focusable, ControllerEvent>(currentFocus(),event));
	}
	
	public void buttonReleased(XBoxEvent event)
	{
		if(currentFocus() instanceof XBoxControllerListener)
			inputEventQueue.add(new Pair<Focusable, ControllerEvent>(currentFocus(),event));
	}
	
	public void analogMoved(XBoxEvent event)
	{
		if(currentFocus() instanceof XBoxControllerListener)
			inputEventQueue.add(new Pair<Focusable, ControllerEvent>(currentFocus(),event));
	}*/

	/** 
	 * progresses the game 1 tick
	 */
	private void tick()
	{
		//ticks
		if(currentLocation!=null)
			currentLocation.tick();
		
		//key presses
		if(!inputEventQueue.isEmpty())
		{
			//System.out.println("queue size: "+inputEventQueue.size());
			ArrayList<Pair<ControllerListener,ControllerEvent>> tempQueue=new ArrayList<Pair<ControllerListener,ControllerEvent>>();
			int size=inputEventQueue.size();
			for(int i=0;i<size;i++)
				if(inputEventQueue.get(i)!=null)
					tempQueue.add(inputEventQueue.get(i));
			inputEventQueue.clear();
			for(int i=0;i<tempQueue.size();i++)
			{
				Pair<ControllerListener, ControllerEvent> pair=tempQueue.get(i);
				if(pair.first()==null)
				{
					//breakpoint!!!
					i--;
					i++;
				}
				pair.first().actionPerformed(pair.second());
				/*if(pair.second() instanceof KeyEvent)
				{
					KeyEvent event=(KeyEvent)pair.second();
					if(event.getID()==KeyEvent.KEY_PRESSED)
						((KeyListener)pair.first()).keyPressed(event);
					else if(event.getID()==KeyEvent.KEY_RELEASED)
						((KeyListener)pair.first()).keyReleased(event);
				}
				if(pair.second() instanceof XBoxEvent)
				{
					XBoxEvent event=(XBoxEvent)pair.second();
					if(event.getEventType()==XBoxEvent.BUTTON_PRESSED)
						((XBoxControllerListener)pair.first()).buttonPressed(event);
					else if(event.getEventType()==XBoxEvent.BUTTON_RELEASED)
						((XBoxControllerListener)pair.first()).buttonReleased(event);
					else if(event.getEventType()==XBoxEvent.ANALOG_MOVED)
						((XBoxControllerListener)pair.first()).analogMoved(event);
				}*/
			}
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
		//string.add("tps: "+(int)(0.5+1.0/tpsAVG*NANOSECOND));
		string.add("tps: "+(int)(0.5+totalTicks*1.0/(elapsedTime*1.0/NANOSECOND)));
		string.add("fps: "+(int)(0.5+totalFrames*1.0/(elapsedTime*1.0/NANOSECOND)));
		
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
	
	private ArrayList<Pair<ControllerListener,ControllerEvent>> inputEventQueue;
	
	public static final long NANOSECOND=1000000000;
	public static final long MILLISECOND=1000;
	private int ticksPerSecond;
	private int framesPerSecond;
	private boolean running;
	
	private int totalFrames;
	private int totalTicks;
	private long elapsedTime;
	
	private double tpsAVG;
}
