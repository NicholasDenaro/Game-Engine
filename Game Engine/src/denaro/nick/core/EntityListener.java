package denaro.nick.core;

public interface EntityListener
{
	
	/**
	 * Invoked when the observed Entity moves.
	 * @param event - Contains information about the event
	 */
	public void EntityMove(EntityEvent event);
	
	
	/**
	 * Invoked when the observed Entity changes depth.
	 * @param event - Contains information about the event
	 */
	public void EntityDepthChange(EntityEvent event);
}
