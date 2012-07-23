package kz.crystalspring.funpoint;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.londatiga.fsq.FoursquareApp;

import com.boomerang.pending.PendingWorkAggregator;
import com.google.android.maps.GeoPoint;

import kz.crystalspring.funpoint.events.EventContainer;
import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQTodo;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItemContainer;
import kz.crystalspring.funpoint.venues.UserActivity;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class MainApplication extends Application
{
	public static Context context;
	public static float mDensity;
	public static MapItemContainer mapItemContainer;
	public static EventContainer eventContainer;
	public static RefreshableMapList refreshable;
	public static CustomMyLocationOverlay gMyLocationOverlay;
	private static GeoPoint currLocation;
	public static SharedPreferences mPrefs;
	public static FoursquareApp FsqApp;
	public static PendingWorkAggregator pwAggregator=new PendingWorkAggregator();
	public static UrlDrawable selectedItemPhoto;
	public static String selectedEventId=null;
	//public static SocialConnector socialConnector;
	
	
	public static final int WIFI=0;
	public static final int UMTS=1;
	public static final int EDGE=2;
	public static final int NO_CONNECTION=4;
	public static int internetConnection=-1;
	
	
	LocationUpdater updater;
	
	public static final int ALPHA = 100;

	public static void refreshMapItems()
	{
		if (refreshable != null)
			refreshable.refreshMapItems();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		mDensity = getApplicationContext().getResources().getDisplayMetrics().density;
		mapItemContainer = new MapItemContainer(getApplicationContext());
		eventContainer = new EventContainer(getApplicationContext());
		
		mPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		context = getApplicationContext();
		FsqApp = new FoursquareApp(this, FSQConnector.CLIENT_ID,
				FSQConnector.CLIENT_SECRET);

		updater = new LocationUpdater(this);
		
	//	socialConnector=new SocialConnector();
		
		
		Runnable task = new Runnable()// ������ ��������, ������� ����
										// ����������� ����� ���� ��� ����������
										// ������� �������� �����.
		{
			@Override
			public void run()
			{
				MainApplication.refreshMapItems();
			}
		};
		
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		NetworkInfo info = connec.getActiveNetworkInfo();

		if (info!=null)
		{	
		int netSubType = info.getSubtype();

		            if (wifi.isConnected()) 
		            {
		            	internetConnection=WIFI;
		            }
		            else if (mobile.isConnected())
		            { 
		            	if(netSubType == TelephonyManager.NETWORK_TYPE_UMTS)
	                {   
		            		internetConnection=UMTS;
	                }
	                else
	                {
	                      internetConnection=EDGE;
	                }
	            }
		}
		else
		{
			internetConnection=NO_CONNECTION;
		}
		
		System.out.println("ЗАГРУЗКА НАЧАТА");
		MainApplication.mapItemContainer.loadNearBy(getCurrentLocation(), task);

		new FileConnector(getApplicationContext());
	}

	public static GeoPoint getCurrentLocation()
	{
		if (gMyLocationOverlay != null&&gMyLocationOverlay.getMyLocation()!=null)
			setCurrLocation(gMyLocationOverlay.getMyLocation());
		else
		{

		}
		return currLocation;
	}

	public static void setCurrLocation(GeoPoint point)
	{
		currLocation = point;
		int i = 0;
	}


	public static void loadAdditionalContent()
	{
		if (FSQConnector.isFSQConnected())
		{
			MainApplication.loadUserActivity();
		}
		eventContainer.loadEventList();
		//loadJamContent();
	}
	
	public static void loadUserActivity()
	{
		if (!FSQConnector.getTodosLoaded())
			FSQConnector.loadTodosAsync();
		if (!FSQConnector.getCheckinsLoaded())
			FSQConnector.loadCheckinsAsync();
		if (!FSQConnector.getBadgessLoaded())
			FSQConnector.loadBadgesAsync();
	}
	
	public static void loadJamContent()
	{
	}

}

class LocationUpdater implements LocationListener
{
	MainApplication app;
	LocationManager locationManager;

	LocationUpdater(MainApplication context)
	{

		String sContext = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) context.getSystemService(sContext);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);

		Location location = locationManager.getLastKnownLocation(provider);
		updateWithLocation(location);

		locationManager.requestLocationUpdates(provider, 2000, 100, this);
	}

	public void disableUpdating()
	{
		locationManager.removeUpdates(this);
	}

	private void updateWithLocation(Location location)
	{
		if (location != null)
			MainApplication.setCurrLocation(new GeoPoint((int) (location
					.getLatitude() * 1e6*1),
					(int) (location.getLongitude() * 1e6)));
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
