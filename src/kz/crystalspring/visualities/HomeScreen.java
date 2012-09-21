package kz.crystalspring.visualities;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.ProfilePage;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.RefreshableMapList;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.visualities.homescreen.ExplorerView;
import kz.crystalspring.visualities.homescreen.FriendFeed;
import kz.crystalspring.visualities.homescreen.PlacesSquareMenu;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ScrollView;

public class HomeScreen extends FragmentActivity implements
RefreshableMapList
{
	
	ViewPager viewPager;
	TabPageIndicator tabIndicator;
	
	View placesMenu;
	FriendFeed friendFeed;
	ExplorerView explorer;
	PlacesSquareMenu psm;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		
		List<ViewFragment> viewList = fillObjectAndEventLists(); 
		ViewFragmentAdapter pagerAdapter = new ViewFragmentAdapter(
				getSupportFragmentManager(), viewList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
		
		View profileButton=findViewById(R.id.profile_button);
		profileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{
				Intent j = new Intent(HomeScreen.this, ProfilePage.class);
				startActivity(j);
			}
		});
		
		View fastCheckinButton=findViewById(R.id.fast_check_btn);
		fastCheckinButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{
				psm.runItemActivityWithFilter(MapItem.FSQ_UNDEFINED);
			}
		});

		tabIndicator.setViewPager(viewPager);

	}
	
	@Override
	public void onBackPressed()
	{
	//	super.onPause();
		finish();
	}

	@Override
	public void refreshMapItems()
	{
		if (friendFeed!=null)
			friendFeed.refresh();
		if (explorer!=null)
			explorer.refresh();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		MainApplication.refreshable=this;
		refreshMapItems();
	}
	
	
	private List<ViewFragment> fillObjectAndEventLists()
	{
		List<ViewFragment> viewList = new ArrayList();

		friendFeed=new FriendFeed(this);
		View friendFeedView=friendFeed.getFriendFeed();

		viewList.add(new ViewFragment(friendFeedView, "Лента"));
		
		psm=new PlacesSquareMenu(this);
		View squareMenu=psm.getSquareMenu();
		viewList.add(new ViewFragment(squareMenu, "Места"));
		
		ListView eventListView = new ListView(getBaseContext());
		eventListView.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
		eventListView.setDivider(getResources().getDrawable(R.drawable.transperent_color));
		eventListView.setDividerHeight(0);
		eventListView.setCacheColorHint(0);

		viewList.add(new ViewFragment(eventListView, "События"));
		
		ListView eventListView1 = new ListView(getBaseContext());
		eventListView1.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
		eventListView1.setDivider(getResources().getDrawable(R.drawable.transperent_color));
		eventListView1.setDividerHeight(0);
		eventListView1.setCacheColorHint(0);

		explorer=new ExplorerView(this);
		View explorerView=explorer.getExplorer();
		viewList.add(new ViewFragment(explorerView, "Рекомендации"));
		return viewList;
	}
	
}
