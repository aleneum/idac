//Pin connected to ST_CP of 74HC595
int latchPin = 8;
//Pin connected to SH_CP of 74HC595
int clockPin = 12;
////Pin connected to DS of 74HC595
int dataPin = 11;

int serialIn[32];
int serialIndex=0;

const int LIGHT_MODE = 1;
const int VOLUME_MODE = 2;
const int SYMBOL_MODE = 3;
const int MOTOR_MODE = 4;
const int ENABLE_MODE = 5;
const int IDLE_MODE = 0;

const int LIGHT_LENGTH = 15;
const int VOLUME_LENGTH = 5;
const int SYMBOL_LENGTH = 3;
const int MOTOR_LENGTH = 3;
const int ENABLE_LENGTH = 2;

int lightState[LIGHT_LENGTH];
int volumeState[VOLUME_LENGTH];
int symbolState[SYMBOL_LENGTH];
int motorState[MOTOR_LENGTH];
int enableState[ENABLE_LENGTH];

int mode = IDLE_MODE;

int count = 0;

void setup() {
  
  //Start Serial for debuging purposes	
  Serial.begin(9600);
  //set pins to output because they are addressed in the main loop
  pinMode(latchPin, OUTPUT);
  
  digitalWrite(latchPin, 0);
  shiftOut(dataPin, clockPin, 0); 
  shiftOut(dataPin, clockPin, 0);
  shiftOut(dataPin, clockPin, 0); 
  shiftOut(dataPin, clockPin, 0);
  digitalWrite(latchPin, 1);
}

void loop() {
  //count up routine
  if (Serial.available()>0) {
    char in = Serial.read() - '0';
    if (mode == IDLE_MODE) {
      switch(mode) {
        case 'l':  mode = LIGHT_MODE;
                   serialIndex = 0;
                   break;
        case 'v':  mode = VOLUME_MODE;
                   serialIndex = 0;
                   break;
        case 's':  mode = SYMBOL_MODE;
                   serialIndex = 0;
                   break;
        case 'm':  mode = MOTOR_MODE;
                   serialIndex = 0;
                   break;
        case 'e':  mode = ENABLE_MODE;
                   serialIndex = 0;
                   break;
        case 'b':  beatMessage();
                   break;  
         
      }
    } else if (mode == LIGHT_MODE) {
      serialIn[serialIndex]=in;
      serialIndex++;
      if (serialIndex >= LIGHT_LENGTH){
        lightMessage();
        mode = IDLE_MODE;       
      }
    } else if (mode == VOLUME_MODE) {
      serialIn[serialIndex]=in;
      serialIndex++;
      if (serialIndex >= VOLUME_LENGTH){
        volumeMessage();
        mode = IDLE_MODE;        
      }
    } else if (mode == SYMBOL_MODE) {
      serialIn[serialIndex]=in;
      serialIndex++;
      if (serialIndex >= SYMBOL_LENGTH){
        symbolMessage();
        mode = IDLE_MODE;        
      }
    } else if (mode == MOTOR_MODE) {
      serialIn[serialIndex]=in;
      serialIndex++;
      if (serialIndex >= MOTOR_LENGTH){
        motorMessage();
        mode = IDLE_MODE;        
      }
    } else if (mode == ENABLE_MODE) {
      serialIn[serialIndex]=in;
      serialIndex++;
      if (serialIndex >= ENABLE_LENGTH){
        enableMessage();
        mode = IDLE_MODE;        
      }
    }
  }
}

void shiftOut(int myDataPin, int myClockPin, byte myDataOut) {
  // This shifts 8 bits out MSB first, 
  //on the rising edge of the clock,
  //clock idles low

  //internal function setup
  int i=0;
  int pinState;
  pinMode(myClockPin, OUTPUT);
  pinMode(myDataPin, OUTPUT);

  //clear everything out just in case to
 //prepare shift register for bit shifting
  digitalWrite(myDataPin, 0);
  digitalWrite(myClockPin, 0);

  //for each bit in the byte myDataOut�
  //NOTICE THAT WE ARE COUNTING DOWN in our for loop
  //This means that %00000001 or "1" will go through such
  //that it will be pin Q0 that lights. 
  for (i=7; i>=0; i--)  {
    digitalWrite(myClockPin, 0);

    //if the value passed to myDataOut and a bitmask result 
    // true then... so if we are at i=6 and our value is
    // %11010100 it would the code compares it to %01000000 
    // and proceeds to set pinState to 1.
    if ( myDataOut & (1<<i) ) {
      pinState= 1;
    }
    else {	
      pinState= 0;
    }

    //Sets the pin to HIGH or LOW depending on pinState
    digitalWrite(myDataPin, pinState);
    //register shifts bits on upstroke of clock pin  
    digitalWrite(myClockPin, 1);
    //zero the data pin after shift to prevent bleed through
    digitalWrite(myDataPin, 0);
  }

  //stop shifting
  digitalWrite(myClockPin, 0);
}

int gotMessage(){
  int resultHighest=0;
  int resultHigher=0;
  int resultLower=0;
  int resultLowest=0;
  
  for (int i=0; i < 8; i++){
    int inRead = serialIn[i];
    if (inRead > 0){
      resultHighest += 1 << (i);
    }
  }
    
    for (int i=8; i < 16; i++){
      int inRead = serialIn[i];
      if (inRead > 0){
        resultHigher += 1 << (i-8);
      }
    }
    
    for (int i=16; i < 24; i++){
      int inRead = serialIn[i];
      if (inRead > 0){
        resultLower += 1 << (i-16);
      }
    }
    
    for (int i=24; i < 32; i++){
      int inRead = serialIn[i];
      if (inRead > 0){
        resultLowest += 1 << (i-24);
      }
    }

    digitalWrite(latchPin, 0);
    shiftOut(dataPin, clockPin, resultLowest); 
    shiftOut(dataPin, clockPin, resultLower);
    shiftOut(dataPin, clockPin, resultHigher);
    shiftOut(dataPin, clockPin, resultHighest);
    digitalWrite(latchPin, 1);
    return 1;
}
