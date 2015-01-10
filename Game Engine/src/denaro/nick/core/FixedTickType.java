package denaro.nick.core;

import java.util.ArrayList;

public class FixedTickType extends EngineType
{
	public FixedTickType(int ticksPerSecond)
	{
		this.ticksPerSecond=ticksPerSecond;
	}
	
	public void run()
	{
		int ticks=0;
		int frames=0;
		long start;
		long time;
		long end=System.nanoTime();
		long nextdraw=0;
		final long NSPT=NANOSECOND/this.ticksPerSecond;
		long endSecond=System.currentTimeMillis()+MILLISECOND;
		
		long ticktime=0;
		long drawtime=0;
		
		long avgDrawTime=0;
		boolean useAvgDrawTime=false;
		long avgTickTime=0;
		boolean useAvgTickTime=false;
		

		//try
		while(!isKilled())
		{
			while(running())
			{
				if(System.nanoTime()>=end)
				{
					start=System.nanoTime();
					engine().tick();
					ticks++;
					ticktime=System.nanoTime()-start;
					avgTickTime=(long)((avgTickTime*(ticks-1)+ticktime)*1.0/ticks+0.5);
					end=System.nanoTime()+(NSPT-ticktime);
				}
				
				if(System.nanoTime()+(useAvgDrawTime?avgDrawTime:0)<end-(useAvgTickTime?avgTickTime:0)||System.nanoTime()>nextdraw)
				{
					frames++;
					start=System.nanoTime();
					engine().redraw();
					drawtime=System.nanoTime()-start;
					avgDrawTime=(long)((avgDrawTime*(frames-1)+drawtime)*1.0/frames+0.5);
					nextdraw=System.nanoTime()+NANOSECOND/20;
				}
					
				/*while(System.nanoTime()<end)
				{
					Thread.yield();
				}*/
				
				if(System.currentTimeMillis()>=endSecond)
				{
					totalTicks=ticks;
					totalFrames=frames;
					ticks=0;
					frames=0;
					useAvgDrawTime=true;
					useAvgTickTime=true;
					//System.out.println("avgTickTime: "+avgTickTime*1.0/NANOSECOND);
					//System.out.println("avgDrawTime: "+avgDrawTime*1.0/NANOSECOND);
					endSecond=System.currentTimeMillis()+MILLISECOND;
				}
			}
		}
		/*catch(InterruptedException e)
		{
			e.printStackTrace();
			running=false;
		}*/
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
		string.add("tps: "+totalTicks);
		string.add("fps: "+totalFrames);
		//string.add("tps: "+(int)(0.5+totalTicks*1.0/(elapsedTime*1.0/NANOSECOND)));
		//string.add("fps: "+(int)(0.5+totalFrames*1.0/(elapsedTime*1.0/NANOSECOND)));
		
		return (string);
	}
	
	public static final long NANOSECOND=1000000000;
	public static final long MILLISECOND=1000;
	
	private int ticksPerSecond;
	
	private int totalTicks;
	private int totalFrames;

}
