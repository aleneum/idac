#include "WProgram.h"
void setup();
void loop();
int ldrPin = 0;
int ledPin = 13;
int val;

void setup()
{
  pinMode(ldrPin, INPUT);
  pinMode(ledPin, OUTPUT);
  val = 0;
  Serial.begin(9600);
}

void loop()
{
  val = analogRead(ldrPin);
  
  if (val > 70)
  {
    digitalWrite(ledPin, HIGH);
    delay (100);
    Serial.println(val);
  }
  
  else
  {
    digitalWrite(ledPin, LOW);
    delay(100);
    Serial.println(val);
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

