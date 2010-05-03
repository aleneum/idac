import controlP5.*;
import processing.serial.*;
import fullscreen.*; 

import totem.comm.*;
import totem.sound.*;

public static final int UPDATE_MILLIS = 100;
int lastChecked = 0;

// gui elements
ControlP5 p5;

// motion values
MotionHandeling motion;
public final String MOTION_LEFT = "Motion Left";
public final String MOTION_RIGHT = "Motion Right";

public final String INSTANT_LEFT = "Instant Left";
public final String INSTANT_RIGHT = "Instant Right"; 

// mic values
AudioInputHandeling input;
public final String SOUND_IN = "Sound Input";

// sound output
AudioOutputHandeling output;

// data model
Model model;
TotemState state;
public final String LEVEL = "Level";


// serial communication
TSerialCommunicator communicator;
int sonic = 0;
public final String SONIC_IN = "Sonic Input";

// controller
Controller controller;

void setup(){
    size(300,600);
    // setup monitor
    p5 = new ControlP5(this);
    p5.addNumberbox(MOTION_LEFT  , 0, 10,  10, 50, 14).setId(1);
    p5.addNumberbox(MOTION_RIGHT , 0, 10,  40, 50, 14).setId(2);
    p5.addNumberbox(SOUND_IN     , 0, 10,  70, 50, 14).setId(3);
    p5.addNumberbox(SONIC_IN     , 0, 10, 100, 50, 14).setId(4);
    
    
    p5.addNumberbox(INSTANT_LEFT   , 0, 70,  10, 50, 14).setId(5);
    p5.addNumberbox(INSTANT_RIGHT  , 0, 70,  40, 50, 14).setId(6);
    p5.addNumberbox(LEVEL          , 0, 70,  70, 50, 15).setId(7);
    
    //setup motion
    motion = new MotionHandeling(this);
    state = new TotemState();
    
    //setup sound
    TPlayer player = new TPlayer(this,"../data/silence.wav");
    input = new AudioInputHandeling(player.getInput());
    output = new AudioOutputHandeling(player);

    // init model
    model = new Model();
    state = new TotemState();
    
    // init serial connection
    communicator = new TSerialCommunicator(this);
    controller = new Controller(communicator,model);
    
    output.addObserver(controller);
    model.addObserver(controller);
    model.addObserver(output);
    player.addAudioObserver(controller);
    noLoop();
}

void draw(){
  p5.controller(MOTION_LEFT).setValue(motion.getMotionLeft());
  p5.controller(MOTION_RIGHT).setValue(motion.getMotionRight());
  p5.controller(INSTANT_LEFT).setValue(motion.getInstantLeft());
  p5.controller(INSTANT_RIGHT).setValue(motion.getInstantRight());

  p5.controller(SOUND_IN).setValue(input.getNoiseLevel());
  p5.controller(SONIC_IN).setValue(sonic);
  p5.controller(LEVEL).setValue(model.getLevel());
 
 if (model.getLevel() < 5) {
  
  input.update();
  motion.update(model.getLevel());
  
 }
  // TODO adapt this system
  
  // TODO figure out a way to include beat;
 
  state.update(motion.getMotionLeft(), motion.getMotionRight(), motion.getInstantLeft(), motion.getInstantRight(), 
               model.getLevel(), false, input.getNoiseLevel(), input.getInputLevel(), output.getVolume(), sonic);
  
  if (millis() > lastChecked + UPDATE_MILLIS) {
    output.refresh();
    model.updateLevel(motion.getMotionLeft(), motion.getMotionRight(), input.getNoiseLevel(),sonic);
    controller.step(state);
    lastChecked = millis();
    
    if (model.getLevel() == 8){
      this.motion.resetMotion();
    }
  }
  delay(10);  
}

// This is called by the serial object that was created by TSerialCommunicator
// We just forward it to the communica tor and gather it's output
void serialEvent (Serial serial){
  //communicator.serialEvent(serial);
  String inString = serial.readStringUntil('\n');
  println(inString);
  //sonic = int(communicator.getActivity());
}

void keyPressed() {
  if (key == 'l') {
    this.motion.calibrateLow();
  } else if (key == 'h') {
    this.motion.calibrateHigh();
  } else if (key == 's') {
    controller.init();
    println("start loop");
    loop();
  }
}
