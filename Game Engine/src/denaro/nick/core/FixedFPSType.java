package denaro.nick.core;

import java.util.ArrayList;

public class FixedFPSType extends EngineType
{

	public FixedFPSType(int ticksPerSecond, int framesPerSecond)
	{
		this.ticksPerSecond=ticksPerSecond;
		this.framesPerSecond=framesPerSecond;
	}
	
	@Override
	public void run()
	{
		
		long startSecond=0;
		long timeTick=0;
		long timeFrame=0;
		long time=0;
		long wait=0;
		long leftover=0;
		
		elapsedTime=0;
		
		try
		{
			while(running())
			{
				time=System.nanoTime();
				sleep(0,1);
				time=System.nanoTime()-time;
				elapsedTime+=time;
				timeTick+=time;
				timeFrame+=time;
				time=System.nanoTime();
				while(timeTick>=NANOSECOND/ticksPerSecond)
				{
					timeTick-=NANOSECOND/ticksPerSecond;
					engine().tick();
					totalTicks++;
				}
				if(timeFrame>=NANOSECOND/framesPerSecond)
				{
					timeFrame-=NANOSECOND/framesPerSecond;
					engine().redraw();
					totalFrames++;
				}
				time=System.nanoTime()-time;
				elapsedTime+=time;
				timeTick+=time;
				timeFrame+=time;
				
				if(elapsedTime>NANOSECOND*2)
				{
					elapsedTime-=NANOSECOND;
					totalTicks-=ticksPerSecond;
					totalFrames-=framesPerSecond;
				}
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			running(false);
		}
		
		engine().kill();
	}
	
	/**
	 * Accessor method for the number of ticks each second
	 * @return - the number of ticks per second
	 */
	public int ticksPerSecond()
	{
		return(ticksPerSecond);
	}
	
	@Override
	public ArrayList<String> information()
	{
		ArrayList<String> string=new ArrayList<String>();
		//string.add("tps: "+(int)(0.5+1.0/tpsAVG*NANOSECOND));
		string.add("tps: "+(int)(0.5+totalTicks*1.0/(elapsedTime*1.0/NANOSECOND)));
		string.add("fps: "+(int)(0.5+totalFrames*1.0/(elapsedTime*1.0/NANOSECOND)));
		
		return (string);
	}
	
	public static final long NANOSECOND=1000000000;
	public static final long MILLISECOND=1000;
	
	private int ticksPerSecond;
	private int framesPerSecond;
	private int totalTicks;
	private int totalFrames;
	private int elapsedTime;
}
