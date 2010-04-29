import totem.comm.*;



class SerialMessageService{
  
  // TemporalTestingStuff
  boolean leftEnabled, rightEnabled;
  
  TSerialCommunicator communicator;
  
  public SerialMessageService(TSerialCommunicator aCommunicator){
    //TemporalTestingStuff
    leftEnabled = false;
    rightEnabled = false;
    
    this.communicator = aCommunicator;
  }
  
  public void sendSymbolMessage(int level){

    //DELETE ME
    if (level >= 4){
      fill(color(255,255,255));
      level = 0;
    } else {
      fill(color(0,0,0));
    }
    ellipse(250, 200, 30, 30);
    if (level == 3){
      fill(color(255,255,255));
      level = 0;  
    } else {
      fill(color(0,0,0));
    }
    ellipse(200, 200, 30, 30);
    
    if (level == 2){
      fill(color(255,255,255));
      level = 0;  
    } else {
      fill(color(0,0,0));
    }
    ellipse(150, 200, 30, 30);
    // DELETE ME END
    
    StringBuffer msg = new StringBuffer("s000");
    if ((level > 1) && (level < 5)) {
      msg.setCharAt(level-1,'1');
    }
    sendMessage(msg.toString());
  }
  
  public void sendMotorMessage(String motorMsg){
    
    // DELETE ME
    if (motorMsg.charAt(0) == '1'){
      fill(color(0,255,0));
    } else if (motorMsg.charAt(1) == '1') {
      fill(color(255,0,0));
    } else if (motorMsg.charAt(2) == '1') {
      fill(color(255,255,255));
    } else{
      fill(color(0,0,0));
    }
    ellipse(185,300,50,50);
    // DELETE ME END
    
    motorMsg = "m" + motorMsg;
    sendMessage(motorMsg);
  }
  
  public void sendVolumeMessage(float volume){
    // DELETE ME 
    if (volume > 0) {
      fill(color(0,255,0));
    } else {
      fill(color(255,255,255));
    }
    
    ellipse (20,500,20,20);
    if (volume > 0.2) {
      fill(color(0,255,0));
    } else {
      fill(color(255,255,255));
    }
    
    ellipse (20,460,20,20);
    if (volume > 0.4) {
      fill(color(0,255,0));
    } else {
      fill(color(255,255,255));
    }
    
    ellipse (20,420,20,20);
    if (volume > 0.6) {
      fill(color(0,255,0));
    } else {
      fill(color(255,255,255));
    }
    
    ellipse (20,380,20,20);
    if (volume > 0.8) {
      fill(color(0,255,0));
    } else {
      fill(color(255,255,255));
    }
    
    ellipse (20,340,20,20);
    
    // DELETE ME END
    
    StringBuffer msg = new StringBuffer("v00000");

    if (volume > 0) {
      msg.setCharAt(1,'1');
    } 
    if (volume > 0.2) {
      msg.setCharAt(2,'1');
    }
    if (volume > 0.4) {
      msg.setCharAt(3,'1');
    }
    if (volume > 0.6) {
      msg.setCharAt(4,'1');
    }
    if (volume > 0.8) {
      msg.setCharAt(5,'1');
    }   
    sendMessage(msg.toString());
  }
  
  public void sendBeatMessage(){
    sendMessage("b");
  }
 
  public void sendEnableMessage(String msg){
    // DELETE ME
    if (msg.charAt(0) == '1') {
      leftEnabled = true;
    } else {
      leftEnabled = false;
    } 
    
    if (msg.charAt(1) == '1'){
      rightEnabled = true;
    } else {
      rightEnabled = false;
    } 
    // DELETE ME END
    sendMessage("e" + msg);
  } 
  
  public void sendLightLevelMessage(LightState state){
    String prev = state.getStateString();
    
    // DELETE ME
    
    for (int i=0; i < 5; i++){
      int r = 0;
      int g = 0;
      int b = 0;
      
      if (prev.charAt(i*3) == '1'){
        r = 255;
      }
      if (prev.charAt(i*3+1) == '1'){
        g = 255;
      }
      if (prev.charAt(i*3+2) == '1'){
        b = 255;
      }
      
      if (leftEnabled){
        fill(color(r,g,b));
      } else {
        fill(color(0,0,0));
      } 
      rect(150,500-(i*40),30,30);
      
      if (rightEnabled){
        fill(color(r,g,b));
      } else {
        fill(color(0,0,0));
      }
      rect(200,500-(i*40),30,30);
    }
    
    // DELETE ME END
    
    String msg = "l" + prev;
    sendMessage(msg);
  }

  public void sendTopMessage(String msg){
    sendMessage("t" + msg);
  }
  
  private void sendMessage(String msg){
    communicator.serialSend(msg);
    //println(minute() + ":" + second() + "->" + msg);
  }
  
}
