 /**
 * controlP5numberbox by andreas schlegel <br />
 * an example to show how to use a numberbox to control <br />
 * variables and events.<br />
 */ 

import controlP5.*;

ControlP5 controlP5;

int myColorBackground = color(0,0,0);

public int numberboxValue = 100;

void setup() {
  size(400,400);
  frameRate(25);
  controlP5 = new ControlP5(this);
  controlP5.addNumberbox("numberbox",100,100,160,100,14);
  controlP5.addNumberbox("numberboxValue",128,100,200,100,14);
}

void draw() {
  background(myColorBackground);
  fill(numberboxValue);
  rect(0,0,width,100);
}

void numberbox(int theColor) {
  myColorBackground = color(theColor);
  println("a numberbox event. setting background to "+theColor);
}
