package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.funMap.CustomMyLocationOverlay;
import kz.crystalspring.funpoint.events.EventContainer;
import kz.crystalspring.funpoint.venues.MapItemContainer;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ContentService extends Service
{
	public static MapItemContainer mapItemContainer;
	public static EventContainer eventContainer;
	private static ContentService singletone;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		int i;
		Log.w("MyService", "OnCreate");
		mapItemContainer = new MapItemContainer(getApplicationContext());
		eventContainer = new EventContainer(getApplicationContext());
		MainApplication.contentService=this;
		singletone=this;
	}
	@Override
	public void onRebind(Intent intent)
	{
		super.onRebind(intent);
		Log.w("MyService", "OnRebind");
		MainApplication.contentService=this;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		MainApplication.contentService=this;
		return null;
	}
	
	public static ContentService getSingletone()
	{
		return singletone;
	}

}
