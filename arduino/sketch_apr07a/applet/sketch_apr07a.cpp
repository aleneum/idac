#include "WProgram.h"
void setup();
void loop();
int myServo = 8;

void setup()
{
  pinMode(myServo, OUTPUT);
}

void loop()
{
  digitalWrite(myServo, HIGH);
  delay(20);
  digitalWrite(myServo, LOW);
  delay(30);
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

