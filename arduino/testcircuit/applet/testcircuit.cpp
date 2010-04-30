#include "WProgram.h"
void setup();
void loop();
int notice1 = 2;
int notice2 = 3;
int notice3 = 4;
int notice4 = 5;
int notice5 = 6;
int notice6 = 7;
int topLed = 8;

void setup()
{
  pinMode(notice1, OUTPUT);
  pinMode(notice2, OUTPUT);
  pinMode(notice3, OUTPUT);
  pinMode(notice4, OUTPUT);
  pinMode(notice5, OUTPUT);
  pinMode(notice6, OUTPUT);
  pinMode(topLed, OUTPUT);
}

void loop()
{
  digitalWrite(notice1, HIGH);
  digitalWrite(notice2, LOW);
  digitalWrite(notice3, LOW);
  digitalWrite(notice4, LOW);
  digitalWrite(notice5, LOW);
  digitalWrite(notice6, LOW);
  digitalWrite(topLed, HIGH);
  delay(500);
  digitalWrite(notice1, LOW);
  digitalWrite(notice2, HIGH);
  digitalWrite(notice3, LOW);
  digitalWrite(notice4, LOW);
  digitalWrite(notice5, LOW);
  digitalWrite(notice6, LOW);
  digitalWrite(topLed, LOW);
  delay(500);
  digitalWrite(notice1, LOW);
  digitalWrite(notice2, LOW);
  digitalWrite(notice3, HIGH);
  digitalWrite(notice4, LOW);
  digitalWrite(notice5, LOW);
  digitalWrite(notice6, LOW);
  digitalWrite(topLed, HIGH);
  delay(500);
  digitalWrite(notice1, LOW);
  digitalWrite(notice2, LOW);
  digitalWrite(notice3, LOW);
  digitalWrite(notice4, HIGH);
  digitalWrite(notice5, LOW);
  digitalWrite(notice6, LOW);
  digitalWrite(topLed, LOW);
  delay(500);
  digitalWrite(notice1, LOW);
  digitalWrite(notice2, LOW);
  digitalWrite(notice3, LOW);
  digitalWrite(notice4, LOW);
  digitalWrite(notice5, HIGH);
  digitalWrite(notice6, LOW);
  digitalWrite(topLed, HIGH);
  delay(500);
  digitalWrite(notice1, LOW);
  digitalWrite(notice2, LOW);
  digitalWrite(notice3, LOW);
  digitalWrite(notice4, LOW);
  digitalWrite(notice5, LOW);
  digitalWrite(notice6, HIGH);
  digitalWrite(topLed, LOW);
  delay(500);
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

