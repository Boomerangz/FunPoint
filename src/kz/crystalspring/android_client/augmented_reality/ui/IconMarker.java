package kz.crystalspring.android_client.augmented_reality.ui;

//import kz.crystalspring.android_client.augmented_reality.common.Utilities;
import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintableIcon;
import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintablePosition;
import android.graphics.Bitmap;
import android.graphics.Canvas;
// не используется!!!

/**
 * This class extends Marker and draws an icon instead of a circle for it's visual representation.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class IconMarker extends Marker {
    private static final float[] symbolArray = new float[3];
    private Bitmap bitmap = null;

    public IconMarker(String name, double latitude, double longitude, double altitude, int color, String pId, Bitmap bitmap) {
        super(name, latitude, longitude, altitude, color, bitmap, pId);
        this.bitmap = bitmap;
    }

	/**
	 * {@inheritDoc}
	 */
//    @Override
    public void drawIcon(Canvas canvas) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();
    	
        if (gpsSymbol==null) gpsSymbol = new PaintableIcon(bitmap,96,96);
    	
//        float vAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]) + 90; // Added by EArt 02 11 2011
        
        symbolXyzRelativeToCameraView.get(symbolArray);
        if (symbolContainer==null) 
            symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], fAngle, 1);
        else 
            symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], fAngle, 1);
        symbolContainer.paint(canvas);
    }
}
