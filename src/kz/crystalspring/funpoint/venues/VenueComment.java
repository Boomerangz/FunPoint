package kz.crystalspring.funpoint.venues;


import org.json.JSONException;
import org.json.JSONObject;

public class VenueComment
{
	String text;
	String author;
	public VenueComment loadFromJSON(JSONObject jObject)
	{	
		VenueComment item=this;
		try
		{
			String userLastName=jObject.getJSONObject("user").getString("lastName");
			String userFirstName=jObject.getJSONObject("user").getString("firstName");
			author=userFirstName+" "+userLastName;
			text=jObject.getString("text");
		} catch (JSONException e)
		{
			e.printStackTrace();
			item=null;
		}
		return item;
	}
	
	public String getAuthor()
	{
		return author;
	} 
	
	public String getText()
	{
		return text;
	}

	public String getShortText()
	{
		return getText().substring(0, 15)+"...";
	}
	
	public boolean isLongText()
	{
		return (getText().length()>40);
	}
}
