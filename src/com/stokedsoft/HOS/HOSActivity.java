package com.stokedsoft.HOS;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class HOSActivity extends MapActivity {
	LinearLayout linearLayout;
	MapView mapView;
	ArrayList<HOSFix> fixes;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
    	                           
        mbMainDetector = false;        
        
        ToggleButton mainDetector = (ToggleButton)findViewById(R.id.toggleButton1);
        editCurrentLat = (EditText)findViewById(R.id.editLat);
        editCurrentLng = (EditText)findViewById(R.id.editLon);
        
        listener = new MyLocationListener();
        
        // register the onClick listeners
        mainDetector.setOnClickListener(new OnClickListener() {
        	@Override 
        	public void onClick(View v) {    		
        		// toggle the detection mode
        		mbMainDetector = !mbMainDetector;

        		if (true == mbMainDetector) {
        			// start listening
        			Time now = new Time();
        			now.setToNow();
        			lTrackID = now.toMillis(true);
        			
        			EditText userText = (EditText)findViewById(R.id.editUser);
        		    sUser = userText.getText().toString();
        		    sDevice = android.os.Build.MODEL;

        		    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        		    if (null == location) {
        		    	// try the network location
        		    	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        		    }
        		    
        		    if (null != location) {
        		    	listener.onLocationChanged(location);
        		    }
        			try {
        		        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, listener);  
        			}
        			catch (NumberFormatException ex) {
        				Log.d(getClass().getSimpleName(), "NumbersFormatExcetion: " + ex.getLocalizedMessage());
        			}
        		}
        		else {
        			// stop listening
        			locationManager.removeUpdates(listener);
        			
        			flushCache();
        		}        		
        	}        	
        });
        
        fixes = new ArrayList<HOSFix>();
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

    protected void flushCache() {
		HOSFix[] cache = new HOSFix[fixes.size()];
		int nIndex = 0;
		for (HOSFix index : fixes) {
			cache[nIndex++] = index;
		}
		
		// post the cache of fixes
		new HOSPostTask().execute(cache);
		
		// clear the cache
		fixes.clear();    	
    }
	
    public class MyLocationListener implements LocationListener {  
    	public void onLocationChanged(Location location) {
    		    		
        	List<Overlay> mapOverlays = mapView.getOverlays();
    		// remove the old current location GPS coordinate from the map
        	mapOverlays.remove(itemizedOverlayCurrent);    		

        	// add the current location GPS coordinates to the map
        	Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
        	itemizedOverlayCurrent = new ProximityOverlay(drawable);
    		
        	Double dLat = location.getLatitude();
        	Double dLng = location.getLongitude();
        	
        	// update edit boxes
        	editCurrentLat.setText(dLat.toString());
        	editCurrentLng.setText(dLng.toString());

        	dLat *= 1e6;
        	dLng *= 1e6;
        	
        	GeoPoint point = new GeoPoint(dLat.intValue(), dLng.intValue());
        	OverlayItem overlayItem= new OverlayItem(point, "Current", "Current Location");
        	itemizedOverlayCurrent.addOverlay(overlayItem);
        	mapOverlays.add(itemizedOverlayCurrent);        	

        	
        	// zoom to the current location
        	mapView.getController().setZoom(12);
        	
        	//mapView.getController().setCenter(point);
        	mapView.getController().animateTo(point);
        	
        	// force a redraw
        	mapView.invalidate();
        	
        	// post the new coordinates in a separate thread
        	HOSFix fix = new HOSFix(lTrackID, sUser, sDevice, location);
        	
        	fixes.add(fix);
        	        	
        	int cacheLimit = Integer.parseInt(getString(R.string.cacheLimit));
        	
        	// only post when the cache is full
        	if (fixes.size() > cacheLimit) {
        		flushCache();
        	}
        	
    	}  
    	public void onStatusChanged(String s, int i, Bundle b) {}  
    	public void onProviderDisabled(String s) {}  
    	public void onProviderEnabled(String s) {}  
    }     
    
    private long lTrackID;
    private String sUser;
    private String sDevice;
    private LocationListener listener;
    private ProximityOverlay itemizedOverlayCurrent;       
	private LocationManager locationManager;
    private boolean mbMainDetector;
    private EditText editCurrentLat;
    private EditText editCurrentLng;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters  
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds  
    private static final long POINT_RADIUS = 1000; // in Meters  
    private static final long METERS_PER_MILE = 1612;
    private static final long PROX_ALERT_EXPIRATION = -1;   
}