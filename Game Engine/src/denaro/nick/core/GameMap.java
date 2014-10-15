package denaro.nick.core;

import java.util.HashMap;

public class GameMap <V extends Identifiable>
{
	/**
	 * Creates a new GameMap with empty values and counter at 0
	 */
	public GameMap()
	{
		values=new HashMap<Integer, V>();
		counter=0;
	}
	
	/**
	 * Accesses the size of the values
	 * @return - the size of the values
	 */
	public int size()
	{
		return(values.size());
	}

	/**
	 * Adds a value to the map and assigns it an id
	 * @param v - the value to add to the map
	 */
	public void add(V v)
	{
		if(values.containsValue(v))
		{
			System.out.println("ERROR: Already contains this object");
			new Exception().printStackTrace();
		}
		else
		{
			v.id(counter);
			values.put(counter++,v);
		}
	}
	
	/**
	 * Gets a values from the map
	 * @param id - the id of the value to get
	 * @return - the value; null if it is not there
	 */
	public V get(int id)
	{
		return(values.get(id));
	}
	
	/** The map of values*/
	private HashMap<Integer,V> values;
	
	/** The counter for assigning ids*/
	private int counter;
}
