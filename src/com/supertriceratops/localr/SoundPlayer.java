package com.supertriceratops.localr;

import java.lang.reflect.Field;
import java.util.HashMap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundPlayer implements SensorEventListener {

	public static final float NOISE = 0.5f;
	
	private SoundPool soundPool;
    private HashMap<Integer, Integer> soundsMap;
    private int soundCount;
    private AudioManager mgr;
    private Context context;
	private float mLastX;
	private float mLastY;
	private float mLastZ;
	private boolean mInitialized;
    
	public SoundPlayer(AudioManager m, Context c) {
		// Set some sounds up
		soundPool     = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundCount    = 0;
        mgr           = m;
        context       = c;
        soundsMap     = new HashMap<Integer, Integer>();
        soundCount    = loadSounds(soundPool, soundsMap);
        mInitialized  = false;
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
		} else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
		
			float speed = Math.abs(deltaX * .4f);
			int sound = Math.abs((int)Math.floor((deltaY * deltaZ) / 2.7f));
			sound = Math.min(sound, soundCount);
			speed = Math.min(2.0f, speed);
			speed = Math.max(0.8f, speed);
			Log.e("Checker", "Got value: " + deltaY + "," + deltaZ + ", -- " + speed + " -- " + sound);
			playSound(sound, speed);
		}
	}
	
	private void playSound(int sound, float fSpeed) {
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    float volume = streamVolumeCurrent / streamVolumeMax;  
	 
	    soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, fSpeed);
	}

    private int loadSounds(SoundPool pool, HashMap<Integer, Integer> map) {
    	int count = 0;
    	try {
			Class<?> raw = R.raw.class;
			for (Field f : raw.getFields()) {
				map.put(count, soundPool.load(context, f.getInt(raw), 1));
				count++;
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		Log.e("Checker", "Loaded " + count + " sounds.");
		return count - 1;
    }
}
