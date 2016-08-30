package denaro.nick.core.controller;

import java.util.ArrayList;
import java.util.HashMap;

import denaro.nick.core.GameEngine;
import denaro.nick.core.Identifiable;

public abstract class Controller extends Identifiable
{
	private static final long serialVersionUID=1L;

	/**
	 * Constructs a controller with specified engine.
	 * @param engine - the engine this controller sends messages to
	 */
	public Controller()
	{
		listeners=new ArrayList<ControllerListener>();
		createDefaultKeymap();
	}
	
	public abstract boolean init(GameEngine engine);
	
	/**
	 * Sends an action performed event to all listeners.
	 * @param event - The controller even to send to the listeners
	 */
	public void actionPerformed(ControllerEvent event)
	{
		for(ControllerListener listener:listeners)
		{
			listener.actionPerformed(event);
		}
	}
	
	/**
	 * Adds a controller listener to the list.
	 * @param listener - The listener to add
	 */
	public void addControllerListener(ControllerListener listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener - the listener to remove
	 */
	public void removeControllerListener(ControllerListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Returns a list of the listeners.
	 * @return - the list of listeners
	 */
	protected ArrayList<ControllerListener> listeners()
	{
		return(listeners);
	}
	
	/**
	 * Sets the keymap to the specified map
	 * @param map - the map to assign the keymap
	 */
	protected void keymap(HashMap<Integer,Integer> map)
	{
		keymap=map;
	}
	
	/**
	 * Gets a copy of the keymap
	 * @return - a copy of the keymap
	 */
	protected HashMap<Integer,Integer> keymap()
	{
		return ((HashMap<Integer,Integer>)keymap.clone());
	}
	
	protected abstract void createDefaultKeymap();
	
	/** The list of listeners */
	private ArrayList<ControllerListener> listeners;
	
	/** The current keymap **/
	private HashMap<Integer,Integer> keymap;
	
	/** The default keymap created in **/
	public HashMap<Integer,Integer> defaultKeymap;
}
