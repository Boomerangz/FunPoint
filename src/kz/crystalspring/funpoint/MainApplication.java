package kz.crystalspring.funpoint;

import java.io.ObjectInputStream.GetField;
import java.nio.channels.FileChannel;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.http.message.BasicNameValuePair;

import net.londatiga.fsq.FoursquareApp;

import com.boomerang.pending.PendingWorkAggregator;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.maps.GeoPoint;

import kz.crystalspring.android_client.C_NetHelper;
import kz.crystalspring.funpoint.events.EventContainer;
import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.FSQUser;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.MapItemContainer;
import kz.crystalspring.funpoint.venues.UserActivity;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ImageCache;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.visualities.gallery.ImageContainer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
	public static RefreshableMapList refreshable;
	public static CustomMyLocationOverlay gMyLocationOverlay;
	private static GeoPoint currLocation;
	public static SharedPreferences mPrefs;
	public static FoursquareApp FsqApp;
	public static PendingWorkAggregator pwAggregator = new PendingWorkAggregator();
	public static CityManager cityManager;
	public static ContentService contentService;
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
		getFilesDir().listFiles();
		Log.w("MainApplication", "Created " + Integer.valueOf(++starts));
		singleTon = this;
		startService(new Intent(this, ContentService.class));

		mDensity = getApplicationContext().getResources().getDisplayMetrics().density;
		new ImageCache(getApplicationContext());

		mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		context = getApplicationContext();
		FsqApp = new FoursquareApp(this, FSQConnector.CLIENT_ID, FSQConnector.CLIENT_SECRET);
		cityManager = new CityManager(context);
		C_NetHelper.SyncData(getApplicationContext(), false, false);
		updater = new LocationUpdater(this);
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
	
	public static boolean loading=false;

	public void onResume()
	{
		contentService = ContentService.getSingletone();
		pwAggregator.setAbleToDo(true);
		if (checkInternetConnection())
		{
			System.out.println("ЗАГРУЗКА НАЧАТА");
			AsyncTask task = new AsyncTask()
			{

				@Override
				protected Object doInBackground(Object... params)
				{
					loading=true;
					MainApplication.loadFromProxy();
					return null;
				}

				@Override
				protected void onPostExecute(Object o)
				{
					MainApplication.loadAdditionalContent();
					MainApplication.loadPoints();
					new FileConnector(getApplicationContext());
					loading=false;
				}
			};
			task.execute();
		} else
			loadNoInternetPage();
	}

	private static void loadFromProxy()
	{
		HttpHelper.getInstance().loadFromProxy(getCurrentLocation());
	}

	public static MapItemContainer getMapItemContainer()
	{
		contentService = ContentService.getSingletone();
		return (MapItemContainer) ProjectUtils.ifnull(contentService.mapItemContainer, new MapItemContainer(context));
	}

	public static EventContainer getEventContainer()
	{
		contentService = ContentService.getSingletone();
		return (EventContainer) ProjectUtils.ifnull(contentService.eventContainer, new EventContainer(context));
	}

	private static void loadPoints()
	{
		if (contentService != null)
		{
			if (cityManager.getSelectedCity() == null)
				MainApplication.contentService.mapItemContainer.loadNearBy(getCurrentLocation());
			else if (MainApplication.getCurrentLocation() != null
					&& ProjectUtils.distance(cityManager.getSelectedCity().getPoint(), MainApplication.getCurrentLocation()) < 10000)
				MainApplication.contentService.mapItemContainer.loadNearBy(getCurrentLocation());
			else
				MainApplication.contentService.mapItemContainer.loadNearBy(null);
			System.gc();
		}
	}

	private static void loadPointsSilent()
	{
		if (cityManager.getSelectedCity() == null)
			MainApplication.contentService.mapItemContainer.loadNearBy(getCurrentLocation());
		else if (MainApplication.getCurrentLocation() != null
				&& ProjectUtils.distance(cityManager.getSelectedCity().getPoint(), MainApplication.getCurrentLocation()) < 10000)
			MainApplication.contentService.mapItemContainer.loadNearBy(getCurrentLocation());
		else
			;
		// MainApplication.contentService.mapItemContainer.loadNearBy(null);
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
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

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
		if (gMyLocationOverlay != null && gMyLocationOverlay.getMyLocation() != null)
			setCurrLocation(gMyLocationOverlay.getMyLocation());
		else
		{

		}
		if (currLocation != null)
			return currLocation;
		else
			return new GeoPoint(43240134, 76923185);
	}

	static Date lastUpdate = null;

	public static void setCurrLocation(GeoPoint point)
	{
		Date nowDate = new Date();
		if (lastUpdate == null || nowDate.getTime() - lastUpdate.getTime() > 30000)
		{
			lastUpdate = nowDate;
			currLocation = point;
			loadPoints();
		}
	}

	public static void loadAdditionalContent()
	{
		FSQConnector.loadCategories();
		if (FSQConnector.isFSQConnected())
		{
			MainApplication.loadUserActivity();
		}
		contentService.eventContainer.loadEventList();
		// loadJamContent();
	}

	public void enableLocationUpdating()
	{
		updater.enableUpdating();
	}

	public void disableLocationUpdating()
	{
		updater.disableUpdating();
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
		if (item == null || !item.equals(cityManager.getSelectedCity()))
		{
			MainApplication.contentService.mapItemContainer.clearContent();
			cityManager.selectCity(item);
			MainApplication.loadPoints();
		}
	}

	public static City getCity()
	{
		return cityManager.getSelectedCity(); // (String)
	}

	public static ImageContainer getSelectedImageContainer()
	{
		if (ProfilePage.class.isInstance(refreshable))
		{
			return FSQUser.getInstance();
		} else
		{
			return (FSQItem) contentService.mapItemContainer.getSelectedMapItem();
		}
	}

}

class LocationUpdater implements LocationListener
{
	MainApplication app;
	LocationManager locationManager;
	String provider;

	LocationUpdater(MainApplication context)
	{
		String sContext = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) context.getSystemService(sContext);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		provider = locationManager.getBestProvider(criteria, true);
		provider = (String) ProjectUtils.ifnull(provider, LocationManager.NETWORK_PROVIDER);
		Location location = locationManager.getLastKnownLocation(provider);
		updateWithLocation(location);
		enableUpdating();
	}

	public void enableUpdating()
	{

		locationManager.requestLocationUpdates(provider, 30 * 1000, 1000, this);
	}

	public void disableUpdating()
	{
		locationManager.removeUpdates(this);
	}

	private void updateWithLocation(Location location)
	{
		if (location != null)
			MainApplication.setCurrLocation(new GeoPoint((int) (location.getLatitude() * 1e6 * 1), (int) (location.getLongitude() * 1e6)));
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
