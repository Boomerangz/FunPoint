package kz.crystalspring.funpoint.events;

import kz.com.pack.jam.R;
import kz.crystalspring.cinema.CinemaTimeTable2;
import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.venues.FileConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

public class FilmEvent extends Event
{
	String genre;
	protected CinemaTimeTable2 table;
	
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
	

	public void loadPlaceTable()
	{
		if (table == null)
		{
			table = new CinemaTimeTable2(CinemaTimeTable2.MODE_FILM);
			JSONArray jArray = FileConnector.loadJSONPlaceList(Integer
					.toString(getId()));
			if (jArray != null)
				table.loadFromJSONArray(jArray);
		}
	}
	
	@Override
	public View getView(View convertView, int position)
	{
		View v=super.getView(convertView, position);
		View dateView=v.findViewById(R.id.date);
		if (dateView!=null)
			dateView.setVisibility(View.GONE);
		return v;
	}

	public CinemaTimeTable2 getTimeTable()
	{
		return table;
	}
}
