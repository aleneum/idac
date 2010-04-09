int ledPinLow = 13;
int ledPinHigh = 12;
int Irinput = 0;
int val = 0;
float val2 = 0;
const int pin1 = 2;  
const int pin2 = 3;
int valav1 = 0;
int valav2 = 0;
int valav3 = 0;
int valav4 = 0;
int valav5 = 0;
int valav6 = 0;
int valav7 = 0;
int valav8 = 0;
int valav9 = 0;
int valav10 = 0;

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

//val2 = ((val - 760)*3); 
valav1 = analogRead(Irinput);
delay(10);
valav2 = analogRead(Irinput);
delay(10);
valav3 = analogRead(Irinput);
delay(10);
valav4 = analogRead(Irinput);
delay(10);
valav5 = analogRead(Irinput);
delay(10);
valav6 = analogRead(Irinput);
delay(10);
valav7 = analogRead(Irinput);
delay(10);
valav8 = analogRead(Irinput);
delay(10);
valav9 = analogRead(Irinput);
delay(10);
valav10 = analogRead(Irinput);
delay(10);
val = (valav1 + valav2 + valav3 + valav4 + valav5 + valav6 + valav7 + valav8 + valav9 + valav10)/10;

  
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
   
  delay(100);
   
  digitalWrite(pin1, LOW);
  digitalWrite(pin2, HIGH);
  delay(100);
}

else
{
  digitalWrite(ledPinLow, HIGH);
  digitalWrite(ledPinHigh, LOW);
  digitalWrite(pin1, HIGH);
  digitalWrite(pin2, HIGH);
}
}
