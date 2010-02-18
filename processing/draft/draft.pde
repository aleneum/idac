import controlP5.*;
import ddf.minim.*;
import ddf.minim.analysis.*;



//sound library stuff
Minim minim;
AudioPlayer player;
BeatDetect beat;
BeatListener bl;

//gui stuff
ControlP5 controlp5;

//my stuff
Visualization currentVis;


public final int INIT_GAIN = 0;
public final String VOLUME_CONTROLLER = "Volume";
public final String TENSION_CONTROLLER = "Tension";
public final String INPUT_CONTROLLER = "Input";
public final String DECAY_CONTROLLER = "Decay";

// size of the window to show
public final int sizeX = 600;
public final int sizeY = 600;

public int gain = INIT_GAIN;
public float tension = 0;
public float decay = 0.01; 
public int input = 0;
public boolean showGUI=true;

public float tensionAcc = 0;

void setup() {
  //Setup GUI
  size(sizeX, sizeY,P2D);
  controlp5 = new ControlP5(this);
  controlp5.addSlider(VOLUME_CONTROLLER,-100,100,INIT_GAIN,10,10,100,14).setId(1);
  controlp5.addSlider(INPUT_CONTROLLER,0,100,input,10,25,100,14).setId(2);
  controlp5.addSlider(DECAY_CONTROLLER,0,1,input,10,40,100,14).setId(3);
  
  controlp5.addNumberbox(TENSION_CONTROLLER, tension, width-110, 10, 100,14).setId(20);

  minim = new Minim(this);
  
  // load a file, give the AudioPlayer buffers that are 1024 samples long
  // player = minim.loadFile("groove.mp3");
  
  // load a file, give the AudioPlayer buffers that are 2048 samples long
  player = minim.loadFile("groove.mp3", 2048);
  beat = new BeatDetect(player.bufferSize(), player.sampleRate());
  beat.setSensitivity(300);
  bl = new BeatListener(beat, player);
  
  // play the file in a loop
  player.loop();
  
  // load visualization
 currentVis = new ChessVisualization();  
}

void draw() {
  
  // paint everything black and repaint gui if activated
  background(0);
  fill(255);
  
  if (beat.isKick()){
      currentVis.kick();
  }
  
  currentVis.draw();
  
  if(showGUI) {
    controlp5.draw();
  }
  
  
  tensionAcc = tensionAcc + 0.00001*(input-tension);
  float diff = 0.01*abs(input-tension)*(tensionAcc);
  tension = tension + diff;
  println(diff);
  
  
  // setVolume
  player.setGain(gain); 
  controlp5.controller(VOLUME_CONTROLLER).setValue(gain);
  controlp5.controller(TENSION_CONTROLLER).setValue(tension);
}

void stop() {
  // always close Minim audio classes when you are done with them
  player.close();
  minim.stop();
  super.stop();
}


// Event handeling below

public void controlEvent(ControlEvent theEvent) {
  //println("got a control event from controller with id "+theEvent.controller().id());
  switch(theEvent.controller().id()) {
    case(1):
      gain = (int)(theEvent.controller().value());
    break;
    case(2):
      input = (int)(theEvent.controller().value());
    break;
    case(3):
      decay = theEvent.controller().value();
    break;
    //default: println("WARNING: Unhandled control5P event!");
  }    
}

void keyPressed() {
  if (key == 'm' || key == 'M') {
    showGUI = !showGUI;
  }
}
