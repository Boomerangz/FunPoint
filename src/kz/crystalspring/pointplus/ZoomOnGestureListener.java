/**
 * Copyright Casper Wakkers, 2010.
 */
package kz.crystalspring.pointplus;

import com.google.android.maps.MapView;

import android.view.MotionEvent;

import android.view.GestureDetector.SimpleOnGestureListener;

/**
 * The ZoomOnGestureListener extends the {@link SimpleOnGestureListener} which
 * enables you just to override the needed methods.
 * @author Casper Wakkers
 */
public class ZoomOnGestureListener extends SimpleOnGestureListener {
	private MapView mapView = null;

	/**
	 * Constructor.
	 * @param mapView reference to the map view.
	 */
	public ZoomOnGestureListener(MapView mapView) {
		this.mapView = mapView;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// Zoom in.
		mapView.getController().zoomIn();

		return true;
	}
}
