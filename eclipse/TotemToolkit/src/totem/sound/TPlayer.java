package totem.sound;

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
	AudioPlayer player;
	AudioInput in;
	BeatDetect beat;
	TBeatListener bl;
	
	/**
	 * Initialize the TPlayer object. the parent object is needed to initialize the
	 * sound framework minim. Later the minim object is held directly and the parent
	 * reference is not used anylonger. The file given will be played if the player
	 * is activated.
	 * @param file mp3 file to be player
	 */
	public TPlayer(PApplet parent, String file){
		this.minim = new Minim(parent);
		this.player = this.minim.loadFile(file, 2048);
		this.in = minim.getLineIn(Minim.STEREO, 512);
		beat = new BeatDetect(player.bufferSize(), player.sampleRate());
		beat.setSensitivity(300);
		bl = new TBeatListener(beat, player);
	}
	
	/**
	 * Starts playing the actual song in a loop.
	 */
	public void loop(){
		this.player.loop();	
	}
	
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
	
	/**
	 * Returns if a beat is detected right now or not. Right now the implementation
	 * just includes lower pitch beat detection and has to be called manually.
	 * @return
	 */
	public boolean beatDetected(){
		return beat.isKick();
	};

	/**
	 * Shut down the TPlayer. This method stops the song and shut down the minim 
	 * framework. It should be called if the application is terminated to avoid locks
	 * of the audio channels.
	 */
	public void shutdown(){
		  player.close();
		  in.close();
		  minim.stop();
	}
}