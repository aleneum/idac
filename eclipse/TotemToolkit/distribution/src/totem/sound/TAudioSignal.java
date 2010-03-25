package totem.sound;

import ddf.minim.AudioSignal;
import ddf.minim.AudioListener;

/**
 * Implementation of {@link AudioSignal}. It just stores the received 
 * stereo sound information and make it accessible for other object.
 * @author alex
 *
 */
public class TAudioSignal implements AudioSignal, AudioListener{ 
	
	// arrays for sound information. two channel stereo sound is supported.
	float[] left, right;

	/**
	 * Receives mono sound information and stores it for later generating.
	 * @param monoChannel mono sound information 
	 */
	public void samples(float[] monoChannel) {
		left = monoChannel;
	}

	/**
	 * Receives stereo sound information and stores it for later generating.
	 * @param leftChannel left sound channel
	 * @param rightChannel sound channel
	 */
	public void samples(float[] leftChannel, float[] rightChannel) {
		left = leftChannel;
		right = rightChannel;
	}

	/**
	 * Gives back mono sound information stored in the signal.
	 * @param monoChannel array for storing the information in
	 */
	public void generate(float[] monoChannel) {
		System.arraycopy(left, 0, monoChannel, 0, monoChannel.length);
	}

	/**
	 * Gives back stereo sound information stored in the signal.
	 * @param leftChannel array for storing the information of the left channel in
	 * @param rightChannel array for storing the information of the right channel in 
	 */
	public void generate(float[] leftChannel, float[] rightChannel) {
				if (left!=null && right!=null){
			System.arraycopy(left, 0, leftChannel, 0, leftChannel.length);
			System.arraycopy(right, 0, rightChannel, 0, rightChannel.length);
		}
	}
}

