
import totem.img.*;
import codeanticode.gsvideo.*; //LINUX
//import processing.video.*; // MAC + WIN

TMotionDetection detection;

GSCapture videoLeft; // LINUX
GSCapture videoRight // LINUX

//Capture videoLeft; // MAC + WIN
//Capture videoRight; // MAC + WIN

int maxMotion = 0;
int motionLeft = 0;
int motionRight = 0;
float motion = 0;

PImage prevLeft, prevRight;


class MotionHandeling{

  public MotionHandeling(){
    detection = new TMotionDetection();
    
    // TODO use both cameras; for testing purpose I just use one.
    videoLeft = new GSCapture(this, 320, 240); // LINUX
    videoRight = videoLeft;
    //videoRight = new GSCapture(this, 320, 240); // LINUX
    
    prevLeft = createImage(videoLeft.width,videoLeft.height,RGB);
    prevRight = createImage(videoRight.width,videoRight.height,RGB);
  }
  
  public update(){
    motionLeft = detection.detectMotion(this, prevLeft , videoLeft );
    motionRight = detection.detectMotion(this, prevRight, videoRight);
  }
}
