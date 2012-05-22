package kz.crystalspring.android_client.augmented_reality.ui.objects;

//import kz.crystalspring.android_client.augmented_reality.camera.CameraModel;
//import kz.crystalspring.android_client.augmented_reality.common.PitchBearingCalculator;
import kz.crystalspring.android_client.augmented_reality.data.ARData;
//import kz.crystalspring.android_client.augmented_reality.data.ScreenPosition;
import kz.crystalspring.android_client.augmented_reality.ui.Marker;
import kz.crystalspring.android_client.augmented_reality.ui.Radar;

import android.graphics.Canvas;
//import android.graphics.Color;

/**
 * This class extends PaintableObject to draw all the Markers at their appropriate locations.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableRadarPoints extends PaintableObject {
    private final float[] locationArray = new float[3];
	private PaintablePoint paintablePoint = null;
	private PaintablePosition pointContainer = null;

	
	
//    private static ScreenPosition fLeftRadarLine = null;
//    private static ScreenPosition fRightRadarLine = null;
//    private static PaintablePosition fLeftLineContainer = null;
//    private static PaintablePosition fRightLineContainer = null;
//    private static PaintableLine fLeftLine = null;
//    private static PaintableLine fRightLine = null;
    
    
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();

		//Draw the markers in the circle
		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
		for (Marker pm : ARData.getMarkers()) {
		    pm.getLocation().get(locationArray);
		    float x = locationArray[0] / scale;
		    float y = locationArray[2] / scale;
		    if ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS)) {
		        if (paintablePoint==null) paintablePoint = new PaintablePoint(pm.getColor(),true);
		        else paintablePoint.set(pm.getColor(),true);

		        if (pointContainer==null) pointContainer = new PaintablePosition( 	paintablePoint, 
		                (x+Radar.RADIUS-1), 
		                (y+Radar.RADIUS-1), 
		                0, 
		                1);
		        else pointContainer.set(paintablePoint, 
		                (x+Radar.RADIUS-1), 
		                (y+Radar.RADIUS-1), 
		                0, 
		                1);

		        pointContainer.paint(canvas);
		    }
		}
//		drawRadarLines(canvas);
    }

	
	
//	 private void drawRadarLines(Canvas canvas) {
//		 int vColor = Color.rgb(255,255,255);
//	    	if (canvas==null) throw new NullPointerException();
//	    	if (fLeftRadarLine==null) fLeftRadarLine = new ScreenPosition();
//	        if (fRightRadarLine==null) fRightRadarLine = new ScreenPosition();
//	        //Left line
//	    	if (fLeftLine==null){
//	    		fLeftRadarLine.set(0, -Radar.RADIUS);
//	    		fLeftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE/2);
//	    		fLeftRadarLine.add(0*Radar.PAD_X+Radar.RADIUS, 0*Radar.PAD_Y+Radar.RADIUS);
//	            float leftX = fLeftRadarLine.getX()-(0*Radar.PAD_X+Radar.RADIUS);
//	            float leftY = fLeftRadarLine.getY()-(0*Radar.PAD_Y+Radar.RADIUS);	            
//	            fLeftLine = new PaintableLine(vColor, leftX, leftY);
//	    	}
//	        if (fLeftLineContainer==null) {
//	        	fLeftLineContainer = new PaintablePosition(  fLeftLine, 
//	        			0*Radar.PAD_X+Radar.RADIUS, 
//	                    0*Radar.PAD_Y+Radar.RADIUS, 
//	                                                        0, 
//	                                                        1);
//	        } else {
//	        	fLeftLineContainer.set(fLeftLine, 
//	        			0*Radar.PAD_X+Radar.RADIUS, 
//	        			0*Radar.PAD_Y+Radar.RADIUS, 
//	        			0,// PitchBearingCalculator.getBearing(), 
//	                                                        1);
//	        }
//	        fLeftLineContainer.paint(canvas);
//	        
//	        //Right line
//	        if (fRightLine==null) {
//	            fRightRadarLine.set(0, -Radar.RADIUS);
//	            fRightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
//	            fRightRadarLine.add(0*Radar.PAD_X+Radar.RADIUS, 0*Radar.PAD_Y+Radar.RADIUS);
//	            float rightX = fRightRadarLine.getX()-(0*Radar.PAD_X+Radar.RADIUS);
//	            float rightY = fRightRadarLine.getY()-(0*Radar.PAD_Y+Radar.RADIUS);
//	            fRightLine = new PaintableLine(vColor, rightX, rightY);
//	        }
//	        if (fRightLineContainer==null) {
//	        	fRightLineContainer = new PaintablePosition( fRightLine, 
//	        			0*Radar.PAD_X+Radar.RADIUS, 
//	        			0*Radar.PAD_Y+Radar.RADIUS, 
//	                                                        0,
//	                                                        1);
////	        } else {
////	        	fRightLineContainer.set( fRightLine, 
////	        			0*Radar.PAD_X+Radar.RADIUS, 
////	        			0*Radar.PAD_Y+Radar.RADIUS, 
////	                                                        0,
////	                                                        1);
//	        }
//	        fRightLineContainer.paint(canvas);
//	    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getWidth() {
        return Radar.RADIUS * 2;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getHeight() {
        return Radar.RADIUS * 2;
    }
}
