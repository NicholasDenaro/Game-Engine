package denaro.nick.core;

public class Pair<T,R>
{
	public Pair(T t, R r)
	{
		this.t=t;
		this.r=r;
	}
	
	public T first()
	{
		return(t);
	}
	
	public R second()
	{
		return(r);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof Pair==false)
			return(false);
		else
		{
			Pair otherPair=(Pair)other;
			return(t==otherPair.t&&r==otherPair.r);
		}
	}
	
	private T t;
	private R r;
}
