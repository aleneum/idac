import controlP5.*;
import ddf.minim.*;
import processing.serial.*;
import fullscreen.*; 

import totem.comm.*;
import totem.sound.*;
import totem.visual.*;
import totem.img.*;

ControlP5 p5;

// motion values
int motionLeft = 0;
int motionRight = 0;
float motion = 0;

public final String MOTION_LEFT = "Motion Left";
public final String MOTION_RIGHT = "Motion  Right";
public final String MOTION_STATE = "Motion State";

void setup{
    // setup monitor
    p5 = new ControlP5(this);
    p5.addNumberbox(MOTION_LEFT , motionLeft , 10, 10, 50, 14).setId(1);
    p5.addNumberbox(MOTION_LEFT , motionRight, 10, 40, 50, 14).setId(2);
    p5.addNumberbox(MOTION_STATE, motion     , 10, 40, 70, 14).setId(3);
}

void draw{


}
