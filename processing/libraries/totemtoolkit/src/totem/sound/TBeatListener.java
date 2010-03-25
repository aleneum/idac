package totem.sound;

import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.analysis.BeatDetect;

/**
 * Simple wrapper for processing beat detection functionality. It connects the signal
 * with the beat detection.
 * @author alex
 *
 */
public class TBeatListener implements AudioListener {
	private BeatDetect beat;
	private AudioPlayer source;

	/**
	 * When initialized audio signal and beat detection are connected.
	 * From this point of time the rest is handled by the involved processing
	 * objects.
	 * @param beat beat detection object 
	 * @param source audio source to be connected to
	 */
	public TBeatListener(BeatDetect beat, AudioPlayer source)
	{
		this.source = source;
		this.source.addListener(this);
		this.beat = beat;
	}

	/**
	 * Sends the mono audio signal from the audio signal to the beat detection.
	 * This method is usually not called manually! The AudioSignal should do the 
	 * trick.
	 * @param samps samples to be transfered 
	 */
	public void samples(float[] samps)
	{
		beat.detect(source.mix);
	}

	/**
	 * Sends the stereo audio signal from the audio signal to the beat detection.
	 * This method is usually not called manually! The AudioSignal should do the
	 * trick. 
	 * @param sampsL left channel samples to be transfered
	 * @param sampsR right channel samples to be transfered
	 */
	public void samples(float[] sampsL, float[] sampsR)
	{
		beat.detect(source.mix);
	}
}
