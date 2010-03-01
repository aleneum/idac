/**
 * FastWebcam. 
 * Implements a GStreamer pipeline that removes delay in video capture under Linux.
 * By equinoxefr
 * 
 */

import codeanticode.gsvideo.*;

GSPipeline camera;

void setup() 
{
   size(800, 400,P3D);
   camera = new GSPipeline(this, "v4l2src  ! queue ! "+"video/x-raw-yuv,width=320,height=240");
   background(0);
}

void draw() 
{
    if (camera.available() == true) 
    {
        camera.read();
        set(10,10,camera);
        stroke(255, 100, 100);
        line(170, 100, 170, 140);
        line(150, 120, 190, 120);
    }
}
