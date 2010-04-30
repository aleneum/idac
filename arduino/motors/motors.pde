#include <Servo.h> 

Servo myservo, motorUp, motorDown;


int motorPin1 = 14;
int motorPin2 = 15;
int enablePin = 13;
int Button1 = 2;
int Button2 = 3;
int Sig1 = 0;
int Sig2 = 0;
int ServoPin = 12;

boolean servoOn = false;

void setup()
{ 
  
 myservo.attach(6);
 myservo.writeMicroseconds(1530);
  
//pinMode(Button1, INPUT);
//pinMode(Button2, INPUT);
pinMode(motorPin1, OUTPUT);
pinMode(motorPin2, OUTPUT);
Serial.begin(9600);
pinMode(enablePin, OUTPUT);
digitalWrite(enablePin, HIGH);
}

void loop()
{
   if (Serial.available()>0) {
     char in = Serial.read();
     if (in == 'n'){
       servoOn = true;
     } else if ( in == 'u') {
        for (int i=0; i < 50; i++) {
          digitalWrite(motorPin1,HIGH);
          delay(20);
          digitalWrite(motorPin1,LOW);
          delay(20);
        }
      } else if ( in == 'd') {
        digitalWrite(motorPin2,HIGH);
        delay(20);
        digitalWrite(motorPin2,LOW);
     } /*else {
       servoOn = false;
     }*/
   }
  /*
  if (servoOn) {
     myservo.writeMicroseconds(1518);   
  } else {
    myservo.writeMicroseconds(1530);
  }
  
 Sig1 = digitalRead(Button1);
 Sig2 = digitalRead(Button2);
 
 if (Sig1 == HIGH)
 {
  digitalWrite(motorPin1, HIGH);
  digitalWrite(motorPin2, LOW);
  //Serial.println("Pin 1");
  delay(10);
 }
 
 else if (Sig2 == HIGH)
 {
  digitalWrite(motorPin2, HIGH);
  digitalWrite(motorPin1, LOW);
  //Serial.println("Pin 2");
  delay(10);
 }
 
 else
 {
  digitalWrite(motorPin1, LOW);
  digitalWrite(motorPin2, LOW);
  //Serial.println("Both OFF");
  delay (10);
}*/
}
