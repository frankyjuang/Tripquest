package com.kknnOTL.tripquest;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PositionListener implements LocationListener {

	private static String TAG="DEBUGGGG";
	private Marker markerMe;
	private GoogleMap map;
	
	public PositionListener(GoogleMap map) {
		this.map = map;
	}
	
	@Override
	public void onLocationChanged(Location location) {
	    Log.d(TAG, "location: " + location.getLatitude() + ", "+location.getLongitude());  
	    showMarkerMe(location.getLatitude(), location.getLongitude());
	    cameraFocusOnMe(location.getLatitude(), location.getLongitude());
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "ProviderEnabled " + provider);
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "ProviderDisabled " + provider);
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
	      case LocationProvider.OUT_OF_SERVICE:
	    	  Log.v(TAG, "Status Changed: Out of Service");
	      break;
	      case LocationProvider.TEMPORARILY_UNAVAILABLE:
	    	  Log.v(TAG, "Status Changed: Temporarily Unavailable");
	      break;
	      case LocationProvider.AVAILABLE:
	    	  Log.v(TAG, "Status Changed: Available");
	      break;
		}
	}
	
	private void showMarkerMe(double lat, double lng){ 
		if (markerMe != null) {
			 markerMe.remove();
		 }

		 MarkerOptions markerOpt = new MarkerOptions();
		 markerOpt.position(new LatLng(lat, lng));
		 markerOpt.title("我在這裡");
		 markerMe = map.addMarker(markerOpt);
	}
	
	private void cameraFocusOnMe(double lat, double lng){
		 CameraPosition camPosition = new CameraPosition.Builder()
		    .target(new LatLng(lat, lng))
		    .zoom(16)
		    .build();

		 map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
	}
}