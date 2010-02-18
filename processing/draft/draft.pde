import ddf.minim.*;

AudioPlayer player;
Minim minim;
int gain;

void setup() {
  size(512, 200, P2D);

  minim = new Minim(this);
  
  // load a file, give the AudioPlayer buffers that are 1024 samples long
  // player = minim.loadFile("groove.mp3");
  
  // load a file, give the AudioPlayer buffers that are 2048 samples long
  player = minim.loadFile("groove.mp3", 2048);
  // play the file
  player.play();
  gain=5;
}

void draw() {
  background(0);
  stroke(255);
  // draw the waveforms
  // the values returned by left.get() and right.get() will be between -1 and 1,
  // so we need to scale them up to see the waveform
  // note that if the file is MONO, left.get() and right.get() will return the same value
  for(int i = 0; i < player.left.size()-1; i++)
  {
    line(i, 50 + player.left.get(i)*50, i+1, 50 + player.left.get(i+1)*50);
    line(i, 150 + player.right.get(i)*50, i+1, 150 + player.right.get(i+1)*50);
  }  
  player.setGain(-40+gain); 
}

void stop() {
  // always close Minim audio classes when you are done with them
  player.close();
  minim.stop();
  
  super.stop();
}
