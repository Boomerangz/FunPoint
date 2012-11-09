package kz.crystalspring.visualities;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.ProfilePage;
import kz.crystalspring.funpoint.RefreshableMapList;
import kz.crystalspring.funpoint.funWaitingActivity;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.visualities.homescreen.EventsFragment;
import kz.crystalspring.visualities.homescreen.ExplorerFragment;
import kz.crystalspring.visualities.homescreen.FriendFeedMenuFragment;
import kz.crystalspring.visualities.homescreen.SquareMenuFragment;
import kz.crystalspring.visualities.homescreen.TitleFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

public class HomeScreen1 extends FragmentActivity implements RefreshableMapList
{

	ViewPager viewPager;
	TabPageIndicator tabIndicator;

	View placesMenu;
	FriendFeedMenuFragment friendFeed;
	ExplorerFragment explorer;
	EventsFragment events;
	SquareMenuFragment spm;

	static int cuurPage = 0;
	ViewFragmentAdapter pagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.w("HomeScreen", "Created");
		setContentView(R.layout.home_screen1);
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabIndicator = (TabPageIndicator) findViewById(R.id.indicator);

		Log.w("HomeScreen", Integer.valueOf(cuurPage).toString());

		View profileButton = findViewById(R.id.profile_button);
		profileButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent j = new Intent(HomeScreen1.this, ProfilePage.class);
				startActivity(j);
			}
		});

		View fastCheckinButton = findViewById(R.id.fast_check_btn);
		fastCheckinButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runItemActivityWithFilter(MapItem.FSQ_UNDEFINED);
			}
		});
		final List<TitleFragment> viewList = fillObjectAndEventLists();
		pagerAdapter = new ViewFragmentAdapter(getSupportFragmentManager(),
				viewList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(4);
		tabIndicator.setViewPager(viewPager);
		viewPager.setCurrentItem(0);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{

			@Override
			public void run()
			{
				if (pagerAdapter.getItem(0).getView() == null)
				{
					Log.w("HomeScreen", "NULLL");
					pagerAdapter.notifyDataSetChanged();
				}
				if (viewList.get(0).getView() == null)
				{
					Log.w("HomeScreen", "NULLL22");
				}
			}
		}, 10);
		MainApplication.tracker.startNewSession(MainApplication.ANALYTIC_SESSION,20,this);
	}

	protected void runItemActivityWithFilter(String visibleFilter)
	{
		MainApplication.getMapItemContainer().setVisibleFilter(visibleFilter);
		Intent intent = new Intent(getBaseContext(), funWaitingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(intent);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		Log.w("HomeScreen", "Stopped");
	}

	@Override
	public void onBackPressed()
	{
		// super.onPause();
		finish();
	}

	@Override
	public void onRestart()
	{
		super.onRestart();
		Log.w("HomeScreen", "Restarted");
	}

	@Override
	public void refreshMapItems()
	{
		// pagerAdapter.notifyDataSetChanged();
		if (friendFeed != null)
			friendFeed.refresh();
		if (events != null)
			events.refresh();
		if (explorer != null)
			explorer.refresh();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.w("HomeScreen", "Resumed");
		MainApplication.refreshable = this;
		refreshMapItems();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		Log.w("HomeScreen", "Paused");
		// finish();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.w("HomeScreen", "Destroyed");
		MainApplication.tracker.stopSession();
	}

	private List<TitleFragment> fillObjectAndEventLists()
	{
		List<TitleFragment> viewList = new ArrayList();
		Log.w("HomeScreen", "Filling ViewPager");
		friendFeed = FriendFeedMenuFragment.newInstance();

		Log.w("HomeScreen_Filling", "Filling FriendFeed");

		// View squareMenu=psm.getSquareMenu();
		spm = SquareMenuFragment.newInstance();
		Log.w("HomeScreen_Filling", "Filling SquareMenu");

		events = new EventsFragment();
		explorer = new ExplorerFragment();
		viewList.add(spm);
		viewList.add(events);
		viewList.add(friendFeed);
		viewList.add(explorer);
		return viewList;
	}

}
