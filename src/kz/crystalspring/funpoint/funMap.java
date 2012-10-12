package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.MyMapView;
import kz.crystalspring.funpoint.R;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.readystatesoftware.mapviewballoons.CustomItemizedOverlay;
import com.readystatesoftware.mapviewballoons.CustomOverlayItem;

public class funMap extends MapActivity implements LocationListener,
		RefreshableMapList
{
	MyMapView mapView;
	List<Overlay> mapOverlays;
	CustomMyLocationOverlay mMyLocationOverlay;
	CustomItemizedOverlay myIO;
	List<MapItem> myItemsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mapView = (MyMapView) findViewById(R.id.mvMain);
		mapView.displayZoomControls(true);
		ImageView currLocationButton = (ImageView) findViewById(R.id.ImageView01);
		currLocationButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				toCurrentLocation();
			}
		});

		ImageView objectListButton = (ImageView) findViewById(R.id.ListBtn);
		objectListButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//MainMenu.goToObjectList();
			}
		});
		mMyLocationOverlay = new CustomMyLocationOverlay(this, mapView);
		MainApplication.gMyLocationOverlay = mMyLocationOverlay;
		mapOverlays = mapView.getOverlays();
	}

	protected void toCurrentLocation()
	{
		if (mMyLocationOverlay.getMyLocation() != null)
			mapView.getController().animateTo(
					mMyLocationOverlay.getMyLocation());
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MainApplication.refreshable = this;
		putItemsOnMap();
		refreshMap();
		if (MainApplication.mapItemContainer.getSelectedItem()!=null)
			selectItem(MainApplication.mapItemContainer.getSelectedMapItem());
	}

	private void putItemsOnMap()
	{
		mapOverlays.clear();
		myIO = new CustomItemizedOverlay(getResources().getDrawable(
				R.drawable.c_1), mapView, this);
		myIO.funmap = this;
		myItemsArray = new ArrayList<MapItem>();
		System.gc();
		mapOverlays.add(mMyLocationOverlay);
		mapOverlays.add(myIO);
		mMyLocationOverlay.enableMyLocation();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mMyLocationOverlay.disableMyLocation();
	}

	public void refreshMap()
	{
			clearMap();
			addItemListOnMap(MainApplication.mapItemContainer
					.getFilteredItemList());
			mapView.invalidate();
	}
	
	private void addItemOnMap(MapItem item)
	{
		CustomOverlayItem oi = new CustomOverlayItem(item.getGeoPoint(),
				item.toString(), "");
		oi.setMarker(item.getIcon());
		myIO.addOverlay(oi);
		myItemsArray.add(item);
	}

	private void addItemListOnMap(List<MapItem> itemsList)
	{
		if (itemsList != null)
			for (MapItem item : itemsList)
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
		Location previousLocation = null;

		public CustomMyLocationOverlay(Context context, MapView mapView)
		{
			super(context, mapView);

			// TODO Auto-generated constructor stub
		}

		@Override
		public void onLocationChanged(Location location)
		{
			super.onLocationChanged(location);
			if (previousLocation == null
					|| previousLocation.distanceTo(location) > 500)
			{
				previousLocation = location;
				MainApplication.mapItemContainer.loadNearBy(
						mMyLocationOverlay.getMyLocation());
				MainApplication.setCurrLocation(new GeoPoint((int) Math
						.round(location.getLatitude() * 1e6), (int) Math
						.round(location.getLongitude() * 1e6)));
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
		//MainMenu.tabHost.setCurrentTab(MainMenu.OBJECT_DETAIL_TAB);
		startActivity(new Intent(this, funObjectDetail.class));
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
