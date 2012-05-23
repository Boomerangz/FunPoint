package kz.crystalspring.funpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.maps.GeoPoint;

import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItemContainer;

import android.app.Application;


public class MainApplication extends Application
{
	public static float mDensity;
	public static MapItemContainer mapItemContainer;
	public static RefreshableMapList refreshable;
	public static CustomMyLocationOverlay gMyLocationOverlay;
	public static GeoPoint currLocation;
	
	
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
	}
	
}



interface RefreshableMapList
{
	public void refreshMapItems();
}
