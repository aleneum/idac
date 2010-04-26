import totem.sound.*;

class AudioOutputHandeling extends EventSupport{

  public static final String BEAT_DETECTED = "btevt";
  public static final String VOLUME_EVENT  = "vlevt";
  
  public static final int BEAT_DELAY = 500;  
  
  private float maxVolume;
  private TPlayer player;
  private int lastChecked;
  
  public AudioOutputHandeling(TPlayer aPlayer){
    this.player = aPlayer;
    player.setSensitivity(BEAT_DELAY);
    player.loop();
    lastChecked = 0;
    maxVolume = 0.01;
  }
  
  public void update(){
    if (player.beatDetected() && (millis()-lastChecked >= BEAT_DELAY)) {
      super.fireEvent(new Event(BEAT_DETECTED, this));
      lastChecked = millis();
    }
    
    super.fireEvent(new Event(VOLUME_EVENT, this));
  }
  
  public float getVolume(){
    float outLevel = player.getOutLevel();
    if (outLevel > maxVolume) maxVolume = outLevel;
    return (outLevel / maxVolume);
  }

}
