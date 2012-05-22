package kz.crystalspring.funpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItemContainer;

import android.app.Application;


public class MainApplication extends Application
{
	public static float mDensity;
	public static MapItemContainer mapItemContainer;
	public static RefreshableMapList refreshable;
	
	
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
		mapItemContainer.addVisibleFilter(MapItem.FSQ_TYPE_CINEMA);
		mapItemContainer.addVisibleFilter(MapItem.FSQ_TYPE_FOOD);
		mapItemContainer.addVisibleFilter(MapItem.FSQ_TYPE_HOTEL);
		MapItem.loadHashMap();
	}
	
}



interface RefreshableMapList
{
	public void refreshMapItems();
}
