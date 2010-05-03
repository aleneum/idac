import processing.serial.*;
import totem.comm.*;

class Controller implements Observer{
  
  // serial communication
  SerialMessageService sms;
  TSerialCommunicator communicator;
  LightShowInterface show;
  Model model;

  int lastStep;
  float blink;
  boolean symbolOn;

  public Controller(TSerialCommunicator comm, Model model){
    this.communicator = comm;
    this.model = model;
    lastStep = 0;
    blink = 0;
    this.show = new LightShowOff();
    this.sms = new SerialMessageService(communicator);
    model.setEnabled(false);
  }
  
  public void init() {
    levelChanged(0);
  }

  public void update(Observable ob, Object o){
    if (o != null){
      Event e = (Event) o;
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
      } else if (msg.equals(Model.ENABLE_CHANGED)) {
        this.sms.sendEnableMessage(model.getEnabledString());
      }
    } else {
      model.initNextLevel();
    }
  }
  
  public void levelChanged(int level){
    switch(level){
      case 0:   this.show = new LightShowOff();
                  model.setEnabled(true);
                  this.sms.sendVolumeMessage(0);
                  this.sms.sendSymbolMessage(level);
                  break;
      case 1:   this.show = new LightShowSimpleBar();
                  model.setEnabled(true);
                  this.sms.sendMotorMessage(Model.MOTOR_OFF);
                  this.sms.sendSymbolMessage(level);
                  break;
      case 2:   this.show = new LightShowColorBar();
                  model.setEnabled(true);
                  this.sms.sendTopMessage("0");
                  break;
      case 3:   this.show = new LightShowClavilux();
                  this.sms.sendMotorMessage(Model.MOTOR_DOWN);
                  this.sms.sendFlashMessage("0");
                  break;
      case 4:   this.show = new LightShowPulse();
                  this.sms.sendFlashMessage("1");
                  break;
      case 5:   this.show = new LightShowRainbow();
                  this.sms.sendMotorMessage(Model.MOTOR_UP);
                  this.sms.sendSymbolMessage(level);
                  break;
      case 6:   this.show = new LightShowRainbow();
                  this.sms.sendMotorMessage(Model.MOTOR_SPIN);
                  break;
      case 7:   this.sms.sendMotorMessage(Model.MOTOR_OFF);
                  break;
      case 8:   this.show = new LightShowShutDown();
                  this.sms.sendMotorMessage(Model.MOTOR_DOWN);
                  this.sms.sendFlashMessage("0");
                  this.sms.sendTopMessage("0");
                  break;
    }
  }
  
  public void step(TotemState state){
    if (model.getLevel() != 7) {
      this.sms.sendLightLevelMessage(this.show.getNextState(state));
      if ((model.getLevel() >= 3) && (model.getLevel() < 5) ){
        this.model.setLeftEnabled(state.getMotionLeft() > Model.LEVEL03_LIMIT);
        this.model.setRightEnabled(state.getMotionRight() > Model.LEVEL03_LIMIT);
      }
      
        switch(state.getLevel()){
          case 2: blinkSymbol(state.getLevel(),map(state.getHigherMotion(),Model.LEVEL02_LIMIT,Model.LEVEL03_LIMIT,0.2,1));
                    break;
          case 3: blinkSymbol(state.getLevel(),map(state.getLowerMotion(),0,Model.LEVEL03_LIMIT,0.2,1));
                    break;
          case 4: blinkSymbol(state.getLevel(), map(state.getSoundInput(),0,Model.LEVEL05_SOUND,0.2,1));
                    break;
        }
    }
  }
  
  private void blinkSymbol(int level, float tension) {
    blink += tension;
   if (blink < 3) {
     if (!symbolOn) {
       this.sms.sendSymbolMessage(level);
       symbolOn = true;
     }
   } else {
      if(symbolOn) {
         this.sms.sendSymbolMessage(0);
         symbolOn = false;
      }
      if (blink > 4) {
        blink = 0;
      }
   }
 } 
}
