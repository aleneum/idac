import processing.serial.*;

public class ArduinoCommunicator{

Serial port;
float value = 0;

ArduinoCommunicator() {
  println(Serial.list());
  port = new Serial(this, Serial.list()[0], 9600);
  port.bufferUntil('\n');
}

void serialEvent (Serial myPort){
  String inString = port.readStringUntil('\n');
  if (inString != null) {
    value = float(inString);
  }
}

float getTension(){
  return value;
}

