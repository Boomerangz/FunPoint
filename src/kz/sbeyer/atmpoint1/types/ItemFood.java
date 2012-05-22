package kz.sbeyer.atmpoint1.types;

import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.IsolatedContext;

public class ItemFood extends MapItem {

	int id;
	String title;
	String address;
	String phone;
	String worktime;
	String kitchen;
	String priceInterval;
	String lunchPrice;
	int isValid;
	public static final String REST_IMG="exchange";
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getWorktime()
	{
		return worktime;
	}
	public void setWorktime(String worktime)
	{
		this.worktime = worktime;
	}
	public String getKitchen()
	{
		return kitchen;
	}
	public void setKitchen(String kitchen)
	{
		this.kitchen = kitchen;
	}
	public String getPriceInterval()
	{
		return priceInterval;
	}
	public void setPriceInterval(String priceInterval)
	{
		this.priceInterval = priceInterval;
	}
	public String getLunchPrice()
	{
		return lunchPrice;
	}
	public void setLunchPrice(String lunchPrice)
	{
		this.lunchPrice = lunchPrice;
	}

	public void setIsValid(int isValid)
	{
		this.isValid=isValid;// TODO Auto-generated method stub
		
	}
	public int getIsValid()
	{
		// TODO Auto-generated method stub
		return isValid;
	}
	
	public ItemFood loadFromJSON(JSONObject jObject)
	{
		try
		{
			float lat=Float.parseFloat(jObject.getString("lat"));
			float lon=Float.parseFloat(jObject.getString("lon"));
			
			this.setAddress(jObject.getString("adr"));
			this.setTitle(jObject.getString("title"));
			this.setId(jObject.getInt("id"));
			this.setIsValid(jObject.getInt("valid"));
			this.setKitchen(jObject.getString("kitch"));
			this.setLatitude(lat);
			this.setLongitude(lon);
			this.setLunchPrice(jObject.getString("lnchpr"));
			this.setPhone(jObject.getString("phone"));
			this.setPriceInterval(jObject.getString("chkpr"));
			this.setWorktime(jObject.getString("wrktm"));
			return this;
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public String getObjTypeId()
	{
		return FSQ_TYPE_FOOD;
	}
	
	@Override
	public String toString()
	{
		return super.toString()+" "+getTitle();
	}
	
	public String getIconName()
	{
		return REST_IMG;
	}
	
	
	
	
}
