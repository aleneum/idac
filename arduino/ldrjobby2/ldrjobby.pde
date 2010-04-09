int ledPinLow = 13;
int ledPinHigh = 12;
int Irinput = 0;
int val = 0;
float val2 = 0;
const int pin1 = 2;  
const int pin2 = 3;

float minValue = 1000;
float maxValue = 0;

void setup()
{
pinMode(ledPinLow, OUTPUT);
pinMode(ledPinHigh, OUTPUT);
pinMode(Irinput, INPUT);
Serial.begin(9600);
pinMode(pin1, OUTPUT);
pinMode(pin2, OUTPUT);
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
  digitalWrite(pin1, HIGH);
  digitalWrite(pin2, LOW);
   
  delay(1000);
   
  digitalWrite(pin1, LOW);
  digitalWrite(pin2, HIGH);
  delay(1000);
}
else
{
  digitalWrite(ledPinLow, HIGH);
  digitalWrite(ledPinHigh, LOW);
}
}
