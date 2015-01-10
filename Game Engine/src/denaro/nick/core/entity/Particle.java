package denaro.nick.core.entity;

import denaro.nick.core.GameEngine;
import denaro.nick.core.Sprite;

public class Particle extends Entity
{
	
	/**
	 * @param sprite - The sprite which will represent the particle
	 * @param x - the horizontal position of the particle
	 * @param y - the vertical position of the particle
	 * @param time - the amount of time in ticks that this particle should remain in existance
	 */
	public Particle(Sprite sprite, double x, double y, int time)
	{
		super(sprite,x,y);
		this.time=time;
	}

	/**
	 * @param sprite - The sprite which will represent the particle
	 * @param x - the horizontal position of the particle
	 * @param y - the vertical position of the particle
	 * @param time - the amount of time in seconds that this particle should remain in existance
	 */
	public Particle(Sprite sprite, double x, double y, double time)
	{
		this(sprite,x,y,(int)(GameEngine.instance().ticksPerSecond()*time));
	}
	
	@Override
	public void tick()
	{
		if(time<0)
			return;
		time--;
		if(time==0)
		{
			GameEngine.instance().removeEntity(this,GameEngine.instance().location());
		}
	}

	private int time;
}
