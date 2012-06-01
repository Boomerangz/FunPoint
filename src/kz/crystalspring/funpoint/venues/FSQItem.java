package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.List;

import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FSQItem extends MapItem
{
	String name;
	String address;
	String category = FSQ_TYPE_FOOD;
	boolean checkedIn;
	boolean checkedToDo;
	int hereNow;

	OptionalInfo optInfo;

	@Override
	public String getObjTypeId()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	@Override
	public MapItem loadFromJSON(JSONObject jObject)
	{
		try
		{
			setName(jObject.getString("name"));
			setId(jObject.getString("id"));
			JSONObject location = jObject.getJSONObject("location");
			float lat = (float) location.getDouble("lat");
			float lng = (float) location.getDouble("lng");
			if (!location.isNull("address"))
				setAddress(location.getString("address"));
			setLatitude(lat);
			setLongitude(lng);
			setHereNow(jObject.getInt("hereNow"));
			setCheckedIn(false);
			setCheckedToDo(false);
			return this;
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getIconName()
	{
		return "m_4";
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

	public void itemFoodLoadOptionalInfo(JSONObject fsqJObject)
	{
		optInfo = new OptionalInfo();
		try
		{
			optInfo.loadComments(fsqJObject.getJSONObject("tips"));
			optInfo.loadCategories(fsqJObject.getJSONArray("categories"));
			optInfo.loadPhones(fsqJObject.getJSONObject("contact"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public OptionalInfo getOptionalInfo()
	{
		return optInfo;
	}

	public int getHereNow()
	{
		return hereNow;
	}

	public void setHereNow(int hereNow)
	{
		this.hereNow = hereNow;
	}
	
	public String getFSQCategoriesString()
	{
		return optInfo.getCategoriesString();
	}

	public List<String> getPhones()
	{
		return optInfo.getFSQPhonesList();
	}

	public boolean isCheckedIn()
	{
		return checkedIn;
	}

	public void setCheckedIn(boolean checkedIn)
	{
		this.checkedIn = checkedIn;
	}

	public boolean isCheckedToDo()
	{
		return checkedToDo;
	}

	public void setCheckedToDo(boolean checkedToDo)
	{
		this.checkedToDo = checkedToDo;
	}
	
	
}
