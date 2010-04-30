int sigPin = 2;

void setup()
{
  pinMode(sigPin, OUTPUT);
}

void loop()
{
  digitalWrite(sigPin, HIGH);
  delay(1000);
  digitalWrite(sigPin, LOW);
  delay(1000);
}
