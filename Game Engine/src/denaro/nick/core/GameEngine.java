package denaro.nick.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class GameEngine extends Thread implements KeyListener
{
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
	
	/**
	 * Adds an entity to the specified location
	 * @param entity - the entity to add
	 * @param location - the location at which to add the entity
	 * @throws LocationAddEntityException 
	 */
	public void addEntity(Entity entity,Location location) throws LocationAddEntityException
	{
		location.addEntity(entity);
	}
	
	/**
	 * Adds the specified listener to the keyListeners
	 * @param listener - the Listener to be added.
	 */
	public void addKeyListener(KeyListener listener)
	{
		if(!keyListeners.contains(listener))
			keyListeners.add(listener);
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
		location.removeEntity(entity);
	}
	
	/**
	 * The accessor for the specified key
	 * @param keyCode - the key to check
	 * @return - true if the specified key is pressed
	 */
	public boolean isKeyPressed(int keyCode)
	{
		return(keys[keyCode]);
	}
	
	
	/**
	 * Assigns the specified key as pressed
	 * @param keyCode - The key to assign to pressed
	 */
	public void keyPressed(int keyCode)
	{
		keys[keyCode]=true;
	}
	
	/**
	 * Assigns the specified key as released
	 * @param keyCode - The key to assign to released
	 */
	public void keyReleased(int keyCode)
	{
		keys[keyCode]=false;
	}
	
	@Override
	public void keyPressed(KeyEvent event)
	{
		if(currentFocus instanceof KeyListener)
			((KeyListener)currentFocus).keyPressed(event);
		keyPressed(event.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		if(currentFocus instanceof KeyListener)
			((KeyListener)currentFocus).keyReleased(event);
		keyReleased(event.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		// TODO Auto-generated method stub
		
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
	 */
	public void location(Location location)
	{
		this.currentLocation=location;
	}
	
	/**
	 * Returns information about the engine
	 * @return - the information of the engine
	 */
	public abstract ArrayList<String> information();
	
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
	
	
	/** The controller that the game has.*/
	protected Controller controller;
	
	/** The current view that the game has.*/
	protected GameView currentView;
	
	/** The current location that the game has.*/
	protected Location currentLocation;
	
	/** The variable used to store the singleton.*/
	private static GameEngine engine;
	
	/** A list of all the KeyListeners*/
	private ArrayList<KeyListener> keyListeners=new ArrayList<KeyListener>();
	
	/** The current KeyListener that has focus*/
	private Focusable currentFocus;
	
	/** An array of the key states*/
	private boolean[] keys=new boolean[KeyEvent.KEY_LAST+1];
	
	/** A list of the actions to be taken by the engine*/
	private LinkedList<EngineAction> actions=new LinkedList<EngineAction>();
	
	/** Listeners of views*/
	private ArrayList<GameViewListener> gameViewListeners;
}
