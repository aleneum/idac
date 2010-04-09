const int pin1 = 2;    // connected to the base of the transistor
const int pin2 = 3;


 void setup() {
   // set  the transistor pin as output:
   pinMode(pin1, OUTPUT);
   pinMode(pin2, OUTPUT);
 }

 void loop() {
   digitalWrite(pin1, HIGH);
   digitalWrite(pin2, LOW);
   
   delay(1000);
   
   digitalWrite(pin1, LOW);
   digitalWrite(pin2, HIGH);
   delay(1000);
 }

