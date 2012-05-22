package kz.crystalspring.funpoint.venues;

import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;

import org.json.JSONException;
import org.json.JSONObject;

public class FSQItem extends MapItem
{
	String name;
	String category=FSQ_TYPE_FOOD;
	@Override
	public String getObjTypeId()
	{
		return category;
	}
	
	public void setCategory(String category)
	{
		this.category=category;
	}

	@Override
	public MapItem loadFromJSON(JSONObject jObject)
	{
		try
		{
		//	float lat=j
			this.name=jObject.getString("name");
			JSONObject location=jObject.getJSONObject("location");
			float lat=(float) location.getDouble("lat");
			float lng=(float) location.getDouble("lng");
			if (!location.isNull("address"))	
				address=location.getString("address");
			setLatitude(lat);
			setLongitude(lng);
			return this;
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getIconName()
	{
		if (category.equals(FSQ_TYPE_CINEMA))
			return ItemCinema.CINEMA_IMG;
			else if (category.equals(FSQ_TYPE_FOOD))
				return ItemFood.REST_IMG;
			else 
				return "m_13";
	}
	
	@Override
	public String toString()
	{
		return name;
	}

}
