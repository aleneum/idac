import processing.serial.*;
import totem.comm.*;

class Controller implements EventListener{

  public static final int STEP_MILLIS = 300;
  
  // serial communication
  SerialMessageService sms;
  TSerialCommunicator communicator;
  LightShowInterface show;
  Model model;

  int lastStep = 0;

  public Controller(TSerialCommunicator comm, Model model){
    this.communicator = comm;
    this.model = model;
    this.show = new LightShowOff();
    this.sms = new SerialMessageService(communicator);
  }

  public void handleEvent(Event e){
    String msg = e.getMessage();
    if (msg.equals(AudioOutputHandeling.BEAT_DETECTED)){
      this.show.beatDetected();
      if (model.getLevel() > 2) {
        this.sms.sendBeatMessage();
      }
    } else if (msg.equals(AudioOutputHandeling.VOLUME_EVENT)){
      if (model.getLevel() > 0) {
        AudioOutputHandeling output = (AudioOutputHandeling) e.getSource();
        this.sms.sendVolumeMessage(output.getVolume());
      }
    } else if (msg.equals(Model.LEVEL_CHANGED)) {
      levelChanged(model.getLevel());
    }
  }
  
  public void levelChanged(int level){
    switch(level){
      case 0:   this.show = new LightShowOff();
                  this.sms.sendEnableMessage(Model.ENABLE_OFF);
                  break;
      case 1:   this.show = new LightShowSimpleBar();
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
      case 2:   this.show = new LightShowColorBar();
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
      case 3:   this.show = new LightShowClavilux();
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
      case 4:   this.show = new LightShowPulse();
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
      case 5:   this.show = new LightShowRainbow();
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
    }
  }
  
  public void step(TotemState state){
    if (millis() > (lastStep + STEP_MILLIS)) {
      this.sms.sendLightLevelMessage(this.show.getNextState(state));
      lastStep = millis();
    }
  }
  
}
