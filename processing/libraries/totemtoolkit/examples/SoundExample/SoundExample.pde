import totem.sound.*;
import ddf.minim.Minim;

TPlayer player;	

void setup(){
  // initializes player with a song
  player = new TPlayer(this,"groove.mp3");
  // start to play it in a loop
  player.loop();
}

// We don't have to draw anything. We just listen to music. 
void draw(){
  
}

// Shuts down application for releasing audio sources. 
void stop(){
  player.stop();
  super.stop();
}