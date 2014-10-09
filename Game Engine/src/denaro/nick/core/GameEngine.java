package denaro.nick.core;

import java.util.ArrayList;
import java.util.LinkedList;

import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.timer.TickingTimer;
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
		
		timers=new ArrayList<TickingTimer>();
	}
	
	public void start()
	{
		type.start();
	}
	
	public void stop()
	{
		type.stopRunning();
	}
	
	public void kill()
	{
		//TODO this is the end of the engine
	}
	
	public void controller(Controller controller)
	{
		controller.init(this);
	}
	
	public boolean hasActions()
	{
		return(!actions.isEmpty());
	}
	
	public void addAction(EngineAction action)
	{
		actions.add(action);
	}
	
	public void pushAction(EngineAction action)
	{
		actions.push(action);
	}
	
	public EngineAction peekAction()
	{
		return(actions.peek());
	}
	
	public EngineAction popAction()
	{
		return(actions.pop());
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		// TODO make sure this works
		if(currentFocus()!=null)
			if(currentFocus() instanceof ControllerListener)
			{
				inputEventQueue.add(new Pair<ControllerListener,ControllerEvent>((ControllerListener)this.currentFocus(),event));
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
		
		//entity adding/deleting
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
	 * @param location - the location at which to add the entity
	 * @throws LocationAddEntityException 
	 */
	public void addEntity(Entity entity, Location location)// throws LocationAddEntityException
	{
		//location.addEntity(entity);
		entityAddQueue.add(new Pair(entity,location));
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
	 * @return - the current focus
	 */
	public Focusable currentFocus()
	{
		return(currentFocus);
	}
	
	/**
	 * Shifts the focus to the specified focusable object
	 * @param focusable - the focusable to shift focus to
	 */
	public void requestFocus(Focusable focusable)
	{
		//TODO Make this check if it should change focus.
		currentFocus=focusable;
	}
	
	/**
	 * Removes an entity from the specified location
	 * @param entity - the entity to remove
	 * @param location - the location at which to remove the entity
	 */
	public void removeEntity(Entity entity,Location location)
	{
		entityRemoveQueue.add(new Pair(entity,location));
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
	
	public static GameEngine instance()
	{
		return(engine);
	}
	
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
	private Focusable currentFocus;
	
	/** A list of the actions to be taken by the engine*/
	private LinkedList<EngineAction> actions=new LinkedList<EngineAction>();
	
	/** Listeners of views*/
	private ArrayList<GameViewListener> gameViewListeners;
	
	private EngineType type;
	
	private ArrayList<TickingTimer> timers;
	
	private ArrayList<Pair<Entity,Location>> entityAddQueue;
	private ArrayList<Pair<Entity,Location>> entityRemoveQueue;
	
	private ArrayList<Pair<ControllerListener,ControllerEvent>> inputEventQueue;
}
