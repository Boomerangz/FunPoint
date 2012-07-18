package kz.crystalspring.funpoint.events;

import org.json.JSONException;
import org.json.JSONObject;

public class FilmEvent extends Event
{
	String genre;
	
	
	public FilmEvent(JSONObject jObject)
	{
		super(jObject);
		try
		{
			setGenre(jObject.getString("genre"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public String getGenre()
	{
		return genre;
	}

	public void setGenre(String genre)
	{
		this.genre = genre;
	}

	@Override
	public String getShortCharacteristic()
	{
		return getGenre();
	}
}
