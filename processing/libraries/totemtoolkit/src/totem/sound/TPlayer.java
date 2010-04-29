package totem.sound;

import java.io.File;
import java.io.IOException;
import java.util.Observer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import processing.core.PApplet;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

/**
 * Wrapper for processing player and beat detection functionality. It can send
 * audio signals to the output and receive them through the microphone.
 * The output signal will be analyzed by the beat detection.
 * @author alex
 *
 */
public class TPlayer {
	Minim minim;
	AudioInput in;
	AudioPlayer player;
	BeatDetect beat;
	TBeatListener bl;
	TPlayerThread runnable;
	SongQueue songs;
	
	/**
	 * Initialize the TPlayer object. the parent object is needed to initialize the
	 * sound framework minim. Later the minim object is held directly and the parent
	 * reference is not used anylonger. The file given will be played if the player
	 * is activated.
	 * @param file mp3 file to be player
	 */
	public TPlayer(PApplet parent, String file){
		this.minim = new Minim(parent);
		this.in = minim.getLineIn(Minim.STEREO, 512);
		this.player = minim.loadFile(file);
		
		beat = new BeatDetect(this.player.bufferSize(), 44100.0f);
		beat.detectMode(BeatDetect.FREQ_ENERGY);
		beat.setSensitivity(1);
		bl = new TBeatListener(beat, this.player);
	}
	
	public SongQueue getSongs() {
		return this.songs;
	}
	
	/** Returns the audio input source offered by minim. 
	 * 
	 */
	public AudioInput getInput(){
		return this.in;
	};

	// TODO let this method break to eliminate calls
	/**
	 * Starts playing the actual song in a loop.
	 */
	public void play(){
		this.player.loop();
	}
	
	//TODO ganz machen
	/**
	 * Set the volume of the output.
	 * @param gain the new gain volume
	 */
	public void setGain(float gain){
		this.player.setGain(gain);
	}
	
	/**
	 * Returns the actual input noise level.
	 * @return actual microphone noise level
	 */
	public float getInLevel(){
		return this.in.left.level();
	}
	
	public float getOutLevel(){
		return this.player.left.level();
	}
	
	/**
	 * Returns if a beat is detected right now or not. Right now the implementation
	 * just includes lower pitch beat detection and has to be called manually.
	 * @return
	 */
	public boolean beatDetected(){
		boolean is = this.beat.isKick();
		return is;
	};

	
	public byte[] loadSample(String fileString) {
		AudioInputStream ain;
		File file = new File(fileString);
		byte bs[] = new byte[0];
		try {
			ain = AudioSystem.getAudioInputStream(file);
			// How long, in *samples*, is the file?
			long len = ain.getFrameLength();

			// 16-bit audio means 4 bytes per sample, so we need
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
	
	//TODO ganz machen
	/**
	 * Shut down the TPlayer. This method stops the song and shut down the minim 
	 * framework. It should be called if the application is terminated to avoid locks
	 * of the audio channels.
	 */
	public void shutdown(){
		  //player.close();
		  in.close();
		  minim.stop();
	}
	
	public void addAudioObserver(Observer o){
		this.player.addAudioObserver(o);
	}
	
	public void addToQueue(String file){
		byte[] sample = this.loadSample(file);
		if (sample.length > 0){
			this.player.getPlayList().add(sample);
		}		
	}
}