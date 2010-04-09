int motor_one_pin_one=6;
int motor_one_pin_two=5;
int optionOne = 8;
int optionTwo = 9;
int optionThree = 10;
int But1;
int But2;
int But3;
int RelayOne = 2;
int RelayTwo = 3;

void setup()
{
pinMode(optionOne, INPUT);
pinMode(optionTwo, INPUT);
pinMode(optionThree, INPUT);
pinMode(motor_one_pin_one,OUTPUT);
pinMode(motor_one_pin_two,OUTPUT);
Serial.begin(9600);
pinMode(RelayOne, OUTPUT);
pinMode(RelayTwo, OUTPUT);
}

void loop()
{
 But1 = digitalRead(optionOne); 
 But2 = digitalRead(optionTwo);
 But3 = digitalRead(optionThree);
  
  if (But1 == HIGH && But2 == LOW && But3 == LOW)
  {
    Serial.println("Button One is Pressed!");
    digitalWrite(motor_one_pin_one, HIGH);
    delay(80);
    digitalWrite(motor_one_pin_one, LOW);
    digitalWrite(RelayOne, HIGH);
    delay(5000);
    digitalWrite(RelayOne, LOW);
    digitalWrite(motor_one_pin_two, HIGH);
    delay(70);
    digitalWrite(motor_one_pin_two, LOW);
    
  }
 
 else if (But1 == LOW && But2 == HIGH && But3 == LOW)
  {
    Serial.println("Button Two is Pressed!");
    digitalWrite(motor_one_pin_one, HIGH);
    delay(57);
    digitalWrite(motor_one_pin_one, LOW);
    digitalWrite(RelayOne, HIGH);
    delay(5000);
    digitalWrite(RelayOne, LOW);
    digitalWrite(motor_one_pin_two, HIGH);
    delay(45);
    digitalWrite(motor_one_pin_two, LOW);
  }
 
else if (But1 == LOW && But2 == LOW && But3 == HIGH)
  {
    Serial.println("Button Three is Pressed!");
    digitalWrite(motor_one_pin_one, HIGH);
    delay(72);
    digitalWrite(motor_one_pin_one, LOW);
    digitalWrite(RelayOne, HIGH);
    delay(5000);
    digitalWrite(RelayOne, LOW);
    digitalWrite(motor_one_pin_two, HIGH);
    delay(68);
    digitalWrite(motor_one_pin_two, LOW);
  }  

 else if (But1 == LOW && But2 == LOW && But3 == LOW)
 {
  Serial.println("No button Action");
  digitalWrite(motor_one_pin_one, LOW);
  digitalWrite(motor_one_pin_two, LOW);
  delay(100);
 }
 
 }
