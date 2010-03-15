package totem.comm;

import processing.serial.Serial;
import processing.core.PApplet;



/** 
 * Communication class written for serial communication with Arduino.
 * 
 * @author Alex
 *
 */
public class TSerialCommunicator {

	Serial port;
	float value = 0;

	/** 
	 * Default constructor for TSerialCommunicator. Creates new {@link Serial} 
	 * object and registers {@link PApplet}. The default stop token is a newline 
	 * ('\n').
	 *  
	 * @param parent the main processing application
	 * @see serialEvent
	 */
	public TSerialCommunicator(PApplet parent) {
		PApplet.println(Serial.list());
		port = new Serial(parent, Serial.list()[0], 9600);
		port.bufferUntil('\n');
	}

	
	/**
	 * Method to be called if a new serial event was registered. 
	 * IMPORTANT: This serialEvent method is not called by default.
	 * You have to implement the serialEvent method in the main application
	 * and forward the Serial event to this method.
	 * <p>
	 * Example (example.pde):
	 * <pre>
	 * <code>
	 * TSerialCommunicator communicator;
	 * void serialEvent(Serial serialPort){
	 * 	this.communicator.serialEvent(serialPort);
	 * }
	 * </code>
	 * </pre>
	 * <p>
	 * Afterward you can get the result via the getActivity method.
	 * @param serialPort The Serial object that called a serialEvent
	 * @see getActivity
	 */
	public void serialEvent (Serial serialPort){
		String inString = serialPort.readStringUntil('\n');
		if (inString != null) {
			value = PApplet.parseFloat(inString);
		}
	}

	/**
	 * Returns the value parsed during the last time serialEvent was called.
	 * 
	 * @return last parsed serial message 
	 */
	public float getActivity(){
		return value;
	}
}
