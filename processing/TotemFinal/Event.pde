class Event{
  Object source;
  String msg;
  
  public Event(String aMsg, Object src){
    this.source = src;
    this.msg = aMsg;    
  }
  
  public String getMessage(){
    return this.msg;
  }
  
  public Object getSource(){
    return this.source;
  }
  
}
