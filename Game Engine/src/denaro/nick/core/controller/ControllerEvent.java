package denaro.nick.core.controller;

public class ControllerEvent
{
	public ControllerEvent(int action, int code)
	{
		this(action,code,1);
	}
	
	public ControllerEvent(int action, int code, float modifier)
	{
		this.action=action;
		this.code=code;
		this.modifier=modifier;
	}
	
	public int action()
	{
		return(action);
	}
	
	public int code()
	{
		return(code);
	}
	
	public float modifier()
	{
		return(modifier);
	}
	
	private int action;
	
	private int code;
	
	private float modifier;
	
	public static final int PRESSED=0;
	public static final int RELEASED=1;
	public static final int MOVED=2;
}
