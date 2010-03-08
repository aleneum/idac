package totem.sound;

import processing.core.PApplet;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

public class TPlayer {
	Minim minim;
	AudioPlayer player;
	AudioInput in;
	BeatDetect beat;
	TBeatListener bl;
	
	public TPlayer(PApplet parent, String file){
		this.minim = new Minim(parent);
		this.player = this.minim.loadFile(file, 2048);
		this.in = minim.getLineIn(Minim.STEREO, 512);
		beat = new BeatDetect(player.bufferSize(), player.sampleRate());
		beat.setSensitivity(300);
		bl = new TBeatListener(beat, player);
	}
	
	public void loop(){
		this.player.loop();	
	}
	
	public void setGain(float gain){
		this.player.setGain(gain);
	}
	
	public float getInLevel(){
		return this.in.left.level();
	}
	
	//TODO implement with more level of detail
	public boolean beatDetected(){
		return beat.isKick();
	};
	
	public void stop(){
		  player.close();
		  in.close();
		  minim.stop();
	}
	
}
