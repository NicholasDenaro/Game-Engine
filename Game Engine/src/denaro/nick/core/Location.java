package denaro.nick.core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class Location extends Identifiable implements EntityListener
{
	/**
	 * Adds an entity to the list of entities by depth
	 * @param entity - The entity to add to the list
	 * @throws LocationAddEntityException 
	 */
	protected void addEntity(Entity entity) throws LocationAddEntityException
	{
		if(entity==null)
			throw new LocationAddEntityException();
		
		if(entitiesByDepth==null)
			entitiesByDepth=new HashMap<Integer,ArrayList<Entity>>();
		if(entitiesByDepth.get(entity.depth())==null)
			entitiesByDepth.put(entity.depth(), new ArrayList<Entity>());
		if(!entitiesByDepth.get(entity.depth()).contains(entity))
		{
			entitiesByDepth.get(entity.depth()).add(entity);
			entity.addListener(this);
		}
		else
		{
			throw new LocationAddEntityException();
		}
	}
	
	/**
	 * Removes an entity from the list of entities by depth
	 * @param entity - the entity to remove from the list
	 */
	protected void removeEntity(Entity entity)
	{
		entitiesByDepth.get(entity.depth()).remove(entity);
	}
	
	/**
	 * The accessor for the entities in this locaiton
	 * @return - all of the entities in this Location
	 */
	public ArrayList<Entity> entityList()
	{
		ArrayList<Entity> allEntities=new ArrayList<Entity>();
		for(Integer key:entitiesByDepth.keySet())
		{
			allEntities.addAll(entitiesByDepth.get(key));
		}
		return(allEntities);
	}
	
	/**
	 * The accessor for the entities of the specified class in this locaiton
	 * @param c - the class of the entity to add to the list.
	 * @return - all of the entities in this Location
	 */
	public ArrayList<Entity> entityList(Class c)
	{
		ArrayList<Entity> allEntities=new ArrayList<Entity>();
		for(Integer key:entitiesByDepth.keySet())
		{
			ArrayList<Entity> entities=entitiesByDepth.get(key);
			for(Entity entity:entities)
			{
				//if(entity.getClass().getName()==c.getName())
				//if(entity.getClass().isAssignableFrom(c))
				if(c.isAssignableFrom(entity.getClass()))
					allEntities.add(entity);
			}
		}
		return(allEntities);
	}
	
	/**
	 * Returns the layers of the background in this location
	 * @return - all the layers for the background
	 */
	protected HashMap<Integer,BufferedImage> backgroundLayers()
	{
		if(backgroundLayers==null)
			backgroundLayers=new HashMap<Integer,BufferedImage>();
		return(backgroundLayers);
		}
	
	/**
	 * Returns the entities in this location 
	 * @return - all the entities
	 */
	protected HashMap<Integer,ArrayList<Entity>> entityListByDepth()
	{
		if(entitiesByDepth==null)
			entitiesByDepth=new HashMap<Integer,ArrayList<Entity>>();
		return(entitiesByDepth);
	}
	
	/**
	 * ticks all of the entities in this location
	 */
	protected void tick()
	{
		if(entitiesByDepth==null)
			return;
		for(ArrayList<Entity> entities:entitiesByDepth.values())
			for(Entity entity:entities)
				entity.tick();
	}
	
	/**
	 * This method does nothing
	 */
	@Override
	public void EntityMove(EntityEvent event)
	{
		//do nothing
	}

	/**
	 * Changes the entity's location in the list by depth
	 */
	@Override
	public void EntityDepthChange(EntityEvent event)
	{
		entitiesByDepth.get(event.fromDepth()).remove(event.source());
		entitiesByDepth.get(event.toDepth()).add(event.source());
	}
	
	
	
	/** Stores all the entities by their depth*/
	private HashMap<Integer,ArrayList<Entity>> entitiesByDepth;
	
	/**	Stores the layers background image for the location*/
	private HashMap<Integer,BufferedImage> backgroundLayers;

}
