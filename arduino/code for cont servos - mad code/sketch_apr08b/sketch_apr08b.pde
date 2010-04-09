//Servo control via direct access to AVR PWM registers



#define servo1control OCR1A	 // servo1 is connected to Arduino pin9


//servo constants -- trim as needed
#define servo1null 3035
#define servo2null 3000
// Most servos are analog devices so the exact null point may vary somewhat device to device.
// This is particularly true of continuous rotation servos.

void setup(){
  pinMode(9,OUTPUT);
  pinMode(10,OUTPUT);
  TCCR1B = 0b00011010;	    // Fast PWM, top in ICR1, /8 prescale (.5 uSec)
  TCCR1A = 0b10100010;	    //clear on compare match, fast PWM
					  // to use pin 9 as normal input or output, use  TCCR1A = 0b00100010
					  // to use pin 10 as normal input or output, use  TCCR1A = 0b10000010
  ICR1 =  39999;		    // 40,000 clocks @ .5 uS = 20mS
  servo1control = servo1null;	   // controls chip pin 15  ARDUINO pin 9

}


void loop(){
  // move servo for demonstration purposes
  servo1control = 2500;		 // 2500 valid range is servonull +/- 1000
					     //  - is counter clockwise
					     //   + is clockwise
					     // The difference from servonull represents servo angle or,
					     //for continuous rotation servos, speed.
					     //These values may differ somewhat unit to unit
  delay(3500);
  servo1control = servo1null;
  delay(3000);
  servo1control = 3570;
  delay(3500);
  servo1control = servo1null;
  delay(3000);
} 

