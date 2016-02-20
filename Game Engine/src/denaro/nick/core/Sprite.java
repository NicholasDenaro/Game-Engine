package denaro.nick.core;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Sprite extends Identifiable
{
	/**
	 * Creates a Sprite from a bufferedImage
	 * @param name - the name of the sprite
	 * @param image - the image of the sprite
	 * @param width - the width of the subimages
	 * @param height - the height of the subimages
	 * @param origin - the origin of the sprite to draw at
	 */
	public Sprite(String name, BufferedImage image, int width, int height, Point origin)
	{
		this.name=name;
		this.image=image;
		this.width=width;
		this.height=height;
		
		if(this.width==-1)
			this.width=this.image.getWidth();
		if(this.height==-1)
			this.height=this.image.getHeight();
		
		this.anchor=origin;
		
		hSubimages=image.getWidth()/width;
		
		spriteMap.put(name,this);
	}
	
	/**
	 * Creates a Sprite form a file
	 * @param name - the name of the sprite
	 * @param fname - the name of the file to load in from
	 * @param width - the width of the subimages
	 * @param height - the height of the subimages
	 * @param origin - the origin of the sprite to draw at
	 * @throws IOException - Throws an exception if the read fails
	 */
	public Sprite(String name, String fname, int width, int height, Point origin) throws IOException
	{
		this(name,ImageIO.read(new File(fname)),width,height,origin);
	}
	
	/**
	 * Sets how many horizontal subimages there are.
	 * @param hSubimages - the number of horizontal subimages
	 */
	public void hSubimages(int hSubimages)
	{
		this.hSubimages=hSubimages;
	}
	
	/**
	 * The accessor for this sprite's anchor
	 * @return - a point representing the anchor
	 */
	public Point anchor()
	{
		return(anchor);
	}
	
	/**
	 * The setter for this sprite's anchor
	 * @param point - the point to set the sprite's anchor
	 */
	public void anchor(Point point)
	{
		anchor=point;
	}
	
	/**
	 * returns a subimage of the buffered image at a specified index
	 * @param index - the index to grab the subimage
	 * @return - the image at the index
	 */
	public Image subimage(int index)
	{
		if(image==null)
			return(null);
		int x=(index%(hSubimages))*width;
		int y=(index/(hSubimages))*height;
		return(image.getSubimage(x,y,width,height));
	}
	
	/**
	 * returns a subimage of the buffered image at a specified index
	 * @param xindex - the x index to grab the subimage
	 * @param yindex - the y index to grab the subimage
	 * @return - the image at the index
	 */
	public Image subimage(int xindex, int yindex)
	{
		if(image==null)
			return(null);
		return(image.getSubimage(xindex*width,yindex*height,width,height));
	}
	
	/**
	 * The accessor for name
	 * @return - the name of the sprite
	 */
	public String name()
	{
		return(name);
	}
	
	/**
	 * The accessor for width
	 * @return - the width of each subimage
	 */
	public int width()
	{
		return(width);
	}
	
	/**
	 * The accessor for height
	 * @return - the height of each subimage
	 */
	public int height()
	{
		return(height);
	}
	
	/**
	 * The accessor for the number of horizontal subimages
	 * @return - the number of horizontal subimages
	 */
	public int hSubimages()
	{
		return(hSubimages);
	}
	
	/**
	 * Tells ObjectOutputStream how to write this object
	 * @param out - the output stream that this object is written to
	 * @throws IOException 
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		System.out.println("starting writing sprite...");
		out.writeObject(name);
		out.writeInt(width);
		out.writeInt(height);
		out.writeInt(hSubimages);
		out.writeObject(anchor);
		ImageIO.write(image,"png",out);
		System.out.println("ending writing sprite...");
	}
	
	/**
	 * Reads the object from an ObjectInputStream
	 * @param in - the ObjectInputStream to read from
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws IOException
	{
		try
		{
			name=(String)in.readObject();
			width=in.readInt();
			height=in.readInt();
			hSubimages=in.readInt();
			anchor=(Point)in.readObject();
			image=ImageIO.read(in);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Sprite sprite(String name)
	{
		return(spriteMap.get(name));
	}
	
	/** The height for subimages.*/
	private int height;
	
	/** The buffered image to store the image.*/
	private BufferedImage image;
	
	/** The name of the sprite.*/
	private String name;
	
	/** The point which anchors the sprite.*/
	private Point anchor;
	
	/** The width for subimages.*/
	private int width;
	
	/** The number of horizontal subimages */
	private int hSubimages;
	
	private static HashMap<String,Sprite> spriteMap=new HashMap<String,Sprite>();
}
