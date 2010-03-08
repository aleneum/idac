package totem.graphic;

import processing.core.PApplet;

public class TSquareVisualization implements TVisualization{
  
  PApplet parent;	
	
  final int H_DIM = 10;
  final int V_DIM = 10;
  final float DECAY = 0.03f;
  
  int[][] board;
  int w,h;
  
  public TSquareVisualization(PApplet aParent){
    this.parent = aParent;
	  
    // check if H_DIM and V_DIM have valid values
    if (V_DIM < 1 || H_DIM < 1){
      PApplet.println("FATAL: H_DIM and/or V_DIM contain invalid values");
    }
    
    board = new int[H_DIM][V_DIM];
    
    w = PApplet.parseInt(parent.width/H_DIM);
    h = PApplet.parseInt(parent.height/V_DIM);
    
    // Two nested loops allow us to visit every spot in a 2D array.   
    // For every column I, visit every row J.
    for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        board[i][j] = 0;
      }
    }
  }
  
  public void kick(){
    int col =  PApplet.parseInt(parent.random(H_DIM));
    int row =  PApplet.parseInt(parent.random(V_DIM));
    board[col][row] = 255;
  }
  
  // not implemented yet
  public void snare(){}
  public void hat(){}
  
  public void draw(){
   for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        int value = board[i][j];
        if (value>0) value--;
        parent.fill(value);
        parent.rect(i*w,j*h,w,h);
        board[i][j] = value - PApplet.parseInt(DECAY*value);
      }
    }
  }
}
