float maxValue = 1;

void setup(){
  Serial.begin(9600);
}

void loop(){
  int value = analogRead(0);
  if (value > maxValue) {
    maxValue = value;
  } 
  
  Serial.println(value/maxValue);
  delay(10);
}
