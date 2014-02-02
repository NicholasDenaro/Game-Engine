package denaro.nick.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class GameView2D extends GameView
{
	public GameView2D(int width, int height, double hscale, double vscale)
	{
		this.width=width;
		this.height=height;
		horizontalScale=hscale;
		verticalScale=vscale;
		setSize((int)(width*horizontalScale),(int)(height*verticalScale));
	}
	
	/**
	 * The accessor for the width pre-scaling
	 * @return - the width
	 */
	public int width()
	{
		return(width);
	}
	
	/**
	 * The accessor for the height pre-scaling
	 * @return - the height
	 */
	public int height()
	{
		return(height);
	}
	
	/**
	 * Draws the location to the graphic
	 * @param currentLocaiton - the location to draw
	 * @param g - the graphic to draw to
	 */
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		HashMap<Integer,BufferedImage> backgrounds=currentLocation.backgroundLayers();
		HashMap<Integer,ArrayList<Entity>> entities=currentLocation.entityListByDepth();
		
		Set<Integer> mergedSortedKeys=mergeKeys(backgrounds.keySet(),entities.keySet());
		
		Iterator<Integer> iterator=mergedSortedKeys.iterator();
		while(iterator.hasNext())
		{
			int key=iterator.next();
			BufferedImage background=backgrounds.get(key);
			if(background!=null)
				g.drawImage(background,0,0,null);
			ArrayList<Entity> entitiesAtDepth=entities.get(key);
			if(entitiesAtDepth!=null)
				for(Entity entity:entitiesAtDepth)
				{
					Sprite sprite=entity.sprite();
					double x=entity.point().x-sprite.anchor().x+entity.offset().x;
					double y=entity.point().y-sprite.anchor().y+entity.offset().y;
					g.drawImage(entity.image(),(int)x,(int)y,null);
				}
		}
	}
	
	/**
	 * Draws system information to the graphic
	 * @param g - the graphic to draw to
	 */
	public void drawSystemInfo(Graphics2D g)
	{
		GameEngine engine=GameEngineByTick.instance();
		
		g.setColor(this.getForeground());
		ArrayList<String> info=engine.information();
		for(int i=0;i<info.size();i++)
			g.drawString(info.get(i),0,(i+1)*12);
	}
	
	/**
	 * Draws to the buffer
	 */
	@Override
	public void redraw()
	{
		GameEngine engine=GameEngineByTick.instance();
		if(buffer==null)
			if(getWidth()>0)
				buffer=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
		if(buffer==null)return;
		Graphics2D g=buffer.createGraphics();
		g.scale(horizontalScale,verticalScale);
		Location currentLocation=engine.location();
		if(currentLocation!=null)
		{
			drawLocation(currentLocation,g);
		}
		
		g.scale(1/horizontalScale,1/verticalScale);
		drawSystemInfo(g);
		
		buffer.flush();
	}
	
	/**
	 * Merges and sorts two sets.
	 * @param set1 - the first set to merge
	 * @param set2 - the second set to merge
	 * @return - a merge of set1 and set2, excluding duplicates
	 */
	private Set<Integer> mergeKeys(Set<Integer> set1, Set<Integer> set2)
	{
		TreeSet<Integer> mergedSet=new TreeSet<Integer>(set1);
		mergedSet.addAll(set2);
		return(mergedSet);
	}
	
	/**
	 * The accessor for the horizontal scale
	 * @return - the horizontal scaling
	 */
	public double hscale()
	{
		return(horizontalScale);
	}
	
	/**
	 * The accessor for the vertical scale
	 * @return - the vertical scaling
	 */
	public double vscale()
	{
		return(verticalScale);
	}
	
	private int width, height;
	private double horizontalScale;
	private double verticalScale;
}
