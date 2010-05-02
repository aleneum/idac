import totem.sound.*;
import ddf.minim.*;

class AudioInputHandeling{

  public static final float NOISE_LIMIT = 0.6;
  public static final float NOISE_INCREASE = 0.005;
  public static final float NOISE_DECREASE = 0.001;
  
  AudioInput in;
  float noiseLevel = 0;
  float maxInLevel = 0.01;
  
  public AudioInputHandeling(AudioInput anIn){
    this.in = anIn;
  }
  
  private float getInputLevel(){
    return this.in.left.level();
  }
  
  public void update(){
    float inLevel = getInputLevel();
    if (maxInLevel < inLevel) maxInLevel = inLevel;
    if (inLevel > NOISE_LIMIT * maxInLevel) {
      if (noiseLevel < 1) {
        noiseLevel += NOISE_INCREASE;
      } else {
        noiseLevel = 1;
      }
    } else {
      if (noiseLevel > 0) {
        noiseLevel -= NOISE_DECREASE;
      } else {
        noiseLevel = 0;
      }
    }
  }
  
  public float getNoiseLevel(){
    return noiseLevel;  
  }
  
}
