package kz.crystalspring.android_client.augmented_reality.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.android_client.augmented_reality.ui.IconMarker;
import kz.crystalspring.android_client.augmented_reality.ui.Marker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;


public class LocalDataSource extends DataSource{
    private List<Marker> fMarkers = new ArrayList<Marker>();
    private Context fContext;

    
    public LocalDataSource(Context pContext, String pMarkersJSON) {
    	fContext = pContext;
		String vIconFileName = "_m.png";
		byte[] vIconBytes;
		try {
			vIconBytes = C_FileHelper.ReadFile(new File(fContext.getFilesDir() + "/" + vIconFileName));
			Bitmap icon = BitmapFactory.decodeByteArray(vIconBytes, 0, vIconBytes.length);
			Location l = ARData.getCurrentLocation();
			float d = 0.01f;
	        for (int i=0; i<3; i++) {
	            Marker marker = null;
	            marker = new IconMarker("Test(++)-"+i, l.getLatitude() + d*(i+1), l.getLongitude() + d*(i+1), l.getAltitude(), Color.LTGRAY, "", icon);
	            fMarkers.add(marker);

	            marker = new IconMarker("Test(+-)-"+i, l.getLatitude() + d*(i+1), l.getLongitude() - d*(i+1), l.getAltitude(), Color.LTGRAY, "", icon);
	            fMarkers.add(marker);

	            marker = new IconMarker("Test(-+)-"+i, l.getLatitude() - d*(i+1), l.getLongitude() + d*(i+1), l.getAltitude(), Color.LTGRAY, "", icon);
	            fMarkers.add(marker);

	            marker = new IconMarker("Test(--)-"+i, l.getLatitude() - d*(i+1), l.getLongitude() - d*(i+1), l.getAltitude(), Color.LTGRAY, "", icon);
	            fMarkers.add(marker);
	        }
		} catch (IOException e) {
			
		}
    }
	
    @Override
	public List<Marker> getMarkers() {
        return fMarkers;
    }
    
}
