/**
* First prototype that use libraries to reduce complexity but still supply same
* amount of functionality. 
*/

import controlP5.*;
import ddf.minim.*;
import processing.serial.*;
import fullscreen.*; 
import codeanticode.gsvideo.*; //LINUX
//import processing.video.*; // MAC + WIN

import totem.comm.*;
import totem.sound.*;
import totem.visual.*;
import totem.img.*;

GSCapture video; // LINUX
//Capture video; // MAC + WIN

// Totem library objects
TMotionDetection detection;
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
public final String MOTION_CONTROLLER = "Motion";
public final String LEVEL_CONTROLLER = "Level";

// size of the window to show
public final int sizeX = 600;
public final int sizeY = 600;

// the starting gain
public int gain = 0;
// tension is raised by serial input
public float tension = 0;
// crowd is the actual noise
public float crowd = 0;
public float inLevel = 0;

//motion detected by camera
public float motion = 0;
public int maxMotion = 1;
public int minMotion = 10000;

public PImage prevFrame;
public float threshold = 150;

// decay defines the decay of tension
public float decay = 0.5; 
// serial input 
public int input = 0;
// toggles the gui
public boolean showGUI=true;
// textColor is a brightness value for the text to show
public int bgColor = 0;
// defines the increase or decrease of the tension in the next loop
public float tensionAcc = 0;


//shows actual level
int level = 0;

// images to show the symbols;
PImage noisePic;
PImage crowdPic;
PImage smilePic;
PImage dancePic;

void setup() {
  //Setup size of the screen
  size(sizeX, sizeY,P2D);
  
  //initialize controlp5 and add gui elements
  controlp5 = new ControlP5(this);
  //sliders on the left side
  controlp5.addSlider(VOLUME_CONTROLLER,-50,50,0,10,10,100,14).setId(1);
  controlp5.addSlider(INPUT_CONTROLLER,0,150,input,10,25,100,14).setId(2);
  controlp5.addSlider(DECAY_CONTROLLER,0,1,decay,10,40,100,14).setId(3);
  controlp5.addSlider(MIC_CONTROLLER,0,1,0,10,55,100,14).setId(4);
  
  // and number boxes on the right
  controlp5.addNumberbox(TENSION_CONTROLLER, tension, width-130, 10, 100,14).setId(20);
  controlp5.addNumberbox(CROWD_CONTROLLER, crowd, width-130, 40, 100,14).setId(21);
  controlp5.addNumberbox(MOTION_CONTROLLER, motion, width-130, 70, 100,14).setId(22);
  controlp5.addNumberbox(LEVEL_CONTROLLER, level, width-130, 100, 100,14).setId(23);
    
  // Initialize and start the audio playback. Starts the microphone input as well. 
  player = new TPlayer(this,"../data/groove.mp3");
  // Song is played in a loop yipeeh :D.
  player.loop();
  
  // Since we have no other visualization we use the text and the square thing.
  currentVis = new TSquareVisualization(this);
  simpleText = new TSimpleText(this);
  
  // Set up serial communication
  communicator = new TSerialCommunicator(this);
  
  // create the font to be used for outputting text
  //myFont = createFont("FFScala", 32);
  //textFont(myFont);
  textAlign(CENTER);
  
  // fullscreen is now available and can be toggled with
  // Windows: Alt+Enter, Ctrl+F
  // Linux: Ctrl+F
  // OS X: ⌘+F
  // ESC: leave fullscreen / exit application
  //fs = new FullScreen(this); 
  
  // load pictures
  noisePic = loadImage("noise.png");
  crowdPic = loadImage("crowd.png");
  smilePic = loadImage("smile.png");
  dancePic = loadImage("dance.png");
  
  //Motion detection
  detection = new TMotionDetection();
  video = new GSCapture(this, 320, 240); // LINUX
  //video = new Capture(this, width, height); // MAC + WIN
  prevFrame = createImage(video.width,video.height,RGB);
}

void draw() {
  background(0);
  inLevel = player.getInLevel();
  drawMotion();
  if(showGUI) {
  	controlp5.controller(VOLUME_CONTROLLER).setValue(gain);
  	controlp5.controller(TENSION_CONTROLLER).setValue(tension);
  	controlp5.controller(CROWD_CONTROLLER).setValue(crowd);
  	controlp5.controller(INPUT_CONTROLLER).setValue(input);
  	controlp5.controller(MIC_CONTROLLER).setValue(inLevel);
        controlp5.controller(MOTION_CONTROLLER).setValue(motion);
        controlp5.controller(LEVEL_CONTROLLER).setValue(level);
        controlp5.draw();
  }
  // check the microphone input from the player object
  
  
  // is showGUI true we draw the GUI. Otherwise it's hidden.
  
   
  // If there is a difference between input and tension (positive or negative)
  // we update tension. First we check if there is a difference...
  if (abs(motion-tension) > 0.1){
  	float sig = 1;
  	
  	//... is it negative? Let's change the sign then.
  	if ((motion-tension) < 0){
        sig = -0.1;
        }
    // Is the tensionAcc working in the right direction?
   	if ((tensionAcc*sig) >= 0) {
   	 // .. if yes: increase it for every step and adapt tension.
     tensionAcc = sig*(abs(tensionAcc) + decay/100);
     
     // we add the acceleration value to the actual tension.
     tension  = tension + tensionAcc;
     
     // tension controls gain. so we adapt this as well
     gain = int(tension*40)-10;
   } else {
     //.. if acc is working in the wrong direction just stop it.
     tensionAcc = 0;
   }
  }
  
  if ((tension < 0.2) && (input > 100)){
    level = 0;
  } else if (tension < 0.2) {
    level = 1;
  } else if (tension < 0.4) {
    level = 2;
  } else if (tension <0.5) {
    level = 3; 
  } else if (tension < 0.7) {
    level = 4;
  } else  if ((tension > 0.7) && (crowd > 0.5)) {
    level = 5;
  } else {
    level = 4;
  }
  
  // adjust volume if the output
  player.setGain(gain);
  
  // the crowd noise increases faster if there is noise than it
  // decreases when there is not
  if (crowd < inLevel) {
    crowd += 0.005; 
  } else {
    crowd -= 0.001;
  }
  
  checkLightLevel(crowd);
  checkLevel(level);  
  
  if (video.available()) {
    // Save previous frame for motion detection!!
    prevFrame.copy(video,0,0,video.width,video.height,0,0,video.width,video.height); // Before we read the new frame, we always save the previous frame for comparison!
    prevFrame.updatePixels();
    video.read();
  }
  
  delay(10);
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
}

// This is called by the serial object that was created by TSerialCommunicator
// We just forward it to the communicator and gather it's output
void serialEvent (Serial serial){
  communicator.serialEvent(serial);
  input = int(communicator.getActivity());
}

// Button m toggles the GUI on or off.
void keyPressed() {
  if (key == 'm' || key == 'M') {
    showGUI = !showGUI;
    println("M pressed: " + showGUI);
  } else if (key == 'l' || key == 'L'){
    level++;
    if (level > 5) {
      level = 0;
    }
  }
}

void checkLevel(int level){
  switch (level){
    case 2: showSecondLevel();
            break;
    case 3: showThirdLevel();
            break;
    case 4: showFourthLevel();
            break;
    case 5: showFifthLevel();
            break;
  }
}

void showSecondLevel(){
  image(dancePic, 150, 120);
}

void showThirdLevel(){
  image(crowdPic, 150, 120);
  showLightBar();
}

void showFourthLevel() {
  image(noisePic, 150, 120);
  showLightBar();
  showNoise();
}

void showFifthLevel() {
  image(smilePic, 150, 120);
  showLightBar();
  showNoise();
}

void drawMotion(){
  int currentMotion;
  int[] pix = detection.detectMotion(this,prevFrame,video);
  currentMotion = detection.getMotion();

  if (currentMotion > maxMotion) {
    maxMotion = currentMotion;
  }

  motion = float(currentMotion) / maxMotion;
  
  int litLights = int(motion * 15);
  StringBuffer buf = new StringBuffer("0000000000000000");
  
  
  if (level >= 3){
    if (player.beatDetected()){
      buf.setCharAt(0,'1');
    }
  } else if (level >= 1) {
    buf.setCharAt(0,'1');
  }
  
  for (int i = 15; i >= (15-litLights); i--){
    buf.setCharAt(i,'1');
  }
  String out = "l"+shuffleString(buf.toString());
  communicator.serialSend(out);  
}

String shuffleString(String input){
  char[] output = {'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
  for (int i=0; i<5; i++){
    if (input.charAt(1+i) > '0') {
      output[9-i] = input.charAt(1+i);
    } else if ( input.charAt(6+i) > '0') {
      output[4-i] = input.charAt(6+i); 
    } else {
      output[14-i] = input.charAt(11+i);
    } 
  }
  
  output[15] = input.charAt(0);
  
  return new String(output);
}



void showLightBar(){
  if (bgColor > 0) {
    bgColor-=20;
  } 
  if (bgColor < 0){
    bgColor = 0;
  }
  fill(bgColor);
  rect(20,460,560,60);
  
  // if a beat was detected by the player object we update the visualization...
  if (player.beatDetected()){
    bgColor = 255;
  }
}

void showNoise(){
   fill(color(0,255,255));
   int bw = int(inLevel*400);
   rect(20, 423-bw, 40, bw);
   rect(width-60, 423-bw, 40, bw);
}

// Shut down the application and close the minim framework (IMPORTANT!)
void stop() {
  // always close Minim audio classes when you are done with them
  player.shutdown();
  super.stop();
}
