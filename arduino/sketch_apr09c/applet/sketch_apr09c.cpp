#include "WProgram.h"
void setup();
void loop();
int motorPin1 = 9;
int motorPin2 = 10;
int enablePin = 13;
int Button1 = 2;
int Button2 = 3;
int Sig1 = 0;
int Sig2 = 0;

void setup()
{ 
pinMode(Button1, INPUT);
pinMode(Button2, INPUT);
pinMode(motorPin1, OUTPUT);
pinMode(motorPin2, OUTPUT);
Serial.begin(9600);
pinMode(enablePin, OUTPUT);
digitalWrite(enablePin, HIGH);
}

void loop()
{
 Sig1 = digitalRead(Button1);
 Sig2 = digitalRead(Button2);
 
 if (Sig1 == HIGH)
 {
  digitalWrite(motorPin1, HIGH);
  digitalWrite(motorPin2, LOW);
  Serial.println("Pin 1");
  delay(10);
 }
 
 else if (Sig2 == HIGH)
 {
  digitalWrite(motorPin2, HIGH);
  digitalWrite(motorPin1, LOW);
  Serial.println("Pin 2");
  delay(10);
 }
 
 else
 {
  digitalWrite(motorPin1, LOW);
  digitalWrite(motorPin2, LOW);
  Serial.println("Both OFF");
  delay (10);
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

