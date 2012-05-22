package kz.sbeyer.atmpoint1.types;

import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.IsolatedContext;

public class ItemFood extends FSQItem
{

	public static final String REST_IMG = "exchange";

	@Override
	public String getIconName()
	{
		return REST_IMG;
	}
	
	public String getLunchPrice()
	{
		return "lunchprice";
	}

	public ItemFood loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);
		return this;
	}
}
