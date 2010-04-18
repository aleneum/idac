#include "WProgram.h"
void setup();
void loop();
int Servo1 = 13;

void setup()
{
pinMode(Servo1, OUTPUT);  
  
}

void loop()
{
digitalWrite(Servo1, HIGH);
delay(1000);
digitalWrite(Servo1, LOW);
delay(1000);
  
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

