package denaro.nick.testgame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import denaro.nick.core.FixedFPSType;
import denaro.nick.core.FixedTickType;
import denaro.nick.core.GameEngine;
import denaro.nick.core.GameEngineException;
//import denaro.nick.core.GameEngineFixedFPS;
//import denaro.nick.core.GameEngineFixedTick;
import denaro.nick.core.Location;
import denaro.nick.core.LocationAddEntityException;
import denaro.nick.core.Sprite;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.timer.TickingTimer;
import denaro.nick.core.timer.Timer;
import denaro.nick.core.view.GameView2D;
import denaro.nick.sound.BGMusic;
import denaro.nick.sound.GameSound;

public class Main
{
	
	public static void main(String[] args)
	{
		JFrame frame=new JFrame("Game");
		frame.setResizable(false);
		GameEngine engine;
		try
		{
			//engine=GameEngine.instance(new FixedFPSType(60,60),false);
			engine=GameEngine.instance(new FixedTickType(60),false);
		}
		catch(GameEngineException e)
		{
			e.printStackTrace();
			System.exit(1);
			return;
		}
		engine.view(new GameView2D(320,320,1,1));
		JPanel panel=new JPanel();
		panel.setPreferredSize(engine.view().getSize());
		panel.setLayout(new BorderLayout());
		
		panel.add(engine.view(),BorderLayout.CENTER);
		Container c=frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(panel,BorderLayout.CENTER);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(size.width/2-frame.getWidth()/2,size.height/2-frame.getHeight()/2);
		
		engine.view().setBackground(Color.black);
		engine.view().setForeground(Color.white);
		
		try
		{
			setup(engine);
		}
		catch(LocationAddEntityException e)
		{
			e.printStackTrace();
		}
		
		engine.start();
	}
	
	public static void setup(GameEngine engine) throws LocationAddEntityException
	{		
		Location testRoom=new Location();
		engine.location(testRoom);
		
		BufferedImage img=new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=img.createGraphics();
		g.setColor(Color.red);
		g.fillOval(0,0,10,10);
		Sprite redBall=new Sprite("Mercury",img,10,10, new Point(5,5));
		
		img=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
		g=img.createGraphics();
		g.setColor(new Color(255,170,30));
		g.fillOval(0,0,32,32);
		Sprite orangeBall=new Sprite("Sun",img,32,32, new Point(16,16));
		
		img=new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
		g=img.createGraphics();
		g.setColor(new Color(20,200,140));
		g.fillOval(0,0,20,20);
		Sprite cyanBall=new Sprite("Earth",img,20,20,new Point(10,10));
		
		Entity sun=new Orbital(orangeBall,new Point.Double(160,160),100,new Point.Double(0,0));
		engine.addEntity(sun,testRoom);
		
		Entity orbital1=new Orbital(redBall,new Point.Double(112,80),1,new Point.Double(0,1.5));
		engine.addEntity(orbital1,testRoom);
		
		orbital1=new Orbital(redBall,new Point.Double(112,96),1,new Point.Double(0,1.8));
		engine.addEntity(orbital1,testRoom);
		
		orbital1=new Orbital(redBall,new Point.Double(192,93),1,new Point.Double(0,0.89));
		engine.addEntity(orbital1,testRoom);
		
		orbital1=new Orbital(redBall,new Point.Double(150,50),1,new Point.Double(-0.8,0.2));
		engine.addEntity(orbital1,testRoom);

		
		Entity orbital2=new Orbital(cyanBall,new Point.Double(60,95),3,new Point.Double(0,0.99));
		engine.addEntity(orbital2,testRoom);
		
		try
		{
			BGMusic m = new BGMusic("spacey","M:\\users\\nicholas\\desktop\\splice.wav", 0, 500000);
			m.startLoop();
			
			new GameSound("woosh","M:\\users\\nicholas\\desktop\\Oracle_WaterFlow1.wav");
			new GameSound("switch","M:\\users\\nicholas\\desktop\\Oracle_Switch.wav");
			
			TickingTimer t = new TickingTimer(60*3, false)
			{

				@Override
				public void action()
				{
					System.out.println(".");
					GameSound.gameSounds.get("switch").play();
				}
				
			};
			//t.start();
			TickingTimer t2 = new TickingTimer(60*3, false)
			{

				@Override
				public void action()
				{
					System.out.println("~");
					GameSound.gameSounds.get("woosh").play();
				}
				
			};
			//t2.start();
			GameEngine.instance().addTimer(t);
			GameEngine.instance().addTimer(t2);
		}
		catch(UnsupportedAudioFileException | IOException
				| LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}
}


class Orbital extends Entity
{
	public Orbital(Sprite sprite, Double point, double mass, Point.Double velocity)
	{
		super(sprite,point.x,point.y);
		this.mass=mass;
		this.center=new Point.Double(point.x,point.y);
		this.velocity=velocity;
		this.acceleration=new Point.Double(0,0);
	}
	
	@Override
	public void tick()
	{
		ArrayList<Entity> orbitals=GameEngine.instance().location().entityList(Orbital.class);
		for(Entity entity:orbitals)
		{
			Orbital orbital=(Orbital)entity;
			if(orbital!=this)
			{
				double slope=Math.atan2((orbital.y()-y()),orbital.lastX()-x());
				double distance=new Point2D.Double(x(),y()).distance(new Point2D.Double(orbital.lastX(),orbital.lastY()));
				acceleration.x+=G*Math.cos(slope)*orbital.mass/Math.pow(distance,2);
				acceleration.y+=G*Math.sin(slope)*orbital.mass/Math.pow(distance,2);
			}
		}
		velocity.x+=acceleration.x;
		velocity.y+=acceleration.y;
		if(mass<100)
		{
			moveDelta(velocity.x,velocity.y);
		}
		acceleration=new Point.Double(0,0);
		
		for(int i=0;i<10000;i++) // why is this here?
		{
			//System.out.format("","");//this lags the game
		}
	}
	
	private double theta; 
	private Point.Double center;
	private double mass;
	private Point.Double velocity;
	private Point.Double acceleration;
	
	//public static double G=6.67384*Math.pow(10,-11);
	public static double G=1;
}