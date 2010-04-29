package totem.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.AudioMetaData;
import ddf.minim.spi.AudioRecording;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;

public class TAudioRecordingStream implements AudioRecordingStream {

	@Override
	public int getLoopCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AudioMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMillisecondLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMillisecondPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loop(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoopPoints(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMillisecondPosition(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int bufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAudioEffect(AudioEffect arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAudioListener(AudioListener arg0) {
		// TODO Auto-generated method stub
		
	}

}
