package kz.sbeyer.atmpoint1.types;

import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.IsolatedContext;

public class ItemFood extends FSQItem
{

	public static final String REST_IMG = "exchange";
	private FoodOptionalInformation foodOptions;

	
	@Override
	public String getIconName()
	{
		return REST_IMG;
	}

	public String getLunchPrice()
	{
		if (foodOptions!=null)
			return foodOptions.getLunchPrice();
		else 
			return "NULL";
	}

	public ItemFood loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);
		return this;
	}

	public FoodOptionalInformation getFoodOptions()
	{
		return foodOptions;
	}

	public void setFoodOptions(FoodOptionalInformation foodOptions)
	{
		this.foodOptions = foodOptions;
	}

	public void loadFoodOptions(JSONObject jObject)
	{
		foodOptions=new FoodOptionalInformation().loadFromJSON(jObject);
	}

	public String getKitchen()
	{
		if (foodOptions!=null)
			return foodOptions.getKitchen();
		else 
			return "NULL";
	}

}

class FoodOptionalInformation
{
	String worktime;
	String kitchen;
	String checkPrice;
	String lunchPrice;

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

	public String getCheckPrice()
	{
		return checkPrice;
	}

	public void setCheckPrice(String checkPrice)
	{
		this.checkPrice = checkPrice;
	}

	public String getLunchPrice()
	{
		return lunchPrice;
	}

	public void setLunchPrice(String lunchPrice)
	{
		this.lunchPrice = lunchPrice;
	}

	public FoodOptionalInformation loadFromJSON(JSONObject jObject)
	{
		FoodOptionalInformation foi=this;
		try
		{
			setCheckPrice(jObject.getString("chkpr"));
			setLunchPrice(jObject.getString("lnchpr"));
			setKitchen(jObject.getString("kitch"));
			setWorktime(jObject.getString("wrktm"));
		}
		catch
		(Exception e)
		{
			foi=null;
			e.printStackTrace();
		}
		return foi;
	}
}
