class Model extends Observable{

  public static final String MOTOR_OFF  = "000";
  public static final String MOTOR_UP   = "100";
  public static final String MOTOR_DOWN = "010";
  public static final String MOTOR_SPIN = "101";
  
  public static final String ENABLE_OFF   = "00";
  public static final String ENABLE_LEFT  = "10";
  public static final String ENABLE_RIGHT = "01";
  public static final String ENABLE_BOTH  = "11";
  
  
  public static final float LEVEL01_LIMIT = 0.05;
  public static final float LEVEL02_LIMIT = 0.2;
  public static final float LEVEL03_LIMIT = 0.35;
  public static final float LEVEL04_LIMIT = 0.5;
  public static final float LEVEL05_LIMIT = 0.8;
  public static final float LEVEL05_SOUND = 0.5;

  public static final int LOCK_TIME = 2000;
 
  public static final String LEVEL_CHANGED = "lvlch";
  public static final String LEVEL_NEXT = "lvlnxt";
  public static final String ENABLE_CHANGED = "enblch";
  
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
  
  public void updateLevel(float motionLeft, float motionRight, float sound, int sonic) {
    float motion;
    boolean bothEnabled;
    
    if (motionLeft > motionRight) {
      motion = motionLeft;
      bothEnabled = (motionRight > LEVEL03_LIMIT);
    } else {
      motion = motionRight;
      bothEnabled = (motionLeft > LEVEL03_LIMIT);
    }
    
    
    
    if (!levelLock) {
      nextLevel = level;
      if (motion < LEVEL01_LIMIT){
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
     } else if (motion < LEVEL04_LIMIT ) {
            if (level < 3) {
              nextLevel = level+1;
            } else if(level > 3) {
            nextLevel = level-1;
        }       
     } else if (bothEnabled) {
         if (motion < LEVEL05_LIMIT) {
            if (level < 4) {
              nextLevel = level+1;
            } else if(level > 4) {
              nextLevel = level-1;
            }
          } else if ((motion >= LEVEL05_LIMIT) && (LEVEL05_SOUND >= 0.5)) {
            if (level < 5) {
              nextLevel = level+1;
            }
          }
      }
      
      if ((level != nextLevel) && (level < 5)){
        levelLock = true;
        super.setChanged();
        super.notifyObservers(new Event(LEVEL_NEXT,this));
      } else if ((level == 5)||(level == 6)||(level==7)) {
        nextLevel = level + 1;
        levelLock = true;
        super.setChanged();
        super.notifyObservers(new Event(LEVEL_NEXT,this));
      } 
        else if (level == 8){
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
    
    
  public boolean isRightEnabled(){
    return rightEnabled;
  }
  
  public String getEnabledString(){
    if (leftEnabled && rightEnabled) {
      return Model.ENABLE_BOTH;
    } else if (leftEnabled) {
      return Model.ENABLE_LEFT;
    } else if (rightEnabled) {
      return Model.ENABLE_RIGHT;
    } 
    return ENABLE_OFF;
  }
    
  public void setLeftEnabled(boolean bool){
    if (bool != this.leftEnabled) {
      this.leftEnabled = bool;
      super.setChanged();
      super.notifyObservers(new Event(Model.ENABLE_CHANGED,this));
    }
  }  
    
  public void setRightEnabled(boolean bool){
     if (bool != this.rightEnabled) {
       this.rightEnabled = bool;
       super.setChanged();
       super.notifyObservers(new Event(Model.ENABLE_CHANGED,this));
    }
  }
  
  public void setEnabled(boolean bool){
    if ((bool != leftEnabled) || (bool != rightEnabled)) {
      this.leftEnabled = bool;
      this.rightEnabled = bool;
      super.setChanged();
      super.notifyObservers(new Event(Model.ENABLE_CHANGED,this));
    }
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
    
    public float getHigherMotion(){
      if (this.motionLeft > this.motionRight){
        return this.motionLeft;
      } else {
        return this.motionRight;
      }
    }
    
    public float getLowerMotion(){
      if (this.motionLeft > this.motionRight){
        return this.motionRight;
      } else {
        return this.motionLeft;
      }
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
    
