import totem.sound.*;
import ddf.minim.Minim;

TPlayer player;	

void setup(){
  player = new TPlayer(this,"groove.mp3");
  player.loop();
}

void draw(){
  
}

void stop(){
  player.stop();
  super.stop();
}