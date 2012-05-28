package kz.crystalspring.funpoint;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.londatiga.fsq.FoursquareApp;

import com.google.android.maps.GeoPoint;

import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItemContainer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;


public class MainApplication extends Application
{
	public static Context context;
	public static float mDensity;
	public static MapItemContainer mapItemContainer;
	public static RefreshableMapList refreshable;
	public static CustomMyLocationOverlay gMyLocationOverlay;
	private static GeoPoint currLocation;
	public static SharedPreferences mPrefs;
	public static FoursquareApp FsqApp;
	LocationUpdater updater;
	
	public static void refreshMap()
	{
		if (refreshable!=null)
			refreshable.refreshMapItems();
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mDensity=getApplicationContext().getResources().getDisplayMetrics().density;
		mapItemContainer=new MapItemContainer(getApplicationContext());
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		context=getApplicationContext();
		FsqApp = new FoursquareApp(this, FSQConnector.CLIENT_ID, FSQConnector.CLIENT_SECRET);
		
		updater=new LocationUpdater(this);

		
		Runnable task = new Runnable()//«адаем действие, которое надо осуществить после того как закончитс€ процесс загрузки точек. 
		{
			@Override
			public void run()
			{
				MainApplication.refreshMap();
			}
		};
		MainApplication.mapItemContainer.loadNearBy(
				getCurrentLocation(), task);
		
		new FileConnector(getApplicationContext());
	}
	
	public static GeoPoint getCurrentLocation()
	{
		if (gMyLocationOverlay!=null)
			setCurrLocation(gMyLocationOverlay.getMyLocation());
		else 
		{
			
		}
		return currLocation;
	}
	
	public static void setCurrLocation(GeoPoint point)
	{
		currLocation=point;
		int i=0;
	}
	
}


class LocationUpdater implements LocationListener
{
	MainApplication app;
	LocationManager locationManager;
	LocationUpdater(MainApplication context)
	{
		
		String sContext= Context.LOCATION_SERVICE;
		locationManager=(LocationManager)context.getSystemService(sContext);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		
		Location location= locationManager.getLastKnownLocation(provider);
		updateWithLocation(location);
		
		
		locationManager.requestLocationUpdates(provider, 2000, 100, this);
	}
	
	public void disableUpdating()
	{
		locationManager.removeUpdates(this);
	}

	private void updateWithLocation(Location location)
	{
		MainApplication.setCurrLocation(new GeoPoint((int)(location.getLatitude()*1e6),(int)(location.getLongitude()*1e6)));
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		updateWithLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}
}



interface RefreshableMapList
{
	public void refreshMapItems();
}
