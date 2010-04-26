import controlP5.*;
import processing.serial.*;
import fullscreen.*; 

import totem.comm.*;
import totem.sound.*;

// gui elements
ControlP5 p5;

// motion values
MotionHandeling motion;
public final String MOTION_LEFT = "Motion Left";
public final String MOTION_RIGHT = "Motion Right";

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
    size(150,150);
    // setup monitor
    p5 = new ControlP5(this);
    p5.addNumberbox(MOTION_LEFT  , 0, 10,  10, 50, 14).setId(1);
    p5.addNumberbox(MOTION_RIGHT , 0, 10,  40, 50, 14).setId(2);
    p5.addNumberbox(SOUND_IN     , 0, 10,  70, 50, 14).setId(3);
    p5.addNumberbox(SONIC_IN     , 0, 10, 100, 50, 14).setId(4);
    
    p5.addNumberbox(LEVEL        , 0, 70,  10, 50, 15).setId(5);
    
    //setup motion
    motion = new MotionHandeling(this);
    state = new TotemState();
    
    //setup sound
    TPlayer player = new TPlayer(this,"../data/groove.mp3");
    input = new AudioInputHandeling(player.getInput());
    output = new AudioOutputHandeling(player);

    // init model
    model = new Model();
    state = new TotemState();
    
    // init serial connection
    communicator = new TSerialCommunicator(this);
    controller = new Controller(communicator,model);
    
    output.addEventListener(controller);
    model.addEventListener(controller);

}

void draw(){
  p5.controller(MOTION_LEFT).setValue(motion.getMotionLeft());
  p5.controller(MOTION_RIGHT).setValue(motion.getMotionRight());
  p5.controller(SOUND_IN).setValue(input.getNoiseLevel());
  p5.controller(SONIC_IN).setValue(sonic);
  p5.controller(LEVEL).setValue(model.getLevel());
  
  input.update();
  output.update();
  motion.update();
  
  // TODO adapt this system
  model.updateLevel(motion.getMotionLeft(),input.getNoiseLevel(),sonic);
  
  // TODO figure out a way to include beat;
 
  state.update(motion.getMotionLeft(), motion.getMotionRight(), motion.getInstantLeft(), motion.getInstantRight(), 
               model.getLevel(), false, input.getNoiseLevel(), input.getInputLevel(), output.getVolume(), sonic);
  
  controller.step(state);
  
  delay(100);
  
}

// This is called by the serial object that was created by TSerialCommunicator
// We just forward it to the communicator and gather it's output
void serialEvent (Serial serial){
  communicator.serialEvent(serial);
  sonic = int(communicator.getActivity());
}
