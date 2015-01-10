package denaro.nick.core;

import java.io.Serializable;

public class Identifiable implements Serializable
{
	/**
	 * The accessor for id;
	 * @return - the id.
	 */
	public int id()
	{
		return(id);
	}
	
	/**
	 * the setter method for id
	 * @param id - the id to assign to the id.
	 */
	protected void id(int id)
	{
		this.id=id;
	}
	
	/**The id which identifies the identifiable*/
	private int id=-1;
}
