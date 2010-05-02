void setup(){
  Serial.begin(9600);
  pinMode(2,INPUT);
}

void loop(){

  int input = digitalRead(2);
  Serial.println(input);
  delay(1000);
}
