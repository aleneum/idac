int myServo = 8;


void setup()
{
  pinMode(myServo, OUTPUT);
}

void loop()
{
  digitalWrite(myServo, HIGH);
  delay(20000);
  digitalWrite(myServo, LOW);
  delay(200);
}
