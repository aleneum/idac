/*                 LightLevel Left                   LightBar        Symbols  VolumeLights
*  |-----------------------------------------|        |              |-----|  |---------|
*  00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 21 22 23 24 25 26 27 28 29
*  R1 G1 B1 R2 G2 B2 R3 G3 B3 R4 G4 B4 R5 G5 B5 EL ER LB ML MR SL L1 L2 L3 V1 V2 V3 V4 V5
*                                                         |-|      |      
*                                                        Motor     GlitterBallLights      
*/

public interface LightShowInterface{
  public LightState getNextState(TotemState state);
  public void reset();
  public void beatDetected();
  
}

public class LightShowOff implements LightShowInterface{
  LightState off;
  public LightShowOff(){
    off = new LightStateOff();
  }
  
  public LightState getNextState(TotemState state){
    return off;
  }
  
  public void beatDetected() {};
  
  public void reset() {}
  
}

public class LightShowSimpleBar implements LightShowInterface {
  LightState simple;
  String currentColor;
  ArrayList colors;
  
  public LightShowSimpleBar() {
    simple = new LightStateOff();
  }
  
  public void beatDetected(){}
  
  public void reset(){}
  
  public LightState getNextState(TotemState state){
    simple = new LightStateOff();   
    float motion = 0;
    if (state.getInstantLeft() > state.getInstantRight()){
      motion = state.getInstantLeft(); 
    } else {
      motion = state.getInstantRight();
    }
    
    if (motion > 0.1){
      simple.setFirst(LightState.WHITE);
    } 
    if (motion > 0.2){
      simple.setSecond(LightState.WHITE);
    }
    if (motion > 0.4){
      simple.setThird(LightState.WHITE);
    }
    if (motion > 0.6){
      simple.setFourth(LightState.WHITE);
    }
    if (motion > 0.8){
      simple.setFifth(LightState.WHITE);
    }
    return simple;
  }
  
}

public class LightShowColorBar implements LightShowInterface {
  LightState simple;
  String currentColor;
  ArrayList colors;
  
  public LightShowColorBar() {
    simple = new LightStateOff();
    colors = new ArrayList(3);
    colors.add(LightState.RED);
    colors.add(LightState.GREEN);
    colors.add(LightState.BLUE);
    currentColor = LightState.RED;
  }
  
  public void beatDetected(){
    currentColor = (String) colors.get(floor(random(3)));
    println ("new Color: " + currentColor);
  }
  
  public void reset(){
    currentColor = LightState.RED;
  }
  
  public LightState getNextState(TotemState state){
    simple = new LightStateMono(LightState.BLUE);   
    float motion = 0;
    if (state.getInstantLeft() > state.getInstantRight()){
      motion = state.getInstantLeft(); 
    } else {
      motion = state.getInstantRight();
    }
    
    if (motion > 0.1){
      simple.setFirst(currentColor);
    } 
    if (motion > 0.2){
      simple.setSecond(currentColor);
    }
    if (motion > 0.4){
      simple.setThird(currentColor);
    }
    if (motion > 0.6){
      simple.setFourth(currentColor);
    }
    if (motion > 0.8){
      simple.setFifth(currentColor);
    }
    return simple;
  } 
}

public class LightShowClavilux implements LightShowInterface{
  String fgColor, bgColor;
  float position;
  int direction;
  LightState current;
  
  public LightShowClavilux(){
    fgColor = LightState.YELLOW;
    bgColor = LightState.BLUE;
    position = 0;
    direction = 1;
  }
  
  public void beatDetected(){
    String tmp = fgColor;
    fgColor = bgColor;
    bgColor = tmp;
  }
  
  public void reset(){
    position = 0;
  }
  
  public LightState getNextState(TotemState state){
     current = new LightStateMono(bgColor);
     float motion = 0;
      if (state.getInstantLeft() > state.getInstantRight()){
        motion = state.getInstantLeft(); 
      } else {
        motion = state.getInstantRight();
      }
      
      position += direction*motion;
      
      if (position < 0){
        position = 0;
        direction = 1;
      } else if (position > 4) {
        position = 4;
        direction = -1;
      }
      
      current.setLight(floor(position), fgColor); 
                 return current;
  }
}
 
public class LightShowPulse implements LightShowInterface{
    LightState current;
    ArrayList colors;    
    String next;
  
    public LightShowPulse(){
      current = new LightStateMono(LightState.WHITE);
      
      colors = new ArrayList();
      colors.add(LightState.RED);
      colors.add(LightState.BLUE);
      colors.add(LightState.GREEN);
      colors.add(LightState.YELLOW);
      colors.add(LightState.MAGENTA);
      colors.add(LightState.CYAN);
      next = LightState.WHITE;
    }
    
    public void beatDetected(){
      next = (String) colors.get(floor(random(6)));
    }
    
    public void reset(){
      current = new LightStateMono(LightState.WHITE);
    }
    
    public LightState getNextState(TotemState state){
      for (int i=4; i > 0; i--){
        current.setLight(i,current.getLight(i-1));
      }
      current.setLight(0, next);
      return current;
    }
    
 }

public class LightShowRainbow implements LightShowInterface{
    ArrayList colors;    
    LightState current;
    Iterator iterator;
    
    public LightShowRainbow(){
      current = new LightStateMono(LightState.WHITE);
      
      colors = new ArrayList();
      colors.add(LightState.RED);
      colors.add(LightState.BLUE);
      colors.add(LightState.GREEN);
      colors.add(LightState.YELLOW);
      colors.add(LightState.MAGENTA);
      colors.add(LightState.CYAN);
      iterator = colors.iterator();
    }
    
    public void beatDetected(){}
    
    public void reset(){
      current = new LightStateMono(LightState.WHITE);
    }
    
    public LightState getNextState(TotemState state){
      if (!iterator.hasNext()){
        iterator = colors.iterator();
      }
      String col = (String) iterator.next();
      for (int i=4; i > 0; i--){ 
        current.setLight(i,current.getLight(i-1));
      }
      current.setLight(0,col);
      return current;
    }
   
} 

