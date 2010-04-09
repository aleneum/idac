#include <Servo.h>

Servo myServo1;
int pos = 0;

void setup(){
myServo1.attach(0);
Serial.begin(9600);
pinMode(0, OUTPUT);
}

void loop(){
   
    if (pos = 0)
    {
      pos = (pos + 360);
      myServo1.write(pos);
      delay (100);
      Serial.write(pos);
  }
    else if (pos == 360)
    {
      pos = (pos - 360);
      myServo1.write(pos);
      delay (1);
      Serial.write(pos);
    }
}
