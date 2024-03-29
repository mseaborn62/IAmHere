package com.example.location;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.iamhere.FullscreenActivity;

public class DeviceLocationListener implements LocationListener {

	//Constants to apply to location for potential battery savings
	private static final long ONE_MIN = 1000 * 60;
	private static final long TWO_MIN = ONE_MIN * 2;
	private static final long FIVE_MIN = ONE_MIN * 5;
	private static final long MEASURE_TIME = 1000 * 30;
	private static final long POLLING_FREQ = 1000 * 10;
	private static final float MIN_ACCURACY = 25.0f;
	private static final float MIN_LAST_READ_ACCURACY = 500.0f;
	private static final float MIN_DISTANCE = 10.0f;

	// Current best location estimate
	private Location mBestReading;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private FullscreenActivity mCaller;


	public DeviceLocationListener(FullscreenActivity caller) {
		
		//used for tasks such as updating display
		mCaller = caller;
		
		// Get best last location measurement
		mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
		
		// Acquire reference to the LocationManager
		if (null == (mLocationManager = (LocationManager) caller.getSystemService(Context.LOCATION_SERVICE)))
			caller.finish();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Determine whether new location is better than current best
		// estimate

		if (null == mBestReading
				|| location.getAccuracy() < mBestReading.getAccuracy()) {

			// Update best estimate
			mBestReading = location;

			// Update display
			mCaller.updateDisplay(location);

			if (mBestReading.getAccuracy() < MIN_ACCURACY)
				mLocationManager.removeUpdates(mLocationListener);

		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public Location bestLastKnownLocation(){
		return bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
	}
	
	// Get the last known location from all providers
	// return best reading is as accurate as minAccuracy and
	// was taken no longer then minTime milliseconds ago

	private Location bestLastKnownLocation(float minAccuracy, long minTime) {

		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;

		List<String> matchingProviders = mLocationManager.getAllProviders();

		for (String provider : matchingProviders) {

			Location location = mLocationManager.getLastKnownLocation(provider);

			if (location != null) {

				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if (accuracy < bestAccuracy) {

					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;

				}
			}
		}
		
		// Return best reading or null
		if (bestAccuracy > minAccuracy || bestTime < minTime) {
			return null;
		} else {
			return bestResult;
		}
	}//bestLastKnowLocation

}
