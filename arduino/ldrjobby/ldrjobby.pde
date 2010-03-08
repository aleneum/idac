int ledPinLow = 13;
int ledPinHigh = 12;
int ldrinput = 2;
int val = 0;

float minValue = 1000;
float maxValue = 0;

void setup()
{
pinMode(ledPinLow, OUTPUT);
pinMode(ledPinHigh, OUTPUT);
pinMode(ldrinput, INPUT);
Serial.begin(9600);
}

void loop()
{
val = analogRead(ldrinput);  
  
if (val < 800)
{
  digitalWrite(ledPinHigh, HIGH);
  digitalWrite(ledPinLow, LOW);
}
else
{
  digitalWrite(ledPinLow, HIGH);
  digitalWrite(ledPinHigh, LOW);
}
  delay(100);
  
  if (val < minValue){
    minValue = val;
  } 
  if (val > maxValue){
    maxValue = val;
  }
  
  if (minValue - maxValue !=0) {
    Serial.print((val-minValue)/(maxValue-minValue));
    Serial.println();
  }
}
