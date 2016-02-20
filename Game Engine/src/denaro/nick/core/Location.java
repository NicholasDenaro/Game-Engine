package denaro.nick.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

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
	 * Adds an entity to the list of entities by depth. DON'T USE UNLESS YOU KNOW WHAT YOU'RE DOING. You PROBABLY want engine.addEntity(location,entity);
	 * @param entity - The entity to add to the list
	 * @throws LocationAddEntityException 
	 */
	public void addEntityUnprotected(Entity entity) throws LocationAddEntityException
	{
		if(entity==null)
			throw new LocationAddEntityException("Can't add a null entity to a room.");
		
		if(entitiesByDepth==null)
			entitiesByDepth=new HashMap<Integer,ArrayList<Entity>>();
		if(entitiesByDepth.get(entity.depth())==null)
			entitiesByDepth.put(entity.depth(), new ArrayList<Entity>());
		if(!entitiesByDepth.get(entity.depth()).contains(entity))
		{
			//System.out.println("adding entity----------------------");
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
	 * Removes an entity from the list of entities by depth. DON'T USE UNLESS YOU KNOW WHAT YOU'RE DOING. You PROBABLY want engine.removeEntity(location,entity);
	 * @param entity - the entity to remove from the list
	 */
	public void removeEntityUnprotected(Entity entity)
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
		if(entitiesByDepth==null)
			return(allEntities);
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
	 * @param <T> - the class of the entity to get
	 * @param x - the horizontal position to check at.
	 * @param y - the vertical position to check at.
	 * @param c - the class of the entity to add to the list.
	 * @return - all of the entities in this Location
	 */
	public <T> ArrayList<T> entitiesAtPoint(double x, double y, Class<T> c)
	{
		ArrayList<T> allEntities=new ArrayList<T>();
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
						allEntities.add((T)entity);
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
	
	
	/**
	 * Reads and returns a location from the inputstream
	 * @param in - the input stream to read from
	 * @return - the location read from the stream
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Location readFromStream(InputStream in) throws ClassNotFoundException, IOException
	{
		Location location=(Location)((ObjectInputStream)in).readObject();
		return(location);
	}
	
	/**
	 * Writes a location to the outputstream
	 * @param out - the outputstream to write the entity to
	 * @param location - the location to write
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void writeToStream(OutputStream out, Location location) throws ClassNotFoundException, IOException
	{
		((ObjectOutputStream)out).writeObject(location);
	}
	
	/**
	 * Writes the location to the specified output stream
	 * @param out - the output stream to write this location to
	 * @throws IOException 
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(entitiesByDepth.size());
		Iterator<Integer> it=entitiesByDepth.keySet().iterator();
		while(it.hasNext())
		{
			int index=it.next();
			out.writeInt(index);
			ArrayList<Entity> entities=entitiesByDepth.get(index);
			out.writeInt(entities.size());
			for(Entity e:entities)
			{
				out.writeObject(e);
			}
		}
		
		out.writeInt(backgroundLayers.size());
		it=backgroundLayers.keySet().iterator();
		while(it.hasNext())
		{
			int index=it.next();
			out.writeInt(index);
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			ImageIO.write(backgroundLayers.get(index),"png",buffer);
			out.writeInt(buffer.size());
			out.write(buffer.toByteArray());
		}
	}
	
	/**
	 * Reads in a Location object
	 * @param in - the ObjectInputStream to read from
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	private void readObject(ObjectInputStream in) throws IOException
	{
		int keys=in.readInt();
		entitiesByDepth=new HashMap<Integer,ArrayList<Entity>>();
		for(int i=0;i<keys;i++)
		{
			int key=in.readInt();
			ArrayList<Entity> entities=new ArrayList<Entity>();
			try
			{
				int size=in.readInt();
				for(int a=0;a<size;a++)
				{
					Entity e=(Entity)in.readObject();
					Entity actual=Entity.entity(e.id());
					actual.move(e.lastX(),e.lastY());
					actual.move(e.x(),e.y());
					actual.imageIndex(e.imageIndex());
					actual.depth(e.lastDepth());
					actual.depth(e.depth());
					actual.offset(e.offset().x,e.offset().y);
					System.out.println("e.mask(): "+e.mask());
					actual.mask(new Area(e.mask()));
					entities.add(actual);
				}
				entitiesByDepth.put(key,entities);
				
			}
			catch(ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		keys=in.readInt();
		backgroundLayers=new HashMap<Integer,BufferedImage>();
		for(int i=0;i<keys;i++)
		{
			int key=in.readInt();
			int size=in.readInt();
			byte[] bytebuf=new byte[size];
			int index=0;
			while(index<size)
			{
				try
				{
					bytebuf[index++]=in.readByte();
				}
				catch(EOFException ex)
				{
					throw ex;
				}
			}
			ByteArrayInputStream buffer=new ByteArrayInputStream(bytebuf);
			BufferedImage img=ImageIO.read(buffer);
			backgroundLayers.put(key,img);
		}
		addEntitiesDepthQueue=new ArrayList<Pair<Entity,Pair<Integer,Integer>>>();
	}
	
	/** Stores all the entities by their depth*/
	private HashMap<Integer,ArrayList<Entity>> entitiesByDepth;
	
	private ArrayList<Pair<Entity,Pair<Integer,Integer>>> addEntitiesDepthQueue=new ArrayList<Pair<Entity,Pair<Integer,Integer>>>();
	
	/**	Stores the layers background image for the location*/
	private HashMap<Integer,BufferedImage> backgroundLayers;

}
