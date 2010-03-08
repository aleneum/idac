import ddf.minim.*;


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

//Here's the runner code:
void setup(){
Minim.start(this);
  AudioOutput ap = Minim.getLineOut();
  AudioInput ai = Minim.getLineIn();
  playBack pb = new playBack();
  ai.addListener(pb); //Samples from mic go to pb
  ap.addSignal(pb); //ap will playback pb constantly.
}
