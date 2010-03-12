package totem.comm;

import processing.serial.Serial;
import processing.core.PApplet;

public class TSerialCommunicator {

	Serial port;
	float value = 0;

	public TSerialCommunicator(PApplet parent) {
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

	public float getActivity(){
		return value;
	}

}
