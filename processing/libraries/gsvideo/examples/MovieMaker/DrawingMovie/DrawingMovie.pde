/**
 * GSVideo drawing movie example.
 *
 * Adapted from Daniel Shiffman's original Drawing Movie 
 * example by Andres Colubri 
 * Makes a movie of a line drawn by the mouse. Press
 * the spacebar to finish and save the movie. 
 */

import codeanticode.gsvideo.*;

GSMovieMaker mm;

int fps = 30;

void setup() {
  size(320, 240);
  frameRate(fps);

  // Save as THEORA, in a OGG container. Options at this point are THEORA, XVID and X264,
  // but some of them might not work because the required gstreamer codec is not installed.  
  // Important: Be sure of using the same framerate as the one set with frameRate(). 
  // If the sketch's framerate is higher than the speed with which GSMovieMaker 
  // can compress frames and save them to file, then the computer's RAM will start to become 
  // clogged with unprocessed frames waiting on the gstreamer's queue. If all the physical RAM 
  // is exhausted, then the whole system might become extremely slow and unresponsive.
  // Using the same framerate as in the frameRate() function seems to be a reasonable choice,
  // assuming that CPU can keep up with encoding at the same pace with which Processing sends
  // frames (which might not be the case is the CPU is slow). As the resolution increases, 
  // encoding becomes more costly and the risk of clogging the computer's RAM increases.    
  mm = new GSMovieMaker(this, width, height, "drawing.ogg", GSMovieMaker.THEORA, GSMovieMaker.MEDIUM, fps);
  mm.start();
  
  background(160, 32, 32);
}

void draw() {
  stroke(7, 146, 168);
  strokeWeight(4);

  // Draw if mouse is pressed
  if (mousePressed && pmouseX != 0 && mouseY != 0) {
    line(pmouseX, pmouseY, mouseX, mouseY);
  }
  loadPixels();
  // Add window's pixels to movie
  mm.addFrame(pixels);
}

void keyPressed() {
  if (key == ' ') {
    // Finish the movie if space bar is pressed
    mm.finish();
    // Quit running the sketch once the file is written
    exit();
  }
}
