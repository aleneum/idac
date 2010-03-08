/**
 * Capture2File. 
 * By Andres Colubri
 * 
 * This example shows how to create a pipeline to capture video into a movie file. 
 */

import codeanticode.gsvideo.*;

GSPipeline pipe;

void setup()
{
  size(640, 480);
  frameRate(30);
  
  // Windows capture.
  pipe = new GSPipeline(this, "ksvideosrc ! ffmpegcolorspace ! video/x-raw-yuv, width=640, height=480, bpp=32, depth=24 ! queue ! videorate ! video/x-raw-yuv, framerate=25/1 ! xvidenc ! queue ! avimux ! queue ! filesink location=C:\\Users\\Andres\\test.avi");

  // Linux capture.
  //pipe = new GSPipeline(this, "v4l2src ! ffmpegcolorspace ! video/x-raw-yuv, width=640, height=480, bpp=32, depth=24 ! queue ! videorate ! video/x-raw-yuv, framerate=25/1 ! xvidenc ! queue ! avimux ! queue ! filesink location=/home/andres/test.avi");
    
  // OSX capture.
  //pipe = new GSPipeline(this, "osxvideosrc ! ffmpegcolorspace ! video/x-raw-yuv, width=640, height=480, bpp=32, depth=24 ! queue ! videorate ! video/x-raw-yuv, framerate=25/1 ! xvidenc ! queue ! avimux ! queue ! filesink location=/home/andres/test.avi");
}

void draw() {
  background(0);
}

void keyPressed() {
  pipe.stop();  
}