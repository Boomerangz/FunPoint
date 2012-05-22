package kz.crystalspring.android_client;

import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONObject;
//import java.util.Locale;

import kz.crystalspring.android_client.augmented_reality.activity.AugmentedReality;
import kz.crystalspring.android_client.augmented_reality.data.ARData;
//import kz.crystalspring.android_client.augmented_reality.data.BuzzDataSource;
//import kz.crystalspring.android_client.augmented_reality.data.LocalDataSource;
//import kz.crystalspring.android_client.augmented_reality.data.NetworkDataSource;
//import kz.crystalspring.android_client.augmented_reality.data.TwitterDataSource;
//import kz.crystalspring.android_client.augmented_reality.data.WikipediaDataSource;
//import kz.crystalspring.android_client.augmented_reality.ui.IconMarker;
import kz.crystalspring.android_client.augmented_reality.ui.Marker;


//import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
//import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
//import android.widget.Toast;


/**
 * This class extends the AugmentedReality and is designed to be an example on how to extends the AugmentedReality
 * class to show multiple data sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class C_ARActivity extends AugmentedReality {
//    private static final String locale = Locale.getDefault().getLanguage();
//	private static Collection<NetworkDataSource> sources = null;    
//    private static Thread thread = null;
	private static String C_TAG = "C_ARActivity";
	private static boolean fIsCallBackFired = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        useCollisionDetection = getIntent().getBooleanExtra("UseCollisionDetection", false);
        String vJSONString = getIntent().getStringExtra("Markers");
        
        double vLatitude = getIntent().getDoubleExtra("Latitude", 0);
        double vLongitude = getIntent().getDoubleExtra("Longitude", 0);
        double vAltitude = getIntent().getDoubleExtra("Altitude", 0);
        fLocationUpdateNeeded = getIntent().getBooleanExtra("UpdateLocation", true);
        
        ARData.fHardFix.setLatitude(vLatitude);
        ARData.fHardFix.setLongitude(vLongitude);
        ARData.fHardFix.setAltitude(vAltitude);

        ARData.setCurrentLocation(ARData.fHardFix);
        
//        useCollisionDetection = false;
//        vJSONString = "{\"Markers\":["+
//        	"{\"Lat\":12.59,\"Lon\":99.92,\"Alt\":-100.0,\"Text\":\"Test банкомат 01\",\"Icon\":\"37_b.png\",\"id\":\"01\"},"+
//        	"{\"Lat\":12.59,\"Lon\":99.9,\"Alt\":-80,\"Text\":\"2\",\"Icon\":\"777777.png\",\"id\":\"02\"},"+
//        	"{\"Lat\":12.57,\"Lon\":99.92,\"Alt\":-40.0,\"Text\":\" \",\"Icon\":\"logo_cs.png\",\"id\":\"03\"},"+
//        	"{\"Lat\":12.57,\"Lon\":99.9,\"Alt\":1.0,\"Text\":\"Test банкомат 04\",\"Icon\":\"splash2.png\",\"id\":\"4\"},"+
//        	"{\"Lat\":12.6,\"Lon\":99.93,\"Alt\":100.0,\"Text\":\"Test банкомат 11\",\"Icon\":\"37_m.png\",\"id\":\"11\"},"+
//        	"{\"Lat\":12.6,\"Lon\":99.89,\"Alt\":50.0,\"Text\":\"Test банкомат 12\",\"Icon\":\"37_m.png\",\"id\":\"12\"},"+
//        	"{\"Lat\":12.56,\"Lon\":99.93,\"Alt\":10.0,\"Text\":\"Test банкомат 13\",\"Icon\":\"37_m.png\",\"id\":\"13\"},"+
//        	"{\"Lat\":12.56,\"Lon\":99.89,\"Alt\":16.0,\"Text\":\"Test банкомат 14\",\"Icon\":\"37_m.png\",\"id\":\"14\"},"+
//        	"{\"Lat\":12.61,\"Lon\":99.94,\"Alt\":200.0,\"Text\":\"Test банкомат 21\",\"Icon\":\"37_m.png\",\"id\":\"21\"},"+
//        	"{\"Lat\":12.61,\"Lon\":99.88,\"Alt\":100.0,\"Text\":\"Test банкомат 22\",\"Icon\":\"37_m.png\",\"id\":\"22\"},"+
//        	"{\"Lat\":12.55,\"Lon\":99.94,\"Alt\":20.0,\"Text\":\"Test банкомат 23\",\"Icon\":\"37_m.png\",\"id\":\"23\"},"+
//        	"{\"Lat\":12.55,\"Lon\":99.88,\"Alt\":71.0,\"Text\":\"Test банкомат 24\",\"Icon\":\"37_m.png\",\"id\":\"24\"}]}";
        
        super.onCreate(savedInstanceState);
        
        ARData.ClearMarkers();
        ARData.addMarkers(AddMarkers(vJSONString));
        
//    	LocalDataSource localData = new LocalDataSource(getBaseContext(), "");
//    	ARData.addMarkers(localData.getMarkers());
    	
//        if (sources==null) {
//        	sources = new ArrayList<NetworkDataSource>();
//        	LocalDataSource localData = new LocalDataSource(getBaseContext(), "");
//        	ARData.addMarkers(localData.getMarkers());
//            NetworkDataSource twitter = new TwitterDataSource(this.getResources());
//            sources.add(twitter);
//            NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources());
//            sources.add(wikipedia);
//            NetworkDataSource buzz = new BuzzDataSource(this.getResources());
//            sources.add(buzz);
//        }
    }

	private List<Marker> AddMarkers (String pJSONString) {
		List<Marker> vMarkers = new ArrayList<Marker>();
		try {
			JSONObject vJSON = new JSONObject(pJSONString);
			JSONArray vArr = vJSON.getJSONArray("Markers");
			for (int i = 0; i < vArr.length(); i++) {
				JSONObject vItem = vArr.getJSONObject(i);
				String vIconName = vItem.getString("Icon");
				if (vIconName.length() > 0) {
					byte[] vIconBytes = C_FileHelper.ReadFile(new File(this.getFilesDir() + "/" + vItem.getString("Icon")));
					Bitmap vIcon = BitmapFactory.decodeByteArray(vIconBytes, 0, vIconBytes.length);
					String vText = vItem.getString("Text");
					if (vText == null) vText = " ";
					if (vText.length()==0) vText = " ";
					vMarkers.add(new Marker(vText, vItem.getDouble("Lat"), vItem.getDouble("Lon"), vItem.getDouble("Alt"), Color.LTGRAY, vIcon, vItem.getString("id")));
//					vMarkers.add(new IconMarker(vItem.getString("Text"), vItem.getDouble("Lat"), vItem.getDouble("Lon"), vItem.getDouble("Alt"), Color.LTGRAY, vItem.getString("id"), vIcon));
//				} else {
//					vMarkers.add(new Marker(vItem.getString("Text"), vItem.getDouble("Lat"), vItem.getDouble("Lon"), vItem.getDouble("Alt"), Color.LTGRAY, vIcon, vItem.getString("id")));
				}
			}			
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "AddMarkers err:" + e.getMessage());
		}
		return vMarkers;
		
//		String vIconFileName = "1_m.png";
//		byte[] vIconBytes;
//		try {
//			vIconBytes = C_FileHelper.ReadFile(new File(this.getFilesDir() + "/" + vIconFileName));
//			Bitmap icon = BitmapFactory.decodeByteArray(vIconBytes, 0, vIconBytes.length);
//			Location l = ARData.getCurrentLocation();
//			float d = 0.01f;
//			String s = "{\"Markers\":[";
//			for (int i=0; i<3; i++) {
//				if (s.length() > 14) s = s + ",";
//				s = s + "{\"Lat\":"+ (float) (l.getLatitude() + d*(i+1)) +	",\"Lon\":"+ (float) (l.getLongitude() + d*(i+1)) + ",\"Alt\":"+ l.getAltitude()*i*100+",\"Text\":\"Test банкомат " + i + "\",\"Icon\":\"37_m.png\"}"; 
//				s = s + "{\"Lat\":"+ (float) (l.getLatitude() + d*(i+1)) +	",\"Lon\":"+ (float) (l.getLongitude() - d*(i+1)) + ",\"Alt\":"+ l.getAltitude()*i*50+",\"Text\":\"Test банкомат " + i + "\",\"Icon\":\"37_m.png\"}"; 
//				s = s + "{\"Lat\":"+ (float) (l.getLatitude() - d*(i+1)) +	",\"Lon\":"+ (float) (l.getLongitude() + d*(i+1)) + ",\"Alt\":"+ l.getAltitude()*i*10+",\"Text\":\"Test банкомат " + i + "\",\"Icon\":\"37_m.png\"}"; 
//				s = s + "{\"Lat\":"+ (float) (l.getLatitude() - d*(i+1)) +	",\"Lon\":"+ (float) (l.getLongitude() - d*(i+1)) + ",\"Alt\":"+ l.getAltitude()+",\"Text\":\"Test банкомат " + i + "\",\"Icon\":\"37_m.png\"}"; 
//				
//				Marker marker = null;
//				marker = new Marker("Test(++)-"+i, l.getLatitude() + d*(i+1), l.getLongitude() + d*(i+1), l.getAltitude(), Color.LTGRAY);
//				vMarkers.add(marker);
//
//				marker = new Marker("Test(+-)-"+i, l.getLatitude() + d*(i+1), l.getLongitude() - d*(i+1), l.getAltitude(), Color.LTGRAY);
//				vMarkers.add(marker);
//
//				marker = new IconMarker("Test(-+)-"+i, l.getLatitude() - d*(i+1), l.getLongitude() + d*(i+1), l.getAltitude(), Color.LTGRAY, icon);
//				vMarkers.add(marker);
//
//				marker = new IconMarker("Test(--)-"+i, l.getLatitude() - d*(i+1), l.getLongitude() - d*(i+1), l.getAltitude(), Color.LTGRAY, icon);
//				vMarkers.add(marker);
//			}
//			s = s + "]}";
//		} catch (IOException e) {
//		
//		}
//		return vMarkers;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onStart() {
        super.onStart();
        
//        Location last = ARData.getCurrentLocation();
//        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
    }

	@Override
	public void onStop() {
	    super.onStop();
		if (!fIsCallBackFired) {
			// при завершении диалога вызввать функцию OnARClickProc с пустым параметром!
			C_JavascriptInterface.OnARClick("");
		}
		fIsCallBackFired = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
//        updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
    }

	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void markerTouched(Marker marker) {
		if (!fIsCallBackFired) {
			C_JavascriptInterface.OnARClick(marker.GetId());
			fIsCallBackFired = true;
		}
		finish();
//        Toast t = Toast.makeText(getApplicationContext(), marker.getName(), Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.CENTER, 0, 0);
//        t.show();
	}

//    private void updateData(final double lat, final double lon, final double alt) {
//    	if (thread!=null && thread.isAlive()) return;
//    	
//    	thread = new Thread(
//    		new Runnable(){ 
//				@Override
//				public void run() {
//					for (NetworkDataSource source : sources) {
//						download(source, lat, lon, alt);
//					}
//				}
//			}
//    	);
//    	thread.start();
//    }
    
//    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
//		if (source==null) return false;
//		
//		String url = null;
//		try {
//			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);    	
//		} catch (NullPointerException e) {
//			return false;
//		}
//    	
//		List<Marker> markers = null;
//		try {
//			markers = source.parse(url);
//		} catch (NullPointerException e) {
//			return false;
//		}
//
//    	ARData.addMarkers(markers);
//    	return true;
//    }
}
