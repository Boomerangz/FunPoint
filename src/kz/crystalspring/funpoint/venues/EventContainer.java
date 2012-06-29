package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;


public class EventContainer
{
	Context context;
	List<Event> eventsList=new ArrayList(0);
	
	
	public EventContainer(Context applicationContext)
	{
		context = applicationContext;
	}
	
	public Event getEventById(int id)
	{
		for (Event event:eventsList)
		{
			if (event.getId()==id)
			{
				return event;
			}
		}
		
		
		JSONObject jEvent=FileConnector.loadJSONEventById(id);
		Event event=new Event(jEvent);
		eventsList.add(event);
		return event;
//		JamDbAdapter jamDb=new JamDbAdapter(context);
//		jamDb.open();
//		Cursor cursor=jamDb.getEventById(id);
//		cursor.moveToFirst();
//		Event event;
//		try
//		{
//			event = new Event(cursor);
//		} catch (Exception e)
//		{
//			event=null;
//			e.printStackTrace();
//		}
//		jamDb.close();
	}
}
