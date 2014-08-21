package denaro.nick.core.controller;

import java.util.ArrayList;

import denaro.nick.core.GameEngine;

public abstract class Controller
{
	/**
	 * Constructs a controller with specified engine
	 * @param engine - the engine this controller sends messages to
	 */
	public Controller()
	{
		listeners=new ArrayList<ControllerListener>();
	}
	
	public abstract void init(GameEngine engine);
	
	public void actionPerformed(ControllerEvent event)
	{
		for(ControllerListener listener:listeners)
			listener.actionPerformed(event);
	}
	
	public void addControllerListener(ControllerListener listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeControllerListener(ControllerListener listener)
	{
		listeners.remove(listener);
	}
	
	protected ArrayList<ControllerListener> listeners()
	{
		return(listeners);
	}
	
	private ArrayList<ControllerListener> listeners;
}
