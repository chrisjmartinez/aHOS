package com.stokedsoft.HOS;

import android.location.Location;

public class HOSFix {

	public HOSFix() {
		
	}
	
	public HOSFix(long inTrackID, String inUser, String inDev, Location inLocation) {
		mTrackID = inTrackID;
		mUser = inUser;
		mDev = inDev;
		mLocation = inLocation;		
	}
	
	public long getTrackID () {
		return mTrackID; 
	}
	
	public String getUser() {
		return mUser;
	}
	
	public String getDevice() {
		return mDev;
	}
	
	public Location getLocation() {
		return mLocation;
	}
	
	private long mTrackID;
	private String mUser;
	private String mDev;
	Location mLocation;
}
