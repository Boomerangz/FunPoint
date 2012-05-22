package kz.crystalspring.android_client.augmented_reality.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import kz.crystalspring.android_client.C_Log;
import kz.crystalspring.android_client.augmented_reality.common.Matrix;
import kz.crystalspring.android_client.augmented_reality.ui.Marker;


import android.location.Location;
//import android.util.Log;


/**
 * Abstract class which should be used to set global data.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class ARData {
    private static final String TAG = "ARData";
	private static Map<String,Marker> markerList = new ConcurrentHashMap<String,Marker>();
    private static List<Marker> cache = new CopyOnWriteArrayList<Marker>();
    private static AtomicBoolean dirty = new AtomicBoolean(false);
    private static float[] locationArray = new float[3];
    
    /*defaulting to our place*/
    public static Location fHardFix = new Location("ATL");
    
    private static final Object radiusLock = new Object();
    private static float radius = new Float(20);
    private static String zoomLevel = new String();
    private static final Object zoomProgressLock = new Object();
    private static int zoomProgress = 0;
    private static Location currentLocation = fHardFix;
    private static Matrix rotationMatrix = new Matrix();
        
    public static void ClearMarkers() {
    	markerList = new ConcurrentHashMap<String,Marker>();
    	cache = new CopyOnWriteArrayList<Marker>();
    	dirty = new AtomicBoolean(false);
    	locationArray = new float[3];
    }
    
    /**
     * Set the zoom level.
     * @param zoomLevel String representing the zoom level.
     */
    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel==null) throw new NullPointerException();
    	
    	synchronized (ARData.zoomLevel) {
    	    ARData.zoomLevel = zoomLevel;
    	}
    }
    
    /**
     * Get the zoom level.
     * @return String representing the zoom level.
     */
    public static String getZoomLevel() {
        synchronized (ARData.zoomLevel) {
            return ARData.zoomLevel;
        }
    }
    
    /**
     * Set the zoom progress.
     * @param zoomProgress int representing the zoom progress.
     */
    public static void setZoomProgress(int zoomProgress) {
        synchronized (ARData.zoomProgressLock) {
            if (ARData.zoomProgress != zoomProgress) {
                ARData.zoomProgress = zoomProgress;
                if (dirty.compareAndSet(false, true)) {
                	C_Log.v(2, TAG, "Setting DIRTY flag!");
//                    Log.v(TAG, "Setting DIRTY flag!");
                    cache.clear();
                }
            }
        }
    }
    
    /**
     * Get the zoom progress.
     * @return int representing the zoom progress.
     */
    public static int getZoomProgress() {
        synchronized (ARData.zoomProgressLock) {
            return ARData.zoomProgress;
        }
    }
    
    /**
     * Set the radius of the radar screen.
     * @param radius float representing the radar screen.
     */
    public static void setRadius(float radius) {
        synchronized (ARData.radiusLock) {
            ARData.radius = radius;
        }
    }
    
    /**
     * Get the radius (in KM) of the radar screen.
     * @return float representing the radar screen.
     */
    public static float getRadius() {
        synchronized (ARData.radiusLock) {
            return ARData.radius;
        }
    }
    
    /**
     * Set the current location.
     * @param currentLocation Location to set.
     * @throws NullPointerException if Location param is NULL.
     */
    public static void setCurrentLocation(Location currentLocation) {
    	if (currentLocation==null) throw new NullPointerException();
//    	C_Log.v(3, TAG, "current location. location="+currentLocation.toString());
//    	Log.d(TAG, "current location. location="+currentLocation.toString());
    	synchronized (currentLocation) {
    	    ARData.currentLocation = currentLocation;
//    	    ARData.currentLocation.setAltitude(0); //!!!!!! чтобы значки не скакали по высоте 
    	}
        onLocationChanged(currentLocation);
    }
    
    private static void onLocationChanged(Location location) {
    	C_Log.v(3, TAG, "New location, updating markers. location="+location.toString());
//        Log.d(TAG, "New location, updating markers. location="+location.toString());
        float vMaxDistance = 0.50f; //EArt 01 11 2011 - max distance from current pos to marker, 500m
        float vMinDistance = 9999999999f;
        for(Marker ma: markerList.values()) {
            ma.calcRelativePosition(location);
            vMaxDistance = (float) Math.max(vMaxDistance, ma.getDistance());
            vMinDistance = (float) Math.min(vMinDistance, ma.getDistance());
        }

        if (dirty.compareAndSet(false, true)) {
        	C_Log.v(2, TAG, "Setting DIRTY flag!");
//            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    	//EArt 01 11 2011 - set radar distance:
    	for(Marker marker : markerList.values()) {
   	    	float vDistance = (float) marker.getDistance();
   	    	float vScale = (float) (0.5 + 1 * ((vMaxDistance - vDistance) / (vMaxDistance - vMinDistance) ));
   	        marker.SetScale(vScale);
    	}        
    	float vRadarRadius = (float) ((vMaxDistance * 1.2) / 1000);
        setRadius(vRadarRadius);
        ARData.setZoomLevel(new DecimalFormat("#.##").format(vRadarRadius));
//        ARData.setZoomProgress(myZoomBar.getProgress());
    }
    
    /**
     * Get the current Location.
     * @return Location representing the current location.
     */
    public static Location getCurrentLocation() {
        synchronized (ARData.currentLocation) {
            return ARData.currentLocation;
        }
    }
    
    /**
     * Set the rotation matrix.
     * @param rotationMatrix Matrix to use for rotation.
     */
    public static void setRotationMatrix(Matrix rotationMatrix) {
        synchronized (ARData.rotationMatrix) {
            ARData.rotationMatrix = rotationMatrix;
        }
    }
    
    /**
     * Get the rotation matrix.
     * @return Matrix representing the rotation matrix.
     */
    public static Matrix getRotationMatrix() {
        synchronized (ARData.rotationMatrix) {
            return rotationMatrix;
        }
    }

    /**
     * Add a List of Markers to our Collection.
     * @param markers List of Markers to add.
     */
    public static void addMarkers(Collection<Marker> markers) {
    	if (markers==null) throw new NullPointerException();
    	C_Log.v(2, TAG, "New markers, updating markers. new markers="+markers.toString());
//    	Log.d(TAG, "New markers, updating markers. new markers="+markers.toString());
        float vMaxDistance = 0.50f; //EArt 01 11 2011 - max distance from current pos to marker, 500m
        float vMinDistance = 9999999999f;
    	for(Marker marker : markers) {
    	    if (!markerList.containsKey(marker.getName())) {
    	        marker.calcRelativePosition(ARData.getCurrentLocation());
    	        markerList.put(marker.getName(),marker);
                vMaxDistance = (float) Math.max(vMaxDistance, marker.getDistance());
                vMinDistance = (float) Math.min(vMinDistance, marker.getDistance());
    	    }
    	}
    	
    	if (dirty.compareAndSet(false, true)) {
        	C_Log.v(2, TAG, "Setting DIRTY flag!");
//    	    Log.v(TAG, "Setting DIRTY flag!");
    	    cache.clear();
    	}

    	//EArt 01 11 2011 - set radar distance:
    	for(Marker marker : markers) {
    	    if (!markerList.containsKey(marker.getName())) {
    	        float vDistance = (float) marker.getDistance();
       	    	float vScale = (float) (0.5 + 0.5 * ((vMaxDistance - vDistance) / (vMaxDistance - vMinDistance) ));
       	        marker.SetScale(vScale);
    	    }
    	}  
    	vMaxDistance = (float) ((vMaxDistance * 1.2) / 1000);
        setRadius(vMaxDistance);
        ARData.setZoomLevel(new DecimalFormat("#.##").format(vMaxDistance));
//        ARData.setZoomProgress(0);
        ARData.zoomProgress = 25;
    }

    /**
     * Get the Markers collection.
     * @return Collection of Markers.
     */
    public static List<Marker> getMarkers() {
        //If markers we added, zero out the altitude to recompute the collision detection
        if (dirty.compareAndSet(true, false)) {
        	C_Log.v(3, TAG, "DIRTY flag found, resetting all marker heights to zero.");
//            Log.v(TAG, "DIRTY flag found, resetting all marker heights to zero.");
            for(Marker ma : markerList.values()) {
                ma.getLocation().get(locationArray);
                locationArray[1]=ma.getInitialY();
                ma.getLocation().set(locationArray);
            }

        	C_Log.v(3, TAG, "Populating the cache.");
//            Log.v(TAG, "Populating the cache.");
            List<Marker> copy = new ArrayList<Marker>();
            copy.addAll(markerList.values());
            Collections.sort(copy,comparator);
            //The cache should be sorted from closest to farthest marker.
            cache.clear();
            cache.addAll(copy);
        }
        return Collections.unmodifiableList(cache);
    }
    
    private static final Comparator<Marker> comparator = new Comparator<Marker>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(Marker arg0, Marker arg1) {
            return Double.compare(arg0.getDistance(),arg1.getDistance());
        }
    };
}
