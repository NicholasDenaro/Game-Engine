package denaro.nick.core;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Sprite extends Identifiable
{
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
	
	public Sprite(String name, String fname, int width, int height, Point origin) throws IOException
	{
		this(name,ImageIO.read(new File(fname)),width,height,origin);
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
