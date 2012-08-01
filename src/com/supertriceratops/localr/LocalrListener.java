package com.supertriceratops.localr;

import java.text.DecimalFormat;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

public class LocalrListener implements LocationListener {

	private TextView locationView;
	private TextView typeView;
	private DecimalFormat df;
	private String deviceId;
	private int count;
	
	LocalrListener(TextView l, TextView t, String id) {
		locationView = l;
		typeView = t;
		deviceId = id;
		count = 0;
		df = new DecimalFormat("#.####");
	}
	
	public void onLocationChanged(Location location) {
		count++;
		locationView.setText(count + " (" + df.format(location.getLatitude()) + ", " 
				+ df.format(location.getLongitude()) + ")");
		new Checker(typeView, deviceId).execute(location);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
}
