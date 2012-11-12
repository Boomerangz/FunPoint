package kz.crystalspring.funpoint.venues;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.events.Event;
import kz.crystalspring.visualities.gallery.ImageContainer;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;
import kz.sbeyer.atmpoint1.types.ItemHotel;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.SlidingDrawer;

public class MapItemContainer
{
	private List<String> visibleFilterMap = new ArrayList<String>();
	private List<MapItem> mapItemArray = new ArrayList<MapItem>();
	private Map<String, MapItem> mapItemMap = new HashMap<String, MapItem>();

	private Context context;
	private GeoPoint point = null;
	private Object selectedItem;

	private final String FILENAME = "map_items";

	public MapItemContainer(Context applicationContext)
	{
		context = applicationContext;
		MapItem.context = context;
	}

	public Object getSelectedItem()
	{
		return selectedItem;
	}

	public MapItem getSelectedMapItem()
	{
		if (MapItem.class.isInstance(selectedItem))
			return (MapItem) selectedItem;
		else
			return null;
	}

	public Event getSelectedEventItem()
	{
		if (Event.class.isInstance(selectedItem))
			return (Event) selectedItem;
		else
			return null;
	}

	public void setSelectedItem(Object selectedItem)
	{
		this.selectedItem = selectedItem;
	}

	Comparator<MapItem> comp = new Comparator<MapItem>()
	{
		final int LHS_WIN = -1;
		final int RHS_WIN = 1;
		final int DRAW = 0;
		final int MAGIC_CONST = 500;

		@Override
		public int compare(MapItem lhs, MapItem rhs)
		{
			Float lDistance = lhs.distanceTo(MainApplication.getCurrentLocation());
			Float rDistance = rhs.distanceTo(MainApplication.getCurrentLocation());
			Float distance = Math.abs(lDistance - rDistance);
			Integer checkinPriority = null;

			if (FSQConnector.isInEverCheckList(lhs.getId()) && !FSQConnector.isInEverCheckList(rhs.getId()))
				checkinPriority = LHS_WIN;
			else if (!FSQConnector.isInEverCheckList(lhs.getId()) && FSQConnector.isInEverCheckList(rhs.getId()))
				checkinPriority = RHS_WIN;
			else
				checkinPriority = DRAW;

			if (checkinPriority == LHS_WIN)
			{
				if (distance < MAGIC_CONST)
					return LHS_WIN;
				else
					return lDistance.compareTo(rDistance);
			}
			if (checkinPriority == RHS_WIN)
			{
				if (distance < MAGIC_CONST)
					return RHS_WIN;
				else
					return lDistance.compareTo(rDistance);
			}
			return lDistance.compareTo(rDistance);
		}
	};

	public synchronized List<MapItem> getFilteredItemList() throws Exception
	{
		List<MapItem> filteredList = filterList(mapItemArray);
		if (filteredList.size() == 0)
		{
			filteredList = filterList(getMapItemListFromFile());
		} else
		{
			itemListFromFile = null;
			System.gc();
		}
		if (MainApplication.getCurrentLocation() != null)
			Collections.sort(filteredList, comp);
		return filteredList;
	}

	private List<MapItem> filterList(List<MapItem> itemArray) throws Exception
	{
		synchronized (visibleFilterMap)
		{
			if (visibleFilterMap.size() == 0)
				throw new Exception("Empty Filter List");
			List<MapItem> filteredList = new ArrayList<MapItem>();
			for (MapItem item : itemArray)
				if (visibleFilterMap.contains(item.getObjTypeId()) || visibleFilterMap.contains(MapItem.FSQ_UNDEFINED))
					filteredList.add(item);
			return filteredList;
		}
	}

	public List<MapItem> getUnFilteredItemList()
	{
		List<MapItem> unFilteredList = new ArrayList<MapItem>();
		unFilteredList.addAll(mapItemArray);
		if (MainApplication.getCurrentLocation() != null)
			Collections.sort(unFilteredList, comp);
		return unFilteredList;
	}

	public void addVisibleFilter(String visibleFilter)
	{
		if (!visibleFilterMap.contains(visibleFilter))
			visibleFilterMap.add(visibleFilter);
		else
			deleteVisibleFilter(visibleFilter);
	}

	public void addVisibleFilterList(List<String> list)
	{
		for (String filter : list)
			addVisibleFilter(filter);
	}

	public void deleteVisibleFilter(String visibleFilter)
	{
		if (visibleFilterMap.contains(visibleFilter))
			visibleFilterMap.remove(visibleFilter);
	}

	public void addItem(MapItem item)
	{
		if (!mapItemMap.containsKey(item.getId()))
		{
			mapItemArray.add(item);
			mapItemMap.put(item.getId(), item);
		}
	}

	public void addItemsList(List<MapItem> items)
	{
		for (MapItem item : items)
			addItem(item);
	}

	private void saveItemListToFile()
	{
		// try
		// {
		// FileOutputStream fos = context.openFileOutput(FILENAME,
		// Context.MODE_PRIVATE);
		// ObjectOutputStream oos = new ObjectOutputStream(fos);
		// oos.writeObject(mapItemArray);
		// oos.close();
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }
	}

	private List<MapItem> itemListFromFile = null;

	private List<MapItem> getMapItemListFromFile()
	{
		// if (itemListFromFile != null)
		// return itemListFromFile;
		// else
		// {
		// List<MapItem> itemArray = null;
		// try
		// {
		// FileInputStream fos = context.openFileInput(FILENAME);
		// ObjectInputStream ois = new ObjectInputStream(fos);
		// itemArray = (List<MapItem>) ois.readObject();
		// ois.close();
		// } catch (Exception e)
		// {
		// itemArray = new ArrayList(0);
		// e.printStackTrace();
		// }
		// itemListFromFile = itemArray;
		// return itemArray;
		// }
		return new ArrayList(0);
	}

	public void loadCategory(String sCategoryId, int radius)
	{
		addItemsList(FSQConnector.loadItems(point, sCategoryId, radius));
	}

	public void loadItemsByNameAsync(final String category, final String name)
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					addItemsList(FSQConnector.getByName(MainApplication.getCurrentLocation().getLatitudeE6() / 1e6, MainApplication
							.getCurrentLocation().getLongitudeE6() / 1e6, category, name));
					saveItemListToFile();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
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
		MainApplication.pwAggregator.addPriorityTask(task, postTask);
	}

	public void loadNearBy(GeoPoint point)
	{
		this.point = point;
		RefreshItemsTask task = new RefreshItemsTask();
		Runnable action = new Runnable()
		{
			@Override
			public void run()
			{
				MainApplication.refreshMapItems();
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, action);
	}

	private class RefreshItemsTask implements Runnable
	{
		@Override
		public void run()
		{
			ArrayList<String> filterArray = new ArrayList();
			filterArray.addAll(Arrays.asList(MapItem.TYPES_ARRAY));
			for (String st : filterArray)
			{
				if (!st.equals(MapItem.FSQ_TYPE_FOOD))
					loadCategory(st, 0);
				else
					loadCategory(st, FSQConnector.AREA_RADIUS);
			}
			saveItemListToFile();
		}
	}

	public void setVisibleFilter(String visibleFilter)
	{
		visibleFilterMap.clear();
		if (visibleFilter != null)
			addVisibleFilter(visibleFilter);
	}

	public String getCategoryName(String categoryID)
	{
		String categoryName = "";
		if (categoryID.equals(MapItem.FSQ_TYPE_CINEMA))
			categoryName = "Кино";
		else if (categoryID.equals(MapItem.FSQ_TYPE_CLUB))
			categoryName = "Клубы";
		else if (categoryID.equals(MapItem.FSQ_TYPE_FOOD))
			categoryName = "Кафе";
		else if (categoryID.equals(MapItem.FSQ_TYPE_HOTEL))
			categoryName = "Отели";
		else if (categoryID.equals(MapItem.FSQ_TYPE_MARKET))
			categoryName = "Магазины";
		return categoryName;
	}

	public MapItem getItemById(String VenueID)
	{
		MapItem item = mapItemMap.get(VenueID);
		if (item == null)
			for (MapItem mItem : getUnFilteredItemList())
			{
				if (mItem.getId().equals(VenueID))
				{
					item = mItem;
					break;
				}
			}
		return item;
	}
	

	public String getCategory()
	{
		if (visibleFilterMap.size() > 0)
			return visibleFilterMap.get(0);
		return null;
	}

	public String getCategoryName()
	{
		String categIdString = getCategory();
		if (categIdString != null)
			return getCategoryName(categIdString);
		else
			return "";
	}

	public MapItem addItem(JSONObject place)
	{
		MapItem item = null;
		String localCat = null;
		try
		{
			localCat = place.getJSONArray("categories").getJSONObject(0).getString("id");
			String globalCat = FSQConnector.getGlobalCategory(localCat);
			localCat = globalCat;
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		if (localCat != null)
		{
			if (localCat.equals(MapItem.FSQ_TYPE_CINEMA))
				item = new ItemCinema();
			else if (localCat.equals(MapItem.FSQ_TYPE_FOOD))
				item = new ItemFood();
			else if (localCat.equals(MapItem.FSQ_TYPE_HOTEL))
				item = new ItemHotel();
			else
				item = new FSQItem();
		} else
		{
			item = new FSQItem();
		}
		if (item != null)
		{
			item.loadFromJSON(place);
			addItem(item);
		}
		return item;
	}

	public List<String> getFlter()
	{
		return visibleFilterMap;
	}

	public void clearContent()
	{
		mapItemArray = new ArrayList();
		mapItemMap.clear();
	}

	public boolean hasItemById(String id)
	{
		return mapItemMap.containsKey(id);
	}
}
