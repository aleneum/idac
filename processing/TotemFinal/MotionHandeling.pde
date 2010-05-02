import totem.img.*;
import codeanticode.gsvideo.*; //LINUX
//import processing.video.*; // MAC + WIN

public class MotionHandeling {
  public static final float MOTION_LIMIT = 0.4;
  public static final float MOTION_INCREASE = 0.005;
  public static final float MOTION_DECREASE = 0.001;
  
  PApplet parent;
  
  TMotionDetection detection;

  GSCapture videoLeft; // LINUX
  GSCapture videoRight; // LINUX

  //Capture videoLeft; // MAC + WIN
  //Capture videoRight; // MAC + WIN

  float motionLeft = 0;
  float motionRight = 0;
  float motion = 0;

  int instantLeft = 0;
  int instantRight = 0;

  int minMotionRight = 0;
  int minMotionLeft = 0;
  
  int maxMotionRight = 1;
  int maxMotionLeft = 1;

  PImage prevLeft, prevRight;

  public MotionHandeling(PApplet aParent){
    this.parent = aParent;
    detection = new TMotionDetection();
    
    // TODO use both cameras; for testing purpose I just use one.
    videoLeft = new GSCapture(parent, 320, 240, "/dev/video0"); // LINUX
    //videoRight = videoLeft;
    videoRight = new GSCapture(parent, 320, 240, "/dev/video2"); // LINUX
    
    prevLeft = createImage(videoLeft.width,videoLeft.height,RGB);
    prevRight = createImage(videoRight.width,videoRight.height,RGB);
  }
  
  public void update(){
    int tmpLeft = 0;
    int tmpRight = 0;
    for (int i=0; i < 10; i++) {
      grabMotion();
      if ( tmpRight < instantRight) tmpRight = instantRight;
      if ( tmpLeft < instantLeft) tmpLeft = instantLeft;
    }
    instantLeft = tmpLeft;
    instantRight = tmpRight;
    checkMotion();
  }
  
  public void grabMotion(){
    instantLeft = updateMotion(videoLeft,prevLeft);
    instantRight = updateMotion(videoRight, prevRight);
    if (maxMotionLeft < instantLeft) maxMotionLeft = instantLeft;
    if (maxMotionRight < instantRight) maxMotionRight = instantRight;
  }
  
  public float getMotionLeft(){
    return motionLeft;
  }
  
  public float getMotionRight(){
    return motionRight;
  }
  
  public float getInstantLeft(){
    return map(instantLeft, minMotionLeft, maxMotionLeft, 0, 1);
  }
  
  public float getInstantRight(){
    return map(instantRight,minMotionRight, maxMotionRight,0,1);
  }
 
  // returns the maximum motion 
  public float getMotion(){
    if (motionLeft > motionRight){
      return motionLeft;
    } else {
      return motionRight;
    }
  }

  public void resetMotion() {
    motionLeft = 0;
    motionRight = 0;
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
  
  private void checkMotion(){
    if (this.getInstantLeft() > MOTION_LIMIT) {
      motionLeft += MOTION_INCREASE;
      if (motionLeft > 1){
        motionLeft = 1;
      }
    } else {
      motionLeft -= MOTION_DECREASE;
      if (motionLeft < 0) {
        motionLeft = 0;
      }
    }
    
    if (this.getInstantRight() > MOTION_LIMIT) {
      motionRight += MOTION_INCREASE;
      if (motionRight > 1){
        motionRight = 1;
      }
    } else {
      motionRight -= MOTION_DECREASE;
      if (motionRight < 0) {
        motionRight = 0;
      }
    }
  }
  
  public void calibrateLow(){
    minMotionLeft = 0;
    minMotionRight = 0; 
    
    for (int i = 0; i < 1000; i++){
      grabMotion();
      minMotionLeft += instantLeft;
      minMotionRight += instantRight;
    }
    
    minMotionLeft /= 1000;
    minMotionRight /= 1000;
    
    
    minMotionLeft = floor(1.2 * minMotionLeft);
    minMotionRight = floor(1.2 * minMotionRight);
    
    
    println("minMotionLeft: " + minMotionLeft + " minMotionRight: " +  minMotionRight);
    
  }
  
  public void calibrateHigh(){
    maxMotionRight = 1;
    maxMotionLeft = 1;
    
    int tmpRight = 0;
    int tmpLeft = 0;
    
    int rightCount = 0;
    int leftCount = 0;
    
    for (int i = 0; i < 1000; i++){
      grabMotion();
      if (maxMotionRight < instantRight){
        maxMotionRight = instantRight; 
      }
      if (maxMotionLeft < instantLeft) {
        maxMotionLeft = instantLeft;
      }
    }
    
    for (int i = 0; i < 1000; i++){
      grabMotion();
      if ((maxMotionRight * 0.4) < instantRight){
        tmpRight += instantRight;
        rightCount ++;
      }
    
      if ((maxMotionLeft * 0.4) < instantLeft){
        tmpLeft += instantLeft;
        leftCount ++;
      }
    }
    
    if (rightCount > 0) {
      maxMotionRight = tmpRight / rightCount;
    } else {
      maxMotionRight = 100000;
    }
    if (leftCount > 0) {
      maxMotionLeft = tmpLeft / leftCount;
    } else {
      maxMotionLeft = 100000;
    }
    
    
    println("maxMotionLeft: " + maxMotionLeft + " maxMotionRight: " +  maxMotionRight );
    
  }
  
}
