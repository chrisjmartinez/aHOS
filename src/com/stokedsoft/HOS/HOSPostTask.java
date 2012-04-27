package com.stokedsoft.HOS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class HOSPostTask extends AsyncTask<HOSFix, Void, Boolean> {
	private static final String TAG = "HOSPostTask";
		
    @Override
    protected Boolean doInBackground(HOSFix ... fixes) {
    	Log.i("HOSPostTask", ".doInBackground() — start");
    	
    	boolean bRet = false;
        
		// get location
        Double dLatitude = 0.0;
        Double dLongitude = 0.0;
        Long lTrackID = 0L;
        Float dSpeed;
        Float dAccuracy;
        Double dAltitude;

    	// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.kickserve.net/loco/poc/web/postdata.php");
        
		// upload values		
		// TODO - Use HTTPS
        HOSFix fix = null;
        
        for (int nIndex = 0; nIndex < fixes.length; nIndex++) {
        	fix = fixes[nIndex];
        	
            try {
            	dLatitude = fix.getLocation().getLatitude();
            	dLongitude = fix.getLocation().getLongitude();
            	lTrackID = fix.getTrackID();
            	dSpeed = fix.getLocation().getSpeed();
            	dAccuracy = fix.getLocation().getAccuracy();
            	dAltitude = fix.getLocation().getAltitude();
            	
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("inTrackID", lTrackID.toString()));
                nameValuePairs.add(new BasicNameValuePair("inLat", dLatitude.toString()));
                nameValuePairs.add(new BasicNameValuePair("inLon", dLongitude.toString()));
                nameValuePairs.add(new BasicNameValuePair("inAcc", dAccuracy.toString()));
                nameValuePairs.add(new BasicNameValuePair("inSpd", dSpeed.toString()));
                nameValuePairs.add(new BasicNameValuePair("inAlt", dAltitude.toString()));
                nameValuePairs.add(new BasicNameValuePair("inAltAcc", dAccuracy.toString()));
                nameValuePairs.add(new BasicNameValuePair("inUser", fix.getUser()));
                nameValuePairs.add(new BasicNameValuePair("inDev", fix.getDevice()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                
                bRet = true;
            } catch (ClientProtocolException e) {
		    	Log.d(TAG, ".HOSPostTask() - doInBackground exception", e);		
				e.printStackTrace();
            } catch (IOException e) {
		    	Log.d(TAG, ".HOSPostTask() - doInBackground exception", e);		
				e.printStackTrace();
            }
        }
        
    	Log.i("HOSPostTask", ".doInBackground() - finish");
		
		return bRet;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
    	Log.i("HOSPostTask", ".onPostExecute() - start");
		
		if (result) {
		}
		
    	Log.i("HOSPostTask", ".onPostExecute() - finish");
	}
}
