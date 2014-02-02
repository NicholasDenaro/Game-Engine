package denaro.nick.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Controller implements KeyListener, MouseListener
{
	/**
	 * Constructs a controller with specified engine
	 * @param engine - the engine this controller sends messages to
	 */
	public Controller(GameEngine engine)
	{
		this.engine=engine;
		engine.view().addKeyListener(this);
		engine.view().addMouseListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent event)
	{
		//TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		engine.keyPressed(event);
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		engine.keyReleased(event);
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	/** The game engine this controller talks to*/
	private GameEngine engine;
	
}
