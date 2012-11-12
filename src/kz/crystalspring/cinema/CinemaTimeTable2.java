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
import java.util.concurrent.atomic.AtomicInteger;

import kz.crystalspring.funpoint.MainApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.SlidingDrawer;

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

	AtomicInteger parseEnded;
	final int threadCount = 1;

	public void loadFromJSONArray(JSONArray jsonArray)
	{
		Log.w("CinemaTimeTable", "Start parsing");
		parseEnded = new AtomicInteger(0);
		filmMap = new HashMap<String, FilmLine>();
		int length = jsonArray.length();
		int section_length = length / threadCount;
		int begin = 0;
		for (int i = 0; i < threadCount; i++)
		{
			ParseAsyncTask task = new ParseAsyncTask();
			int end = (i < threadCount) ? begin + section_length : length - 1;
			// Log.w("CinemaTimeTable","Thread from "+Integer.toString(begin)+" "+Integer.toString(end)+" "+Integer.toString(length));
			task.execute(jsonArray, begin, end);
			begin += section_length;
		}
		while (parseEnded.intValue() < threadCount)
		{
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		Log.w("CinemaTimeTable", "End parsing");
	}

	class ParseAsyncTask extends AsyncTask
	{
		@Override
		protected Object doInBackground(Object... params)
		{
			JSONArray jsonArray = (JSONArray) params[0];
			int begin = (Integer) params[1];
			int end = (Integer) params[2];
			for (int i = begin; i < end; i++)
			{
				try
				{
					// //Log.w("CinemaTimeTable1","begin getting object");
					//Log.w("CinemaTimeTable1", "object accepting begin");
					JSONObject jEvent = jsonArray.getJSONObject(i);
					//Log.w("CinemaTimeTable1", "parseBegin");
					// //Log.w("CinemaTimeTable1","end getting object");
					String id;
					String title;
					if (currMode == MODE_CINEMA)
					{
						id = jEvent.getString("events_id");
						title = jEvent.getString("title");
					} else
					{
						id = jEvent.getString("fsq_id");
						if (!MainApplication.getMapItemContainer().hasItemById(id))
							id = null;
						title = jEvent.getString("name");
					}
					//Log.w("CinemaTimeTable1", "id parsed");
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
						//Log.w("CinemaTimeTable1", "date parsed");
						String filmTime = time_formatter.format(clearDate);
						clearDate.setHours(0);
						clearDate.setMinutes(0);
						clearDate.setSeconds(0);
						String sHash = jEvent.getString("url_mobile");// "3:128:1348659300";//"1:71:1340860800";//
						CinemaTime ct = new CinemaTime(filmTime, sHash);
						//Log.w("CinemaTimeTable1", "CinemaTime created");
						FilmLine filmLine;
						if (filmMap.containsKey(id))
							filmLine = filmMap.get(id);
						else
						{
							filmLine = new FilmLine(title, id);
							filmMap.put(id, filmLine);
						}
						//Log.w("CinemaTimeTable1", "FilmLine accepted");
						// //Log.w("CinemaTimeTable1","object created");
						filmLine.addCinemaTime(ct, clearDate);
						//Log.w("CinemaTimeTable1", "added to filmline");
						// //Log.w("CinemaTimeTable1","object added");
					}

				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			parseEnded.incrementAndGet();
		}

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
