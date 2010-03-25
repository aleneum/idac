package totem.visual;

import processing.core.PApplet;

/**
 * Simple beat visualization. The visualization draws squares on the screen that change
 * brightness if specific methods were called.
 * @author alex
 *
 */
public class TSquareVisualization implements TVisualization{
  
  PApplet parent;	
	
  // defines how many squares are drawn horizontally
  final int H_DIM = 10;
  // defines how many squares are drawn vertically
  final int V_DIM = 10;
  // defines how fast the brightness fades out
  final float DECAY = 0.03f;
  
  // holds the brightness values of all squares
  int[][] board;
  
  // width and height of every square
  int w,h;
  
  /**
   * Initialize the TSquareVisualization. It initialize the square's colors, 
   * width and height. In addition it holds a reference to the main application
   * for later drawing actions.
   * @param aParent main processing application that holds drawing methods
   */
  public TSquareVisualization(PApplet aParent){
    this.parent = aParent;
    this.board = new int[H_DIM][V_DIM];
    this.w = PApplet.parseInt(parent.width/H_DIM);
    this.h = PApplet.parseInt(parent.height/V_DIM);
    
    // Two nested loops allow us to visit every spot in a 2D array.   
    // For every column I, visit every row J.
    for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        board[i][j] = 0;
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void kick(){
    int col =  PApplet.parseInt(parent.random(H_DIM));
    int row =  PApplet.parseInt(parent.random(V_DIM));
    board[col][row] = 255;
  }
  
  //TODO implement
  /**
   * {@inheritDoc}
   * NOT IMPLEMENTED RIGHT NOW -- DOES NOTHING.
   */
  public void snare(){}
  
  /**
   * {@inheritDoc}
   * NOT IMPLEMENTED RIGHT NOW -- DOES NOTHING.
   */
  public void hat(){}
  
  /**
   * Draw the actual state of the square to the screen. Call this method to
   * redraw visualization.
   */
  public void draw(){
   for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        int value = board[i][j];
        if (value>0) value--;
        // set color for the square
        parent.fill(value);
        // draws a rectangle at the square's position
        parent.rect(i*w,j*h,w,h);
        // reduces brightness for the next iteration
        board[i][j] = value - PApplet.parseInt(DECAY*value);
      }
    }
  }
}
