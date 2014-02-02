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
	
	private T t;
	private R r;
}
