//Pin connected to ST_CP of 74HC595

int topPin = 8;

int noiseLeftPin = 4;
int getMoreLeftPin = 3;
int danceLeftPin = 2;

int noiseRightPin = 7; 
int getMoreRightPin = 6;  
int danceRightPin = 5;

int latchPin = 10;
int clockPin = 12;
int dataPin = 11;

int serialIndex=0;

const int LIGHT_MODE = 1;
const int VOLUME_MODE = 2;
const int SYMBOL_MODE = 3;
const int MOTOR_MODE = 4;
const int ENABLE_MODE = 5;
const int TOP_MODE = 6;

const int IDLE_MODE = 0;

const int LIGHT_LENGTH = 15;
const int VOLUME_LENGTH = 5;
const int SYMBOL_LENGTH = 3;
const int MOTOR_LENGTH = 3;
const int ENABLE_LENGTH = 2;
const int TOP_LENGTH = 1;

int lightState[LIGHT_LENGTH];
int lightTmp[LIGHT_LENGTH];

int volumeState[VOLUME_LENGTH];
int volumeTmp[VOLUME_LENGTH];

int symbolState[SYMBOL_LENGTH];
int symbolTmp[SYMBOL_LENGTH];

int motorState[MOTOR_LENGTH];
int motorTmp[MOTOR_LENGTH];

int enableState[ENABLE_LENGTH];
int enableTmp[ENABLE_LENGTH];

int topState[TOP_LENGTH];
int topTmp[TOP_LENGTH];

int mode = IDLE_MODE;

int count = 0;

void setup() {
  
  for (int i = 0; i < 14; i++){
    pinMode(i,OUTPUT);
  }
  
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
    char in = Serial.read();
    if (mode != IDLE_MODE) {
      if ((in != '0') && (in != '1')){
        mode = LIGHT_MODE;
        serialIndex = 0;
      }
    } else {
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
        case 't':  mode = TOP_MODE;
                   serialIndex = 0;
                   break;
        case 'b':  beatMessage();
                   break;
      }
    }
    if (mode == LIGHT_MODE) {
      lightTmp[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= LIGHT_LENGTH){
        for (int i=0; i < LIGHT_LENGTH; i++) {
          lightState[i] = lightTmp[i];
        }
        updateShift();
        mode = IDLE_MODE;       
      }
    } else if (mode == VOLUME_MODE) {
      volumeTmp[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= VOLUME_LENGTH){
        for (int i=0; i < VOLUME_LENGTH; i++) {
          volumeState[i] = volumeTmp[i];
        }
        updateShift();
        mode = IDLE_MODE;
      }
    } else if (mode == SYMBOL_MODE) {
      symbolTmp[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= SYMBOL_LENGTH){
        for (int i=0; i < SYMBOL_LENGTH; i++) {
          symbolState[i] = symbolTmp[i];
        }
        symbolMessage();
        mode = IDLE_MODE;        
      }
    } else if (mode == MOTOR_MODE) {
      motorTmp[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= MOTOR_LENGTH){
        for (int i=0; i < MOTOR_LENGTH; i++) {
          motorState[i] = motorTmp[i];
        }
        updateShift();
        mode = IDLE_MODE;        
      }
    } else if (mode == ENABLE_MODE) {
      enableTmp[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= ENABLE_LENGTH){
        for (int i=0; i < ENABLE_LENGTH; i++) {
          enableState[i] = enableTmp[i];
        }
        updateShift();
        mode = IDLE_MODE;        
      }
    } else if (mode == TOP_MODE) {
      topTmp[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= TOP_LENGTH){
        for (int i=0; i < TOP_LENGTH; i++) {
          topState[i] = topTmp[i];
        }
        topMessage();
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

int updateShift(){
  int resultHighest=0;
  int resultHigher=0;
  int resultLower=0;
  int resultLowest=0;
  
  int serialIn[32];
  
  for (int i=0; i < 15; i++) {
    serialIn[i] = lightState[i];
  }
  
  for (int i=15; i < 20; i++) {
    serialIn[i] = volumeState[i - 15];
  }
  
  for (int i=20; i < 23; i++) {
    serialIn[i] = motorState[i - 20];
  }
   
  serialIn[23] = enableState[0];
  serialIn[24] = enableState[1];
   
  for (int i=25; i < 32; i++){
    serialIn[i] = 0;
  }
  
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

void symbolMessage(){
  for (int i=0; i < SYMBOL_LENGTH; i++){
    if (symbolState[i] == 1){
      digitalWrite(danceLeftPin + i, HIGH);
      digitalWrite(danceRightPin + i , HIGH);
    } else {
      digitalWrite(danceLeftPin + i, LOW);
      digitalWrite(danceRightPin + i, LOW);
    }
  }  
}

void beatMessage(){
  Serial.println("beatMessage : not implemented yet");
  exit(-1);
}

void topMessage(){
  if (topState[0] == 1){
    digitalWrite(topPin, HIGH);
  } else {
    digitalWrite(topPin, LOW);
  }
}
