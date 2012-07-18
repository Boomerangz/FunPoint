package kz.crystalspring.funpoint.events;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.venues.FileConnector;

import org.json.JSONArray;
import org.json.JSONException;
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
	
	public void loadEventList()
	{
		JSONArray cinemaJSONArray=FileConnector.loadJSONCinemaEventsList();
		for (int i=0;i<cinemaJSONArray.length();i++)
		{
			try
			{
				JSONObject jEvent=cinemaJSONArray.getJSONObject(i);
				Event event=new Event(jEvent);
				addEventToList(event);
			} 
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void addEventToList(Event event)
	{
		if (!eventsList.contains(event))
		{
			eventsList.add(event);
		}
	}

	public List<Event> getFilteredEventsList()
	{
		return getUnFilteredEventsList();
	}
	
	public List<Event> getUnFilteredEventsList()
	{
		return eventsList;
	}
	
	
	
}
