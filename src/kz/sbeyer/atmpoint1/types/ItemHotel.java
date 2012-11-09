package kz.sbeyer.atmpoint1.types;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;


public class ItemHotel extends FSQItem 
{

	public static final String HOTEL_IMG="m_13"; 

	public ItemHotel loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);
		ItemHotel ht=this;
		return ht;
	}

	@Override
	public String getIconName()
	{
		return HOTEL_IMG;
	}
	
	
	@Override
	public int getItemColor()
	{
		return context.getResources().getColor(R.color.hotel);
	}
	
	@Override
	public Drawable getIconDrawable()
	{
		return context.getResources().getDrawable(R.drawable.icon_hot);
	}
	
}
