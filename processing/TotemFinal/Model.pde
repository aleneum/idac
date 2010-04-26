class Model extends EventSupport{

  public static final String MOTOR_OFF  = "000";
  public static final String MOTOR_UP   = "100";
  public static final String MOTOR_DOWN = "010";
  public static final String MOTOR_SPIN = "001";
  
  public static final String ENABLE_OFF   = "00";
  public static final String ENABLE_LEFT  = "10";
  public static final String ENABLE_RIGHT = "01";
  public static final String ENABLE_BOTH  = "11";
 
  public static final String LEVEL_CHANGED = "lvlch";
  
  int level;
  boolean levelLock;
  
  public Model(){
    level = 0;
    levelLock = false;
  }
  
  public void updateLevel(float motion, float sound, int sonic) {
    int tmpLevel = level;
    motion = 0.8;
    sound = 0.6;
    if (!levelLock) {
      if ((motion < 0.2) && (sonic > 100)){
        level = 0;
      } else if (motion < 0.2) {
        level = 1;        
      } else if (motion < 0.35) {
        level = 2;
      } else if (motion <0.5) {
        level = 3; 
      } else if (motion < 0.7) {
        level = 4;
      } else  if ((motion >= 0.7) && (sound >= 0.5)) {
        level = 5;
      } else {
        level = 4;
      }
    }
    if (tmpLevel != level){
      super.fireEvent(new Event(LEVEL_CHANGED,this));
    }
  }
  
  public int getLevel(){
    return level;
  }
  
  public void lockLevel(){
    levelLock = true;
  }
  
  public void unlockLevel(){
    levelLock = false;
  }
  
}

public class TotemState{
  
  // motion
  float motionLeft = 0;
  float motionRight = 0;
  float instantRight = 0;
  float instantLeft = 0;
 
  int level = 0;
  boolean beat = false;
 
  float soundState = 0;
  float soundInput = 0;
  float soundOutput = 0;
  int sonicInput = 0;
  
    public void update(float mLeft, float mRight, float iLeft, float iRight, int lvl, boolean beat, float sState, float iSound, float oSound, int sonic){
      this.motionLeft = mLeft;
      this.motionRight = mRight;
    
      this.instantLeft = iLeft;
      this.instantRight = iRight;
    
      this.level = lvl;
      this.soundState = sState;
      this.soundInput = iSound;
      this.soundOutput = oSound;
      this.sonicInput = sonic;
    }
    
    public float getMotionLeft(){
      return this.motionLeft;
    }
    
    public float getMotionRight(){
      return this.motionRight;
    }
    
    public float getInstantLeft(){
      return this.instantLeft;
    }
    
    public float getInstantRight(){
      return this.instantRight;
    }
    
    public int getLevel(){
      return this.level;
    }
    
    public float getSoundState(){
      return this.soundState;
    }
    
    public float getSoundInput(){
      return this.soundInput;
    }
    
    public float getSoundOutput(){
      return this.soundOutput;
    }
    
    public int getSonicInput(){
      return this.sonicInput;
    } 
    
  }
    
