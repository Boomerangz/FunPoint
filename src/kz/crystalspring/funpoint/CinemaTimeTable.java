package kz.crystalspring.funpoint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

public class CinemaTimeTable
{
	class TimeLine
	{
		int filmId;
		String filmName;
		String date;
		boolean ticketable=false;;
		List<CinemaTime> times=new ArrayList(0);
	}
	
	class CinemaTime
	{
		final DateFormat formatter = new SimpleDateFormat("hh:mm");
		
		
		
		private Date time;
		private String hash;
		CinemaTime(String time, String hash)
		{
			try
			{
				setTime(formatter.parse(time));
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
			return formatter.format(time);//time.toLocaleString();
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
	
	private List<TimeLine> timeLines=new ArrayList();
	
	public List<TimeLine> getTimeLines()
	{
		return timeLines;
	}


	public void loadFromDBCursor(Cursor cursor)
	{
		cursor.moveToFirst();
		for (int i=0; i<cursor.getCount(); i++)
		{
			cursor.moveToPosition(i);
			int filmId=cursor.getInt(2);
			String filmTitle=cursor.getString(3);
			String date=cursor.getString(4);
			String sTime=cursor.getString(6);
			String sHash=cursor.getString(8);
			boolean ticketAble=cursor.getInt(7)==1;
			
			TimeLine timeline=null;
			for (int j=0;j<timeLines.size();j++)
			{
				if (timeLines.get(j).filmId==filmId&&timeLines.get(j).date.equals(date))
				{
					timeline=timeLines.get(j);
					break;
				}
			}
			if (timeline==null)
			{
				timeline=new TimeLine();
				timeLines.add(timeline);
			}
			
			timeline.filmId=filmId;
			timeline.filmName=filmTitle;
			timeline.times.add(new CinemaTime(sTime,sHash));
			timeline.date=date;
			timeline.ticketable=ticketAble;
		}
		Collections.sort(timeLines, new Comparator<TimeLine>()
		{
			@Override
			public int compare(TimeLine lhs, TimeLine rhs)
			{
				if (lhs.date.compareTo(rhs.date)!=0)
					return lhs.date.compareTo(rhs.date);
				return lhs.filmName.compareTo(rhs.filmName);
			}
		});
		System.out.println(0);
	}
	
}
