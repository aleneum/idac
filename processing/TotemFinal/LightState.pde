class LightState{
  public static final String BLACK   = "000";
  public static final String RED     = "100";
  public static final String GREEN   = "010";
  public static final String BLUE    = "001";
  public static final String YELLOW  = "110";
  public static final String MAGENTA = "101";
  public static final String CYAN    = "011";
  public static final String WHITE   = "111"; 
  
  private String firstLed, secondLed, thirdLed, fourthLed, fifthLed;
  
  public LightState(String frst, String scnd, String thrd, String frth, String ffth){
    this.firstLed  = frst;
    this.secondLed = scnd;
    this.thirdLed  = thrd;
    this.fourthLed = frth;
    this.fifthLed  = ffth;
  }

  public void setLight(int i, String col){
    switch(i){
      case 0: this.firstLed = col;
              break;
      case 1: this.secondLed = col;
              break;
      case 2: this.thirdLed  = col;
              break;
      case 3: this.fourthLed = col;
              break;
      case 4: this.fifthLed  = col;
              break;
    }
  }
  
  public String getLight(int i){
    switch(i){
      case 0: return this.firstLed;
      case 1: return this.secondLed;
      case 2: return this.thirdLed;
      case 3: return this.fourthLed;
      case 4: return this.fifthLed;
    }
    return LightState.BLACK;
  }
  
  public void setFirst(String state){
    this.firstLed  = state;
  }
  
  public void setSecond(String state){
    this.secondLed  = state;
  }
  
  public void setThird(String state){
    this.thirdLed  = state;
  }
  
  public void setFourth(String state){
    this.fourthLed  = state;
  }
    
  public void setFifth(String state){
    this.fifthLed  = state;
  }
  
  public String getStateString(){
    String msg = firstLed + secondLed + thirdLed + fourthLed + fifthLed;
    return msg;
  }
  
}

class LightStateOff extends LightState{
  public LightStateOff(){
    super(LightState.BLACK,LightState.BLACK,LightState.BLACK,LightState.BLACK,LightState.BLACK);
  }
}

class LightStateMono extends LightState{
  public LightStateMono(String  col){
    super(col, col, col, col, col);
  }
}
