package kz.crystalspring.funpoint;

import java.io.ObjectInputStream.GetField;
import java.nio.channels.FileChannel;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import net.londatiga.fsq.FoursquareApp;

import com.boomerang.pending.PendingWorkAggregator;
import com.google.android.maps.GeoPoint;

import kz.crystalspring.funpoint.events.EventContainer;
import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQTodo;
import kz.crystalspring.funpoint.venues.FSQUser;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItemContainer;
import kz.crystalspring.funpoint.venues.UserActivity;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;

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
	public static PendingWorkAggregator pwAggregator = new PendingWorkAggregator();
	public static CityManager cityManager;
	public static kz.crystalspring.funpoint.venues.UrlDrawable selectedItemPhoto;
	public static String selectedEventId = null;
	private static MainApplication singleTon;
	// public static SocialConnector socialConnector;

	public static final int WIFI = 0;
	public static final int UMTS = 1;
	public static final int EDGE = 2;
	public static final int NO_CONNECTION = 4;
	public static int internetConnection = -1;

	LocationUpdater updater;

	public static final int ALPHA = 100;

	public static void refreshMapItems()
	{
		if (refreshable != null)
			refreshable.refreshMapItems();
	}

	static int starts = 0;

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.w("MainApplication", "Created " + Integer.valueOf(++starts));
		singleTon = this;

		mDensity = getApplicationContext().getResources().getDisplayMetrics().density;
		mapItemContainer = new MapItemContainer(getApplicationContext());
		eventContainer = new EventContainer(getApplicationContext());

		mPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		context = getApplicationContext();
		FsqApp = new FoursquareApp(this, FSQConnector.CLIENT_ID,
				FSQConnector.CLIENT_SECRET);
		cityManager = new CityManager(context);

		updater = new LocationUpdater(this);
		onResume();
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Log.w("MainApplication", "Low Memory");
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		Log.w("MainApplication", "Terminated");
	}

	public void onResume()
	{
		pwAggregator.setAbleToDo(true);
		if (checkInternetConnection())
		{
			System.out.println("ЗАГРУЗКА НАЧАТА");
			AsyncTask task = new AsyncTask()
			{

				@Override
				protected Object doInBackground(Object... params)
				{
					MainApplication.loadFromProxy();
					return null;
				}

				@Override
				protected void onPostExecute(Object o)
				{
					MainApplication.loadAdditionalContent();
					MainApplication.loadPoints();
					new FileConnector(getApplicationContext());
				}
			};
			task.execute();
		} else
			loadNoInternetPage();
	}

	private static void loadFromProxy()
	{
		HttpHelper.loadFromProxy(getCurrentLocation());
	}

	private static void loadPoints()
	{
		if (cityManager.getSelectedCity() == null)
			MainApplication.mapItemContainer.loadNearBy(getCurrentLocation());
		else if (MainApplication.getCurrentLocation() != null
				&& ProjectUtils.distance(cityManager.getSelectedCity()
						.getPoint(), MainApplication.getCurrentLocation()) < 10000)
			MainApplication.mapItemContainer.loadNearBy(getCurrentLocation());
		else
			MainApplication.mapItemContainer.loadNearBy(null);
		System.gc();
	}

	public static void loadNoInternetPage()
	{
		Intent i = new Intent(context, NoInternetActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(i);
	}

	public static CityManager getCityManager()
	{
		return cityManager;
	}

	public boolean checkInternetConnection()
	{
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		android.net.NetworkInfo mobile = connec
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		NetworkInfo info = connec.getActiveNetworkInfo();
		if (info != null)
		{
			int netSubType = info.getSubtype();

			if (wifi.isConnected())
			{
				internetConnection = WIFI;
			} else if (mobile.isConnected())
			{
				if (netSubType == TelephonyManager.NETWORK_TYPE_UMTS)
				{
					internetConnection = UMTS;
				} else
				{
					internetConnection = EDGE;
				}
			}
		} else
		{
			internetConnection = NO_CONNECTION;
		}
		pwAggregator.setAbleToDo(internetConnection != NO_CONNECTION);
		return internetConnection != NO_CONNECTION;
	}

	public static GeoPoint getCurrentLocation()
	{
		if (gMyLocationOverlay != null
				&& gMyLocationOverlay.getMyLocation() != null)
			setCurrLocation(gMyLocationOverlay.getMyLocation());
		else
		{

		}
		if (currLocation != null)
			return currLocation;
		else
			return new GeoPoint(43240134, 76923185);
	}

	public static void setCurrLocation(GeoPoint point)
	{
		currLocation = point;
	}

	public static void loadAdditionalContent()
	{
		FSQConnector.loadCategories();
		if (FSQConnector.isFSQConnected())
		{
			MainApplication.loadUserActivity();
		}
		eventContainer.loadEventList();
		// loadJamContent();
	}

	public static void loadUserActivity()
	{
		FSQUser.getInstance().fillIfNot();
		if (!FSQConnector.getTodosLoaded())
			FSQConnector.loadTodosAsync();
		if (!FSQConnector.getCheckinsLoaded())
			FSQConnector.loadCheckinsAsync();
		if (!FSQConnector.getBadgessLoaded())
			FSQConnector.loadBadgesAsync();
		if (!FSQConnector.getFriendFeedLoaded())
			FSQConnector.loadFriendFeed();
		if (!FSQConnector.getExploringLoaded())
			FSQConnector.loadExploring(getCurrentLocation());

	}

	public static void loadJamContent()
	{
	}

	public static MainApplication getInstance()
	{
		return singleTon;
	}

	public static void setCity(City item)
	{
		if (item==null||!item.equals(cityManager.getSelectedCity()))
		{
			cityManager.selectCity(item);
			MainApplication.mapItemContainer.clearContent();
			MainApplication.loadPoints();
		}
	}

	public static City getCity()
	{
		return cityManager.getSelectedCity();   // (String)
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
					.getLatitude() * 1e6 * 1),
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
