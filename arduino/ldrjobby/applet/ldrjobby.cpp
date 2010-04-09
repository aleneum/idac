#include "WProgram.h"
void setup();
void loop();
int ledPinLow = 13;
int ledPinHigh = 12;
int Irinput = 0;
int val = 0;
float val2 = 0;

float minValue = 1000;
float maxValue = 0;

void setup()
{
pinMode(ledPinLow, OUTPUT);
pinMode(ledPinHigh, OUTPUT);
pinMode(Irinput, INPUT);
Serial.begin(9600);
}

void loop()
{
val = analogRead(Irinput);
//val2 = ((val - 760)*3); 
  

  delay(100);
  
  if (val < minValue){
    minValue = val;
  } 
  if (val > maxValue){
    maxValue = val;
  }
  
  if (minValue - maxValue !=0) {
    val2 = ((val-minValue)/(maxValue-minValue));
    Serial.print(val2);
    Serial.println();
  }
  
  if (val2 > 0.05)
{
  digitalWrite(ledPinHigh, HIGH);
  digitalWrite(ledPinLow, LOW);
}
else
{
  digitalWrite(ledPinLow, HIGH);
  digitalWrite(ledPinHigh, LOW);
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

