package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.os.AsyncTask;

public class MapItemContainer
{
	private List<String> visibleFilterMap = new ArrayList<String>();
	private List<MapItem> mapItemArray = new ArrayList<MapItem>();

	private Context context;
	private GeoPoint point = null;
	private MapItem selectedItem;

	public MapItemContainer(Context applicationContext)
	{
		context = applicationContext;
		MapItem.context = context;
	}

	public MapItem getSelectedItem()
	{
		return selectedItem;
	}

	public void setSelectedItem(MapItem selectedItem)
	{
		this.selectedItem = selectedItem;
	}

	Comparator comp = new Comparator<MapItem>()
	{

		@Override
		public int compare(MapItem lhs, MapItem rhs)
		{
			if (lhs.distanceTo(MainApplication.getCurrentLocation()) > rhs
					.distanceTo(MainApplication.getCurrentLocation()))
				return 1;
			else if (lhs.distanceTo(MainApplication.getCurrentLocation()) < rhs
					.distanceTo(MainApplication.getCurrentLocation()))
				return -1;
			else
				return 0;
		}
	};

	public synchronized List<MapItem> getFilteredItemList()
	{
		List<MapItem> filteredList = new ArrayList<MapItem>();
		for (MapItem item : mapItemArray)
			if (visibleFilterMap.contains(item.getObjTypeId()))
				filteredList.add(item);

		if (MainApplication.getCurrentLocation() != null)
			Collections.sort(filteredList, comp);
		return filteredList;
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
		if (!mapItemArray.contains(item))
			mapItemArray.add(item);
	}

	public void addItemsList(List<MapItem> items)
	{
		for (MapItem item : items)
			addItem(item);
	}

	public void loadCategory(String sCategoryId, int radius)
	{
		addItemsList(FSQConnector.loadItems(point, sCategoryId, radius));
	}

	public void loadNearBy(GeoPoint point, Runnable action)
	{
		this.point = point;
		RefreshItemsTask task = new RefreshItemsTask();
		MainApplication.pwAggregator.addTaskToQueue(task, action);
		// task.execute(action);
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
		}
	}

	public void setVisibleFilter(String visibleFilter)
	{
		visibleFilterMap.clear();
		addVisibleFilter(visibleFilter);
	}

	public MapItem getItemById(String VenueID)
	{
		MapItem item = null;
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

	// public void loadItem(String id)
	// {
	// // TODO Auto-generated method stub
	//
	// }
	//

	// private class SearchItemsTask extends AsyncTask<Runnable, Integer,
	// Runnable>
	// {
	//
	// @Override
	// protected Runnable doInBackground(Runnable... params)
	// {
	//
	// ArrayList<String> filterArray=new ArrayList();
	// filterArray.addAll(Arrays.asList(MapItem.TYPES_ARRAY));
	//
	// for (String i:filterArray)
	// {
	// addItemsList(FSQConnector.loadItems(point,i));
	// }
	//
	//
	// if (params.length>0)
	// return params[0];
	// else
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Runnable task)
	// {
	// task.run();
	// }
	// }

}
