package kz.crystalspring.pointplus;

import java.util.ArrayList;

import kz.crystalspring.funpoint.funMap;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>
{

	private ArrayList<OverlayItem> myOverlays;
	private Context c;
	private funMap map;

	public MyItemizedOverlay(Drawable defaultMarker, funMap map)
	{
		super(boundCenterBottom(defaultMarker));
		myOverlays = new ArrayList<OverlayItem>();
		populate();
		this.map=map;
	}

	public void addOverlay(OverlayItem overlay)
	{
		myOverlays.add(overlay);
		//populate();
	}

	@Override
	protected OverlayItem createItem(int i)
	{
		return myOverlays.get(i);
	}

	// Removes overlay item i
	public void removeItem(int i)
	{
		myOverlays.remove(i);
		populate();
	}

	// Handle tap events on overlay icons
	/*
	 * @Override public boolean onTap(int i){
	 * 
	 * GeoPoint gpoint = myOverlays.get(i).getPoint(); double lat =
	 * gpoint.getLatitudeE6()/1e6; double lon = gpoint.getLongitudeE6()/1e6;
	 * String toast =
	 * "Title: "+String.valueOf(i)+";"+myOverlays.get(i).getTitle(); toast +=
	 * "\nText: "+myOverlays.get(i).getSnippet(); toast +=
	 * "\nSymbol coordinates: Lat = "+lat+" Lon = "+lon+" (microdegrees)";
	 * Toast.makeText(Map.context, toast, Toast.LENGTH_LONG).show();
	 * return(true); }
	 * 
	 * /* In this case we will just put a transient Toast message on the screen
	 * indicating that we have captured the relevant information about the
	 * overlay item. In a more serious application one could replace the Toast
	 * with display of a customized view with the title, snippet text, and
	 * additional features like an embedded image, video, or sound, or links to
	 * additional information. (The lat and lon variables return the coordinates
	 * of the icon that was clicked, which could be used for custom positioning
	 * of a display view.)
	 */
	/*
	 * GeoPoint gpoint = myOverlays.get(i).getPoint(); double lat =
	 * gpoint.getLatitudeE6()/1e6; double lon = gpoint.getLongitudeE6()/1e6;
	 * String toast = "Title: "+myOverlays.get(i).getTitle(); toast +=
	 * "\nText: "+myOverlays.get(i).getSnippet(); toast +=
	 * "\nSymbol coordinates: Lat = "+lat+" Lon = "+lon+" (microdegrees)";
	 * Toast.makeText(Map.context, toast, Toast.LENGTH_LONG).show();
	 * return(true); }
	 */

	// @Override
	// protected boolean onBalloonTap(int index, OverlayItem item)
	// {
	//
	// // String txt = item.getTitle();
	// //
	// // int indx = txt.indexOf("ID ");
	// // String substr = txt.substring(indx+3);
	// // int indx1 = substr.indexOf(" ");;
	// // String substr1 = substr.substring(0,indx1);
	// //
	// // Prefs.setSelObjId(MainMenu.context, substr1);
	// // MainMenu.tabHost.setCurrentTab(1);
	// return false;
	// }

	// Returns present number of items in list
	@Override
	public int size()
	{
		return myOverlays.size();
	}

	public void populateNow()
	{
		populate();
	}

	public void removeAll()
	{
		myOverlays.clear();
		populate();
	}
	
	@Override
	public boolean onTap(int index)
	{
		System.out.println("OnTap "+Integer.toString(index));
		map.selectItem(index);
		return super.onTap(index);
	}
}
