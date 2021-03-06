package denaro.nick.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.timer.TickingTimer;
import denaro.nick.core.timer.Timer;
import denaro.nick.core.view.GameView;
import denaro.nick.core.view.GameViewListener;

public class GameEngine/* extends Thread*/ implements ControllerListener
{
	private GameEngine(EngineType type)
	{
		this.type=type;
		
		currentView=null;
		
		entityAddQueue=new ArrayList<Pair<Entity,Location>>();

		entityRemoveQueue=new ArrayList<Pair<Entity,Location>>();
		
		inputEventQueue=new ArrayList<Pair<ControllerListener, ControllerEvent>>();
		
		focusQueue=new ArrayList<Pair<Integer,Focusable>>();
		
		timers=new ArrayList<TickingTimer>();
		
		if(type instanceof FixedTickType)
			ticksPerSecond=((FixedTickType)type).ticksPerSecond();
		if(type instanceof FixedFPSType)
			ticksPerSecond=((FixedFPSType)type).ticksPerSecond();
	}
	
	/**
	 * Start the engine
	 */
	public void start()
	{
		type.start();
	}
	
	/**
	 * Stop the engine
	 */
	public void stop()
	{
		type.running(false);
	}
	
	/**
	 * Resume the engine
	 */
	public void resume()
	{
		type.running(true);
	}
	
	/**
	 * This kills the engine
	 */
	public void kill()
	{
		type.kill();
	}
	
	public int ticksPerSecond()
	{
		return(ticksPerSecond);
	}
	
	/**
	 * Set's the controller and calls the controller's init method
	 * @param controller - the controller to set as the controller
	 */
	public void controller(Controller controller)
	{
		controllers.add(controller);
		controller.init(this);
	}
	
	/**
	 * Adds a TickingTimer to the engine
	 * @param timer - the timer to be added
	 */
	public void addTimer(TickingTimer timer)
	{
		timers.add(timer);
	}
	
	/**
	 * Checks if there are any engineActions in the list
	 * @return - true if there are actions in the list
	 */
	public boolean hasActions()
	{
		return(!actions.isEmpty());
	}
	
	/**
	 * Adds an EngineAction to the stack
	 * @param action - the action to add
	 */
	public void addAction(EngineAction action)
	{
		actions.add(action);
	}
	
	/**
	 * Pushes an EngineAction to the front of the stack
	 * @param action - the action to push
	 */
	public void pushAction(EngineAction action)
	{
		actions.push(action);
	}
	
	/**
	 * Look at the first action without removing it from the stack
	 * @return - the EngineAction at the front of the stack
	 */
	public EngineAction peekAction()
	{
		return(actions.peek());
	}
	
	/**
	 * Gets the first EngineAction on the stack
	 * @return - the first EngineAction
	 */
	public EngineAction popAction()
	{
		return(actions.pop());
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		// TODO make sure this works
		/*if(currentFocus()!=null)
			if(currentFocus() instanceof ControllerListener)
			{
				inputEventQueue.add(new Pair<ControllerListener,ControllerEvent>((ControllerListener)this.currentFocus(),event));
			}
		//*/
		for(int i=0;i<currentFocus.size();i++)
		{
			
			if(event.source().id()==i)
			{
				Focusable focus=currentFocus.get(i);
				if(focus instanceof ControllerListener)
				{
					inputEventQueue.add(new Pair<ControllerListener,ControllerEvent>((ControllerListener)focus,event));
				}
			}
		}
	}
	
	public void tick()
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
				pair.first().actionPerformed(pair.second());
			}
		}
		
		//tick all of the ticking timers
		for(int i=0;i<timers.size();i++)
		{
			if(timers.get(i).tick())
				timers.remove(i--);
		}
		
		//perform the EngineActions!
		while(hasActions())
		{
			EngineAction action=peekAction();
			action.callFunction();
			if(action.shouldEnd())
				popAction();
		}
		
		//entity adding
		if(!entityAddQueue.isEmpty())
		{
			ArrayList<Pair<Entity,Location>> clone=(ArrayList<Pair<Entity,Location>>)entityAddQueue.clone();
			for(Pair<Entity,Location> pair:clone)
			{
				try
				{
					if(pair.second()==null)
						throw new LocationAddEntityException("Can't add an entity to a null location.");
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
		
		//entity deleting
		if(!entityRemoveQueue.isEmpty())
		{
			ArrayList<Pair<Entity,Location>> clone=(ArrayList<Pair<Entity,Location>>)entityRemoveQueue.clone();
			for(Pair<Entity,Location> pair:clone)
			{
				pair.second().removeEntity(pair.first());
			}
			entityRemoveQueue.clear();
		}
		
		//focus controll
		if(!focusQueue.isEmpty())
		{
			ArrayList<Pair<Integer,Focusable>> clone=(ArrayList<Pair<Integer,Focusable>>)focusQueue.clone();
			for(Pair<Integer,Focusable> pair:clone)
			{
				if(currentFocus.containsKey(pair.first()))
				{
					if(currentFocus.get(pair.first())!=null)
						currentFocus.get(pair.first()).focusLost();
				}
				currentFocus.put(pair.first(),pair.second());
				if(currentFocus.get(pair.first())!=null)
					currentFocus.get(pair.first()).focusGained();
			}
			focusQueue.clear();
		}
	}
	
	public void redraw()
	{
		if(currentView!=null)
		{
			currentView.redraw();
			currentView.repaint();
		}
	}
	
	/**
	 * Adds an entity to the specified location
	 * @param entity - the entity to add
	 * @param location - the location at which to add the entity. If null, uses currentLocation.
	 * @throws LocationAddEntityException 
	 */
	public void addEntity(Entity entity, Location location)// throws LocationAddEntityException
	{
		//location.addEntity(entity);
		if(location!=null)
			entityAddQueue.add(new Pair<Entity,Location>(entity,location));
		else
			entityAddQueue.add(new Pair<Entity,Location>(entity,currentLocation));
			 
	}
	
	/**
	 * Adds the specified listener to the keyListeners
	 * @param listener - the Listener to be added.
	 */
	public void addControllerListener(ControllerListener listener)
	{
		if(!controllerListeners.contains(listener))
			controllerListeners.add(listener);
	}
	
	/**
	 * Adds a GameViewListener to the list
	 * @param listener - the listener to be added
	 */
	public void addGameViewListener(GameViewListener listener)
	{
		if(gameViewListeners==null)
			gameViewListeners=new ArrayList<GameViewListener>();
		
		if(!gameViewListeners.contains(listener))
			gameViewListeners.add(listener);
	}
	
	/**
	 * The accessor for the current focus
	 * @param index - the index of the focus (There can be multiple focused things for different controllers).
	 * @return - the current focus
	 */
	public Focusable currentFocus(int index)
	{
		return(currentFocus.get(index));
	}
	
	/**
	 * Shifts the focus to the specified focusable object
	 * @param index - the index at which to request the focus. Basically the controller that will hold the focus.
	 * @param focusable - the focusable to shift focus to
	 */
	public void requestFocus(int index, Focusable focusable) throws IndexOutOfBoundsException
	{
		//TODO Make this check if it should change focus.
		if(index>controllers.size())
			throw new IndexOutOfBoundsException("The index is out of bounds!");
		
		focusQueue.add(new Pair<Integer,Focusable>(index,focusable));
		
		/*if(currentFocus.containsKey(index))
			currentFocus.get(index).focusLost();
		currentFocus.put(index,focusable);
		currentFocus.get(index).focusGained();*/
	}
	
	@Override
	public void focusGained()
	{
	}

	@Override
	public void focusLost()
	{
	}
	
	/**
	 * Removes an entity from the specified location
	 * @param entity - the entity to remove
	 * @param location - the location at which to remove the entity
	 */
	public void removeEntity(Entity entity,Location location)
	{
		if(location!=null)
			entityRemoveQueue.add(new Pair(entity,location));
		else
			entityRemoveQueue.add(new Pair(entity,currentLocation));
	}
	
	/**
	 * The accessor for currentLocaiton
	 * @return - the current location
	 */
	public Location location()
	{
		return(currentLocation);
	}
	
	/**
	 * The setter for currentLocaiton
	 * @param location - the location to assign to the current location
	 */
	public void location(Location location)
	{
		this.currentLocation=location;
	}
	
	/**
	 * Returns information about the engine
	 * @return - the information of the engine
	 */
	public ArrayList<String> information()
	{
		return(type.information());
	}
	
	/**
	 * the accessor method for currentView
	 * @return - the current view for this engine
	 */
	public GameView view()
	{
		return(currentView);
	}
	
	/**
	 * the setter method for currentView. Alerts game view listeners
	 * @return - the current view for this engine
	 */
	public void view(GameView view)
	{
		currentView=view;
		
		if(gameViewListeners==null)
			gameViewListeners=new ArrayList<GameViewListener>();
		
		for(GameViewListener listener:gameViewListeners)
			listener.viewChanged(view);
	}
	
	/**
	 * Returns the singleton instance of GameEngine
	 * @return
	 */
	public static GameEngine instance()
	{
		return(engine);
	}
	
	/**
	 * Initialize the singleton instance of GameEngine
	 * @param type - the way the GameEngine ticks
	 * @param force - If true, stops the previous engine and assigns a new type
	 * @return
	 * @throws GameEngineException
	 */
	public static GameEngine instance(EngineType type) throws GameEngineException
	{
		return instance(type, false);
	}
	
	/**
	 * Initialize the singleton instance of GameEngine
	 * @param type - the way the GameEngine ticks
	 * @param force - If true, stops the previous engine and assigns a new type
	 * @return
	 * @throws GameEngineException
	 */
	public static GameEngine instance(EngineType type, boolean force) throws GameEngineException
	{
		if(engine==null)
		{
			if(type!=null)
			{
				engine=new GameEngine(type);
				type.setEngine(engine);
				return(engine);
			}
			else
			{
				throw new GameEngineException("Can't create an engine without a type");
			}
		}
		else
		{
			if(force)
			{
				if(type!=null)
				{
					engine.stop();
					while(engine.type.isAlive())
					{
						try
						{
							Thread.sleep(1);
						}
						catch(InterruptedException e)
						{
							//who cares?!
						}
					};
					//engine=new GameEngine(type);
					//type.setEngine(engine);
					engine.type=type;
					type.setEngine(engine);
					return(engine);
				}
				else
				{
					throw new GameEngineException("Can't create an engine without a type");
				}
			}
			else
			{
				throw new GameEngineException("Can't create an engine if one exists");
			}
		}
	}
	
	/** The current view that the game has.*/
	protected GameView currentView;
	
	/** The current location that the game has.*/
	protected Location currentLocation;
	
	/** The variable used to store the singleton.*/
	private static GameEngine engine;
	
	/** A list of all the KeyListeners*/
	private ArrayList<ControllerListener> controllerListeners=new ArrayList<ControllerListener>();
	
	/** The current KeyListener that has focus*/
	private HashMap<Integer,Focusable> currentFocus=new HashMap<Integer,Focusable>();
	
	private GameMap<Controller> controllers=new GameMap<Controller>();
	
	/** A list of the actions to be taken by the engine*/
	private LinkedList<EngineAction> actions=new LinkedList<EngineAction>();
	
	/** Listeners of views*/
	private ArrayList<GameViewListener> gameViewListeners;
	
	/** The type of ticks the engine does*/
	private EngineType type;
	
	/** The number of ticks per second*/
	private int ticksPerSecond;
	
	/** The TickingTimers that are called every tick*/
	private ArrayList<TickingTimer> timers;
	
	/** The queue to add entities to the location*/
	private ArrayList<Pair<Entity,Location>> entityAddQueue;
	
	/** The queue to remove entities from a location*/
	private ArrayList<Pair<Entity,Location>> entityRemoveQueue;
	
	/** The queue to send controller events to the currently focused focusable*/
	private ArrayList<Pair<ControllerListener,ControllerEvent>> inputEventQueue;
	
	private ArrayList<Pair<Integer,Focusable>> focusQueue;
}
