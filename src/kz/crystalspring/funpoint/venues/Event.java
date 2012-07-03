package kz.crystalspring.funpoint.venues;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
public class Event
{
	private static final String COLUMN_NAME="title";
	private static final String COLUMN_DESC="description";
	private static final String COLUMN_IMGURL="image";
	
	
	
	private String name;
	private String description;
	private String imageUrl;
	private Drawable image;
	int id;
	
	Event(int id,String name, String description, String imageUrl)
	{
		this.id=id;
		setName(name);
		setDescription(description);
		setImageUrl(imageUrl);
	}
	
	Event ()
	{
	}
	
	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	Event (Cursor cursor) throws Exception
	{
		String[] columnNames=cursor.getColumnNames();
		String nm=null;
		String dsc=null;
		String imgurl=null;
		int i=0;
		for (String columName:columnNames)
		{
			if (columName.trim().toUpperCase().equals(COLUMN_NAME.toUpperCase().trim()))
			{
				nm=cursor.getString(i);
			}
			if (columName.trim().toUpperCase().equals(COLUMN_DESC.toUpperCase().trim()))
			{
				dsc=cursor.getString(i);
			}
			if (columName.trim().toUpperCase().equals(COLUMN_IMGURL.toUpperCase().trim()))
			{
				imgurl=cursor.getString(i);
			}
			i++;
		}
		if (nm!=null&&dsc!=null&&imgurl!=null)
		{
			setName(nm);
			setDescription(dsc);
			setImageUrl(imgurl);
		}
		else 
		{
			throw new Exception("Name or Description or Image URl not found in cursor");
		}
	}

	public Event(JSONObject jEvent)
	{
		try
		{
			id=jEvent.getInt("events_id");
			setName(jEvent.getString("title"));
			setDescription(jEvent.getString("description"));
			setImageUrl(jEvent.getString("img_url"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

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

	public Drawable getImage()
	{
		return image;
	}

	public void setImage(Drawable image)
	{
		this.image = image;
	}

	public int getId()
	{
		return id;
	}
	
	
}
