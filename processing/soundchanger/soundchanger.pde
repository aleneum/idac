import ddf.minim.*;

int lastPos = 0;
Minim minim;
AudioSnippet current, next, tmp;

void setup()
{
  size(200, 200);
 
  minim = new Minim(this);
  // load a file into an AudioSnippet
  // it must be in this sketches data folder
  current = minim.loadSnippet("human.mp3");
  //next = minim.loadSnippet("loop03.mp3");
  current.setLoopPoints(523, 4054); 
  current.loop();
}
 
void draw()
{
    background(0);
  /*int timeLeft = current.length() - current.position();
  background(0);
  println(timeLeft);
  if (timeLeft < 100){
    delay(timeLeft+160);
    next.play();
    tmp = current;
    current = next;
    next = tmp;
  } */
}
 
void keyPressed()
{
  if (key == 'b') {
    print("gotoLoop1");
    lastPos=523;
    if (lastPos <= current.position()){
      while(lastPos <= current.position()){
        lastPos = current.position();
        delay(10);
      }
      current.cue(1635);
    }
    current.setLoopPoints(523, 4054);
  } else if (key == 'n'){
    print("gotoLoop2");
    lastPos=7798;
    while(lastPos <= current.position()){
      lastPos = current.position();
      delay(10);
    }
    current.setLoopPoints(7798, 21891);
  } else if (key == 'm'){
    print("gotoLoop3");
    current.setLoopPoints(21891, 35968);
  }
}
 
void stop()
{
  // always close Minim audio classes
  current.close();
 // next.close();
  //tmp.close();
  // always stop Minim before exiting
  minim.stop();
 
  super.stop();
}
