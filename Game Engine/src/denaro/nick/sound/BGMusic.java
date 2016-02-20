package denaro.nick.sound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class BGMusic
{
	private String name;
	private Clip clip;
	private int loopStart;
	private int loopEnd;
	
	public BGMusic(String name, Clip clip, int loopStart, int loopEnd) throws LineUnavailableException
	{
		this.name = name;
		this.clip = clip;
		this.loopStart = loopStart;
		this.loopEnd = loopEnd;
		
		bgmMap.put(name, this);
	}
	
	public BGMusic(String name, String fname, int loopStart, int loopEnd) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		this(name, (Clip)null, loopStart, loopEnd);
		File file = new File(fname);
		AudioInputStream aistream = AudioSystem.getAudioInputStream(file);
		this.clip = AudioSystem.getClip();
		clip.open(aistream);
	}
	
	public void start()
	{
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void startLoop()
	{
		clip.setFramePosition(loopStart);
		clip.setLoopPoints(loopStart, loopEnd);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop()
	{
		clip.stop();
	}
	
	private static HashMap<String,BGMusic> bgmMap=new HashMap<String,BGMusic>();
}
