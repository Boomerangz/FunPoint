package kz.crystalspring.funpoint.venues;

import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;

import org.json.JSONException;
import org.json.JSONObject;

public class FSQItem extends MapItem
{
	String name;
	String address;
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
			setName(jObject.getString("name"));
			setId(jObject.getString("id"));
			JSONObject location=jObject.getJSONObject("location");
			float lat=(float) location.getDouble("lat");
			float lng=(float) location.getDouble("lng");
			if (!location.isNull("address"))	
				setAddress(location.getString("address"));
			setLatitude(lat);
			setLongitude(lng);
			return this;
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	

	@Override
	public String getIconName()
	{
				return "m_13";
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCategory()
	{
		return category;
	}

}
