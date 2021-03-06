package com.supertriceratops.localr;

import java.util.UUID;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class Localr extends Activity {

	private TextView locationView;
	private TextView typeView;
	private TextView idView;
	private String locationProvider = LocationManager.GPS_PROVIDER;
	private LocationManager locationManager;
	private LocalrListener listener;
	private String deviceId;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SoundPlayer player;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localr);
  
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        deviceId = deviceUuid.toString();
        
        locationView = (TextView) findViewById (R.id.location);
        typeView = (TextView) findViewById (R.id.type);
        idView = (TextView) findViewById (R.id.deviceId);
                
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocalrListener(locationView, typeView, deviceId);
        
        locationView.setText("Starting Service");
        typeView.setText("SS");
        idView.setText(deviceId);
        
        // And enable the sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
        	Log.e("Checker", "No Acceleromiter found");	
        }
                
        player = new SoundPlayer((AudioManager)getSystemService(Context.AUDIO_SERVICE), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_localr, menu);
        return true;
    }
    
    @Override
    protected void onStart() { 
    	super.onStart();
        locationManager.requestLocationUpdates(locationProvider, 0, 0, listener);
    }
    
    @Override
    protected void onStop() { 
    	super.onStop();
    	locationManager.removeUpdates(listener);
    }
    
    @Override
    protected void onResume() {
      super.onResume();
      mSensorManager.registerListener(player, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
      super.onPause();
      mSensorManager.unregisterListener(player);
    }
    
    public void handleNetworkChoice(View view) {
    	// Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.gps:
                if (checked)
                	locationProvider = LocationManager.GPS_PROVIDER;
                    typeView.setText("GPS");                   
                break;
            case R.id.network:
                if (checked)
                	locationProvider = LocationManager.NETWORK_PROVIDER;
                	typeView.setText("NETWORK");
                break;
        }
        
        locationManager.removeUpdates(listener);
        locationManager.requestLocationUpdates(locationProvider, 0, 0, listener);
        Location last = locationManager.getLastKnownLocation(locationProvider);
        if (last != null) {
        	new Checker(typeView, deviceId).execute(last);
        }
    }
    
}
