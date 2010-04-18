int relayPin1 = 6;
int relayPin2 = 7;
int soundIn = 2;
int val = 0;

void setup()
{
  pinMode(relayPin1, OUTPUT);
  pinMode(relayPin2, OUTPUT);
  pinMode(soundIn, INPUT);
  Serial.begin(9600);
}

void loop()
{
val = digitalRead(soundIn);
Serial.println(val);

  
digitalWrite(relayPin1, HIGH);
digitalWrite(relayPin2, LOW);
delay(400);
digitalWrite(relayPin1, LOW);
digitalWrite(relayPin2, HIGH);
delay(400);
  
}
