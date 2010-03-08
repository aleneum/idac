package totem.sound;

import ddf.minim.AudioSignal;
import ddf.minim.AudioListener;

public class TAudioSignal implements AudioSignal, AudioListener{ 

	float[] left, right;

	public void samples(float[] arg0) {
		left = arg0;
	}

	public void samples(float[] arg0, float[] arg1) {
		left = arg0;
		right = arg1;
	}

	//Sending back.
	public void generate(float[] arg0) {
		System.arraycopy(left, 0, arg0, 0, arg0.length);
	}

	public void generate(float[] arg0, float[] arg1) {
		System.out.println(arg0[0]);
		if (left!=null && right!=null){
			System.arraycopy(left, 0, arg0, 0, arg0.length);
			System.arraycopy(right, 0, arg1, 0, arg1.length);
		}
	}
}

