package kz.crystalspring.pointplus;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MyMapView extends MapView {
	public MyMapView(Context context, String apiKey) { super(context, apiKey); }
	public MyMapView(Context context, AttributeSet attrs) { super(context, attrs); }
	public MyMapView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
	
	private GeoPoint mOldTopLeft;
	private GeoPoint mOldCenter;
	private GeoPoint mOldBottomRight;
	private int oldZoom = 0;
	
	private GeoPoint newTopLeft;
	private GeoPoint newBottomRight;

	/*private MapViewListener mMapViewListener;
	public MapViewListener getMapViewListener() { return mMapViewListener; }
	public void setMapViewListener(MapViewListener value) { mMapViewListener = value; }*/
	
	public GeoPoint getNewTopLeft(){
		return newTopLeft;
	}
	public GeoPoint getNewBottomRight(){
		return newBottomRight;
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			
			GeoPoint newCenter = this.getMapCenter();
			newTopLeft = this.getProjection().fromPixels(0, 0);
			newBottomRight = this.getProjection().fromPixels(this.getWidth(), this.getHeight());

			String strOldCenter = String.valueOf(mOldCenter);
			String strNewCenter = String.valueOf(newCenter);
			
			if((!strOldCenter.contentEquals(strNewCenter))){
				//Toast.makeText(Map.context, String.valueOf(mOldCenter), Toast.LENGTH_SHORT).show();
				oldZoom = this.getZoomLevel();
				mOldCenter = newCenter;
			}
			/*if (this.mMapViewListener != null &&
				newTopLeft.getLatitudeE6() == mOldTopLeft.getLatitudeE6() &&
				newTopLeft.getLongitudeE6() == mOldTopLeft.getLongitudeE6()) {
				mMapViewListener.onClick(this.getProjection().fromPixels((int)ev.getX(), (int)ev.getY()));

				Toast.makeText(Map.context, "Click", Toast.LENGTH_SHORT).show();
			}*/
		}
		return super.onTouchEvent(ev);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if(oldZoom != this.getZoomLevel()){
			//Toast.makeText(Map.context, String.valueOf(oldZoom), Toast.LENGTH_SHORT).show();
			oldZoom = this.getZoomLevel();
		}
		/*	GeoPoint newCenter = this.getMapCenter();
		GeoPoint newTopLeft = this.getProjection().fromPixels(0, 0);
		GeoPoint newBottomRight = this.getProjection().fromPixels(this.getWidth(), this.getHeight());
		int newZoomLevel = this.getZoomLevel();


		if (mOldCenter == null)
			mOldCenter = newCenter;

		if (mOldTopLeft == null)
			mOldTopLeft = newTopLeft;

		if (mOldBottomRight == null)
			mOldBottomRight = newBottomRight;

		if (newTopLeft.getLatitudeE6() != mOldTopLeft.getLatitudeE6() || newTopLeft.getLongitudeE6() != mOldTopLeft.getLongitudeE6()) {

			if (this.mMapViewListener != null) {
				Toast.makeText(Map.context, String.valueOf(newZoomLevel), Toast.LENGTH_SHORT).show();
				GeoPoint oldTopLeft, oldCenter, oldBottomRight;

				oldTopLeft = mOldTopLeft;
				oldCenter = mOldCenter;
				oldBottomRight = mOldBottomRight;

				mOldBottomRight = newBottomRight;
				mOldTopLeft = newTopLeft;
				mOldCenter = newCenter;

				mMapViewListener.onPan(oldTopLeft,
									   oldCenter,
									   oldBottomRight,
									   newTopLeft,
									   newCenter,
									   newBottomRight);
				Toast.makeText(Map.context, "Pan", Toast.LENGTH_SHORT).show();
			}
		}

		if (mOldZoomLevel == -1)
			mOldZoomLevel = newZoomLevel;
		else if (mOldZoomLevel != newZoomLevel && mMapViewListener != null) {
			int oldZoomLevel = mOldZoomLevel;
			GeoPoint oldTopLeft, oldCenter, oldBottomRight;
			oldTopLeft = mOldTopLeft;
			oldCenter = mOldCenter;
			oldBottomRight = mOldBottomRight;

			mOldZoomLevel = newZoomLevel;
			mOldBottomRight = newBottomRight;
			mOldTopLeft = newTopLeft;
			mOldCenter = newCenter;

			mMapViewListener.onZoom(oldTopLeft,
									oldCenter,
									oldBottomRight,
									newTopLeft,
									newCenter,
									newBottomRight,
									oldZoomLevel,
									newZoomLevel);
			Toast.makeText(Map.context, "Zoom", Toast.LENGTH_SHORT).show();
		}*/
	}
	
}
