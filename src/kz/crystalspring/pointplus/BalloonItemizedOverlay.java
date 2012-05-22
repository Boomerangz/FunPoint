	package kz.crystalspring.pointplus;

import java.util.List;

import kz.crystalspring.funpoint.Map;
import kz.crystalspring.pointplus.R;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * An abstract extension of ItemizedOverlay for displaying an information balloon
 * upon screen-tap of each marker overlay.
 * 
 * @author Jeff Gilfelt
 */
public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> {

	private MapView mapView;
	private BalloonOverlayView<Item> balloonView;
	private View clickRegion;
	private View closeRegion;
	private int viewOffset;
	final MapController mc;
	private Item currentFocusedItem;
	private int currentFocusedIndex;
	
	/**
	 * Create a new BalloonItemizedOverlay
	 * 
	 * @param defaultMarker - A bounded Drawable to be drawn on the map for each item in the overlay.
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
	}
	
	/**
	 * Set the horizontal distance between the marker and the bottom of the information
	 * balloon. The default is 0 which works well for center bounded markers. If your
	 * marker is center-bottom bounded, call this before adding overlay items to ensure
	 * the balloon hovers exactly above the marker. 
	 * 
	 * @param pixels - The padding between the center point and the bottom of the
	 * information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	public int getBalloonBottomOffset() {
		return viewOffset;
	}
	
	/**
	 * Override this method to handle a "tap" on a balloon. By default, does nothing 
	 * and returns false.
	 * 
	 * @param index - The index of the item whose balloon is tapped.
	 * @param item - The item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index, Item item) {
		return false;
	}
	
	/**
	 * Override this method to perform actions upon an item being tapped before 
	 * its balloon is displayed.
	 * 
	 * @param index - The index of the item tapped.
	 */
	protected void onBalloonOpen(int index) {}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	//protected final boolean onTap(int index) {
	public boolean onTap(int index) {
		
		try
		{
		System.out.println(index);
		System.out.println(size());
		currentFocusedIndex = index;
		currentFocusedItem = createItem(index);
		setLastFocusedIndex(index);
		

	    float scale = Map.context.getResources().getDisplayMetrics().density; 
	    int bottomOffset = Math.round(41 * scale); 

		Animation fadeInAnimation = AnimationUtils.loadAnimation(Map.context, R.anim.fade_in_map);
	   // Map.balloon_main_layout.startAnimation(fadeInAnimation);
	    
	    Map.balloon_main_layout.setVisibility(View.VISIBLE);
        
        int padSize = Math.round(0 * scale); 
        int padTop = Math.round(200*scale);  
        int padLeft = Math.round(280*scale);  
        int padTopBottomMenus = Math.round(100*scale);  
        int balloonHeight = Math.round(90*scale);
        int margTopSize = Math.round((Map.hheight-padTop-balloonHeight-padTopBottomMenus)/2); 
        int margLeftSize = Math.round((Map.wwidth-padLeft)/2); 
        int imgSize1 = Math.round(280*scale);
	    
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imgSize1,LayoutParams.WRAP_CONTENT);
        lp.setMargins(margLeftSize, margTopSize, 0, 0);
        Map.balloon_main_layout.setLayoutParams(lp);
	    
	    onBalloonOpen(index);
		setBalloonBottomOffset(bottomOffset);
		createAndDisplayBalloonOverlay();
		/*
        final View target = balloonView;
        final View targetParent = (View) target.getParent();

        Animation a = new TranslateAnimation(0.0f,
                0.0f,-targetParent.getHeight(), 0.0f);
        
        a.setDuration(400);
        a.setStartOffset(400);
        a.setRepeatMode(Animation.START_ON_FIRST_FRAME);
        a.setRepeatCount(0);
        a.setInterpolator(AnimationUtils.loadInterpolator(Map.context,
                android.R.anim.decelerate_interpolator));

        target.startAnimation(a);
        */
	    
		mc.animateTo(currentFocusedItem.getPoint());
		}
		catch (IndexOutOfBoundsException e) 
		{
			System.out.println("Opa! error!");
		}
		return true;
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that
	 * can populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView() {
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}
	
	/**
	 * Expose map view to subclasses.
	 * Helps with creation of balloon views. 
	 */
	protected MapView getMapView() {
		return mapView;
	}
	
	/**
	 * Sets the visibility of this overlay's balloon view to GONE and unfocus the item. 
	 */
	public void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
		currentFocusedItem = null;
	}
	
	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {
		
		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}
		
	}
	
	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden {@link #onBalloonTap} method.
	 */
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			
			float startX;
			float startY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = {android.R.attr.state_pressed};
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					startX = event.getX();
					startY = event.getY();
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					if (Math.abs(startX - event.getX()) < 40 && 
							Math.abs(startY - event.getY()) < 40 ) {
						// call overridden method
						onBalloonTap(currentFocusedIndex, currentFocusedItem);
					}
					return true;
				} else {
					return false;
				}
				
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#getFocus()
	 */
	@Override
	public Item getFocus() {
		return currentFocusedItem;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#setFocus(Item)
	 */
	@Override
	public void setFocus(Item item) {
		super.setFocus(item);	
		currentFocusedIndex = getLastFocusedIndex();
		currentFocusedItem = item;
		if (currentFocusedItem == null) {
			//hideBalloon();
		} else {
			createAndDisplayBalloonOverlay();
		}	
	}
	
	/**
	 * Creates and displays the balloon overlay by recycling the current 
	 * balloon or by inflating it from xml. 
	 * @return true if the balloon was recycled false otherwise 
	 */
	private boolean createAndDisplayBalloonOverlay(){
		boolean isRecycled;
		if (balloonView == null) {
			balloonView = createBalloonOverlayView();
			balloonView.setVisibility(View.GONE);
			clickRegion = balloonView.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());
			closeRegion = balloonView.findViewById(R.id.balloon_close);
			if (closeRegion != null) {
				closeRegion.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Map.balloon_main_layout.setVisibility(View.GONE);
						//hideOtherBalloons(mapView.getOverlays());
					}
				});
			}
			isRecycled = false;
		} else {
			isRecycled = true;
		}

		balloonView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			//Map.balloon_main_layout.setVisibility(View.GONE);
			//hideOtherBalloons(mapOverlays);
		}
		
		if (currentFocusedItem != null)
			balloonView.setData(currentFocusedItem);
		
		GeoPoint point = currentFocusedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		balloonView.setVisibility(View.GONE);
		
		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}
		
		return isRecycled;
	}
	
}