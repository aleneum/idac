/**
 * TestPipelines. 
 * By Andres Colubri
 * 
 * This example shows how to create GStreamer pipelines using the GSPipeline object 
 */

import codeanticode.gsvideo.*;

GSPipeline pipe;

void setup()
{
  size(720, 480);
  
  // VideoTestSrc pipeline  
  //pipe = new GSPipeline(this, "videotestsrc pattern=0 ! ffmpegcolorspace ! video/x-raw-rgb, width=320, height=240, bpp=32, depth=24");
  
  // Other pipelines:
  
  // DirectShow capture pipeline.
  pipe = new GSPipeline(this, "taa_dsvideosrc ! decodebin ! ffmpegcolorspace ! video/x-raw-rgb, bpp=32, depth=24");

  // Vide4Linux2 capture pipeline.    
  //pipe = new GSPipeline(this, "v4l2src ! ffmpegcolorspace ! video/x-raw-rgb, width=320, height=240, bpp=32, depth=24");    
    
  // dv1394 capture pipeline (doesn't work, though).
  //pipe = new GSPipeline(this, "dv1394src port=0 ! queue ! dvdemux ! ffdec_dvvideo ! ffmpegcolorspace ! video/x-raw-yuv, width=720");
}

void draw() {
  if (pipe.available() == true) {
    pipe.read();
    image(pipe, 0, 0);
  }
}