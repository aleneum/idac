/**
* First prototype that use libraries to reduce complexity but still supply same
* amount of functionality. 
*/

import controlP5.*;
import ddf.minim.*;
import processing.serial.*;

import totem.comm.*;
import totem.sound.*;
import totem.graphic.*;

import fullscreen.*;  

TPlayer player;
TSerialCommunicator communicator;

//gui stuff
ControlP5 controlp5;

//my stuff
TVisualization currentVis;
TSimpleText simpleText;

// Fullscreen support
FullScreen fs;

// Font for the text to be displayed
PFont myFont;

// Variables for the gui.
public final String VOLUME_CONTROLLER = "Volume (dB)";
public final String TENSION_CONTROLLER = "Tension (%)";
public final String INPUT_CONTROLLER = "Input (%)";
public final String DECAY_CONTROLLER = "Speed (rel)";
public final String MIC_CONTROLLER = "Mic (rel)";
public final String CROWD_CONTROLLER = "Noise (rel)";

// size of the window to show
public final int sizeX = 600;
public final int sizeY = 600;

// the starting gain
public int gain = 0;
// tension is raised by serial input
public float tension = 0;
// crowd is the actual noise
public float crowd = 0;
// decay defines the decay of tension
public float decay = 0.5; 
// serial input 
public int input = 0;
// toggles the gui
public boolean showGUI=true;
// textColor is a brightness value for the text to show
int textColor = 0;
// defines the increase or decrease of the tension in the next loop
public float tensionAcc = 0;

void setup() {
  //Setup size of the screen
  size(sizeX, sizeY,P2D);
  
  //initialize controlp5 and add gui elements
  controlp5 = new ControlP5(this);
  //sliders on the left side
  controlp5.addSlider(VOLUME_CONTROLLER,-50,50,INIT_GAIN,10,10,100,14).setId(1);
  controlp5.addSlider(INPUT_CONTROLLER,0,100,input,10,25,100,14).setId(2);
  controlp5.addSlider(DECAY_CONTROLLER,0,1,decay,10,40,100,14).setId(3);
  controlp5.addSlider(MIC_CONTROLLER,0,1,INIT_GAIN,10,55,100,14).setId(4);
  
  // and number boxes on the right
  controlp5.addNumberbox(TENSION_CONTROLLER, tension, width-110, 10, 100,14).setId(20);
  controlp5.addSlider(CROWD_CONTROLLER, 0, 1, crowd, width-110, 25, 100,14).setId(21);

  // Initialize and start the audio playback. Starts the microphone input as well. 
  player = new TPlayer(this,"groove.mp3");
  // Song is played in a loop yipeeh :D.
  player.loop();
  
  // Since we have no other visualization we use the text and the square thing.
  currentVis = new TSquareVisualization(this);
  simpleText = new TSimpleText(this);
  
  // Set up serial communication
  communicator = new TSerialCommunicator(this);
  
  // create the font to be used for outputting text
  myFont = createFont("FFScala", 32);
  textFont(myFont);
  textAlign(CENTER);
  
  // fullscreen is now available and can be toggled with
  // Windows: Alt+Enter, Ctrl+F
  // Linux: Ctrl+F
  // OS X: ⌘+F
  // ESC: leave fullscreen / exit application
  fs = new FullScreen(this); 
}

void draw() {
  
  // set background color to black and fill color to white
  background(0);
  fill(255);
  
  // if a beat was detected by the player object we update the visualization...
  if (player.beatDetected()){
  	currentVis.kick();
  }
  
  // ... and draw it afterwards
  currentVis.draw();
  
  // and the text with the actual crowd value. This influences the text that will
  // be written.
  simpleText.draw(crowd);
  
  // is showGUI true we draw the GUI. Otherwise it's hidden.
  if(showGUI) {
  	controlp5.controller(VOLUME_CONTROLLER).setValue(gain);
  	controlp5.controller(TENSION_CONTROLLER).setValue(tension);
  	controlp5.controller(CROWD_CONTROLLER).setValue(crowd);
  	controlp5.controller(INPUT_CONTROLLER).setValue(input);
  	controlp5.controller(MIC_CONTROLLER).setValue(inLevel);  
    controlp5.draw();
  }
   
  // If there is a difference between input and tension (positive or negative)
  // we update tension. First we check if there is a difference...
  if (abs(input-tension) > 0.1){
  	int sig = 1;
  	
  	//... is it negative? Let's change the sign then.
  	if ((input-tension) < 0){
      sig = -1;
    }
    // Is the tensionAcc working in the right direction?
   	if ((tensionAcc*sig) >= 0) {
   	 // .. if yes: increase it for every step and adapt tension.
     tensionAcc = sig*(abs(tensionAcc) + decay/100);
     
     // we add the acceleration value to the actual tension.
     tension  = tension + tensionAcc;
     
     // tension controls gain. so we adapt this as well
     gain = int(tension/4)-10;
   } else {
     
     //.. if acc is working in the wrong direction just stop it.
     tensionAcc = 0;
   }
  }
  player.setGain(gain);


  // check the microphone input from the player object
  float inLevel = player.getInLevel();
  
  // the crowd noise increases faster if there is noise than it
  // decreases when there is not
  if (crowd < inLevel) {
    crowd += 0.005; 
  } else {
    crowd -= 0.001;
  }
  checkLightLevel(crowd);
}



// Event handling below

// controlEvent() is called by controlP5 if a slider is touched 
public void controlEvent(ControlEvent theEvent) {
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
    // TODO if all events are defined we should give out warnings as a default
  }    
}

void checkLightLevel(float level){
  int lightlevel = int(level * 15);
  communicator.send
}

// This is called by the serial object that was created by TSerialCommunicator
// We just forward it to the communicator and gather it's output
void serialEvent (Serial serial){
  communicator.serialEvent(serial);
  input = int(communicator.getActivity() * 100);
}

// Button m toggles the GUI on or off.
void keyPressed() {
  if (key == 'm' || key == 'M') {
    showGUI = !showGUI;
  }
}

// Shut down the application and close the minim framework (IMPORTANT!)
void stop() {
  // always close Minim audio classes when you are done with them
  player.stop();
  super.stop();
}