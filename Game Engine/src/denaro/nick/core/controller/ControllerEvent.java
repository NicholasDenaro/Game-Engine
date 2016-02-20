package denaro.nick.core.controller;

public class ControllerEvent
{
	public ControllerEvent(Controller controller, int action, int code)
	{
		this(controller,action,code,1);
	}
	
	public ControllerEvent(Controller controller, int action, int code, float modifier)
	{
		this.controller=controller;
		this.action=action;
		this.code=code;
		this.modifier=modifier;
	}
	
	public Controller source()
	{
		return(controller);
	}
	
	/**
	 * Returns the action code for Pressed/Released etc.
	 * @return - the action code of the event
	 */
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
	
	private Controller controller;
	
	private int action;
	
	private int code;
	
	private float modifier;
	
	public static final int PRESSED=0;
	public static final int RELEASED=1;
	public static final int MOVED=2;
}
