import totem.sound.*;


public class LightViz{
int mode;
int step;
TPlayer player;

public final String ALL_OFF   = "0000000000000000";
public final String ALL_RED   = "0000000000011111";
public final String ALL_GREEN = "0000001111100000";
public final String ALL_BLUE  = "0111110000000000";

  public LightViz(TPlayer aPlayer){
    player = aPlayer;
    mode = 0;
    step = 0;
  }
  
  public String getStep(){
    switch(mode){
      case 0: return doMonoRotate();
      default : return ALL_OFF;
    }
  } 
  
  private String doMonoRotate(){
    StringBuffer returnString = new StringBuffer(ALL_RED);
    if (step < 5) {
      returnString.setCharAt(15-step, '0');
      returnString.setCharAt(10-step, '1');
      step++;
    } else if (step < 10) {
      returnString.setCharAt(6+step, '0');
      returnString.setCharAt(1+step, '1');
      step++; 
    } else {
      step = 0;
    }
    return returnString.toString();
  }
  
  private StringBuffer setRed(StringBuffer lightState,int lightNumber){
    lightState.setCharAt(15-step,'1');
    return lightState;
  }
  
  private StringBuffer setGreen(StringBuffer lightState,int lightNumber){
    lightState.setCharAt(10-step,'1');
    return lightState;
  }
  
  private StringBuffer setBlue(StringBuffer lightState,int lightNumber){
    lightState.setCharAt(5-step,'1');
    return lightState;
  }
  
  
}
