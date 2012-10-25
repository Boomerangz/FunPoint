package kz.crystalspring.funpoint.venues;

import kz.crystalspring.pointplus.ProjectUtils;

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
		int i = 1;
		String userLastName = null;
		String userFirstName = null;
		while (i <= 5)
		{
			try
			{
				switch (i)
				{
				case 1:
					if (jObject.getJSONObject("user").has("lastName"))
						userLastName = jObject.getJSONObject("user").getString("lastName");
					i++;
				case 2:
					userFirstName = jObject.getJSONObject("user").getString("firstName");
					i++;
				case 3:
					author = (String) ProjectUtils.ifnull(userFirstName, "") + " " + (String) ProjectUtils.ifnull(userLastName, "");
					i++;
				case 4:
					int unixTime = jObject.getInt("createdAt");
					createdAt = new java.util.Date((long) unixTime * 1000);
					i++;
				case 5:
					text = jObject.getString("text");
					i++;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
				i++;
			}
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

	// public void setCreatedAt(java.util.Date createdAt)
	// {
	// this.createdAt = createdAt;
	// }
}
