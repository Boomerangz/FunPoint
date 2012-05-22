package kz.sbeyer.atmpoint1.types;

import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class ItemHotel extends MapItem 
{
	
//	int id;
//	String address;
//	float longitude;
//	float latitude;
//	int valid;
	
	String title="";
	String city="";
	String phone="";
	String description="";
	public static final String HOTEL_IMG="m_13"; 
	
	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String getObjTypeId()
	{
		return FSQ_TYPE_HOTEL;
	}



	public ItemHotel loadFromJSON(JSONObject jObject)
	{
		ItemHotel ht=this;
		try 
		{
			float lat=Float.parseFloat(jObject.getString("lat"));
			float lon=Float.parseFloat(jObject.getString("lon"));
			this.setId(jObject.getInt("id"));
			this.setTitle(jObject.getString("name"));
			this.setAddress(jObject.getString("adr"));
			this.setLatitude(lat);
			this.setLongitude(lon);
			this.setIsValid(1);
			this.setCity(jObject.getString("city"));
			this.setPhone(jObject.getString("phone"));
			this.setDescription(jObject.getString("addinfo"));		
			ht=this;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ht=null;
		}
		return ht;
	}
	
	@Override
	public String toString()
	{
		return super.toString()+" "+getTitle();
	}
	
	public String getIconName()
	{
		return HOTEL_IMG;
	}
	

}
