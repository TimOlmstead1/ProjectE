
//retrieved from https://www.gamedev.net/articles/programming/general-and-gameplay-programming/java-games-keyboard-and-mouse-r2439/

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;

public class MouseInput implements MouseListener, MouseMotionListener {

  private static final int BUTTON_COUNT = 3;
  // Used for relative movement
  private int dx, dy;
  // Used to re-center the mouse
  private Robot robot = null;
  // Convert coordinates from component to screen
  private Component component;
  // The center of the component
  private Point center;
  // Is this relative or absolute
  private boolean relative;
  // Polled position of the mouse cursor
  private Point mousePos = null;
  // Current position of the mouse cursor
  private Point currentPos = null;
  // Current state of mouse buttons
  private boolean[] state = null;
  // Colled mouse buttons
  private MouseState[] poll = null;
        
  private enum MouseState {
    RELEASED, // Not down
    PRESSED,  // Down, but not the first time
    ONCE      // Down for the first time
  }
        
  public MouseInput( Component component ) {
    // Need the component object to convert screen coordinates 
    this.component = component;
    // Calculate the component center
    int w = component.getBounds().width;
    int h = component.getBounds().height;
    center = new Point( w/2, h/2 );
    try {
      robot = new Robot();
    } catch( Exception e ) {
      // Handle exception [game specific]
    }
  
    // Create default mouse positions
    mousePos = new Point( 0, 0 );
    currentPos = new Point( 0, 0 );
    // Setup initial button states
    state = new boolean[ BUTTON_COUNT ];
    poll = new MouseState[ BUTTON_COUNT ];
    for( int i = 0; i < BUTTON_COUNT; ++i ) {
      poll[ i ] = MouseState.RELEASED;
    }
  }
        
  public synchronized void poll() {
    // If relative, return only the delta movements,
    // otherwise return the current position...
    if( isRelative() ) {
      mousePos = new Point( dx, dy );
    } else {
      mousePos = new Point( currentPos );
    }
    // Since we have polled, need to reset the delta
    // so the values do not accumulate
    dx = dy = 0;
    // Check each mouse button
    for( int i = 0; i < BUTTON_COUNT; ++i ) {
      // If the button is down for the first
      // time, it is ONCE, otherwise it is
      // PRESSED.  
      if( state[ i ] ) {
        if( poll[ i ] == MouseState.RELEASED )
          poll[ i ] = MouseState.ONCE;
        else
          poll[ i ] = MouseState.PRESSED;
      } else {
          // Button is not down
          poll[ i ] = MouseState.RELEASED;
      }
    }
  }
  
  //Added by me section
  public void setCenter(Point newCenter) {
	    center = newCenter;
	    }
  
  public Point getCenter() {
	    return center;
	    }
  //
  
  public boolean isRelative() {
    return relative;
  }
  
  public void setRelative( boolean relative ) {
    this.relative = relative;
    if( relative ) {
      centerMouse();
    }
  }

  public Point getPosition() {
    return mousePos;
  }

  public boolean buttonDownOnce( int button ) {
    return poll[ button-1 ] == MouseState.ONCE;
  }

  public boolean buttonDown( int button ) {
    return poll[ button-1 ] == MouseState.ONCE ||
           poll[ button-1 ] == MouseState.PRESSED;
  }
  
  public synchronized void mousePressed( MouseEvent e ) {
    state[ e.getButton()-1 ] = true;
  }

  public synchronized void mouseReleased( MouseEvent e ) {
    state[ e.getButton()-1 ] = false;
  }

  public synchronized void mouseEntered( MouseEvent e ) {
    mouseMoved( e );
  }
  
  public synchronized void mouseExited( MouseEvent e ) {
    mouseMoved( e );
  }
  
  public synchronized void mouseDragged( MouseEvent e ) {
    mouseMoved( e );
  }

  public synchronized void mouseMoved( MouseEvent e ) {
    if( isRelative() ) {
      Point p = e.getPoint();
      dx += p.x - center.x;
      dy += p.y - center.y;
      centerMouse();
    } else {
      currentPos = e.getPoint();
    }
  }
  
  public void mouseClicked( MouseEvent e ) {
    // Not needed
  }
  
  private void centerMouse() {
    if( robot != null && component.isShowing() ) {
    // Because the convertPointToScreen method 
    // changes the object, make a copy!
      Point copy = new Point( center.x, center.y );
      SwingUtilities.convertPointToScreen( copy, component );
      robot.mouseMove( copy.x, copy.y );
    }
  }
}