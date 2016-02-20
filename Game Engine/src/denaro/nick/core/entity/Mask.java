package denaro.nick.core.entity;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import denaro.nick.core.Identifiable;

public class Mask extends Identifiable
{
	/**
	 * Creates a new mask using an area
	 * @param area - the area to set as the mask
	 */
	public Mask(Area area)
	{
		this.area=area;
	}
	
	public Area area()
	{
		return(area);
	}
	
	/**
	 * Tells ObjectOutputStream how to write this object
	 * @param out - the output stream that this object is written to
	 * @throws IOException 
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		System.out.println("starting writing mask...");
		PathIterator it=area.getPathIterator(null);
		double[] coords=new double[6];
		while(!it.isDone())
		{
			int type=it.currentSegment(coords);
			
			out.writeInt(type);
			
			if(type==PathIterator.SEG_MOVETO||type==PathIterator.SEG_LINETO)
			{
				
				out.writeDouble(coords[0]);
				out.writeDouble(coords[1]);
			}
			else if(type==PathIterator.SEG_QUADTO)
			{
				out.writeDouble(coords[0]);
				out.writeDouble(coords[1]);
				out.writeDouble(coords[2]);
				out.writeDouble(coords[3]);
			}
			else if(type==PathIterator.SEG_CUBICTO)
			{
				out.writeDouble(coords[0]);
				out.writeDouble(coords[1]);
				out.writeDouble(coords[2]);
				out.writeDouble(coords[3]);
				out.writeDouble(coords[4]);
				out.writeDouble(coords[5]);
			}
			
			it.next();
		}
		out.writeInt(Integer.MIN_VALUE);
		System.out.println("ending writing mask...");
	}
	
	/**
	 * Reads the object from an ObjectInputStream
	 * @param in - the ObjectInputStream to read from
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws IOException
	{
		GeneralPath path=new GeneralPath();
		
		int type;
		while((type=in.readInt())!=Integer.MIN_VALUE)
		{
			if(type==PathIterator.SEG_MOVETO||type==PathIterator.SEG_LINETO)
			{
				double x1=in.readDouble();
				double y1=in.readDouble();
				if(type==PathIterator.SEG_MOVETO)
					path.moveTo(x1,y1);
				else
					path.lineTo(x1,y1);
			}
			else if(type==PathIterator.SEG_QUADTO)
			{
				double x1=in.readDouble();
				double y1=in.readDouble();
				double x2=in.readDouble();
				double y2=in.readDouble();
				path.quadTo(x1,y1,x2,y2);
			}
			else if(type==PathIterator.SEG_CUBICTO)
			{
				double x1=in.readDouble();
				double y1=in.readDouble();
				double x2=in.readDouble();
				double y2=in.readDouble();
				double x3=in.readDouble();
				double y3=in.readDouble();
				path.curveTo(x1,y1,x2,y2,x3,y3);
			}
		}
		area=new Area(path);
	}
	
	private Area area;
}
