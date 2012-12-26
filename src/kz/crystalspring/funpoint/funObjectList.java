package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.google.analytics.tracking.android.EasyTracker;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.events.Event;
import kz.crystalspring.funpoint.venues.ListItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.visualities.homescreen.TitleFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class funObjectList extends FragmentActivity implements RefreshableMapList, canBeRefreshing
{
	List<MapItem> itemsList;
	List<Event> eventsList;
	ObjectAdapter objectAdapter;
	ObjectAdapter eventAdapter;
	ViewFragmentAdapter pagerAdapter;

	TextView categorySubHeader;

	ViewPager viewPager;
	TabPageIndicator tabIndicator;

	// Button mapBtn;
	ImageView openSearchButton;
	EditText searchEdit;

	ProgressBar pgBar;

	View mainView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(null);

		LayoutInflater inflater = getLayoutInflater();

		mainView = inflater.inflate(R.layout.object_list, null);

		viewPager = (ViewPager) mainView.findViewById(R.id.pager);
		tabIndicator = (TabPageIndicator) mainView.findViewById(R.id.indicator);
		// mapBtn = (Button) mainView.findViewById(R.id.mapBtn);
		pgBar = (ProgressBar) mainView.findViewById(R.id.progressBar1);

		categorySubHeader = (TextView) mainView.findViewById(R.id.category_subheader);
		View profileButton = mainView.findViewById(R.id.profile_button);
		profileButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent j = new Intent(funObjectList.this, ProfilePage.class);
				startActivity(j);
			}
		});

		objectAdapter = new ObjectAdapter(this, this);
		eventAdapter = new ObjectAdapter(this, this);

		List<TitleFragment> viewList = fillObjectAndEventLists();
		pagerAdapter = new ViewFragmentAdapter(getSupportFragmentManager(), viewList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);

		tabIndicator.setViewPager(viewPager);
		// mapBtn.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// MainMenu.goToObjectMap();
		// }
		// });

		searchEdit = (EditText) mainView.findViewById(R.id.search_edit);
		searchEdit.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				filterByString(s.toString());
			}
		});

		openSearchButton = (ImageView) mainView.findViewById(R.id.search_btn);
		openSearchButton.setOnClickListener(new OnClickListener()
		{
			boolean searchVisible = false;

			@Override
			public void onClick(View v)
			{
				searchVisible = !searchVisible;
				if (searchVisible)
					searchEdit.setVisibility(View.VISIBLE);
				else
					searchEdit.setVisibility(View.GONE);
			}
		});
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onBackPressed()
	{
		// super.onPause();
		MainApplication.getMapItemContainer().setVisibleFilter(null);
		EasyTracker.getInstance().activityStop(this);
		finish();
	}

	static int a = 0;

	@Override
	public void onResume()
	{
		super.onResume();
		MainApplication.refreshable = this;
		refreshMapItems();
		MainApplication.getInstance().enableLocationUpdating();
		if (MainApplication.getMapItemContainer().getUnFilteredItemList().size()==0&&!MainApplication.loading)
		{
			MainApplication.getInstance().onResume();
		}
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		MainApplication.getInstance().disableLocationUpdating();
	}

	private void filterByString(String filter)
	{
		objectAdapter.setStringFilter(filter);
		refreshList();
	}

	private void refreshList()
	{
		stopRefreshing();

		Bundle extras = getIntent().getExtras();
		boolean fast_checkin;
		if (extras != null)
			fast_checkin = extras.getBoolean("fast_checkin");
		else
			fast_checkin = false;

		List<MapItem> newItemsList;
		try
		{
			if (!fast_checkin)
			{
				newItemsList = MainApplication.getMapItemContainer().getFilteredItemList();
				eventsList = MainApplication.getEventContainer().getFilteredEventsList();
			} else
			{
				newItemsList = MainApplication.getMapItemContainer().getUnFilteredItemList();
				eventsList = MainApplication.getEventContainer().getUnFilteredEventsList();
			}
		} catch (Exception e)
		{
			exit();
			return;
		}

		categorySubHeader.setText(MainApplication.getMapItemContainer().getCategoryName().toLowerCase());

		boolean needToRefreshItems = true;

		if (itemsList != null && newItemsList != null)
		{
			if (newItemsList.equals(itemsList) && objectAdapter.haveData())
				needToRefreshItems = false;
		}

		itemsList = newItemsList;
		objectAdapter.setData(itemsList);
		objectAdapter.refreshState();
		a++;
		eventAdapter.setData(eventsList);
		eventAdapter.refreshState();

		if (needToRefreshItems)
		{
			if (objectAdapter.getCount() > 0)
			{
				setContentView(mainView);
			} else
			{
				setContentView(R.layout.waiting_layout);
			}
		}
		System.gc();

		// if
		// (pagerAdapter.getCount()==0&&MainApplication.getMapItemContainer().getFlter().size()>0)
		// {
		// finish();
		// }
	}

	private void exit()
	{
		finish();
	}

	private List<TitleFragment> fillObjectAndEventLists()
	{
		List<TitleFragment> viewList = new ArrayList();
		viewList.add(new ObjectListFragment(getBaseContext(), objectAdapter));
		viewList.add(new EventListFragment(getBaseContext(), eventAdapter));
		return viewList;
	}

	@Override
	public void refreshMapItems()
	{
		refreshList();
	}

	@Override
	public void startRefreshing()
	{
		pgBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void stopRefreshing()
	{
		pgBar.setVisibility(View.GONE);
	}

}

class ObjectAdapter extends BaseAdapter
{
	private Handler handler;

	private List<ListItem> data = new ArrayList();
	private Context context;
	private String filterString = "";
	private canBeRefreshing refresher;

	private List<ListItem> filteredData;

	private ArrayList<String> usedSearches = new ArrayList();

	public ObjectAdapter(Context context, canBeRefreshing refresher)
	{
		this.context = context;
		this.handler = new Handler();
		this.refresher = refresher;
	}

	public boolean haveData()
	{
		return data != null;
	}

	public void setData(List _data)
	{
		data = _data;
	}

	public int getCount()
	{
		return filteredData.size();
	}

	public Object getItem(int position)
	{
		return filteredData.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = filteredData.get(position).getView(convertView, position);
		v.setMinimumHeight(Math.round(70 * MainApplication.mDensity));
		return v;
	}

	// public void fillLayout(LinearLayout l)
	// {
	// ArrayList<View> viewList = new ArrayList<View>(getCount());
	// for (int i = 0; i < getCount(); i++)
	// {
	// View v = getView(i);
	// viewList.add(v);
	// }
	// l.removeAllViews();
	// for (View v : viewList)
	// {
	// l.addView(v);
	// }
	// }

	public void setStringFilter(String s)
	{
		filterString = s.toUpperCase().trim();
		refreshState();
	}

	public void refreshState()
	{
		handler.removeCallbacks(myRunnable);
		filteredData = new ArrayList();

		if (!filterString.trim().equals(""))
		{
			System.out.println("Фильтрация в списке объектов начата");
			for (ListItem item : data)
			{
				String shortCharacteristic = item.getShortCharacteristic().toUpperCase();
				String itemName = item.toString().toUpperCase();

				if (itemName.contains(filterString) || shortCharacteristic.contains(filterString))
					filteredData.add(item);
			}
			if (!usedSearches.contains(filterString))
			{
				myRunnable.setSearchString(filterString);
				handler.postDelayed(myRunnable, 1000);
			}
			System.out.println("Фильтрация в списке объектов окончена");
		} else
			filteredData.addAll(data);
		if (filteredData.size() > 0)
		{
			int size = filteredData.size();
			size = (size < 50) ? size - 1 : 50 - 1;
			filteredData = filteredData.subList(0, size);
		}
	}

	SearchRunnable myRunnable = new SearchRunnable();

	class SearchRunnable implements Runnable
	{
		private String searchString = null;

		public String getSearchString()
		{
			return searchString;
		}

		public void setSearchString(String searchString)
		{
			this.searchString = searchString;
		}

		@Override
		public void run()
		{
			refresher.startRefreshing();
			if (searchString != null)
			{
				MainApplication.getMapItemContainer().loadItemsByNameAsync(MainApplication.getMapItemContainer().getCategory(),
						searchString);
				usedSearches.add(searchString);
			}
		}
	}

}

class ObjectListFragment extends TitleFragment
{
	Context context;
	ObjectAdapter adapter;

	public ObjectListFragment(Context context, ObjectAdapter adapter)
	{
		this.context = context;
		this.adapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		ListView objectListView = new ListView(context);
		objectListView.setDivider(getResources().getDrawable(R.drawable.transperent_color));
		objectListView.setDividerHeight(0);
		objectListView.setCacheColorHint(0);
		objectListView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
		objectListView.setAdapter(adapter);
		return objectListView;

	}

	@Override
	public String getTitle()
	{
		if (MainApplication.getMapItemContainer().getCategoryName().toUpperCase().equals("КИНО"))
			return "Кинотеатры";
		return "Места";
	}

}

class EventListFragment extends TitleFragment
{
	Context context;
	ObjectAdapter adapter;

	public EventListFragment(Context context, ObjectAdapter adapter)
	{
		this.context = context;
		this.adapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		ListView eventListView = new ListView(context);
		eventListView
				.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
		eventListView.setDivider(getResources().getDrawable(R.drawable.transperent_color));
		eventListView.setDividerHeight(0);
		eventListView.setCacheColorHint(0);
		eventListView.setAdapter(adapter);
		return eventListView;
	}

	@Override
	public String getTitle()
	{
		try
		{
			if (MainApplication.getMapItemContainer().getCategoryName().toUpperCase().equals("КИНО"))
				return "Фильмы";
		} catch (NullPointerException e)
		{
			return "События";
		}
		return "События";
	}
}

interface canBeRefreshing
{
	public void startRefreshing();

	public void stopRefreshing();
}