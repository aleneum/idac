package totem.sound;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import ddf.minim.javasound.*;

import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioSource;

public class TPlayerThread implements Runnable {

	private AudioFormat stdFormat;
	private DataLine.Info info;
	private SourceDataLine sdl;
	private TargetDataLine tdl;
	private byte[] currentRaw;
	private TPlayer parent;
	private float minAmp,maxAmp;
	private FloatSampleBuffer buffer;

	// Write at most this many bytes per inner loop execution
	static public final int innerLoopWriteSize = 512;

	// The position within the currently playing sound
	// (i.e. the next sample to write)
	int cursor = 0;


	public TPlayerThread(TPlayer aPlayer) {
		this.parent = aPlayer;
		this.currentRaw = loadSample((String) this.parent.getSongs().get());

		/*AudioFormat(AudioFormat.Encoding encoding, float sampleRate, 
		 *			int sampleSizeInBits, int channels, int frameSize, 
		 *			float frameRate, boolean bigEndian) 
		 */
		stdFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f,
				16, 2, 4, 44100.0f, false);
		info = new DataLine.Info(SourceDataLine.class, stdFormat);
		try {
			sdl = (SourceDataLine)AudioSystem.getLine(info);
			sdl.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			System.out.println("Not available");
			e.printStackTrace();
			
		}

		this.buffer = new FloatSampleBuffer(sdl.getFormat().getChannels(), innerLoopWriteSize/this.sdl.getFormat().getFrameSize(), sdl.getFormat().getSampleRate());

		this.minAmp = 100;
		this.maxAmp = 1;
	}

	public byte[] loadSample(String fileString) {
		AudioInputStream ain;
		File file = new File(fileString);
		byte bs[] = new byte[0];
		try {
			ain = AudioSystem.getAudioInputStream(file);
			// How long, in *samples*, is the file?
			long len = ain.getFrameLength();

			// 16-bit audio means 2 bytes per sample, so we need
			// a byte array twice as long as the number of samples
			bs = new byte[(int) len * 4];

			// Read everything, and make sure we got it all
			int r = ain.read(bs);
			if (r != len * 4)
				throw new RuntimeException("Read only " + r + " of " + file
						+ " out of " + (len * 4) + " sound bytes");
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("could not load " + file.getAbsolutePath());
			e.printStackTrace();
		}
		return bs;
	}

	public int getBufferSize() {
		return this.sdl.getBufferSize();
	}

	public synchronized float getLevel() {		
		float level = 0;	

		int towrite = innerLoopWriteSize;
		if (towrite > currentRaw.length-cursor) {
			towrite = currentRaw.length-cursor;
		}
		byte[] raw = Arrays.copyOfRange(currentRaw, cursor, cursor+towrite);
		buffer.setSamplesFromBytes(raw, 0, sdl.getFormat(), 0, towrite/this.sdl.getFormat().getFrameSize());
		float[] samples = buffer.getChannel(0);
		for (int i = 0; i < samples.length; i++) {
			level += (samples[i] * samples[i]);
		}

		level /= samples.length;
		level = (float) Math.sqrt(level);
		System.out.println(level);
		return level;
	}


	public void setVolume(float vol){
		if( sdl.isControlSupported( FloatControl.Type.MASTER_GAIN ) ) {
			FloatControl volume = (FloatControl) sdl.getControl( FloatControl.Type.MASTER_GAIN );
			volume.setValue(vol);
		} else {
			System.out.println("Volume Control is not supported");
		}
	}

	public void run() {
		sdl.start();
		while (true) {

			// How many bytes are the left to write from this snippet?
			int bytesLeft = currentRaw.length - cursor;

			// If we've reached the end, start from the top
			// of the sound
			if (bytesLeft <= 0) {
				// restart sound

				if (this.parent.getSongs().numWaiting() > 0){
					currentRaw = loadSample((String) this.parent.getSongs().get());
				}
				cursor = 0;
				bytesLeft = currentRaw.length;
			}

			// Don't write more than this in one loop
			int towrite = innerLoopWriteSize;
			if (towrite > bytesLeft)
				towrite = bytesLeft;

			// Write a chunk

			int r = sdl.write(currentRaw, cursor, towrite);
			// Remember how much we wrote, by advancing the cursor
			// to the next chunk of sound
			cursor += r;
		}
	}

	public void printInfoMixers(){
		Mixer.Info[] infoMixers = AudioSystem.getMixerInfo();
		if(infoMixers!=null && infoMixers.length>0){
			for (int i = 0; i < infoMixers.length; i++) {
				Mixer.Info info = infoMixers[i];
				Mixer objMixer = AudioSystem.getMixer(info);
				try {
					objMixer.open();
					Line.Info[] lineasCompatibles = objMixer.getSourceLineInfo();
					Line.Info[] targetsCompatibles = objMixer.getTargetLineInfo();
					Line[] lineas = objMixer.getTargetLines();
					System.out.println("Mixer: " + info.getName());
					System.out.println("===================================================================================================");
					for (int j = 0; j < lineasCompatibles.length; j++) {
						System.out.println("Source " + j + " - " + lineasCompatibles[j]);
					}
					for (int j = 0; j < targetsCompatibles.length; j++) {
						System.out.println("Target " + j + " - " + targetsCompatibles[j]);
					}
					for (int j = 0; j < lineas.length; j++) {
						System.out.println("Line " + j + " - " + lineas[j].getLineInfo());
					}
					System.out.println("===================================================================================================");
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}
