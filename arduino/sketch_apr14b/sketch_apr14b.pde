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
