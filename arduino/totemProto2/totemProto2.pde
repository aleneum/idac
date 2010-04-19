// UltraSonicPin
int ultraSoundSignal = 2;

// pins for turning the light on and off
int relayPin1 = 3;
int relayPin2 = 4;

//Pin connected to ST_CP of 74HC595
int latchPin = 8;
//Pin connected to SH_CP of 74HC595
int clockPin = 12;
////Pin connected to DS of 74HC595
int dataPin = 11;

int higher[] = {0,4,8,16,32,64,0,0,0,0,0,0,0,0,1,2};
int lower[] = {0,0,0,0,0,0,1,2,4,8,16,32,64,128,0,0}; 

int serialIn[16];
int serialIndex=0;

// echo of UltraSonic
int echo = 0;

// can be used for things that dont have to be executed every loop;
int loopStep = 0;

boolean lightIsOn = false;
boolean readLightLevel = false;

unsigned long lastChecked = 0;

void setup() {
  //Start Serial for debuging purposes	
  Serial.begin(9600);
  //set pins to output because they are addressed in the main loop
  pinMode(latchPin, OUTPUT);
  pinMode(ultraSoundSignal,OUTPUT);
  pinMode(relayPin1, OUTPUT);
  pinMode(relayPin2, OUTPUT);
  switchLightOff();
  lightIsOn = false;
  serialIndex = 0;
  
  digitalWrite(latchPin, 0);
  shiftOut(dataPin, clockPin, 0); 
  shiftOut(dataPin, clockPin, 0);
  digitalWrite(latchPin, 1);
}

void loop() {
  //count up routine
  checkDistance();
  
  while (Serial.available()>0){
    char in = Serial.read();
    
    if (in == 'l'){
        readLightLevel = true;
        serialIndex=0;
    } else if (readLightLevel == true) {
      serialIn[serialIndex]=in - '0';
      serialIndex++;
      if (serialIndex >= 16){
          gotLevelMessage();
          readLightLevel=false;
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

int gotLevelMessage(){
  int resultHigher=0;
  int resultLower=0;
  
  for (int i=0; i < 8; i++){
    int inRead = serialIn[i];
    if (inRead > 0){
      resultHigher += 1 << (i);
    }
  }
    
  for (int j=8; j < 16; j++){
    int inRead = serialIn[j];
     if (inRead > 0){
        resultLower += 1 << (j-8);
      }
    }    
    digitalWrite(latchPin, 0);
    shiftOut(dataPin, clockPin, resultLower); 
    shiftOut(dataPin, clockPin, resultHigher);
    digitalWrite(latchPin, 1);
    if (serialIn[15]>0){
      switchLightOn();
    } else {
      switchLightOff();
    }
    return 1;
}

unsigned long ping(){
  float ultrasoundValue;
   pinMode(ultraSoundSignal, OUTPUT); // Switch signalpin to output
   digitalWrite(ultraSoundSignal, LOW); // Send low pulse
   delayMicroseconds(2); // Wait for 2 microseconds
   digitalWrite(ultraSoundSignal, HIGH); // Send high pulse
   delayMicroseconds(5); // Wait for 5 microseconds
   digitalWrite(ultraSoundSignal, LOW); // Holdoff
   pinMode(ultraSoundSignal, INPUT); // Switch signalpin to input
   digitalWrite(ultraSoundSignal, HIGH); // Turn on pullup resistor
   echo = pulseIn(ultraSoundSignal, HIGH); //Listen for echo
   ultrasoundValue = (echo / 58.138) * .39; //convert to CM then to inches
   return ultrasoundValue;
}
 
void switchLightOn(){
  digitalWrite(relayPin1, LOW);
  digitalWrite(relayPin2, HIGH);
  lightIsOn = true;
}
  
void switchLightOff(){
  digitalWrite(relayPin1, HIGH);
  digitalWrite(relayPin2, LOW);
  lightIsOn = false;
}
 
void toggleLight(){
  if (lightIsOn == false){
    switchLightOn();
  } else {
    switchLightOff();
  }
}

void checkDistance(){
  if (millis() - lastChecked > 1000) {
    int distance = ping();
    lastChecked = millis();
    Serial.println(distance);
  }
}
