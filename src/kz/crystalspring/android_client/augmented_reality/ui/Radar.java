package kz.crystalspring.android_client.augmented_reality.ui;

//import kz.crystalspring.android_client.augmented_reality.camera.CameraModel;
import kz.crystalspring.android_client.augmented_reality.common.PitchBearingCalculator;
import kz.crystalspring.android_client.augmented_reality.data.ARData;
import kz.crystalspring.android_client.augmented_reality.data.ScreenPosition;
import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintableCircle;
//import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintableLine;
import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintablePosition;
import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintableRadarPoints;
//import kz.crystalspring.android_client.augmented_reality.ui.objects.PaintableText;
import android.graphics.Canvas;
import android.graphics.Color;



/**
 * This class will visually represent a radar screen with a radar radius and blips on the screen in their appropriate
 * locations. 
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Radar {
//    public static final float RADIUS = 48;
    public static final float RADIUS = 50;
    
    public static final int LINE_COLOR = Color.argb(150,0,0,220);
//    public static final float PAD_X = 10;
//    public static final float PAD_Y = 20;
    public static final float PAD_X = 20;
    public static final float PAD_Y = 30;
//    private static final int RADAR_COLOR = Color.argb(100, 0, 0, 200);
    private static final int RADAR_COLOR = Color.argb(120, 200, 200, 200);
//    private static final int TEXT_COLOR = Color.rgb(255,255,255);
//    private static final int TEXT_SIZE = 12;

    private static ScreenPosition leftRadarLine = null;
    private static ScreenPosition rightRadarLine = null;
//    private static PaintablePosition leftLineContainer = null;
//    private static PaintablePosition rightLineContainer = null;
    private static PaintablePosition circleContainer = null;
    
    private static PaintableRadarPoints radarPoints = null;
    private static PaintablePosition pointsContainer = null;
    
//    private static PaintableText paintableText = null;
//    private static PaintablePosition paintedContainer = null;
//    
//    private static PaintableLine fLeftLine = null;
//    private static PaintableLine fRightLine = null;

    public Radar() {
        if (leftRadarLine==null) leftRadarLine = new ScreenPosition();
        if (rightRadarLine==null) rightRadarLine = new ScreenPosition();
    }

    /**
     * Draw the radar on the given Canvas.
     * @param canvas Canvas to draw on.
     * @throws NullPointerException if Canvas is NULL.
     */
    public void draw(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();

    	//Update the pitch and bearing using the phone's rotation matrix
    	PitchBearingCalculator.calcPitchBearing(ARData.getRotationMatrix());

        //Update the radar graphics and text based upon the new pitch and bearing
        drawRadarCircle(canvas);
        drawRadarPoints(canvas);
//        drawRadarLines(canvas);
//        drawRadarText(canvas);
    }
    
    private void drawRadarCircle(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        if (circleContainer==null) {
            PaintableCircle paintableCircle = new PaintableCircle(RADAR_COLOR,RADIUS,true);
            circleContainer = new PaintablePosition(paintableCircle,PAD_X+RADIUS,PAD_Y+RADIUS,0,1);
        }
        circleContainer.paint(canvas);
    }
    
    private void drawRadarPoints(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        if (radarPoints==null) radarPoints = new PaintableRadarPoints();
        
        if (pointsContainer==null) 
        	pointsContainer = new PaintablePosition( radarPoints, 
                                                     PAD_X, 
                                                     PAD_Y, 
                                                     -PitchBearingCalculator.getBearing(), 
                                                     1);
        else 
        	pointsContainer.set(radarPoints, 
                    			PAD_X, 
                    			PAD_Y, 
                    			-PitchBearingCalculator.getBearing(), 
                    			1);
        
        pointsContainer.paint(canvas);
    }
    
//    private void drawRadarLines(Canvas canvas) {
//    	if (canvas==null) throw new NullPointerException();
//    	
//        //Left line
//    	if (fLeftLine==null){
////    		leftRadarLine.set(0, -RADIUS);
////            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
////            leftRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);
////            float leftX = leftRadarLine.getX()-(PAD_X+RADIUS);
////            float leftY = leftRadarLine.getY()-(PAD_Y+RADIUS);
//    		
////    		leftRadarLine.set(PAD_X+RADIUS, PAD_Y+RADIUS);
////            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
////            leftRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);
////            float leftX = leftRadarLine.getX()-(PAD_X+RADIUS);
////            float leftY = leftRadarLine.getY()-(PAD_Y+RADIUS);
//            
//            float leftX = 0*PAD_X+0*RADIUS;
//            float leftY = 0*PAD_Y+2*RADIUS;
//            fLeftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
//    	}
//        if (leftLineContainer==null) {
//            leftLineContainer = new PaintablePosition(  fLeftLine, 
//            		0*PAD_X+1*RADIUS, 
//                    0*PAD_Y+1*RADIUS,
//                    0, //PitchBearingCalculator.getPitch()-90,
//                    1);
//        } else {
//        	leftLineContainer.set(fLeftLine, 
//        			0*PAD_X+1*RADIUS, 
//                    0*PAD_Y+1*RADIUS, 
//                    0, //ARData.fAngle, 
//                    1);
//        }
//        leftLineContainer.paint(canvas);
//        
//        //Right line
//        if (fRightLine==null) {
//            rightRadarLine.set(0, -RADIUS);
//            rightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
//            rightRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);
//            
//            float rightX = rightRadarLine.getX()-(PAD_X+RADIUS);
//            float rightY = rightRadarLine.getY()-(PAD_Y+RADIUS);
//            fRightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
//        }
//        if (rightLineContainer==null) {
//            rightLineContainer = new PaintablePosition( fRightLine, 
//                                                        PAD_X+RADIUS, 
//                                                        PAD_Y+RADIUS, 
//                                                        0,//-PitchBearingCalculator.getBearing(),
//                                                        1);
//        } else {
//        	rightLineContainer.set( fRightLine, 
//                                                        PAD_X+RADIUS, 
//                                                        PAD_Y+RADIUS, 
//                                                        0, //-PitchBearingCalculator.getBearing(),
//                                                        1);
//        }
//        rightLineContainer.paint(canvas);
//        
////        rightLineContainer.paintObj(canvas, fRightLine, 100, 100, 0, 1);
//        
//    }
//    
//    
//    private void drawRadarLines_o(Canvas canvas) {
//    	if (canvas==null) throw new NullPointerException();
//    	
//        //Left line
//    	if (fLeftLine==null){
//    		leftRadarLine.set(0, -RADIUS);
//            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
//            leftRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);
//            float leftX = leftRadarLine.getX()-(PAD_X+RADIUS);
//            float leftY = leftRadarLine.getY()-(PAD_Y+RADIUS);
//            fLeftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
//    	}
//        if (leftLineContainer==null) {
//            leftLineContainer = new PaintablePosition(  fLeftLine, 
//                                                        PAD_X+RADIUS, 
//                                                        PAD_Y+RADIUS, 
//                                                        0, //PitchBearingCalculator.getBearing(), 
//                                                        1);
//        } else {
//        	leftLineContainer.set(fLeftLine, 
//                                                        PAD_X+RADIUS, 
//                                                        PAD_Y+RADIUS, 
//                                                        0, //-PitchBearingCalculator.getBearing(), 
//                                                        1);
//        }
//        leftLineContainer.paint(canvas);
//        
//        //Right line
//        if (fRightLine==null) {
//            rightRadarLine.set(0, -RADIUS);
//            rightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
//            rightRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);
//            
//            float rightX = rightRadarLine.getX()-(PAD_X+RADIUS);
//            float rightY = rightRadarLine.getY()-(PAD_Y+RADIUS);
//            fRightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
//        }
//        if (rightLineContainer==null) {
//            rightLineContainer = new PaintablePosition( fRightLine, 
//                                                        PAD_X+RADIUS, 
//                                                        PAD_Y+RADIUS, 
//                                                        0,//-PitchBearingCalculator.getBearing(),
//                                                        1);
//        } else {
//        	rightLineContainer.set( fRightLine, 
//                                                        PAD_X+RADIUS, 
//                                                        PAD_Y+RADIUS, 
//                                                        0, //-PitchBearingCalculator.getBearing(),
//                                                        1);
//        }
//        rightLineContainer.paint(canvas);
//    }
//
//    private void drawRadarText(Canvas canvas) {
//    	if (canvas==null) throw new NullPointerException();
////    	
////        //Direction text
////        int range = (int) (PitchBearingCalculator.getBearing() / (360f / 16f)); 
////        String  dirTxt = "";
////        if (range == 15 || range == 0) dirTxt = "C"; //EArt: old="N"; 
////        else if (range == 1 || range == 2) dirTxt = "СВ"; //EArt: old= "NE"; 
////        else if (range == 3 || range == 4) dirTxt = "В"; //EArt: old="E"; 
////        else if (range == 5 || range == 6) dirTxt = "ЮВ"; //EArt: old="SE";
////        else if (range == 7 || range == 8) dirTxt= "Ю"; //EArt: old="S"; 
////        else if (range == 9 || range == 10) dirTxt = "ЮЗ"; //EArt: old="SW"; 
////        else if (range == 11 || range == 12) dirTxt = "З"; //EArt: old="W"; 
////        else if (range == 13 || range == 14) dirTxt = "СЗ"; //EArt: old="NW";
////        int bearing = (int) PitchBearingCalculator.getBearing(); 
////        radarText(  canvas, 
////                    ""+bearing+((char)176)+" "+dirTxt, 
////                    (PAD_X + RADIUS), 
////                    (PAD_Y - 5), 
////                    true
////                 );
//        
//        //Zoom text
//        radarText(  canvas, 
//                    formatDist(ARData.getRadius() * 1000), 
//                    (PAD_X + RADIUS), 
//                    (PAD_Y + RADIUS*2 -10), 
//                    false
//                 );
//    }
//    
//    private void drawRadarText_o(Canvas canvas) {
//    	if (canvas==null) throw new NullPointerException();
//    	
//        //Direction text
//        int range = (int) (PitchBearingCalculator.getBearing() / (360f / 16f)); 
//        String  dirTxt = "";
//        if (range == 15 || range == 0) dirTxt = "C"; //EArt: old="N"; 
//        else if (range == 1 || range == 2) dirTxt = "СВ"; //EArt: old= "NE"; 
//        else if (range == 3 || range == 4) dirTxt = "В"; //EArt: old="E"; 
//        else if (range == 5 || range == 6) dirTxt = "ЮВ"; //EArt: old="SE";
//        else if (range == 7 || range == 8) dirTxt= "Ю"; //EArt: old="S"; 
//        else if (range == 9 || range == 10) dirTxt = "ЮЗ"; //EArt: old="SW"; 
//        else if (range == 11 || range == 12) dirTxt = "З"; //EArt: old="W"; 
//        else if (range == 13 || range == 14) dirTxt = "СЗ"; //EArt: old="NW";
//        int bearing = (int) PitchBearingCalculator.getBearing(); 
//        radarText(  canvas, 
//                    ""+bearing+((char)176)+" "+dirTxt, 
//                    (PAD_X + RADIUS), 
//                    (PAD_Y - 5), 
//                    true
//                 );
//        
//        //Zoom text
//        radarText(  canvas, 
//                    formatDist(ARData.getRadius() * 1000), 
//                    (PAD_X + RADIUS), 
//                    (PAD_Y + RADIUS*2 -10), 
//                    false
//                 );
//    }
//    
//    private void radarText(Canvas canvas, String txt, float x, float y, boolean bg) {
//    	if (canvas==null || txt==null) throw new NullPointerException();
//    	
//        if (paintableText==null) paintableText = new PaintableText(txt,TEXT_COLOR,TEXT_SIZE,bg);
//        else paintableText.set(txt,TEXT_COLOR,TEXT_SIZE,bg);
//        
//        if (paintedContainer==null) paintedContainer = new PaintablePosition(paintableText,x,y,0,1);
//        else paintedContainer.set(paintableText,x,y, 0,1);
//        
//        paintedContainer.paint(canvas);
//    }
//
//    private static String formatDist(float meters) {
//        if (meters < 1000) {
//            return ((int) meters) + "m";
//        } else if (meters < 10000) {
//            return formatDec(meters / 1000f, 1) + "km";
//        } else {
//            return ((int) (meters / 1000f)) + "km";
//        }
//    }
//
//    private static String formatDec(float val, int dec) {
//        int factor = (int) Math.pow(10, dec);
//
//        int front = (int) (val );
//        int back = (int) Math.abs(val * (factor) ) % factor;
//
//        return front + "." + back;
//    }
}
