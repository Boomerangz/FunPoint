package kz.sbeyer.atmpoint1.types;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.cinema.CinemaTimeTable2;
import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.pointplus.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Path.FillType;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ItemCinema extends FSQItem
{
	boolean hallInfoFilled = false;
	CinemaTimeTable2 timeTable;

	public static final String TICKETON_URL = "http://m.ticketon.kz/hallplan/";

	public static final String CINEMA_IMG = "m_2";

	@Override
	public String getObjTypeId()
	{
		return FSQ_TYPE_CINEMA;
	}

	private static Set<String> jam_cinema_set = null;

	public ItemCinema loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);

		if (jam_cinema_set == null)
		{
			fillCinemaSet();
		}
		if (jam_cinema_set.contains(this.getId())||jam_cinema_set.size()==0)
			return this;
		else
			return null;
	}

	private void fillCinemaSet()
	{
		jam_cinema_set = FileConnector.getJamCinemaList();
	}

	public void loadHallTableFromJSON(JSONArray jCinemaEvents, JSONArray jCinemaPlaces, JSONArray jCinemaSection)
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
		timeTable = new CinemaTimeTable2();
		JSONObject jObject;
		try
		{
			jObject = FileConnector.loadCinemaInfo(getId());

			if (jObject != null)
			{
				try
				{
					timeTable.loadFromJSONArray(jObject.getJSONArray("events"));
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		hallInfoFilled = true;
	}

	public CinemaTimeTable2 getTimeTable()
	{
		return timeTable;
	}

	public void setTimeTable(CinemaTimeTable2 timeTable)
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

}
