package com.kknnOTL.tripquest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;

public class MapsFragment extends Fragment {
	
	private static String TAG="DEBUGGGG";
	private GoogleMap map;
	private LocationManager status;
	private String bestProvider = LocationManager.GPS_PROVIDER;
	private Activity context;
	private PositionListener posListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
	    View view = inflater.inflate(R.layout.maps, container, false);
	    MapsInitializer.initialize(getActivity());
	    
	    map = ((MapFragment)getFragmentManager().findFragmentById(R.id.location_map)).getMap();
	    posListener = new PositionListener(map);
	    
	    return view;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();

		status = (LocationManager) (context.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Criteria criteria = new Criteria();
			bestProvider = status.getBestProvider(criteria, true);
			Location location = status.getLastKnownLocation(bestProvider);
			Log.d(TAG, bestProvider);
			if(location != null) {
				posListener.onLocationChanged(location);
			}
			else {
				Log.d(TAG, "無法定位座標");
			}
		} else {
			Log.d(TAG, "Location Service Off");
		}
		
		status.requestLocationUpdates(bestProvider, 5000, 5,  posListener);
	}
	
	
	
}

