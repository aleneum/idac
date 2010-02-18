/**
ControlP5 Bang. demonstrates how to use a bang. By default a bang is triggered when 
pressed but this can be changed to release with method setTriggerEvent.
A bang doesnt have a value but only triggers an event that can be received by a
function named after the bang's name or within controlEvent.
by andreas schlegel, 2009
*/

import controlP5.*;

ControlP5 controlP5;
int myColorBackground = color(0,0,0);

void setup() {
  size(400,400);
  frameRate(30);
  controlP5 = new ControlP5(this);
  for(int i=0;i<4;i++) {
    controlP5.addBang("bang"+i,40+i*80,100,40,40).setId(i);
  }
  // change the trigger event, by default it is PRESSED.
  controlP5.addBang("bang",40,200,120,40).setTriggerEvent(Bang.RELEASE);
  controlP5.controller("bang").setLabel("changeBackground");
}

void draw() {
  background(myColorBackground);
}

void bang() {
  int theColor = (int)random(255);
  myColorBackground = color(theColor);
  println("### bang(). a bang event. setting background to "+theColor);
}

void controlEvent(ControlEvent theEvent) {
  println(
  "## controlEvent / id:"+theEvent.controller().id()+
  " / name:"+theEvent.controller().name()+
  " / label:"+theEvent.controller().label()+
  " / value:"+theEvent.controller().value()
  );
}

