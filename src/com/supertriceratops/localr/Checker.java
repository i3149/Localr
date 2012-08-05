package com.supertriceratops.localr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class Checker extends AsyncTask<Location, Integer, String> {

	public static final String URL_LOCALR_BASE = "http://localr.herokuapp.com/user/";
	
	private TextView view;
	private String deviceId;
	private String url_final;
	
	public Checker(TextView v, String id) {
		view = v;
		deviceId = id;
		url_final = URL_LOCALR_BASE + deviceId + "/" + "locations";
	}
	
	private String getPlace (Location location) {
		
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url_final);		
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())));
		nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
		nameValuePairs.add(new BasicNameValuePair("sensor", "true"));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("Checker", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
	@Override
	protected void onPostExecute(String raw) {
		if (raw != null) {
			try {
				
				JSONObject obj = new JSONObject(raw);
				JSONArray jsonArray = obj.getJSONArray("types");
				String name = obj.getString("name");
				if (jsonArray.length() > 0) {
					view.setText(name + ": " + jsonArray.getString(0));
				}
			} catch (JSONException e) {
				e.printStackTrace();	
			}
		}
	}

	@Override
	protected String doInBackground(Location... locations) {
		if (locations.length > 0) {
			return getPlace(locations[0]);
		}
		return null;
	}
}
