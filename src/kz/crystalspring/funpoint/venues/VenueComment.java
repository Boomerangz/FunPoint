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
			author="John Doew";
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
}
