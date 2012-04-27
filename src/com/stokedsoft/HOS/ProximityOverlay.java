package com.stokedsoft.HOS;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ProximityOverlay extends ItemizedOverlay {

	public ProximityOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
		
	public void addOverlay(OverlayItem overlay) {    
		mOverlays.add(overlay);    
		populate();
	}
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
}
