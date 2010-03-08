package totem.sound;

import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.analysis.BeatDetect;

public class TBeatListener implements AudioListener {
	private BeatDetect beat;
	private AudioPlayer source;

	public TBeatListener(BeatDetect beat, AudioPlayer source)
	{
		this.source = source;
		this.source.addListener(this);
		this.beat = beat;
	}

	public void samples(float[] samps)
	{
		beat.detect(source.mix);
	}

	public void samples(float[] sampsL, float[] sampsR)
	{
		beat.detect(source.mix);
	}
}
