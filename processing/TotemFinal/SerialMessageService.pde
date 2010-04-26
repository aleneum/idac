import totem.comm.*;


class SerialMessageService{
  TSerialCommunicator communicator;
  
  public SerialMessageService(TSerialCommunicator aCommunicator){
    this.communicator = aCommunicator;
  }
  
  public void sendSymbolMessage(int level){
    StringBuffer msg = new StringBuffer("s000");
    if ((level > 1) && (level < 5)) {
      msg.setCharAt(level-1,'1');
    }
    sendMessage(msg.toString());
  }
  
  public void sendMotorMessage(String motorMsg){
    motorMsg = "m" + motorMsg;
    sendMessage(motorMsg);
  }
  
  public void sendVolumeMessage(float volume){
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
    sendMessage("e" + msg);
  } 
  
  public void sendLightLevelMessage(LightState state){
    String msg = "l" + state.getStateString();
    sendMessage(msg);
  }

  public void sendTopMessage(String msg){
    sendMessage("t" + msg);
  }
  
  private void sendMessage(String msg){
    communicator.serialSend(msg);
    println(minute() + ":" + second() + "->" + msg);
  }
  
}
