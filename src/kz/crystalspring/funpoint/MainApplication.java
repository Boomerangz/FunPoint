package kz.crystalspring.funpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.londatiga.fsq.FoursquareApp;

import com.google.android.maps.GeoPoint;

import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItemContainer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MainApplication extends Application
{
	public static Context context;
	public static float mDensity;
	public static MapItemContainer mapItemContainer;
	public static RefreshableMapList refreshable;
	public static CustomMyLocationOverlay gMyLocationOverlay;
	public static GeoPoint currLocation;
	public static SharedPreferences mPrefs;
	public static FoursquareApp FsqApp;
	
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
	}
	
}



interface RefreshableMapList
{
	public void refreshMapItems();
}
