import hypermedia.video.*;

OpenCV opencv;

void setup() {

    size( 640, 480 );

    // open video stream
    opencv = new OpenCV( this );
    opencv.capture( 640, 480 );

}

void draw() {

    background(192);

    opencv.read();           // grab frame from camera
    opencv.threshold(0);    // set black & white threshold 

    image( opencv.image(), 0, 0 );
    
    // find blobs
    
    /*Blob[] blobs = opencv.blobs( 10, width*height/2, 100, true, OpenCV.MAX_VERTICES*4 );

    // draw blob results
    for( int i=0; i<blobs.length; i++ ) {
        beginShape();
        for( int j=0; j<blobs[i].points.length; j++ ) {
            vertex( blobs[i].points[j].x, blobs[i].points[j].y );
        }
        endShape(CLOSE);
    }*/

}


