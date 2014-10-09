//package denaro.nick.core;
//
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.util.ArrayList;
//
//import denaro.nick.core.controller.ControllerEvent;
//import denaro.nick.core.controller.ControllerListener;
//import denaro.nick.core.entity.Entity;
//import denaro.nick.core.timer.TickingTimer;
//
//public class GameEngineFixedTick extends GameEngine
//{
//	/**
//	 * Creates a new default GameEngine with 0 ticksPerSecond, 0 framesPerSecond and no view
//	 */
//	private GameEngineFixedTick()
//	{
//		engine=this;
//		ticksPerSecond=0;
//		currentView=null;
//		
//		running=false;
//		entityAddQueue=new ArrayList<Pair<Entity,Location>>();
//
//		entityRemoveQueue=new ArrayList<Pair<Entity,Location>>();
//		
//		inputEventQueue=new ArrayList<Pair<ControllerListener, ControllerEvent>>();
//		
//		timers=new ArrayList<TickingTimer>();
//	}
//	
//	@Override
//	public void actionPerformed(ControllerEvent event)
//	{
//		//TODO make this work =) //is this working?
//		if(currentFocus()!=null)
//			if(currentFocus() instanceof ControllerListener)
//			{
//				inputEventQueue.add(new Pair<ControllerListener,ControllerEvent>((ControllerListener)this.currentFocus(),event));
//			}
//	}
//	
//	/**
//	 * Removes an entity to the specified location
//	 * @param entity - the entity to add
//	 * @param location - the location at which to add the entity
//	 */
//	@Override
//	public void removeEntity(Entity entity,Location location)
//	{
//		entityRemoveQueue.add(new Pair(entity,location));
//	}
//	
//	/**
//	 * Adds an entity to the specified location
//	 * @param entity - the entity to add
//	 * @param location - the location at which to add the entity
//	 */
//	@Override
//	public void addEntity(Entity entity,Location location)
//	{
//		entityAddQueue.add(new Pair(entity,location));
//	}
//	
//	public void addTimer(TickingTimer timer)
//	{
//		timers.add(timer);
//	}
//	
//	/**
//	 * Changes the ticksPerSecond to ticks
//	 * @param ticks - the current rate to set the ticksPerSecond to.
//	 * @throws Exception 
//	 */
//	public void setTicksPerSecond(int ticks) throws Exception
//	{
//		if(running)
//			throw new Exception("Can't change ticks per second while running.");
//		ticksPerSecond=ticks;
//	}
//	
//	/** 
//	 * The loop the game runs in
//	 */
//	@Override
//	public void run()
//	{
//		int ticks=0;
//		int frames=0;
//		long start;
//		long time;
//		long end=System.nanoTime();
//		long nextdraw=0;
//		final long NSPT=NANOSECOND/this.ticksPerSecond;
//		long endSecond=System.currentTimeMillis()+MILLISECOND;
//		
//		long ticktime=0;
//		long drawtime=0;
//		
//		long avgDrawTime=0;
//		boolean useAvgDrawTime=false;
//		long avgTickTime=0;
//		boolean useAvgTickTime=false;
//		
//		running=true;
//		//try
//		{
//			while(running)
//			{
//				if(System.nanoTime()>=end)
//				{
//					start=System.nanoTime();
//					tick();
//					ticks++;
//					ticktime=System.nanoTime()-start;
//					avgTickTime=(long)((avgTickTime*(ticks-1)+ticktime)*1.0/ticks+0.5);
//					end=System.nanoTime()+(NSPT-ticktime);
//				}
//				
//				if(System.nanoTime()+(useAvgDrawTime?avgDrawTime:0)<end-(useAvgTickTime?avgTickTime:0)||System.nanoTime()>nextdraw)
//				{
//					frames++;
//					start=System.nanoTime();
//					redraw();
//					drawtime=System.nanoTime()-start;
//					avgDrawTime=(long)((avgDrawTime*(frames-1)+drawtime)*1.0/frames+0.5);
//					nextdraw=System.nanoTime()+NANOSECOND/20;
//				}
//					
//				/*while(System.nanoTime()<end)
//				{
//					Thread.yield();
//				}*/
//				
//				if(System.currentTimeMillis()>=endSecond)
//				{
//					totalTicks=ticks;
//					totalFrames=frames;
//					ticks=0;
//					frames=0;
//					useAvgDrawTime=true;
//					useAvgTickTime=true;
//					//System.out.println("avgTickTime: "+avgTickTime*1.0/NANOSECOND);
//					//System.out.println("avgDrawTime: "+avgDrawTime*1.0/NANOSECOND);
//					endSecond=System.currentTimeMillis()+MILLISECOND;
//				}
//			}
//		}
//		/*catch(InterruptedException e)
//		{
//			e.printStackTrace();
//			running=false;
//		}*/
//	}
//
//	/** 
//	 * progresses the game 1 tick
//	 */
//	private void tick()
//	{
//		//System.out.println("tick");
//		//ticks
//		if(currentLocation!=null)
//			currentLocation.tick();
//		
//		//key presses
//		if(!inputEventQueue.isEmpty())
//		{
//			//System.out.println("queue size: "+inputEventQueue.size());
//			ArrayList<Pair<ControllerListener,ControllerEvent>> tempQueue=new ArrayList<Pair<ControllerListener,ControllerEvent>>();
//			int size=inputEventQueue.size();
//			for(int i=0;i<size;i++)
//				if(inputEventQueue.get(i)!=null)
//					tempQueue.add(inputEventQueue.get(i));
//			inputEventQueue.clear();
//			for(int i=0;i<tempQueue.size();i++)
//			{
//				Pair<ControllerListener, ControllerEvent> pair=tempQueue.get(i);
//				pair.first().actionPerformed(pair.second());
//			}
//		}
//		
//		//tick all of the ticking timers
//		for(int i=0;i<timers.size();i++)
//		{
//			if(timers.get(i).tick())
//				timers.remove(i--);
//		}
//		
//		//perform the EngineActions!
//		while(hasActions())
//		{
//			EngineAction action=peekAction();
//			action.callFunction();
//			if(action.shouldEnd())
//				popAction();
//		}
//		
//		//entity adding/deleting
//		if(!entityAddQueue.isEmpty())
//		{
//			ArrayList<Pair<Entity,Location>> clone=(ArrayList<Pair<Entity,Location>>)entityAddQueue.clone();
//			for(Pair<Entity,Location> pair:clone)
//			{
//				try
//				{
//					pair.second().addEntity(pair.first());
//				}
//				catch(LocationAddEntityException ex)
//				{
//					//ex.printStackTrace();
//					//System.out.println("*ERROR*: Entity already exists at location");
//				}
//			}
//			entityAddQueue.clear();
//		}
//		
//		if(!entityRemoveQueue.isEmpty())
//		{
//			ArrayList<Pair<Entity,Location>> clone=(ArrayList<Pair<Entity,Location>>)entityRemoveQueue.clone();
//			for(Pair<Entity,Location> pair:clone)
//			{
//				pair.second().removeEntity(pair.first());
//			}
//			entityRemoveQueue.clear();
//		}
//	}
//	
//	/** 
//	 * Tells the current View to redraw itself
//	 */
//	private void redraw()
//	{
//		if(currentView!=null)
//		{
//			currentView.redraw();
//			currentView.repaint();
//		}
//	}
//	
//	/**
//	 * Returns information about the engine
//	 * @return - the information of the engine
//	 */
//	public ArrayList<String> information()
//	{
//		ArrayList<String> string=new ArrayList<String>();
//		//string.add("tps: "+(int)(0.5+1.0/tpsAVG*NANOSECOND));
//		string.add("tps: "+totalTicks);
//		string.add("fps: "+totalFrames);
//		//string.add("tps: "+(int)(0.5+totalTicks*1.0/(elapsedTime*1.0/NANOSECOND)));
//		//string.add("fps: "+(int)(0.5+totalFrames*1.0/(elapsedTime*1.0/NANOSECOND)));
//		
//		return (string);
//	}
//	
//	/**
//	 * The accessor method for the static member engine, lazily instantiates if null.
//	 */
//	public static GameEngine instance()
//	{
//		if(engine==null)
//			engine=new GameEngineFixedTick();
//		return(engine);
//	}
//	
//	private static GameEngine engine;
//	
//	private ArrayList<TickingTimer> timers;
//	
//	private ArrayList<Pair<Entity,Location>> entityAddQueue;
//	private ArrayList<Pair<Entity,Location>> entityRemoveQueue;
//	
//	private ArrayList<Pair<ControllerListener,ControllerEvent>> inputEventQueue;
//	
//	public static final long NANOSECOND=1000000000;
//	public static final long MILLISECOND=1000;
//	public static final byte MAX_FRAME_SKIP=10;
//	private int ticksPerSecond;
//	private boolean running;
//	
//	private int totalFrames;
//	private int totalTicks;
//	//private long elapsedTime;
//}
