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


public class CinemaTimeTable
{
	public static final DateFormat full_formatter = new SimpleDateFormat(
			"yyyy-MM-dd hh-mm-ss");
	public static final DateFormat date_formatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final DateFormat time_formatter = new SimpleDateFormat(
			"HH-mm");

	class TimeLine
	{
		String filmId;
		String title;
		Date date;
		boolean ticketable = false;
		
		List<CinemaTime> times = new ArrayList<CinemaTime>(0);

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


	public void loadFromCinemaJSONArray(JSONArray jsonArray)
	{
		for (int i = 0; i < jsonArray.length(); i++)
		{
			try
			{
				JSONObject jEvent = jsonArray.getJSONObject(i);

				String cinemaId = jEvent.getString("fsq_id");
				if (MainApplication.getMapItemContainer().getItemById(cinemaId) != null)
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
					String sHash = jEvent.getString("url_mobile");
					boolean ticketAble = !sHash.toUpperCase().equals("");

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
