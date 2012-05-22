package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.C_ARActivityN;
import kz.crystalspring.pointplus.MyItemizedOverlay;
import kz.crystalspring.pointplus.MyMapView;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.pointplus.R;
import kz.crystalspring.pointplus.ZoomOverlay;
import kz.crystalspring.pointplus.R.id;
import kz.crystalspring.pointplus.R.layout;
import kz.crystalspring.pointplus.R.raw;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;
import kz.sbeyer.atmpoint1.types.ItemHotel;

import com.erdao.android.mapviewutil.markerclusterer.MarkerBitmap;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Map extends MapActivity implements LocationListener
{

	private ArrayList<String> selBnkArrays1;
	String[] selBnkArrays;

	private ArrayList<Object> bankList;

	public static LinearLayout balloon_main_layout;
	public static TextView balloon_item_title;
	public static TextView balloon_item_snippet;
	public static int hheight;
	public static int wwidth;
	public static Context context;

	public static final String CINEMA_IMG = "exchange";
	public static final String REST_IMG = "exchange";
	public static final String HOTEL_IMG = "m_13";

	String pLang;
	String pLangActivity;
	String pCity;
	String pCityActivity;
	static LinearLayout layout1;

	static MyLocationOverlay mMyLocationOverlay;
	static MyMapView map;

	MapController controller;
	int x, y;
	GeoPoint touchedPoint;
	Drawable d;
	List<Overlay> listOverlay;

	LocationManager locationManager;
	String towers;
	int lat;
	int longi;

	private static List<Overlay> mapOverlays;
	private Drawable drawable1;
	private static Drawable drawable2;
	private MyItemizedOverlay itemizedOverlay1;
	private static MyItemizedOverlay itemizedOverlay2;

	private static List<MyItemizedOverlay> itemizedOverlayArray;

	public static boolean firstLoad = true;

	static boolean printing = false;
	static Thread tr;

	static AssetManager assetManager;
	// ArrayList of objects to Show
	private static ArrayList<MapItem> visibleList;

	String provider = LocationManager.NETWORK_PROVIDER;
	long GPSupdateInterval; // In milliseconds
	float GPSmoveInterval; // In meters

	float scale;
	int padSize;
	int margTopSize;
	int margLeftSize;
	int imgSize1;
	static LinearLayout.LayoutParams lp;
	ImageView iv;

	// ArrayList of objects that are shown
	public static ArrayList displayedMarkers = new ArrayList();

	Runnable r;
	Runnable rAndShow;
	// marker icons
	private List<MarkerBitmap> markerIconBmps_ = new ArrayList<MarkerBitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		displayedMarkers.clear();

		MapItem.context = this;
		// Get application context for later use
		context = getApplicationContext();

		r = new Runnable()
		{
			@Override
			public void run()
			{
			//	m1();
			}
		};
		rAndShow = new Runnable()
		{
			@Override
			public void run()
			{
				// balloon_main_layout.setVisibility(View.VISIBLE);
				//m1();
			}
		};

		// Set up location manager for determining present location of phone
		GPSupdateInterval = 5000; // milliseconds
		GPSmoveInterval = 1; // meters

		// Get the location manager
		/*
		 * locationManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE); // Define the criteria
		 * how to select the locatioin provider -> use // default Criteria
		 * criteria = new Criteria(); provider =
		 * locationManager.getBestProvider(criteria, false); Location location =
		 * locationManager.getLastKnownLocation(provider); if (location != null)
		 * { int lat = (int) (location.getLatitude()); int lng = (int)
		 * (location.getLongitude()); } else { }
		 */

		bankList = new ArrayList<Object>();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);

		scale = this.getResources().getDisplayMetrics().density;
		padSize = Math.round(0 * scale);
		margTopSize = Math.round(2 * scale);
		margLeftSize = Math.round(1 * scale);
		imgSize1 = Math.round(70 * scale);
		lp = new LinearLayout.LayoutParams(imgSize1, imgSize1);
		lp.setMargins(margLeftSize, margTopSize, margLeftSize, margTopSize);

		map = (MyMapView) findViewById(R.id.mvMain);
		map.setBuiltInZoomControls(true);
		map.getController().setZoom(17);

		listOverlay = map.getOverlays();
		mMyLocationOverlay = new MyLocationOverlay(this, map);
		listOverlay.add(mMyLocationOverlay);
		mMyLocationOverlay.enableMyLocation();

		assetManager = getAssets();
		visibleList = new ArrayList<MapItem>();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		hheight = displaymetrics.heightPixels;
		wwidth = displaymetrics.widthPixels;

		balloon_main_layout = (LinearLayout) findViewById(R.id.balloon_main_layout);
		balloon_item_title = (TextView) findViewById(R.id.balloon_item_title);
		balloon_item_snippet = (TextView) findViewById(R.id.balloon_item_snippet);

		int objTypeId = 1;
		String vFilterType = Prefs.getFilterType(context);
		if (vFilterType.contentEquals("0"))
		{
			objTypeId = Integer.valueOf(Prefs.getSelObjType(context));
		} else if (vFilterType.contentEquals("1"))
		{
			objTypeId = Integer.valueOf(Prefs.getSelProdType(context));
		}

		prepareOverlaysList(objTypeId);

		final Handler handler = new Handler();
		mMyLocationOverlay.runOnFirstFix(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						printPointsOnMap();
						map.getController().animateTo(
								mMyLocationOverlay.getMyLocation(), r);
					}
				});
			}

		});

		// Create itemizedOverlay2 if it doesn't exist and display all three
		// items
		mapOverlays = map.getOverlays();
		drawable1 = this.getResources().getDrawable(R.raw.test);

		map.postInvalidate();

		firstLoad = true;

		map.getOverlays().add(new ZoomOverlay(getBaseContext(), map));

		map.invalidate();

	}

	public void showObjDet(View v)
	{

		String txt = balloon_item_title.getText().toString();
		int indx = txt.indexOf("ID ");
		String substr = txt.substring(indx + 3);
		int indx1 = substr.indexOf(" ");
		;
		String substr1 = substr.substring(0, indx1);

		Prefs.setInitTab(context, "0");
		Prefs.setSelObjId(MainMenu.context, substr1);
		MainMenu.tabHost.setCurrentTab(3);
	}

	public void showObjList(View v)
	{
		Prefs.setInitTab(context, "0");
		MainMenu.tabHost.setCurrentTab(4);
	}

	// ������� ���������� � ������������ ���������

	public void refreshMap()
	{
		int objTypeId = 1;
		String vFilterType = Prefs.getFilterType(context);
		if (vFilterType.contentEquals("0"))
		{
			objTypeId = Integer.valueOf(Prefs.getSelObjType(context));
		} else if (vFilterType.contentEquals("1"))
		{
			objTypeId = Integer.valueOf(Prefs.getSelProdType(context));
		}
		prepareOverlaysList(objTypeId);
		printPointsOnMap();
	}

	public void prepareOverlaysList(int objTypeId)
	{
		visibleList.clear();
	}

	// ���������� �������� � ������ ����������
	public void AddObjectToVisibleList(MapItem item)
	{
		visibleList.add(item);
	}

	public static void printPointsOnMap()
	{
		if (!printing)
		{
			mapOverlays.clear();
			if (itemizedOverlayArray != null)
				itemizedOverlayArray.clear();
			else
				itemizedOverlayArray = new ArrayList();
			displayedMarkers.clear();
			map.getOverlays().add(new ZoomOverlay(context, map));
			mapOverlays.add(mMyLocationOverlay);
			printing = true;
			tr = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						sleep(500);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally
					{
						// Toast.makeText(getApplicationContext(), "CLICK",
						// Toast.LENGTH_SHORT).show();
						printing = false;
					}
				}
			};
			tr.start();
			int dispMarkLen = displayedMarkers.size();
			float coef_dp = context.getResources().getDisplayMetrics().density;
			GeoPoint topLeft = map.getProjection().fromPixels(0, 0);
			GeoPoint bottomRight = map.getProjection().fromPixels(
					map.getWidth(), map.getHeight());

			ArrayList<MyRestOverlay> textOverlays = new ArrayList();
			for (int i = 0; i < visibleList.size(); i++)
			{
				boolean toDisplay = true;
				// check if was printed
				for (int j = 0; j < dispMarkLen; j++)
				{
					if (Integer.valueOf(displayedMarkers.get(j).toString()) == i)
					{
						toDisplay = false;
						break;
					}
				}
				if (toDisplay)
				{
					MapItem visMapObject = visibleList.get(i);

					GeoPoint position = new GeoPoint(
							(int) (visMapObject.getLatitude() * 1e6),
							(int) (visMapObject.getLongitude() * 1e6));

					MyRestOverlay myOverlay = new MyRestOverlay();
					myOverlay.setMapItem(visMapObject);

					OverlayItem overlItem = new OverlayItem(new GeoPoint(
							(int) (visMapObject.getLatitude() * 1e6),
							(int) (visMapObject.getLongitude() * 1e6)),
							visMapObject.toString(),
							visMapObject.toStringLong());

					boolean toPrint = true;
					// Log.i("visMapObject.getImgSrcStr()",
					// visMapObject.getImgSrcStr());
					int imgId = MapItem.getImgId(visMapObject.getIconName());
					if (imgId == 0)
					{
						toPrint = false;
					}

					if (toPrint)
					{
						// drawable2 = new BitmapDrawable(image);
						drawable2 = visMapObject.getIcon();
						//itemizedOverlay2 = new MyItemizedOverlay(drawable2);

						int pLat = position.getLatitudeE6();
						int pLon = position.getLongitudeE6();
						itemizedOverlayArray.add(itemizedOverlay2);
						if (((topLeft.getLatitudeE6() > pLat) && (pLat > bottomRight
								.getLatitudeE6()))
								&& ((topLeft.getLongitudeE6() < pLon) && (pLon < bottomRight
										.getLongitudeE6())))
						{
							textOverlays.add(myOverlay);
							itemizedOverlay2.addOverlay(overlItem);

							displayedMarkers.add(i);
							mapOverlays.add(itemizedOverlay2);
						}
					}
				}
			}
			mapOverlays.addAll(textOverlays);
			map.postInvalidate();
		}
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(this);
		mMyLocationOverlay.disableMyLocation();
		// Log.i("Map", "OnPause");
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub

		super.onResume();

		int objTypeId = 1;
		String vFilterType = Prefs.getFilterType(context);
		if (vFilterType.contentEquals("0"))
		{
			objTypeId = Integer.valueOf(Prefs.getSelObjType(context));
		} else if (vFilterType.contentEquals("1"))
		{
			objTypeId = Integer.valueOf(Prefs.getSelProdType(context));
		}
		mapOverlays.clear();
		displayedMarkers.clear();
		visibleList.clear();
		prepareOverlaysList(objTypeId);
		//m1();
		balloon_main_layout.setVisibility(View.GONE);

		if (Prefs.getMapObjLat(context) != "")
		{
			// Animation fadeInAnimation =
			// AnimationUtils.loadAnimation(Map.context, R.anim.fade_in_map);
			// Map.balloon_main_layout.startAnimation(fadeInAnimation);

			float lat = ProjectUtils.getFloatFromString(Prefs
					.getMapObjLat(context));
			float lon = ProjectUtils.getFloatFromString(Prefs
					.getMapObjLon(context));

			Message msg = new Message();
			// handlerMsg.obtainMessage()
			balloon_item_title.setText(Prefs.getMapObjTitShort(context));
			balloon_item_snippet.setText(Prefs.getMapObjTitLong(context));
			map.getController().animateTo(
					new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)),
					rAndShow);
		} else
		{
		}

		mMyLocationOverlay.enableMyLocation();
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 500, 1, this);
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location l)
	{
		// TODO Auto-generated method stub
		/*
		 * lat = (int) (l.getLatitude()*1E6); longi = (int)
		 * (l.getLongitude()*1E6);
		 * 
		 * mMyLocationOverlay = new MyLocationOverlay(this,map);
		 * listOverlay.add(mMyLocationOverlay);
		 */
		// Log.i("locccc", "chng");
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

//	public static void m1()
//	{
//		printPointsOnMap();
//
//		// Toast.makeText(Map.context, String.valueOf(map.getNewBottomRight()),
//		// Toast.LENGTH_SHORT).show();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void toCurrLoc(View v)
	{
		balloon_main_layout.setVisibility(View.GONE);
		if (mMyLocationOverlay.getMyLocation() != null)
		{
			map.getController()
					.animateTo(mMyLocationOverlay.getMyLocation(), r);
		}
	}

	public void toAr(View v)
	{
		balloon_main_layout.setVisibility(View.GONE);
		String JsonMarkers = "";
		JsonMarkers = " {'Markers\':[";

		for (int i = 0; i < visibleList.size(); i++)
		{
			MapItem visMapObject = (MapItem) visibleList.get(i);
			// String iconStr = visMapObject.getImgSrcStr();
			String iconStr = visMapObject.getIconName().substring(
					visMapObject.getIconName().indexOf("_") + 1)
					+ "_m";
			// String iconStr = "26_m";
			if (i == 10)
			{
				break;
			}
			if (i != 0)
			{
				JsonMarkers += ",";
			}
			JsonMarkers += "{'Lat':" + visMapObject.getLongitude() + ",'Lon\':"
					+ visMapObject.getLatitude() + ",'Alt':0.0,'Text':\'"
					+ visMapObject.toStringLong() + "','Icon':'" + iconStr
					+ ".png','id':'" + visMapObject.getId() + "'}";
		}
		JsonMarkers += "]};";

		Intent in = new Intent(context, C_ARActivityN.class);
		in.putExtra("Markers", JsonMarkers);
		in.putExtra("UseCollisionDetection", false);
		startActivity(in);
	}

	public void selectPoint(final int index)
	{
		iv = (ImageView) findViewById(R.id.ImageView03);
		map.getController().animateTo(visibleList.get(index).getGeoPoint());
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			public void run()
			{
				refreshMap();
				MyItemizedOverlay miol = itemizedOverlayArray.get(index);
				if (miol.size() > 0)
				{//	miol.onTap(0);}
				
				}
				iv.setEnabled(true);
			}
		}, 500);

	}

	public void randomize(View v)
	{
		int i = 0;
		try
		{
			iv = (ImageView) findViewById(R.id.ImageView03);
			if (iv.isEnabled())
			{
				iv.setEnabled(false);
				List<MapItem> nearestItems = new ArrayList<MapItem>();
				nearestItems.addAll(visibleList);
				MapItem.sortMapItemList(nearestItems,
						mMyLocationOverlay.getMyLocation());
				i = 0;
				System.out.println("|");
				while (nearestItems.get(i).distanceTo(
						mMyLocationOverlay.getMyLocation()) < 500
						&& i < nearestItems.size() - 1)
					i++;
				if (i > 0 && i < nearestItems.size())
				{
					nearestItems = nearestItems.subList(0, i);
					i = (int) Math.round(Math.random() * (nearestItems.size()));
					System.out.println("|||");
					if (visibleList.contains(nearestItems.get(i)))
					{
						i = visibleList.indexOf(nearestItems.get(i));
						System.out.println("|V");
						selectPoint(i);
					}
				}
			}
		} catch (IndexOutOfBoundsException e)
		{
			System.out.println("Opa! error2! " + Integer.toString(i));
			iv.setEnabled(true);
		}
	}
}
