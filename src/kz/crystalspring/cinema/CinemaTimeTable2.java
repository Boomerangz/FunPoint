package kz.crystalspring.cinema;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.crystalspring.funpoint.MainApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CinemaTimeTable2
{
	public static final DateFormat full_formatter = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
	public static final DateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat time_formatter = new SimpleDateFormat("HH-mm");

	public static final int MODE_CINEMA = 0;
	public static final int MODE_FILM = 1;

	private int currMode;
	Map<String, FilmLine> filmMap;

	public CinemaTimeTable2(int mode)
	{
		currMode = mode;
	}

	public void loadFromJSONArray(JSONArray jsonArray)
	{
		filmMap = new HashMap<String, FilmLine>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			try
			{
				JSONObject jEvent = jsonArray.getJSONObject(i);

				String id;
				String title;
				if (currMode == MODE_CINEMA)
				{
					id = jEvent.getString("events_id");
					title = jEvent.getString("title");
				} else
				{
					id = jEvent.getString("fsq_id");
					if (MainApplication.getMapItemContainer().getItemById(id) == null)
						id = null;
					title = jEvent.getString("name");
				}
				if (id != null)
				{
					Date clearDate;
					try
					{
						clearDate = full_formatter.parse(jEvent.getString("ts"));
					} catch (ParseException e)
					{
						clearDate = new Date();
						e.printStackTrace();
					}
					String filmTime = time_formatter.format(clearDate);
					clearDate.setHours(0);
					clearDate.setMinutes(0);
					clearDate.setSeconds(0);
					String sHash = jEvent.getString("url_mobile");// "3:128:1348659300";//"1:71:1340860800";//
																	// jEvent.getString("hash");

					CinemaTime ct = new CinemaTime(filmTime, sHash);
					FilmLine filmLine;
					if (filmMap.containsKey(id))
						filmLine = filmMap.get(id);
					else
					{
						filmLine = new FilmLine(title, id);
						filmMap.put(id, filmLine);
					}
					filmLine.addCinemaTime(ct, clearDate);
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println(0);
	}

	public int getFilmsCount()
	{
		return filmMap.size();
	}

	public Object getFilmStr(int position)
	{
		return filmMap.values().toArray()[position];
	}
}
