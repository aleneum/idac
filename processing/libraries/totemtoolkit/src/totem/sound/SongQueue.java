package totem.sound;

import java.util.*;

public class SongQueue
{
  // Internal storage for the queue'd objects
  private Vector vec = new Vector();

  synchronized public int numWaiting() {
    return vec.size();
  }

  synchronized public void put( Object o ) {
    // Add the element
    vec.addElement( o );

    // There might be threads waiting for the new object --
    // give them a chance to get it
    notifyAll();
  }

  synchronized public Object get() {
    while (true) {
      if (vec.size()>0) {
        // There's an available object!
        Object o = vec.elementAt( 0 );

        // Remove it from our internal list, so someone else
        // doesn't get it.
        vec.removeElementAt( 0 );

        // Return the object
        return o;
      } else {
        // There aren't any objects available.  Do a wait(),
        // and when we wake up, check again to see if there
        // are any.
        try { wait(); } catch( InterruptedException ie ) {}
      }
    }
  }
}
