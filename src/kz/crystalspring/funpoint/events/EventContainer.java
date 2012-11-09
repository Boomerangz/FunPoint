package kz.crystalspring.funpoint.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class EventContainer
{
	Context context;
	List<Event> eventsList = new ArrayList(0);

	public EventContainer(Context applicationContext)
	{
		context = applicationContext;
	}

	public Event getEventById(String id)
	{
		for (Event event : eventsList)
		{
			if (Integer.toString(event.getId()).equals(id))
			{
				return event;
			}
		}
		JSONObject jEvent = FileConnector.loadJSONEventById(id);
		Event event = new SimpleEvent(jEvent);
		eventsList.add(event);
		return event;
	}

	public void loadEventList()
	{
		Runnable preTaskEvent = new Runnable()
		{

			@Override
			public void run()
			{
				Log.w("cinema", "НАЧАЛ ЗАГРУЗКУ СОБЫТИЙ");
				JSONArray eventsJSONArray = FileConnector.loadJSONEventsList();

				Log.w("cinema", "НАЧАЛ СОЗДАНИЕ СПИСКА СОБЫТИЙ");
				if (eventsJSONArray != null)
					for (int i = 0; i < eventsJSONArray.length(); i++)
					{
						try
						{
							JSONObject jEvent = eventsJSONArray.getJSONObject(i);
							Event event = new SimpleEvent(jEvent);
							addEventToList(event);
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
				Log.w("cinema", "ЗАКОНЧИЛ СОЗДАНИЕ СПИСКА СОБЫТИЙ");
			}
		};
		Runnable preTaskFilm = new Runnable()
		{

			@Override
			public void run()
			{
				Log.w("cinema", "НАЧАЛ ЗАГРУЗКУ ФИЛЬМОВ");
				JSONArray cinemaJSONArray = FileConnector.loadJSONCinemaEventsList();
				Log.w("cinema", "НАЧАЛ СОЗДАНИЕ СПИСКА ФИЛЬМОВ");
				if (cinemaJSONArray != null)
					for (int i = 0; i < cinemaJSONArray.length(); i++)
					{
						try
						{
							JSONObject jEvent = cinemaJSONArray.getJSONObject(i);
							FilmEvent event = new FilmEvent(jEvent);
							addEventToList(event);
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
				Log.w("cinema", "ЗАКОНЧИЛ СОЗДАНИЕ СПИСКА ФИЛЬМОВ");
			}
		};
		Runnable postTask = new Runnable()
		{

			@Override
			public void run()
			{
				MainApplication.refreshMapItems();
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(preTaskEvent, postTask);
		MainApplication.pwAggregator.addTaskToQueue(preTaskFilm, postTask);
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
		List<Event> filteredEvents = filterEventsList(eventsList, MainApplication.getMapItemContainer().getFlter());
		return filteredEvents;
	}

	private List<Event> filterEventsList(List<Event> events, List<String> flter)
	{
		List<Event> list = new ArrayList();
		for (Event e : events)
		{
			if (flter.contains(e.getPlace_type()))
			{
				list.add(e);
			}
		}
		Collections.sort(list, new Comparator<Event>()
		{

			@Override
			public int compare(Event lhs, Event rhs)
			{
				if (lhs != null && rhs != null)
					return lhs.getName().compareTo(rhs.getName());
				else
					return 0;
			}
		});
		return list;
	}

	public List<Event> getUnFilteredEventsList()
	{
		List<Event> eventsWithoutCinema = new ArrayList();
		for (Event event : eventsList)
		{
			if (SimpleEvent.class.isInstance(event))
				eventsWithoutCinema.add((SimpleEvent) event);
		}
		Collections.sort(eventsWithoutCinema, new Comparator<Event>()
		{
			@Override
			public int compare(Event lhs, Event rhs)
			{
				if (SimpleEvent.class.isInstance(lhs) && SimpleEvent.class.isInstance(rhs))
					return ((SimpleEvent) lhs).getEventDate().compareTo(((SimpleEvent) rhs).getEventDate());
				else
					return lhs.getName().compareTo(rhs.getName());
			}
		});
		Collections.sort(eventsWithoutCinema, new Comparator<Event>()
		{

			@Override
			public int compare(Event lhs, Event rhs)
			{
				if (lhs != null && rhs != null)
					return lhs.getName().compareTo(rhs.getName());
				else
					return 0;
			}
		});
		return eventsWithoutCinema;
	}

}
