package denaro.nick.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import denaro.nick.core.entity.Entity;
import denaro.nick.core.entity.EntityEvent;
import denaro.nick.core.entity.EntityListener;

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
			throw new LocationAddEntityException("Can't add a null entity to a room.");
		
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
			throw new LocationAddEntityException("Entity already exists in this location.");
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
	 * The accessor for the entities at a specified point of the specified class in this locaiton
	 * @param x - the horizontal position to check at.
	 * @param y - the vertical position to check at.
	 * @param c - the class of the entity to add to the list.
	 * @return - all of the entities in this Location
	 */
	public ArrayList<Entity> entitiesAtPoint(double x, double y, Class c)
	{
		ArrayList<Entity> allEntities=new ArrayList<Entity>();
		for(Integer key:entitiesByDepth.keySet())
		{
			ArrayList<Entity> entities=entitiesByDepth.get(key);
			for(Entity entity:entities)
			{
				Area temp=new Area(entity.mask());
				temp.transform(AffineTransform.getTranslateInstance(entity.x(),entity.y()));
				if(temp.contains(x,y))
				{
					if(c.isAssignableFrom(entity.getClass()))
						allEntities.add(entity);
				}
			}
		}
		return(allEntities);
	}
	
	/**
	 * Returns the layers of the background in this location
	 * @return - all the layers for the background
	 */
	public HashMap<Integer,BufferedImage> backgroundLayers()
	{
		if(backgroundLayers==null)
			backgroundLayers=new HashMap<Integer,BufferedImage>();
		return(backgroundLayers);
		}
	
	/**
	 * Returns the entities in this location 
	 * @return - all the entities
	 */
	public HashMap<Integer,ArrayList<Entity>> entityListByDepth()
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
		
		for(Pair<Entity,Pair<Integer,Integer>> pair:addEntitiesDepthQueue)
		{
			entitiesByDepth.get(pair.second().first()).remove(pair.first());
			if(!entitiesByDepth.containsKey(pair.second().second()))
				entitiesByDepth.put(pair.second().second(),new ArrayList<Entity>());
			entitiesByDepth.get(pair.second().second()).add(pair.first());
		}
		addEntitiesDepthQueue.clear();
	}
	
	/**
	 * This method does nothing
	 */
	@Override
	public void entityMove(EntityEvent event)
	{
		//do nothing
	}

	/**
	 * Changes the entity's location in the list by depth
	 */
	@Override
	public void entityDepthChange(EntityEvent event)
	{
		addEntitiesDepthQueue.add(new Pair<Entity,Pair<Integer,Integer>>(event.source(),new Pair<Integer,Integer>(event.fromDepth(),event.toDepth())));
	}
	
	
	
	/** Stores all the entities by their depth*/
	private HashMap<Integer,ArrayList<Entity>> entitiesByDepth;
	
	private ArrayList<Pair<Entity,Pair<Integer,Integer>>> addEntitiesDepthQueue=new ArrayList<Pair<Entity,Pair<Integer,Integer>>>();;
	
	/**	Stores the layers background image for the location*/
	private HashMap<Integer,BufferedImage> backgroundLayers;

}
