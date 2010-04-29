import processing.serial.*;
import totem.comm.*;

class Controller implements Observer{

  public static final int STEP_MILLIS = 10;
  
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
      }
    } else {
      model.initNextLevel();
    }
  }
  
  public void levelChanged(int level){
    switch(level){
      case 0:   this.show = new LightShowOff();
                  model.setEnabled(false);
                  this.sms.sendEnableMessage(Model.ENABLE_OFF);
                  break;
      case 1:   this.show = new LightShowSimpleBar();
                  model.setEnabled(true);
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
      case 2:   this.show = new LightShowColorBar();
                  model.setEnabled(true);
                  this.sms.sendEnableMessage(Model.ENABLE_BOTH);
                  break;
      case 3:   this.show = new LightShowClavilux();
                  this.sms.sendMotorMessage(Model.MOTOR_DOWN);
                  break;
      case 4:   this.show = new LightShowPulse();
                  this.sms.sendMotorMessage(Model.MOTOR_UP);
                  break;
      case 5:   this.show = new LightShowRainbow();
                  this.sms.sendMotorMessage(Model.MOTOR_SPIN);
                  break;
      case 6:   this.show = new LightShowShutDown();
                  this.sms.sendMotorMessage(Model.MOTOR_OFF);
                  break;
    }
    this.sms.sendSymbolMessage(level);
  }
  
  public void step(TotemState state){
    if (millis() > (lastStep + STEP_MILLIS)) {
      this.sms.sendLightLevelMessage(this.show.getNextState(state));
      if (model.getLevel() >= 3 ){
        if ((state.getMotionLeft() < Model.LEVEL03_LIMIT) || (state.getMotionRight() < Model.LEVEL03_LIMIT)){
           if ((state.getMotionLeft() < Model.LEVEL03_LIMIT) && (model.isLeftEnabled())){
             model.setLeftEnabled(false);
             this.sms.sendEnableMessage(Model.ENABLE_RIGHT);
           } else if (((state.getMotionRight() < Model.LEVEL03_LIMIT) && (model.isRightEnabled()))){
             model.setRightEnabled(false);
             this.sms.sendEnableMessage(Model.ENABLE_LEFT);
           }
        } else if(!model.isLeftEnabled() || !model.isRightEnabled()){
            model.setEnabled(true);
            this.sms.sendEnableMessage(Model.ENABLE_BOTH);
        }
      }
      lastStep = millis();
    }
  }
  
}
