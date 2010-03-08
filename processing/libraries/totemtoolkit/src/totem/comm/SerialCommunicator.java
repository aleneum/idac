package totem.comm;

import processing.serial.*;
import processing.core.*;

public class SerialCommunicator {

	Serial port;
	float value = 0;

	public SerialCommunicator(PApplet parent) {
		PApplet.println(Serial.list());
		port = new Serial(parent, Serial.list()[0], 9600);
		port.bufferUntil('\n');
	}

	public void serialEvent (Serial myPort){
		String inString = port.readStringUntil('\n');
		if (inString != null) {
			value = PApplet.parseFloat(inString);
		}
	}

	float getActivity(){
		return value;
	}

}
