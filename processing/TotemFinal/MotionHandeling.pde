import totem.img.*;
import codeanticode.gsvideo.*; //LINUX
//import processing.video.*; // MAC + WIN

public class MotionHandeling {
  public static final float MOTION_LIMIT = 0.4;
  public static final float MOTION_INCREASE = 0.01;
  public static final float MOTION_DECREASE = 0.001;
  
  PApplet parent;
  
  TMotionDetection detection;

  GSCapture videoLeft; // LINUX
  GSCapture videoRight; // LINUX

  //Capture videoLeft; // MAC + WIN
  //Capture videoRight; // MAC + WIN

  int maxMotionLeft = 1;
  int maxMotionRight = 1;
  float motionLeft = 0;
  float motionRight = 0;
  float motion = 0;

  int instantLeft = 0;
  int instantRight = 0;

  PImage prevLeft, prevRight;

  public MotionHandeling(PApplet aParent){
    this.parent = aParent;
    detection = new TMotionDetection();
    
    // TODO use both cameras; for testing purpose I just use one.
    videoLeft = new GSCapture(parent, 320, 240, "/dev/video0"); // LINUX
    //videoRight = videoLeft;
    videoRight = new GSCapture(parent, 320, 240, "/dev/video1"); // LINUX
    
    prevLeft = createImage(videoLeft.width,videoLeft.height,RGB);
    prevRight = createImage(videoRight.width,videoRight.height,RGB);
  }
  
  public void update(){
    instantLeft = updateMotion(videoLeft,prevLeft);
    instantRight = updateMotion(videoRight, prevRight);
    if (maxMotionLeft < instantLeft) maxMotionLeft = instantLeft;
    if (maxMotionRight < instantRight) maxMotionRight = instantRight;
    motionLeft = checkMotion(instantLeft, maxMotionLeft, motionLeft);
    motionRight = checkMotion(instantRight, maxMotionRight, motionRight);
  }
  
  public float getMotionLeft(){
    return motionLeft;
  }
  
  public float getMotionRight(){
    return motionRight;
  }
  
  public float getInstantLeft(){
    return float(instantLeft)/maxMotionLeft;
  }
  
  public float getInstantRight(){
    return float(instantRight)/maxMotionRight;
  }
 
  // returns the maximum motion 
  public float getMotion(){
    if (motionLeft > motionRight){
      return motionLeft;
    } else {
      return motionRight;
    }
  }
    
  private int updateMotion(GSCapture video, PImage prevFrame){
    if (video.available()) {
      // Save previous frame for motion detection!!
      prevFrame.copy(video,0,0,video.width,video.height,0,0,video.width,video.height); // Before we read the new frame, we always save the previous frame for comparison!
      prevFrame.updatePixels();
      video.read();
    }
    video.loadPixels();
    prevFrame.loadPixels();
    
    detection.detectMotion(this.parent,prevFrame,video);
    return detection.getMotion();
  }
  
  private float checkMotion(int diff, int maxDiff, float motion){
    if (diff > MOTION_LIMIT * maxDiff) {
      motion += MOTION_INCREASE;
      if (motion > 1) {
        motion = 1;
      }
    } else {
      motion -= MOTION_DECREASE;
        if (motion < 0) {
        motion = 0;
      }
    }
    return motion;
  }
  
}
