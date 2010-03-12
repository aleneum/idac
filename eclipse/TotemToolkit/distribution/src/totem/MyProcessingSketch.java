package totem;
import processing.core.PApplet;
import totem.sound.TPlayer;

public class MyProcessingSketch extends PApplet {

	TPlayer player;
	

	public void setup(){
		player = new TPlayer(this,"groove.mp3");
	}	

	public void draw(){
  
	}

}