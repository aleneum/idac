class Model extends Observable{

  public static final String MOTOR_OFF  = "000";
  public static final String MOTOR_UP   = "100";
  public static final String MOTOR_DOWN = "010";
  public static final String MOTOR_SPIN = "001";
  
  public static final String ENABLE_OFF   = "00";
  public static final String ENABLE_LEFT  = "10";
  public static final String ENABLE_RIGHT = "01";
  public static final String ENABLE_BOTH  = "11";
  
  public static final float LEVEL02_LIMIT = 0.2;
  public static final float LEVEL03_LIMIT = 0.35;
  public static final float LEVEL04_LIMIT = 0.5;
  public static final float LEVEL05_LIMIT = 0.8;
  public static final float LEVEL05_SOUND = 0.5;

  public static final int LOCK_TIME = 2000;
 
  public static final String LEVEL_CHANGED = "lvlch";
  public static final String LEVEL_NEXT = "lvlnxt";
  
  int level;
  int nextLevel;
  
  boolean levelLock;
  int lockExceed;
  
  boolean leftEnabled = false;
  boolean rightEnabled = false;
  
  public Model(){
    level = 0;
    levelLock = false;
    lockExceed = 0;
  }
  
  public void updateLevel(float motion, float sound, int sonic) {
    sound = 0.5;
    motion = 1;
    if (!levelLock) {
      nextLevel = level;
      if ((motion < LEVEL02_LIMIT) && (sonic > 100)){
        if (level > 0) {
          nextLevel = level-1;
        } 
      } else if (motion < LEVEL02_LIMIT) {
        if (level < 1) {
          nextLevel = level+1;
        } else if(level > 1) {
          nextLevel = level-1;
        }
      } else if (motion < LEVEL03_LIMIT) {
        if (level < 2) {
          nextLevel = level+1;
        } else if(level > 2) {
          nextLevel = level-1;
        }
      } else if (motion < LEVEL04_LIMIT) {
        if (level < 3) {
          nextLevel = level+1;
        } else if(level > 3) {
          nextLevel = level-1;
        } 
      } else if (motion < LEVEL05_LIMIT) {
        if (level < 4) {
          nextLevel = level+1;
        } else if(level > 4) {
          nextLevel = level-1;
        }
      } else  if ((motion >= LEVEL05_LIMIT) && (LEVEL05_SOUND >= 0.5)) {
        if (level < 5) {
          nextLevel = level+1;
        }
      } else {
        if (level < 4) {
          nextLevel = level+1;
        } else if(level > 4) {
          nextLevel = level-1;
        }
      }
    
      if ((level != nextLevel) && (level < 5)){
        levelLock = true;
        super.setChanged();
        super.notifyObservers(new Event(LEVEL_NEXT,this));
      } else if (level == 5) {
        nextLevel = 6;
        levelLock = true;
        super.setChanged();
        super.notifyObservers(new Event(LEVEL_NEXT,this));
      } else if (level == 6){
        nextLevel = 0;
        levelLock = true;
        super.setChanged();
        super.notifyObservers(new Event(LEVEL_NEXT,this));
      }
    }
    if (lockExceed > 0){
      if (lockExceed < millis()) {
        lockExceed = 0;
        levelLock = false;
      }
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
  
  public boolean isLeftEnabled(){
    return leftEnabled;
  }
    
  public void setLeftEnabled(boolean bool){
    this.leftEnabled = bool;
  }
    
  public boolean isRightEnabled(){
    return rightEnabled;
  }
    
  public void setRightEnabled(boolean bool){
    this.rightEnabled = bool;
  }
  
  public void setEnabled(boolean bool){
    this.leftEnabled = bool;
    this.rightEnabled = bool;
  }

  public int getNextLevel(){
    return nextLevel;
  }  
  
  public void initNextLevel(){
      println("initNextLevel");
      level = nextLevel;
      lockExceed = millis() + LOCK_TIME;
      super.setChanged();
      super.notifyObservers(new Event(LEVEL_CHANGED,this));
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
    
