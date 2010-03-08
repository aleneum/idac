import ddf.minim.*;
 
Minim minim;
AudioInput in;
AudioOutput out;
playBack pb;

float[] left, right;
 
void setup()
{
  size(512, 200, P3D);
 
  minim = new Minim(this);
  minim.debugOn();
 
  // get a line in from Minim, default bit depth is 16
  in = minim.getLineIn(Minim.STEREO, 1024);
  out = minim.getLineOut(Minim.STEREO, 1024);
  pb = new playBack();
  
  in.addListener(pb);
  out.addSignal(pb);
}
 
void draw()
{
  background(0);
  stroke(255);
  // draw the waveforms
  
  
  
  for(int i = 0; i < in.bufferSize() - 1; i++)
  {
    line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
    line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
  }
}
 
 
void stop()
{
  // always close Minim audio classes when you are done with them
  in.close();
  out.close();
  minim.stop();
 
  super.stop();
}

class playBack implements AudioSignal, AudioListener{ //Just a simple "re-route" audio class.
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
