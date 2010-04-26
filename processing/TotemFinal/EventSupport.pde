class EventSupport {
  
  ArrayList eventListener;
  
  public EventSupport(){
    eventListener = new ArrayList();
  }
  
  public void addEventListener(EventListener e){
    if (!eventListener.contains(e)) {
      eventListener.add(e);
    }
  }
  
  public void removeEventListener(EventListener e){
    if (eventListener.contains(e)){
      eventListener.remove(eventListener.indexOf(e));
    }
  }
  
  private void fireEvent(Event e){
    for(int i=0; i < eventListener.size(); i++){
      EventListener listener = (EventListener) eventListener.get(i);
      listener.handleEvent(e);
    }
  }

}
