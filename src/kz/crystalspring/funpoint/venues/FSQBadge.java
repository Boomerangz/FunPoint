package kz.crystalspring.funpoint.venues;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class FSQBadge extends UrlDrawable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8319211027451219320L;
	private String name;
	private String description;
	
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
			badge.bigUrl=begUrl+bigPictureSize+endUrl;
			badge.smallUrl=begUrl+smallPictureSize+endUrl;
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
