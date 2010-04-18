import totem.comm.*;
import processing.serial.*;

TSerialCommunicator communicator;
int count;
int sig = 1;

void setup(){
  communicator = new TSerialCommunicator(this);
}

void draw(){
  communicator.serialSend(iterLight());
  delay(10);
}

public String iterLight(){
  StringBuffer buffer = new StringBuffer("00000000000000000000000000000000");
  buffer.setCharAt(count,'1');
  
  count += sig;
  if (count > 30){
    sig = -1;
  } else if(count < 1) {
    sig = 1;
  }
  
  return buffer.toString();
}
