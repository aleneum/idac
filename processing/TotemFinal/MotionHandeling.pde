import totem.img.*;
import codeanticode.gsvideo.*; //LINUX
//import processing.video.*; // MAC + WIN

public class MotionHandeling {
  public static final float MOTION_LIMIT = 0.5;
  public static final float MOTION_INCREASE = 0.005;
  public static final float MOTION_DECREASE = 0.0001; 
  
  public static final int MOTION_FRAMES = 3;
  public static final int MOTION_CALIB_STEPS = 100;
  
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

  int minMotionRight = 700;
  int minMotionLeft = 500;
  
  int maxMotionRight =1700;
  int maxMotionLeft = 2200;
  
  float decreaseLeft = 0;
  float decreaseRight = 0;

  PImage prevLeft, prevRight;

  float[] limits;

  public MotionHandeling(PApplet aParent){
    this.parent = aParent;
    detection = new TMotionDetection();
    
    // TODO use both cameras; for testing purpose I just use one.
    //videoLeft = new Capture(parent, 320, 240); // Windows
    //videoRight = new Capture(parent, 320, 240); // Windows
    
    videoLeft = new GSCapture(parent, 320, 240, "/dev/video0"); // LINUX
    videoRight = new GSCapture(parent, 320, 240, "/dev/video2"); // LINUX
    
    prevLeft = createImage(videoLeft.width,videoLeft.height,RGB);
    prevRight = createImage(videoRight.width,videoRight.height,RGB);
    
    limits = new float[9];
    
    limits[0] = Model.LEVEL01_LIMIT + 0.05;
    limits[1] = Model.LEVEL02_LIMIT + 0.05;
    limits[2] = Model.LEVEL03_LIMIT + 0.05;
    limits[3] = Model.LEVEL04_LIMIT + 0.05;
    limits[4] = 1;
    limits[5] = 1;
    limits[6] = 1;
    limits[7] = 1;
    limits[8] = 1;
  }
  
  public void update(int level){
    int tmpLeft = 0;
    int tmpRight = 0;
    for (int i=0; i < 10; i++) {
      grabMotion();
      if ( tmpRight < instantRight) tmpRight = instantRight;
      if ( tmpLeft < instantLeft) tmpLeft = instantLeft;
    }
    instantLeft = tmpLeft;
    instantRight = tmpRight;
    checkMotion(level);
  }
  
  public void grabMotion(){
    instantLeft = updateMotion(videoLeft,prevLeft);
    instantRight = updateMotion(videoRight, prevRight);
  }
  
  public float getMotionLeft(){
    return motionLeft;
  }
  
  public float getMotionRight(){
    return motionRight;
  }
  
  public float getInstantLeft(){
    float back = map(instantLeft, minMotionLeft, maxMotionLeft, 0, 1);
    if (back < 0) back = 0;
    if (back > 1) back = 1;
    return back;
  }
  
  public float getInstantRight(){
    float back = map(instantRight,minMotionRight, maxMotionRight,0,1);
    if (back < 0) back = 0;
    if (back > 1) back = 1; 
    return back;
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
  
  private void checkMotion(int level){
    if (this.getInstantLeft() > MOTION_LIMIT) {
      motionLeft += MOTION_INCREASE;
      decreaseLeft = 0;
      
      if (motionLeft > limits[level]){
        motionLeft = limits[level];
      }
    } else {
      decreaseLeft += MOTION_DECREASE;
      motionLeft -= decreaseLeft;
      if (motionLeft < 0) {
        motionLeft = 0;
      }
    }
    
    if (this.getInstantRight() > MOTION_LIMIT) {
      motionRight += MOTION_INCREASE;
      decreaseRight = 0;
      if (motionRight > limits[level]){
        motionRight = limits[level];
      }
    } else {
      decreaseRight += MOTION_DECREASE;
      motionRight -= decreaseRight;
      if (motionRight < 0) {
        motionRight = 0;
      }
    }
  }
  
  public void calibrateLow(){
    println("Calibrate lowest input threshold...");
    minMotionLeft = 0;
    minMotionRight = 0; 
    
    for (int i = 0; i < MOTION_CALIB_STEPS; i++){
      grabMotion();
      minMotionLeft += instantLeft;
      minMotionRight += instantRight;
    }
    
    minMotionLeft /= MOTION_CALIB_STEPS;
    minMotionRight /= MOTION_CALIB_STEPS;
    
    
    minMotionLeft = floor(1.2 * minMotionLeft);
    minMotionRight = floor(1.2 * minMotionRight);
    
    
    println("Calibration done: minMotionLeft: " + minMotionLeft + " minMotionRight: " +  minMotionRight);
    
  }
  
  public void calibrateHigh(){
    println("Calibrate highest input threshold...");
    maxMotionRight = 1;
    maxMotionLeft = 1;
    
    int tmpRight = 0;
    int tmpLeft = 0;
    
    int rightCount = 0;
    int leftCount = 0;
    
    for (int i = 0; i < MOTION_CALIB_STEPS; i++){
      grabMotion();
      if (maxMotionRight < instantRight){
        maxMotionRight = instantRight; 
      }
      if (maxMotionLeft < instantLeft) {
        maxMotionLeft = instantLeft;
      }
    }
    
    for (int i = 0; i < MOTION_CALIB_STEPS; i++){
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
    
    
    println("Calibration done: maxMotionLeft: " + maxMotionLeft + " maxMotionRight: " +  maxMotionRight );
    
  }
  
}
