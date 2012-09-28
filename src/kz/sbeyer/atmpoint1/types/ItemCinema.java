package kz.sbeyer.atmpoint1.types;

import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.FileConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ItemCinema extends FSQItem
{
	boolean hallInfoFilled = false;
	CinemaTimeTable timeTable;
	
	
	public static final String TICKETON_URL="http://m.ticketon.kz/hallplan/";

	public static final String CINEMA_IMG = "m_2";

	@Override
	public String getObjTypeId()
	{
		return FSQ_TYPE_CINEMA;
	}

	public ItemCinema loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);
		return this;
	}

	public void loadHallTableFromJSON(JSONArray jCinemaEvents,
			JSONArray jCinemaPlaces, JSONArray jCinemaSection)
	{
		try
		{
			JSONObject jPlace = jCinemaPlaces.getJSONObject(0);
			setAddress(jPlace.getString("address"));
			setName(jPlace.getString("title"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public void loadAdditionalInfo()
	{
		timeTable = new CinemaTimeTable();
		JSONObject jObject = FileConnector.loadCinemaInfo(getId());
		if (jObject != null)
		{
			try
			{
				timeTable.loadFromJSONArray(jObject.getJSONArray("events"));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			hallInfoFilled = true;
		}
	}

	public CinemaTimeTable getTimeTable()
	{
		return timeTable;
	}

	public void setTimeTable(CinemaTimeTable timeTable)
	{
		this.timeTable = timeTable;
	}

	public void setHallInfoNotFilled()
	{
		hallInfoFilled = false;
	}

	public boolean isHallInfoFilled()
	{
		return hallInfoFilled;
	}

	public String getIconName()
	{
		return CINEMA_IMG;
	}

	public static class ViewHolderCinema
	{
		public TextView name1;
		public ViewSwitcher switcher;
		public Button okButton;
		public Button cancelButton;
	}
	
	@Override
	public int getItemColor()
	{
		return context.getResources().getColor(R.color.cinema);
	}

	public void itemCinemaLoadOptionalInfo()
	{
		if (getOptionalInfo() == null)
		{
			JSONObject jObject = FSQConnector.getVenueInformation(getId());
			itemCinemaLoadOptionalInfo(jObject);
		}
	}

}
