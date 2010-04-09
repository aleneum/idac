import processing.core.*; 
import processing.xml.*; 

import controlP5.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import processing.serial.*; 
import ddf.minim.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class draft extends PApplet {







//sound library stuff
Minim minim;
AudioPlayer player;
AudioInput in;
BeatDetect beat;
BeatListener bl;
ArduinoCommunicator communicator;

//gui stuff
ControlP5 controlp5;

//my stuff
Visualization currentVis;

PFont myFont;


public final int INIT_GAIN = 0;
public final String VOLUME_CONTROLLER = "Volume (dB)";
public final String TENSION_CONTROLLER = "Tension (%)";
public final String INPUT_CONTROLLER = "Input (%)";
public final String DECAY_CONTROLLER = "Speed (rel)";
public final String MIC_CONTROLLER = "Mic (rel)";
public final String CROWD_CONTROLLER = "Noise (rel)";

// size of the window to show
public final int sizeX = 600;
public final int sizeY = 600;

public int gain = INIT_GAIN;
public float tension = 0;
public float crowd = 0;
public float decay = 0.5f; 
public int input = 0;
public boolean showGUI=true;

int textColor = 0;

public float tensionAcc = 0;

public void setup() {
  //Setup GUI
  size(sizeX, sizeY,P2D);
  controlp5 = new ControlP5(this);
  controlp5.addSlider(VOLUME_CONTROLLER,-50,50,INIT_GAIN,10,10,100,14).setId(1);
  controlp5.addSlider(INPUT_CONTROLLER,0,100,input,10,25,100,14).setId(2);
  controlp5.addSlider(DECAY_CONTROLLER,0,1,decay,10,40,100,14).setId(3);
  controlp5.addSlider(MIC_CONTROLLER,0,1,INIT_GAIN,10,55,100,14).setId(4);
  
  controlp5.addNumberbox(TENSION_CONTROLLER, tension, width-110, 10, 100,14).setId(20);
  controlp5.addSlider(CROWD_CONTROLLER, 0, 1, crowd, width-110, 25, 100,14).setId(21);

  minim = new Minim(this);
  
  // load a file, give the AudioPlayer buffers that are 1024 samples long
  // player = minim.loadFile("groove.mp3");
  
  // load a file, give the AudioPlayer buffers that are 2048 samples long
  player = minim.loadFile("groove.mp3", 2048);
  in = minim.getLineIn(Minim.STEREO, 512);
  
  beat = new BeatDetect(player.bufferSize(), player.sampleRate());
  beat.setSensitivity(300);
  bl = new BeatListener(beat, player);
  
  // play the file in a loop
  player.loop();
  
  // load visualization
 currentVis = new ChessVisualization();  
 
 //communicator = new ArduinoCommunicator(this);
 
 myFont = createFont("FFScala", 32);
 textFont(myFont);
 textAlign(CENTER);
}

public void draw() {
  
  // paint everything black and repaint gui if activated
  background(0);
  fill(255);
  
  if (beat.isKick()){
      currentVis.kick();
  }
  
  currentVis.draw();
  
  if(showGUI) {
    controlp5.draw();
  }
  
  if (abs(input-tension)>0.1f){
    int sig = 1;
    if ((input-tension) < 0){
      sig = -1;
    }
   if ((tensionAcc*sig) >= 0) {
     tensionAcc = sig*(abs(tensionAcc) + decay/100);
     tension  = tension + tensionAcc;
     gain = PApplet.parseInt(tension/4)-10;
     println (tensionAcc);
   } else {
     tensionAcc = 0;
   }
  }
  
  //input = int(communicator.getActivity() * 100);
  
  float inLevel = in.left.level();
  
  if (crowd < inLevel) {
    crowd += 0.005f; 
  } else {
    crowd -= 0.001f;
  }
  
  // setVolume
  player.setGain(gain);
  controlp5.controller(VOLUME_CONTROLLER).setValue(gain);
  controlp5.controller(TENSION_CONTROLLER).setValue(tension);
  controlp5.controller(CROWD_CONTROLLER).setValue(crowd);
  controlp5.controller(INPUT_CONTROLLER).setValue(input);
  controlp5.controller(MIC_CONTROLLER).setValue(inLevel);
  
  
  //just mess around
  
  fill(textColor);
  if (textColor >= 255) {
    textColor = 0;  
  } else {
    textColor+=4;
  }
  
  String outText = "error";
  
  if (crowd < 0.2f) {
    outText = "LAME";
  } else if (crowd < 0.4f) {
    outText = "YEAH, GOOD WORK";
  } else if (crowd < 0.6f) {
    outText = "PARTY HARD!";
  } else if (crowd < 0.8f) {
    outText = "IT'S ON!!";  
  } else if (crowd < 0.9f) {
    outText = "AWESOME!!!";
  } else {
    outText = "O N   F I R E ! ! !";
  }
  
  text(outText,width/2,height/2);
  
}



// Event handeling below

public void controlEvent(ControlEvent theEvent) {
  //println("got a control event from controller with id "+theEvent.controller().id());
  switch(theEvent.controller().id()) {
    case(1):
      gain = (int)(theEvent.controller().value());
    break;
    case(2):
      input = (int)(theEvent.controller().value());
    break;
    case(3):
      decay = theEvent.controller().value();
    break;
    //default: println("WARNING: Unhandled control5P event!");
  }    
}

//forwarding serialEvent to communicator
public void serialEvent (Serial serial){
  communicator.serialEvent(serial);
}

public void keyPressed() {
  if (key == 'm' || key == 'M') {
    showGUI = !showGUI;
  }
}

public void stop() {
  // always close Minim audio classes when you are done with them
  player.close();
  in.close();
  minim.stop();
  super.stop();
}


public class ArduinoCommunicator{

Serial port;
float value = 0;

ArduinoCommunicator(PApplet parent) {
  println(Serial.list());
  port = new Serial(parent, Serial.list()[0], 9600);
  port.bufferUntil('\n');
}

public void serialEvent (Serial myPort){
  String inString = port.readStringUntil('\n');
  if (inString != null) {
    value = PApplet.parseFloat(inString);
  }
}

public float getActivity(){
  return value;
}

}
class BeatListener implements AudioListener
{
  private BeatDetect beat;
  private AudioPlayer source;
  
  BeatListener(BeatDetect beat, AudioPlayer source)
  {
    this.source = source;
    this.source.addListener(this);
    this.beat = beat;
  }
  
  public void samples(float[] samps)
  {
    beat.detect(source.mix);
  }
  
  public void samples(float[] sampsL, float[] sampsR)
  {
    beat.detect(source.mix);
  }
}
public class ChessVisualization implements Visualization{
  
  final int H_DIM = 10;
  final int V_DIM = 10;
  final float DECAY = 0.03f;
  
  int[][] board;
  int w,h;
  
  ChessVisualization(){
    
    // check if H_DIM and V_DIM have valid values
    if (V_DIM < 1 || H_DIM < 1){
      println("FATAL: H_DIM and/or V_DIM contain invalid values");
    }
    
    board = new int[H_DIM][V_DIM];
    
    w = PApplet.parseInt(width/H_DIM);
    h = PApplet.parseInt(height/V_DIM);
    
    // Two nested loops allow us to visit every spot in a 2D array.   
    // For every column I, visit every row J.
    for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        board[i][j] = 0;
      }
    }
  }
  
  public void kick(){
    int col =  PApplet.parseInt(random(H_DIM));
    int row =  PApplet.parseInt(random(V_DIM));
    board[col][row] = 255;
  }
  
  // not implemented yet
  public void snare(){}
  public void hat(){}
  
  public void draw(){
   for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        int value = board[i][j];
        if (value>0) value--;
        fill(value);
        rect(i*w,j*h,w,h);
        board[i][j] = value - PApplet.parseInt((DECAY*value));
      }
    }
  }
}
public class CircleVisualization implements Visualization{
  
  
  CircleVisualization(){
    println("FATAL: CircleVisualization not implemented yet");
    exit();
  
  }
  
  public void kick(){}
  public void snare(){}
  public void hat(){}
  
  public void draw(){}
}


class MicSignal implements AudioSignal, AudioListener{ //Just a simple "re-route" audio class.
float[] left, right;

//Getting.
public void samples(float[] arg0) {
left = arg0;
}

public void samples(float[] arg0, float[] arg1) {
left = arg0;
right = arg1;
}

//Sending back.
public void generate(float[] arg0) {
System.arraycopy(left, 0, arg0, 0, arg0.length);
}

public void generate(float[] arg0, float[] arg1) {
System.out.println(arg0[0]);
if (left!=null && right!=null){
System.arraycopy(left, 0, arg0, 0, arg0.length);
System.arraycopy(right, 0, arg1, 0, arg1.length);
}
}
}
public interface Visualization{
  public void kick();
  public void hat();
  public void snare();
  public void draw();
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "draft" });
  }
}
