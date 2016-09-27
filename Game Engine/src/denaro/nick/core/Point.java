package denaro.nick.core;

public class Point
{
	public final double x;
	public final double y;
	
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean isInBounds(int x1, int y1, int x2, int y2)
	{
		return x >= x1 && y >= y1 && x <= x2 && y <= y2;
	}
}
