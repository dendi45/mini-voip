package com.evanram.voip;

import static com.evanram.voip.Utils.isMostlyQuiet;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioManager
{
	//16k default buffer and sample rate work, as far as my tests have gone, well in terms of call quality.
	public static final int DEFAULT_BUFFER_SIZE = 16_000;

	private static final int DATA_LINE_INFO_BUFFER_SIZE = 1024;

	private int bufferSize;
	private int nextBufferSize = bufferSize;

	private DataLine.Info dataLineInfo;
	private TargetDataLine targetDataLine;
	private AudioFormat audioFormat;

	public AudioManager(int initialBufferSize)
	{
		nextBufferSize = initialBufferSize;
		update();
	}

	public void update()
	{
		try
		{
			if(targetDataLine != null)
			{
				targetDataLine.stop();
				targetDataLine.close();
			}

			bufferSize = nextBufferSize;

			audioFormat = new AudioFormat(bufferSize, 16, 2, true, false);
			dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat, DATA_LINE_INFO_BUFFER_SIZE);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
		}
		catch(LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	public byte[] read()
	{
		byte[] buffer = new byte[bufferSize];
		targetDataLine.read(buffer, 0, buffer.length);
		return buffer;
	}

	public void playSound(byte[] buffer)
	{
		if(isMostlyQuiet(buffer))
			return;

		try
		{
			final Clip clip = AudioSystem.getClip();

			clip.addLineListener(new LineListener()
			{
				@Override
				public void update(LineEvent event)
				{
					if(event.getType() == LineEvent.Type.STOP)
						clip.close();
				}
			});

			clip.open(audioFormat, buffer, 0, buffer.length);
			clip.start();
		}
		catch(LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	public void setNextBufferSize(int nextBufferSize)
	{
		this.nextBufferSize = nextBufferSize;
	}

	public int getBufferSize()
	{
		return bufferSize;
	}
}
