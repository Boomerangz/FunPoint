package kz.crystalspring.funpoint.venues;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class FSQItem extends MapItem
{
	String name;
	String address;
	String category = FSQ_TYPE_FOOD;
	int hereNow;
	List<String> categoryList = new ArrayList<String>(); 

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
			setHereNow(jObject.getJSONObject("hereNow").getInt("count"));
			loadCategories(jObject.getJSONArray("categories"));
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
			optInfo.loadPhones(fsqJObject.getJSONObject("contact"));
			optInfo.loadPhotoUrls(fsqJObject.getJSONObject("photos"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public void itemCinemaLoadOptionalInfo(JSONObject fsqJObject)
	{
		optInfo = new OptionalInfo();
		try
		{
			optInfo.loadComments(fsqJObject.getJSONObject("tips"));
			optInfo.loadPhones(fsqJObject.getJSONObject("contact"));
			optInfo.loadPhotoUrls(fsqJObject.getJSONObject("photos"));
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


	public List<String> getPhones()
	{
		if (optInfo!=null)
			return optInfo.getFSQPhonesList();
		else
			return null;
	}

	public boolean isCheckedIn()
	{
		return FSQConnector.isInCheckList(getId());
	}

	public boolean isCheckedToDo()
	{
		return FSQConnector.isInTodoList(getId());
	}
	
	
	public void loadCategories(JSONArray jArray)
	{
		for (int i=0; i<jArray.length(); i++)
		{
			try
			{
				JSONObject jCateg=jArray.getJSONObject(i);
				String sCateg=jCateg.getString("name");
				categoryList.add(sCateg);
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		if (ArrayList.class.isInstance(categoryList))
			((ArrayList<String>)categoryList).trimToSize();
	}
	
	public String getCategoriesString()
	{
		String st="";
		for (String s:categoryList)
		{
			st+=", "+s;
		}
		st=st.substring(2).trim();
		return st;
	}
	
	@Override
	public String getShortCharacteristic()
	{
		return getCategoriesString();
	}
	
	public int getPhotosCount()
	{
		return optInfo.getPhotosCount();
	}
	
	public UrlDrawable getUrlAndPhoto(int i)
	{
		return optInfo.getUrlAndPhoto(i);
	}
	
	@Override
	public int getItemColor()
	{
		if (getObjTypeId().equals(FSQ_TYPE_MARKET))
			return context.getResources().getColor(R.color.shop);
		return context.getResources().getColor(R.color.selected_blue);
	}

}
