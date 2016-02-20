package denaro.nick.sound;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSound implements LineListener
{
	private String name;
	private AudioInputStream clipStream;
	private ByteBuffer baseClip;
	private AudioFormat format;
	
	public GameSound(String name, String fname) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		this.name = name;
		File file = new File(fname);
		clipStream = AudioSystem.getAudioInputStream(file);

		format = clipStream.getFormat();
		
		baseClip = ByteBuffer.allocate(0);
		createBaseClip();
		
		gameSounds.put(name, this);
	}
	
	private void createBaseClip() throws IOException
	{
		do
		{
			byte[] b = new byte[clipStream.available()];
			clipStream.read(b);
			ByteBuffer temp = ByteBuffer.allocate(baseClip.capacity() + b.length);
			temp.put(baseClip.array());
			temp.put(b);
			baseClip = temp;
		}
		while(clipStream.available() > 0);
	}
	
	public void play()
	{
		Clip clip;
		try
		{
			clip = AudioSystem.getClip();
			clip.open(format, baseClip.array(), 0, baseClip.capacity());
			clip.setFramePosition(0);
			clip.start();
			clip.addLineListener(this);
		}
		catch(LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, GameSound> gameSounds = new HashMap<String, GameSound>();

	@Override
	public void update(LineEvent le)
	{
		if(le.getType() == LineEvent.Type.STOP)
		{
			le.getLine().close();
		}
	}
}
