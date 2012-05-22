package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;


import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.MyMapView;
import kz.crystalspring.pointplus.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.readystatesoftware.mapviewballoons.CustomItemizedOverlay;
import com.readystatesoftware.mapviewballoons.CustomOverlayItem;

public class funMap extends MapActivity implements LocationListener, RefreshableMapList
{
	MyMapView mapView;
	List<Overlay> mapOverlays;
	CustomMyLocationOverlay mMyLocationOverlay;
	CustomItemizedOverlay myIO;
	List myItemsArray;
	boolean refreshing=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mapView=(MyMapView) findViewById(R.id.mvMain);
		mapOverlays=mapView.getOverlays();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		MainApplication.refreshable=this;
		putItemsOnMap();
		refreshMap();
	}
	
	
	
	private void putItemsOnMap()
	{
		mapOverlays.clear();
		myIO=new CustomItemizedOverlay(getResources().getDrawable(R.drawable.c_1),mapView, this);
		myIO.funmap=this;
		myItemsArray=new ArrayList();
		System.gc();
		mMyLocationOverlay = new CustomMyLocationOverlay(this, mapView);
		mapOverlays.add(mMyLocationOverlay);
		mapOverlays.add(myIO);
		mMyLocationOverlay.enableMyLocation();
	}
	
	public void refreshMap()
	{
		if (!refreshing)
		{
			clearMap();
			addItemListOnMap(MainApplication.mapItemContainer.getFilteredItemList());
		}
	}
	
	private void addItemOnMap(MapItem item)
	{
		CustomOverlayItem oi=new CustomOverlayItem(item.getGeoPoint(),item.toString(),"");
		oi.setMarker(item.getIcon());
		myIO.addOverlay(oi);
		myItemsArray.add(item);
	}
	
	
	private void addItemListOnMap(List<MapItem> itemsList)
	{
		if (itemsList!=null)
			for (MapItem item:itemsList)
			{
				addItemOnMap(item);
			}
		myIO.populateNow();
	}
	
	private void clearMap()
	{
		myIO.removeAll();
		myItemsArray.clear();
	}

	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
	class CustomMyLocationOverlay extends MyLocationOverlay
	{
		Location previousLocation=null;
		public CustomMyLocationOverlay(Context context, MapView mapView)
		{
			super(context, mapView);
			
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onLocationChanged(Location location)
		{
			super.onLocationChanged(location);
			if (previousLocation==null||previousLocation.distanceTo(location)>500)
			{
				refreshing=true;
				previousLocation=location;
				
				Runnable task=new Runnable()//тут указываем что надо сделать после завершения загрузки. произвольные действия, например обновление
				{
					@Override
					public void run()
					{
						addItemListOnMap(MainApplication.mapItemContainer.getFilteredItemList());
						refreshing=false;
						refreshMap();
					}

				};
				MainApplication.mapItemContainer.loadNearBy(mMyLocationOverlay.getMyLocation(), task);
			}
		}
		
	}

	

	public void selectItem(int index)
	{
		selectItem((MapItem) myItemsArray.get(index));
	}
	
	public void showInfo(int index)
	{
		showInfo((MapItem) myItemsArray.get(index));
	}
	
	public void showInfo(MapItem item)
	{
		MainApplication.mapItemContainer.setSelectedItem(item);
		MainMenu.tabHost.setCurrentTab(3);
	}
	
	public void selectItem(MapItem item)
	{
		myIO.select(myItemsArray.indexOf(item));
		mapView.getController().animateTo(item.getGeoPoint());
	}


	@Override
	public void refreshMapItems()
	{
		refreshMap();
	}

}
