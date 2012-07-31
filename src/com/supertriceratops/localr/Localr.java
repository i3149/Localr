package com.supertriceratops.localr;


import java.util.UUID;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.TextView;

public class Localr extends Activity {

	private TextView locationView;
	private TextView typeView;
	private TextView idView;
	private String locationProvider = LocationManager.NETWORK_PROVIDER;
	private LocationManager locationManager;
	private LocalrListener listener;
	
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
        String deviceId = deviceUuid.toString();
        
        locationView = (TextView) findViewById (R.id.location);
        typeView = (TextView) findViewById (R.id.type);
        idView = (TextView) findViewById (R.id.deviceId);
        
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocalrListener(locationView, typeView, deviceId);
        
        locationView.setText("Starting Service");
        typeView.setText("SS");
        idView.setText(deviceId);
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
}

/*


    }
*/
