package denaro.nick.core;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import denaro.nick.core.view.GameView;
import denaro.nick.core.view.GameViewListener;

public class GameFrame extends JFrame implements FocusListener, GameViewListener
{
	/**
	 * Creates a new JFrame with a specified title
	 * @param title - the title to set the frame
	 */
	public GameFrame(String title,GameEngine engine)
	{
		super(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setFocusable(false);
		//this.requestFocus();
		//this.addKeyListener(engine);
		this.addFocusListener(this);
		
		buildFrame(engine);
	}
	
	/**
	 * Lays out the frame to fit the engine's current view
	 * @param engine - the engine which contains the view
	 */
	private void buildFrame(GameEngine engine)
	{
		currentPanel=new JPanel();
		currentPanel.setPreferredSize(engine.view().getSize());
		currentPanel.setLayout(new BorderLayout());
		
		currentPanel.add(engine.view(),BorderLayout.CENTER);
		Container c=getContentPane();
		c.setLayout(new BorderLayout());
		c.add(currentPanel,BorderLayout.CENTER);
		
		currentPanel.requestFocusInWindow();
		
		pack();
	}
	
	@Override
	public void viewChanged(GameView view)
	{
		this.remove(currentPanel);
		currentPanel=new JPanel();
		currentPanel.setPreferredSize(view.getSize());
		currentPanel.setLayout(new BorderLayout());
		
		currentPanel.add(view,BorderLayout.CENTER);
		Container c=getContentPane();
		c.setLayout(new BorderLayout());
		c.add(currentPanel,BorderLayout.CENTER);
		
		currentPanel.requestFocusInWindow();
		
		pack();
		
	}

	@Override
	public void focusGained(FocusEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent event)
	{
		if(event.getOppositeComponent()!=null)
			currentPanel.requestFocusInWindow();
	}
	
	private JPanel currentPanel;
}
