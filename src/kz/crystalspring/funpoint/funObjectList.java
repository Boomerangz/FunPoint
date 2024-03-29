package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.crystalspring.funpoint.events.Event;
import kz.crystalspring.funpoint.venues.ListItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.R;
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

public class funObjectList extends FragmentActivity implements
		RefreshableMapList, canBeRefreshing
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

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(null);
		setContentView(R.layout.object_list);

		viewPager = (ViewPager) findViewById(R.id.pager);
		tabIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		// mapBtn = (Button) findViewById(R.id.mapBtn);
		pgBar = (ProgressBar) findViewById(R.id.progressBar1);

		categorySubHeader = (TextView) findViewById(R.id.category_subheader);
		View profileButton = findViewById(R.id.profile_button);
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
		pagerAdapter = new ViewFragmentAdapter(
				getSupportFragmentManager(), viewList);
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

		searchEdit = (EditText) findViewById(R.id.search_edit);
		searchEdit.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				filterByString(s.toString());
			}
		});

		openSearchButton = (ImageView) findViewById(R.id.search_btn);
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
	}

	@Override
	public void onBackPressed()
	{
		// super.onPause();
		finish();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MainApplication.refreshable = this;
		refreshMapItems();
	}

	private void filterByString(String filter)
	{
		objectAdapter.setStringFilter(filter);
		refreshList();
	}

	private void refreshList()
	{
		stopRefreshing();
		itemsList = MainApplication.mapItemContainer.getFilteredItemList();
		eventsList = MainApplication.eventContainer.getFilteredEventsList();

		categorySubHeader.setText(MainApplication.mapItemContainer
				.getCategoryName().toLowerCase());

		objectAdapter.setData(itemsList);
		objectAdapter.refreshState();

		eventAdapter.setData(eventsList);
		eventAdapter.refreshState();

		pagerAdapter.notifyDataSetChanged();
		System.gc();
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

	public View getView(int position)
	{
		View v = filteredData.get(position).getView(null, position);
		v.setMinimumHeight(Math.round(70 * MainApplication.mDensity));
		return v;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return getView(position);
	}

	public void fillLayout(LinearLayout l)
	{
		ArrayList<View> viewList = new ArrayList<View>(getCount());
		for (int i = 0; i < getCount(); i++)
		{
			View v = getView(i);
			viewList.add(v);
		}
		l.removeAllViews();
		for (View v : viewList)
		{
			l.addView(v);
		}
	}

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
				String shortCharacteristic = item.getShortCharacteristic()
						.toUpperCase();
				String itemName = item.toString().toUpperCase();

				if (itemName.contains(filterString)
						|| shortCharacteristic.contains(filterString))
					filteredData.add(item);
			}
			if (filteredData.size() == 0
					&& !usedSearches.contains(filterString))
			{
				myRunnable.setSearchString(filterString);
				handler.postDelayed(myRunnable, 1000);
			}
			System.out.println("Фильтрация в списке объектов окончена");
		} else
			filteredData.addAll(data);
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
				MainApplication.mapItemContainer.loadItemsByNameAsync(
						MainApplication.mapItemContainer.getCategory(),
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
		this.adapter=adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		ListView objectListView = new ListView(context);
		objectListView.setDivider(getResources().getDrawable(
				R.drawable.transperent_color));
		objectListView.setDividerHeight(0);
		objectListView.setCacheColorHint(0);
		objectListView.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
		objectListView.setAdapter(adapter);
		return objectListView;

	}

	@Override
	public String getTitle()
	{
		return "Объекты";
	}

}

class EventListFragment extends TitleFragment
{
	Context context;
	ObjectAdapter adapter;

	public EventListFragment(Context context, ObjectAdapter adapter)
	{
		this.context = context;
		this.adapter=adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		
		ListView eventListView = new ListView(context);
		eventListView.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
		eventListView.setDivider(getResources().getDrawable(
				R.drawable.transperent_color));
		eventListView.setDividerHeight(0);
		eventListView.setCacheColorHint(0);
		eventListView.setAdapter(adapter);
		return eventListView;
	}

	@Override
	public String getTitle()
	{
		return "СССбытия";
	}

}


interface canBeRefreshing
{
	public void startRefreshing();

	public void stopRefreshing();
}