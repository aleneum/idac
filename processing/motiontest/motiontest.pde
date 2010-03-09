import totem.img.*;

import codeanticode.gsvideo.*; //LINUX
//import processing.video.*; // MAC + WIN

GSCapture video; // LINUX
//Capture video; // MAC + WIN

TMotionDetection detection;

// Previous Frame
PImage prevFrame;
float threshold = 150;

void setup() {
  size(320,240);
  frameRate(30);
  detection = new TMotionDetection();
  
  video = new GSCapture(this, width, height); // LINUX
  //video = new Capture(this, width, height); // MAC + WIN
  
  
  prevFrame = createImage(video.width,video.height,RGB);
}

void draw() {
  
  // Capture video
  if (video.available()) {
    // Save previous frame for motion detection!!
    prevFrame.copy(video,0,0,video.width,video.height,0,0,video.width,video.height); // Before we read the new frame, we always save the previous frame for comparison!
    prevFrame.updatePixels();
    video.read();
  }
  
  loadPixels();
  video.loadPixels();
  prevFrame.loadPixels();
  
  int[] pix = detection.detectMotion(this,prevFrame,video);
  arraycopy(pix,pixels);
  updatePixels();
}

