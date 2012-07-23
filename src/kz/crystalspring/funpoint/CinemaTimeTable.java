package kz.crystalspring.funpoint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class CinemaTimeTable
{
	public static final DateFormat full_formatter = new SimpleDateFormat(
			"yyyy-MM-dd hh-mm-ss");
	public static final DateFormat date_formatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final DateFormat time_formatter = new SimpleDateFormat(
			"hh-mm");

	class TimeLine
	{
		String filmId;
		String title;
		Date date;
		boolean ticketable = false;;
		List<CinemaTime> times = new ArrayList(0);

		public String getStrDate()
		{
			return date_formatter.format(date);
		}

		public String getTitle()
		{
			return title;
		}
	}

	class CinemaTime
	{
		private Date time;
		private String hash;

		CinemaTime(String time, String hash)
		{
			try
			{
				setTime(full_formatter.parse(time));
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
			this.hash = hash;
		}

		public Date getTime()
		{
			return time;
		}

		public String getStringTime()
		{
			return time_formatter.format(time);// time.toLocaleString();
		}

		public void setTime(Date time)
		{
			this.time = time;
		}

		public String getHash()
		{
			return hash;
		}

		public void setHash(String hash)
		{
			this.hash = hash;
		}

	}

	private List<TimeLine> timeLines = new ArrayList();

	public List<TimeLine> getTimeLines()
	{
		return timeLines;
	}

	// public void loadFromDBCursor(Cursor cursor)
	// {
	// cursor.moveToFirst();
	// for (int i=0; i<cursor.getCount(); i++)
	// {
	// cursor.moveToPosition(i);
	// int filmId=cursor.getInt(2);
	// String filmTitle=cursor.getString(3);
	// String date=cursor.getString(4);
	// String sTime=cursor.getString(6);
	// String sHash=cursor.getString(8);
	// boolean ticketAble=cursor.getInt(7)==1;
	//
	// TimeLine timeline=null;
	// for (int j=0;j<timeLines.size();j++)
	// {
	// if (timeLines.get(j).filmId==filmId&&timeLines.get(j).date.equals(date))
	// {
	// timeline=timeLines.get(j);
	// break;
	// }
	// }
	// if (timeline==null)
	// {
	// timeline=new TimeLine();
	// timeLines.add(timeline);
	// }
	//
	// timeline.filmId=filmId;
	// timeline.filmName=filmTitle;
	// timeline.times.add(new CinemaTime(sTime,sHash));
	// timeline.date=date;
	// timeline.ticketable=ticketAble;
	// }
	// Collections.sort(timeLines, new Comparator<TimeLine>()
	// {
	// @Override
	// public int compare(TimeLine lhs, TimeLine rhs)
	// {
	// if (lhs.date.compareTo(rhs.date)!=0)
	// return lhs.date.compareTo(rhs.date);
	// return lhs.filmName.compareTo(rhs.filmName);
	// }
	// });
	// System.out.println(0);
	// }

	public void loadFromJSONArray(JSONArray jsonArray)
	{
		for (int i = 0; i < jsonArray.length(); i++)
		{
			try
			{
				JSONObject jEvent = jsonArray.getJSONObject(i);

				String filmId = jEvent.getString("events_id");
				String filmTitle = jEvent.getString("title");
				Date clearDate;
				try
				{
					clearDate = full_formatter.parse(jEvent.getString("ts"));
				} catch (ParseException e)
				{
					clearDate = new Date();
					e.printStackTrace();
				}
				clearDate.setHours(0);
				clearDate.setMinutes(0);
				clearDate.setSeconds(0);
				String sHash = "1:71:1340860800";// jEvent.getString("hash");
				boolean ticketAble = true;

				TimeLine timeline = null;
				for (int j = 0; j < timeLines.size(); j++)
				{
					if (timeLines.get(j).filmId.equals(filmId)
							&& timeLines.get(j).date.equals(clearDate))
					{
						timeline = timeLines.get(j);
						break;
					}
				}
				if (timeline == null)
				{
					timeline = new TimeLine();
					timeLines.add(timeline);
				}
				timeline.filmId = filmId;
				timeline.title = filmTitle;
				timeline.times
						.add(new CinemaTime(jEvent.getString("ts"), sHash));
				timeline.date = clearDate;
				timeline.ticketable = ticketAble;
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		Collections.sort(timeLines, new Comparator<TimeLine>()
		{
			@Override
			public int compare(TimeLine lhs, TimeLine rhs)
			{
				if (lhs.date.compareTo(rhs.date) != 0)
					return lhs.date.compareTo(rhs.date);
				return lhs.title.compareTo(rhs.title);
			}
		});
		System.out.println(0);
	}

	public void loadFromCinemaJSONArray(JSONArray jsonArray)
	{
		for (int i = 0; i < jsonArray.length(); i++)
		{
			try
			{
				JSONObject jEvent = jsonArray.getJSONObject(i);

				String cinemaId = jEvent.getString("fsq_id");
				if (MainApplication.mapItemContainer.getItemById(cinemaId) != null)
				{
					String filmTitle = jEvent.getString("name");
					Date clearDate;
					try
					{
						clearDate = full_formatter
								.parse(jEvent.getString("ts"));
					} catch (ParseException e)
					{
						clearDate = new Date();
						e.printStackTrace();
					}
					clearDate.setHours(0);
					clearDate.setMinutes(0);
					clearDate.setSeconds(0);
					String sHash = "1:71:1340860800";// jEvent.getString("hash");
					boolean ticketAble = true;

					TimeLine timeline = null;
					for (int j = 0; j < timeLines.size(); j++)
					{
						if (timeLines.get(j).filmId.equals(cinemaId)
								&& timeLines.get(j).date.equals(clearDate))
						{
							timeline = timeLines.get(j);
							break;
						}
					}
					if (timeline == null)
					{
						timeline = new TimeLine();
						timeLines.add(timeline);
					}
					timeline.filmId = cinemaId;
					timeline.title = filmTitle;
					timeline.times.add(new CinemaTime(jEvent.getString("ts"),
							sHash));
					timeline.date = clearDate;
					timeline.ticketable = ticketAble;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		Collections.sort(timeLines, new Comparator<TimeLine>()
		{
			@Override
			public int compare(TimeLine lhs, TimeLine rhs)
			{
				if (lhs.date.compareTo(rhs.date) != 0)
					return lhs.date.compareTo(rhs.date);
				return lhs.title.compareTo(rhs.title);
			}
		});
		System.out.println(0);
	}

}
