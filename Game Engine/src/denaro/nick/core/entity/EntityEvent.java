package denaro.nick.core.entity;

import java.awt.Point;

public class EntityEvent
{
	/**
	 * Creates an event with the specified source
	 * @param source - The entity which created this event.
	 */
	public EntityEvent(Entity source)
	{
		this.source=source;
	}
	
	/**
	 * accesses the location the entity moved to
	 * @return - a point containing the change in location
	 */
	public Point.Double movedDelta()
	{
		Point.Double delta=new Point.Double(movedTo().x-movedFrom().x,movedTo().y-movedFrom().y);
		return(delta);
	}
	
	/**
	 * accesses the location the entity moved from
	 * @return - a point containing the location
	 */
	public Point.Double movedFrom()
	{
		return(new Point.Double(source.lastX(),source.lastY()));
	}
	
	/**
	 * accesses the location the entity moved to
	 * @return - a point containing the location
	 */
	public Point.Double movedTo()
	{
		return(new Point.Double(source.x(),source.y()));
	}
	
	/**
	 * accesses the depth the entity moved from
	 * @return - the last depth
	 */
	public int fromDepth()
	{
		return(source.lastDepth());
	}
	
	/**
	 * accesses the depth the entity moved to
	 * @return - the current depth
	 */
	public int toDepth()
	{
		return(source.depth());
	}
	
	/**
	 * The accessor for entity that created this event
	 * @return - the entity that created the event
	 */
	public Entity source()
	{
		return(source);
	}
	
	/** The Entity which created this event*/
	private Entity source;
}
