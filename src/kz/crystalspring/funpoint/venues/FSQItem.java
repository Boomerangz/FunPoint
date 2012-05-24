package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.List;

import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FSQItem extends MapItem
{
	String name;
	String address;
	String category = FSQ_TYPE_FOOD;

	OptionalInfo optInfo;

	@Override
	public String getObjTypeId()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	@Override
	public MapItem loadFromJSON(JSONObject jObject)
	{
		try
		{
			setName(jObject.getString("name"));
			setId(jObject.getString("id"));
			JSONObject location = jObject.getJSONObject("location");
			float lat = (float) location.getDouble("lat");
			float lng = (float) location.getDouble("lng");
			if (!location.isNull("address"))
				setAddress(location.getString("address"));
			setLatitude(lat);
			setLongitude(lng);
			return this;
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getIconName()
	{
		return "m_4";
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCategory()
	{
		return category;
	}

	public void itemFoodLoadOptionalInfo(JSONObject jObject)
	{
		optInfo = new OptionalInfo();
		try
		{
			optInfo.loadComments(jObject.getJSONObject("tips"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}


	class OptionalInfo
	{
		List<Comment> commentList = new ArrayList<Comment>();
		private void addCommentToList(Comment comment)
		{
			commentList.add(comment);
		}
		
		public List<Comment> getCommentsList()
		{
			return commentList;
		}
		
		public void loadComments(JSONObject jObject)
		{
			JSONArray tipItems;
			try
			{
				tipItems = jObject.getJSONObject("groups").getJSONArray("items");
			} catch (JSONException e)
			{
				e.printStackTrace();
				tipItems = null;
			}
			if (tipItems != null)
			{
				for (int i=0; i < tipItems.length(); i++)
				{
					Comment comment = new Comment();
					try
					{
						comment.loadFromJSON(tipItems.getJSONObject(i));
					} catch (JSONException e)
					{
						comment=null;
						e.printStackTrace();
					}
					if (comment != null)
						optInfo.addCommentToList(comment);
				}
			}
		}
	}

	class Comment
	{
		String text;
		String author;
		public Comment loadFromJSON(JSONObject jObject)
		{	
			Comment item=this;
			try
			{
				author="John Doe";
				text=jObject.getString("text");
			} catch (JSONException e)
			{
				e.printStackTrace();
				item=null;
			}
			return item;
		}
	}
}
