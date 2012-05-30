package kz.crystalspring.funpoint.venues;

import kz.crystalspring.funpoint.MainApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class FSQTodo
{
	private String created;
	private String id;
	private String venueId;
	private int beenHere;
	

	public String getCreated()
	{
		return created;
	}

	public void setCreated(String created)
	{
		this.created = created;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getVenueId()
	{
		return venueId;
	}

	public void setVenueId(String venueId)
	{
		this.venueId = venueId;
	}

	public int getBeenHere()
	{
		return beenHere;
	}

	public void setBeenHere(int beenHere)
	{
		this.beenHere = beenHere;
	}

	public FSQTodo loadFromJSON(JSONObject jObject)
	{
		FSQTodo todo=this;
		try
		{
			setId(jObject.getString("id"));
			setCreated(jObject.getString("createdAt"));
			setVenueId(jObject.getJSONObject("tip").getJSONObject("venue").getString("name"));//"id"));
			setBeenHere(jObject.getJSONObject("tip").getJSONObject("venue").getJSONObject("beenHere").getInt("count"));
		//	MainApplication.mapItemContainer.loadItem(getId());
		} catch (JSONException e)
		{
			todo=null;
			e.printStackTrace();
		}
		return todo;
	}
	
	@Override
	public String toString()
	{
		return getVenueId() +"\n"+ getBeenHere();
	}

	
}
