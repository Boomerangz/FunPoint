package kz.crystalspring.funpoint.venues;

import com.boomerang.database.JamDbAdapter;

import android.content.Context;
import android.database.Cursor;


public class EventContainer
{
	Context context;
	
	public EventContainer(Context applicationContext)
	{
		context = applicationContext;
	}
	
	public Event getEventById(int id)
	{
		JamDbAdapter jamDb=new JamDbAdapter(context);
		jamDb.open();
		Cursor cursor=jamDb.getEventById(id);
		cursor.moveToFirst();
		Event event;
		try
		{
			event = new Event(cursor);
		} catch (Exception e)
		{
			event=null;
			e.printStackTrace();
		}
		jamDb.close();
		return event;
	}
}
