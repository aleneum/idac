import totem.sound.*;

class AudioOutputHandeling extends Observable implements Observer{

  public static final String BEAT_DETECTED = "btevt";
  public static final String VOLUME_EVENT  = "vlevt";
  public static final int BEAT_DELAY = 1000;
  
  private float maxVolume;
  private TPlayer player;
  private int lastChecked;

  private ArrayList upList, downList;
  
  public AudioOutputHandeling(TPlayer aPlayer){
    initLists();
    this.player = aPlayer;
    player.play();
    lastChecked = 0;
    maxVolume = 0.01;
  }
  
  public void refresh(){
    if (player.beatDetected() && (millis()-lastChecked >= BEAT_DELAY)){
      super.setChanged();
      super.notifyObservers(new Event(BEAT_DETECTED, this));
      lastChecked = millis();
    }
    super.setChanged();
    super.notifyObservers(new Event(VOLUME_EVENT, this)); 
  }
  
  public float getVolume(){
    float outLevel = player.getOutLevel();
    if (outLevel > maxVolume) maxVolume = outLevel;
    return (outLevel / maxVolume);
  }
  
  public void update(Observable ob, Object o){
    if (o!=null){
      Event e = (Event) o;
      String msg = e.getMessage();
      if (msg.equals(Model.LEVEL_NEXT)){
        Model m = (Model) ob;
        levelStep(m.getLevel(), m.getNextLevel());
      }
    }
  }
  
  private void levelStep(int level, int nextLevel){
    println("LevelStep " + level + " -> " +nextLevel);
    if (level < nextLevel) {
      ArrayList filesToAdd = (ArrayList) upList.get(level);
      for (int i=0; i < filesToAdd.size(); i++){
        this.player.addToQueue((String) filesToAdd.get(i));
      }
    } else if (level > nextLevel) {
      ArrayList filesToAdd = (ArrayList) downList.get(nextLevel);
      for (int i=0; i < filesToAdd.size(); i++){
        this.player.addToQueue((String) filesToAdd.get(i));
      }
    }
  }
  
  private void initLists(){
    ArrayList fileList = new ArrayList();
    
    upList = new ArrayList(7);
   
    // level0 -> level1 
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP1);
    upList.add(fileList);
    
    // level1 -> level2
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.BRIDGE1);
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP2);
    upList.add(fileList);
    
    // level2 -> level3
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP3);
    upList.add(fileList);
    
    // level3 -> level4
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.BRIDGE3);
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP4);
    upList.add(fileList);
    
    // level4 -> level4a
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.BRIDGE4);
    upList.add(fileList);
    
    // level4a -> level5
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP5);
    upList.add(fileList);
    
    // level5-> level5a
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.SILENCE);
    upList.add(fileList);
    
    // level5 -> level6 
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.FINAL);
    upList.add(fileList);
    
    
    // DOWN LIST
    downList = new ArrayList(7);
    
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.SILENCE);
    downList.add(fileList);
    
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.BACKBRIDGE1);
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP1);
    downList.add(fileList);
    
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP2);
    downList.add(fileList);
    
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.BACKBRIDGE3);
    fileList.add(PlayList.AUDIO_PATH + PlayList.LOOP3);
    downList.add(fileList);
    
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.FINAL);
    downList.add(fileList);
    
    fileList = new ArrayList();
    fileList.add(PlayList.AUDIO_PATH + PlayList.SILENCE);
    downList.add(fileList);
  }
}
