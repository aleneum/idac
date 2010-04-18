#include <Servo.h> 

#include "WProgram.h"
void setup();
void loop();
Servo myservo;
int serialIn[4];
int serialIndex;


void setup() 
{ 
  Serial.begin(9600);
  myservo.attach(9);
  myservo.writeMicroseconds(1518); //1522 set servo to mid-point
} 

void loop() {
 if (Serial.available()>0) {
    // read the incoming byte:
    while((Serial.available()>0)){
      char in = Serial.read() - '0';
      serialIn[serialIndex]=in;
      serialIndex++;
      if (serialIndex >= 4){
        int newValue=serialIn[0]*1000+serialIn[1]*100+serialIn[2]*10+serialIn[3];
        myservo.writeMicroseconds(newValue);
        delay(1000);
        myservo.writeMicroseconds(1518);
        serialIndex=0;        
      }
    }
  }
} 


int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

