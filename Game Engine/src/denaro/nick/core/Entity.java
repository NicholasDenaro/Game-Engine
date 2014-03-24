package denaro.nick.core;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class Entity extends Identifiable
{
	/**
	 * Creates an Entity with a sprite at a specified point
	 * @param sprite - the sprite for the entity
	 * @param point - the position to set the entity at
	 */
	public Entity(Sprite sprite, double x, double y)
	{
		this.sprite=sprite;
		this.x=x;
		this.y=y;
		this.lastX=x;
		this.lastY=y;
		if(sprite!=null)
		{
			Rectangle.Double rect=new Rectangle.Double(-sprite.anchor().x,-sprite.anchor().y,this.sprite.width(),this.sprite.height());
			this.mask=new Area(rect);
		}
		this.depth=0;
		this.lastDepth=this.depth;
		this.listeners=new ArrayList<EntityListener>();
		this.imageIndex=0;
		this.offset=new Point.Double(0,0);
	}
	
	/**
	 * Adds a listener to the list of listeners
	 * @param listener - the listener to add to the list
	 */
	public void addListener(EntityListener listener)
	{
		if(!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}
	
	/**
	 * Checks if there is a collision between 2 entities
	 * @param loc - temporarily moves the entity to this location to check for a collision
	 * @param other - the area to check a collision with
	 * @return - true if the sprites boundaries overlap
	 */
	public boolean collision(Point.Double loc, Area other)
	{
		AffineTransform af=new AffineTransform();
		if(loc!=null)
			af.translate(loc.x,loc.y);
		else
			af.translate(this.x,this.y);
		Area myarea=mask.createTransformedArea(af);
		
		
		myarea.intersect(other);
		return(!myarea.isEmpty());
	}
	
	/**
	 * Checks if there is a collision between 2 entities
	 * @param x - temporarily moves the entity to this location to check for a collision
	 * @param y - temporarily moves the entity to this location to check for a collision
	 * @param other - the entity to check a collision with
	 * @return - true if the area boundaries overlap
	 */
	public boolean collision(double x, double y, Entity other)
	{
		Point.Double loc=new Point.Double(x,y);
		/*AffineTransform af=new AffineTransform();
		if(loc!=null)
			af.translate(loc.x,loc.y);
		else
			af.translate(point.x,point.y);
		Area myarea=mask.createTransformedArea(af);*/
		
		AffineTransform af=new AffineTransform();
		af.translate(other.x,other.y);
		Area otherarea=other.mask.createTransformedArea(af);
		
		/*myarea.intersect(otherarea);
		return(!myarea.isEmpty());*/
		return(collision(loc, otherarea));
	}
	
	/**
	 * Checks if there is a collision between this entity and the entities in the list
	 * @param x - temporarily moves the entity to this location to check for a collision
	 * @param y - temporarily moves the entity to this location to check for a collision
	 * @param others - a list of entities to check collisions with
	 * @return - true if any of the entities collide with this entity
	 */
	public boolean collision(double x, double y, ArrayList<Entity> others)
	{
		//Rectangle2D.Double mybounds=new Rectangle2D.Double(point.x-sprite.origin().x,point.y-sprite.origin().y,sprite.width(),sprite.height());
		//Rectangle2D.Double otherbounds;
		for(Entity other:others)
		{
			if(other!=this)
			{
				//otherbounds=new Rectangle2D.Double(other.point.x-other.sprite.origin().x,other.point.y-other.sprite.origin().y,other.sprite.width(),other.sprite.height());
				if(collision(x,y,other))
					return(true);
			}
		}
		
		return(false);
	}
	
	/**
	 * The accessor for depth
	 * @return - the depth of the entity
	 */
	public int depth()
	{
		return(depth);
	}
	
	/**
	 * Changes the entity's depth
	 * @param depth - the depth to set the current entity
	 */
	public void depth(int depth)
	{
		lastDepth=this.depth;
		this.depth=depth;
		EntityEvent event=new EntityEvent(this);
		if(lastDepth!=this.depth)
			for(EntityListener listener:listeners)
				listener.entityDepthChange(event);
		
	}
	
	/**
	 * Returns the direction from this entity to the other
	 * @param other - the entity to check against
	 * @return - the direction from this entity to the other
	 */
	public double direction(Entity other)
	{
		return(Math.atan2(other.y-this.y,other.x-this.x));
	}
	
	/**
	 * Returns the image that represents the entity
	 * @return - the image that currently represents the entity
	 */
	public Image image()
	{
		return(sprite.subimage(imageIndex));
	}
	
	/**
	 * The setter method for imageIndex
	 * @param index - the index to assign to imageIndex
	 */
	public void imageIndex(int index)
	{
		imageIndex=index;
	}
	
	/**
	 * The accessor for the image index.
	 * @return - the image index
	 */
	public int imageIndex()
	{
		return(imageIndex);
	}
	
	/**
	 * The accessor for the entity's last depth
	 * @return - The Entity's last depth
	 */
	public int lastDepth()
	{
		return(lastDepth);
	}
	
	/**
	 * The accessor for the entity's last horizontal position
	 * @return - The Entity's last position
	 */
	public double lastX()
	{
		return(lastX);
	}
	
	/**
	 * The accessor for the entity's last vertical position
	 * @return - The Entity's last position
	 */
	public double lastY()
	{
		return(lastY);
	}
	
	/**
	 * The accessor for the collision mask
	 * @return - The shape of the mask
	 */
	public Area mask()
	{
		return(mask);
	}
	
	/**
	 * Assigns the mask the specified shape
	 * @param area - the area to assign to the mask
	 */
	public void mask(Area area)
	{
		mask.reset();
		mask=area;
	}
	
	/**
	 * Moves the player to the location, (x,y)
	 * @param x - the horizontal position
	 * @param y - the vertical position
	 */
	public void move(double x, double y)
	{
		move(new Point.Double(x,y));
	}
	
	/**
	 * Moves the player to the location p
	 * @param p - the location to move to
	 */
	private void move(Point.Double p)
	{
		lastX=x;
		lastY=y;
		x=p.x;
		y=p.y;
		//AffineTransform af=new AffineTransform();
		//af.setToTranslation(point.x-lastPoint.x,point.y-lastPoint.y);
		//mask.transform(af);
		EntityEvent event=new EntityEvent(this);
		for(EntityListener listener:listeners)
			listener.entityMove(event);
	}
	
	/**
	 * Shifts the location of the player by p
	 * @param p - the change in position
	 */
	private void moveDelta(Point.Double p)
	{
		Point.Double to=new Point.Double(this.x+p.x,this.y+p.y);
		move(to);
	}
	
	/**
	 * Shifts the location of the player by p
	 * @param x - the change in x position
	 * @param y - the change in y position
	 */
	public void moveDelta(double x, double y)
	{
		Point.Double to=new Point.Double(this.x+x,this.y+y);
		move(to);
	}
	
	/**
	 * The accessor for this entity's sprite offset
	 * @return - a point representing the offset
	 */
	public Point.Double offset()
	{
		return(offset);
	}
	
	/**
	 * The setter for this entity's sprite offset
	 * @param x - the horizontal point to set the sprite's offset
	 * @param y - the vertical point to set the sprite's offset
	 */
	public void offset(double x, double y)
	{
		offset=new Point.Double(x,y);
	}
	
	/**
	 * The accessor for the entity's current horizontal position
	 * @return - A copy of the Entity's position
	 */
	public double x()
	{
		return(x);
	}
	
	/**
	 * The accessor for the entity's current vertical position
	 * @return - A copy of the Entity's position
	 */
	public double y()
	{
		return(y);
	}
	
	/**
	 * Removes a listener from the list of listeners
	 * @param listener - the listener to remove from the list
	 */
	public void removeListener(EntityListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * The accessor for this entity's sprite
	 * @return - the sprite that this entity has
	 */
	public Sprite sprite()
	{
		return(sprite);
	}
	
	/**
	 * The setter for this entity's sprite
	 * @param sprite - the sprite to assign this entities sprite
	 */
	public void sprite(Sprite sprite)
	{
		this.sprite=sprite;
	}
	
	
	public static double dot(double direction1, double direction2)
	{
		Point2D.Double pointDirection1=new Point2D.Double(Math.cos(direction1),Math.sin(direction1));
		Point2D.Double pointDirection2=new Point2D.Double(Math.cos(direction2),Math.sin(direction2));
		
		return(pointDirection1.x*pointDirection2.x+pointDirection1.y*pointDirection2.y);
	}
	
	/**
	 * 
	 */
	public abstract void tick();
	
	/** A list of all the listeners that are currently listening to this object*/
	private ArrayList<EntityListener> listeners;
	
	/** The index of the current sprite*/
	private int imageIndex;
	
	/** The offset at which to draw the image.*/
	private Point.Double offset;
	
	/** The horizontal component of the entity's location*/
	private double x;
	
	/** The vertical component of the entity's location*/
	private double y;
	
	/** The last depth of the entity*/
	private int lastDepth;
	
	/** The last horizontal component of the entity's last location*/
	private double lastX;
	
	/** The last vertical component of the entity's last location*/
	private double lastY;
	
	/** The depth of the entity*/
	private int depth;
	
	/** The collision mask for this entity*/
	private Area mask;
	
	/** The sprite which contains the image for the entity.*/
	private Sprite sprite;
	
	
}
