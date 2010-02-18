/**
 * ControlP5 Slider.
 *
 * by andreas schlegel, 2009
 */

import controlP5.*;


ControlP5 controlP5;
controlP5.Label label;
int myColorBackground = color(0,0,0);

int sliderValue = 100;

void setup() {
  size(400,400);
  controlP5 = new ControlP5(this);
  //  controlP5.setControlFont(new ControlFont(createFont("Times",20),20));
  Slider s = controlP5.addSlider("slider",100,167,128,100,160,10,100);
  s = controlP5.addSlider("sliderValue",0,255,128,200,200,10,100);

  label = s.valueLabel();
  label.setControlFont(new ControlFont(createFont("Times",20),20));
  label.setControlFontSize(14);
  label.style().marginTop = -5;
  label = s.captionLabel();
  label.toUpperCase(false);
  label.setControlFont(new ControlFont(createFont("Times",20),20));
  label.set("bla");
  label.style().marginLeft = 20;
  label.style().marginTop = 4;
  //label.style().marginTop = -10;
  //s.captionLabel().setPFont(createFont("Times",20));
}

void draw() {
  background(myColorBackground);
  fill(sliderValue);
  rect(0,0,width,100);
  label.style().marginLeft = sliderValue;
}

void slider(float theColor) {
  myColorBackground = color(theColor);
  println("a slider event. setting background to "+theColor);
}

