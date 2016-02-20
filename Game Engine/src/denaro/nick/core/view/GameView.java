package denaro.nick.core.view;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public abstract class GameView extends Canvas
{
	/**
	 * Draws the the screen
	 */
	public abstract void redraw();
	
	@Override
	public void repaint()
	{
		//super.repaint();
		if(this.getGraphics()!=null&&buffer!=null)
		{
			this.getGraphics().drawImage(buffer,0,0,null);
			Graphics2D g=buffer.createGraphics();
			//g.setComposite(AlphaComposite.Clear);
			g.setColor(this.getBackground());
			g.fillRect(0,0,getWidth(),getHeight());
			//g.setComposite(AlphaComposite.SrcOver);
		}
	}
	
	/**The image that draws are performed on before being drawn to the screen.*/
	protected BufferedImage buffer;
}
