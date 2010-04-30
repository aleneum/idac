#include "WProgram.h"
void setup();
void loop();
int SigPin1 = 8;
int SigPin2 = 9;

void setup()
{
  pinMode(SigPin1, OUTPUT);
  pinMode(SigPin2, OUTPUT);
}


void loop()
{
  digitalWrite(SigPin1, HIGH);
  digitalWrite(SigPin2, LOW);
  delay(1000);
  digitalWrite(SigPin1, LOW);
  digitalWrite(SigPin2, LOW);
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

