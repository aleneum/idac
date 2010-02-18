public class ChessVisualization implements Visualization{
  
  final int H_DIM = 4;
  final int V_DIM = 4;
  final float DECAY = 0.05;
  
  int[][] board;
  int w,h;
  
  ChessVisualization(){
    
    // check if H_DIM and V_DIM have valid values
    if (V_DIM < 1 || H_DIM < 1){
      println("FATAL: H_DIM and/or V_DIM contain invalid values");
    }
    
    board = new int[H_DIM][V_DIM];
    
    w = int(width/H_DIM);
    h = int(height/V_DIM);
    
    // Two nested loops allow us to visit every spot in a 2D array.   
    // For every column I, visit every row J.
    for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        board[i][j] = 0;
      }
    }
  }
  
  public void kick(){
    int col =  int(random(H_DIM));
    int row =  int(random(V_DIM));
    board[col][row] = 255;
  }
  
  public void snare(){}
  public void hat(){}
  
  public void draw(){
   for (int i = 0; i < H_DIM; i++) {
      for (int j = 0; j < V_DIM; j++) {
        int value = board[i][j];
        fill(value);
        rect(i*w,j*h,w,h);
        board[i][j] = value - int((DECAY*value));
      }
    }
  }
}
