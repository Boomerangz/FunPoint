package kz.sbeyer.atmpoint1.types;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.OptionalInfo;
import kz.crystalspring.pointplus.ProjectUtils;

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
			return "-";
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
		if (foodOptions!=null) this.setAddress(foodOptions.getAddress());
	}

	public String getKitchen()
	{
		if (foodOptions!=null||!foodOptions.getKitchen().equals(""))
			return foodOptions.getKitchen();
		else 
			return getFSQCategoriesString();
	}


	public CharSequence getAvgPrice()
	{
		if (foodOptions!=null)
			return foodOptions.getCheckPrice();
		else 
			return "NULL";
	}
	
	@Override
	public List<String> getPhones()
	{
		if (foodOptions!=null&&foodOptions.getPhones()!=null&&foodOptions.getPhones().size()>0)
			return foodOptions.getPhones();
		else 
			return super.getPhones();
	}


}

class FoodOptionalInformation
{
	String worktime;
	String kitchen;
	String checkPrice;
	String lunchPrice;
	String address;
	List<String> phones;

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
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

	public List<String> getPhones()
	{
			return phones;

	}

	public void setPhones(List<String> phones)
	{
		this.phones = phones;
	}

	public FoodOptionalInformation loadFromJSON(JSONObject jObject)
	{
		FoodOptionalInformation foi=this;
		try
		{
			setCheckPrice(jObject.getString("chkpr"));
			setLunchPrice(jObject.getString("lnchpr"));
			
			if (getLunchPrice().contains("-")&&!getLunchPrice().equals("-")) 
				setLunchPrice(getLunchPrice().substring(0,getLunchPrice().indexOf("-")));
			
			setKitchen(jObject.getString("kitch"));
			setWorktime(jObject.getString("wrktm"));
			setAddress(jObject.getString("adr"));
			setPhones(ProjectUtils.separateStrings(jObject.getString("phone"),","));
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
