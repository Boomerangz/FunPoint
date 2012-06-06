package kz.crystalspring.funpoint.venues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class FSQBadge
{

	private String name;
	private String description;
	private String pictureBigURL;
	private String pictureMiddleURL;
	
	private Drawable pictureMiddle;
	private Drawable pictureBig;
	
	private Drawable picture;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getPictureBigURL()
	{
		return pictureBigURL;
	}
	public void setPictureBigURL(String pictureBigURL)
	{
		this.pictureBigURL = pictureBigURL;
	}
	public String getPictureMiddleURL()
	{
		return pictureMiddleURL;
	}
	public void setPictureMiddleURL(String pictureMiddleURL)
	{
		this.pictureMiddleURL = pictureMiddleURL;
	}
	public String getPictureSmallURL()
	{
		return pictureMiddleURL;
	}
	
	
	public static FSQBadge loadFromJSON(JSONObject jObject)
	{
		FSQBadge badge=new FSQBadge();
		try
		{
			badge.setName(jObject.getString("name"));
			badge.setDescription(jObject.getString("description"));
			String begUrl=jObject.getJSONObject("image").getString("prefix");
			String endUrl=jObject.getJSONObject("image").getString("name");
			JSONArray sizes=jObject.getJSONObject("image").getJSONArray("sizes");
			String smallPictureSize=sizes.getString(Math.round((sizes.length()/2)));
			String bigPictureSize=sizes.getString(sizes.length()-1);
			badge.setPictureBigURL(begUrl+bigPictureSize+endUrl);
			badge.setPictureMiddleURL(begUrl+smallPictureSize+endUrl);
		} catch (JSONException e)
		{
			badge=null;
			e.printStackTrace();
		}
		return badge;
	}
	
	public void loadPictures()
	{
		FSQConnector.loadBadgesPictureAsync(this);
	}
	
	
	
}
