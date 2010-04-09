const int signalPin = 7;
int val;
int val2;
int val3;
int val4;
int val5;
int val6;
int val7;
int val8;
int val9;
int val10;

int average;

int ledPin = 13;


void setup()
{
  pinMode(ledPin, OUTPUT);
  Serial.begin(9600);
}

void loop()
{ 
 long duration;
 pinMode(signalPin, OUTPUT);
 digitalWrite(signalPin, LOW);
 delay(2);
 digitalWrite(signalPin, HIGH);
 delay(5);
 digitalWrite(signalPin, LOW);
 
 
 pinMode(signalPin, INPUT);
 duration = pulseIn(signalPin, HIGH);

 val = valDetermine(duration);
 val2 = valDetermine(duration);
 val3 = valDetermine(duration);
 val4 = valDetermine(duration);
 val5 = valDetermine(duration);
 val6 = valDetermine(duration);
 val7 = valDetermine(duration);
 val8 = valDetermine(duration);
 val9 = valDetermine(duration);
 val10 = valDetermine(duration);
 
 average = (val + val2 + val3 + val4 + val5 + val6 + val7 + val8 + val9 + val10)/10;


 Serial.println(average);
 delay (100); 

 if (average < 17)
 {
   digitalWrite(ledPin, HIGH);
 }
 
 else
 { 
   digitalWrite(ledPin, LOW);
 }
}

long valDetermine(float microseconds)
{ 
  return microseconds / 1000;
}
