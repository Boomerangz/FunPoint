package kz.crystalspring.funpoint.venues;

import org.json.JSONException;
import org.json.JSONObject;

public class VenueComment implements Comparable<VenueComment>
{
	String text;
	String author;
	java.util.Date createdAt;

	public VenueComment loadFromJSON(JSONObject jObject)
	{
		VenueComment item = this;
		try
		{
			String userLastName = jObject.getJSONObject("user").getString(
					"lastName");
			String userFirstName = jObject.getJSONObject("user").getString(
					"firstName");
			author = userFirstName + " " + userLastName;
			int unixTime = jObject.getInt("createdAt");
			createdAt = new java.util.Date((long) unixTime * 1000);
			text = jObject.getString("text");
		} catch (JSONException e)
		{
			e.printStackTrace();
			item = null;
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
		return getText().substring(0, 15) + "...";
	}

	public boolean isLongText()
	{
		return (getText().length() > 40);
	}

	public java.util.Date getCreatedAt()
	{
		return createdAt;
	}

	@Override
	public int compareTo(VenueComment another)
	{
		return createdAt.compareTo(another.createdAt);
	}
	
//	public void setCreatedAt(java.util.Date createdAt)
//	{
//		this.createdAt = createdAt;
//	}
}
