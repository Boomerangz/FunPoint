package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;

public class CinemaTimeTable
{
	class TimeLine
	{
		int filmId;
		String filmName;
		String date;
		String hash;
		boolean ticketable=false;;
		List<String> times=new ArrayList(0);
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
			String time=cursor.getString(6);
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
			timeline.times.add(time);
			timeline.date=date;
			timeline.ticketable=ticketAble;
		}
		Collections.sort(timeLines, new Comparator<TimeLine>()
		{

			@Override
			public int compare(TimeLine lhs, TimeLine rhs)
			{
				return new Integer(lhs.filmId).compareTo(new Integer(rhs.filmId));
			}
		});
		System.out.println(0);
	}
	
}
