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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
	
	
	static int cuurPage=0;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.w("HomeScreen", "Created");
		setContentView(R.layout.home_screen1);
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		
		Log.w("HomeScreen", Integer.valueOf(cuurPage).toString());
		
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
	//	super.onPause();
		finish();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		Log.w("HomeScreen","Restarted");
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
		Log.w("HomeScreen", "Resumed");
		MainApplication.refreshable=this;
		List<ViewFragment> viewList = fillObjectAndEventLists(); 
		ViewFragmentAdapter pagerAdapter = new ViewFragmentAdapter( 
				getSupportFragmentManager(), viewList);
		viewPager.setAdapter(pagerAdapter);
		tabIndicator.setViewPager(viewPager);
		viewPager.setCurrentItem(0);
		refreshMapItems();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Log.w("HomeScreen", "Paused");
	//	finish();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.w("HomeScreen", "Destroyed");
	}
	
	
	private List<ViewFragment> fillObjectAndEventLists()
	{
		List<ViewFragment> viewList = new ArrayList();
		Log.w("HomeScreen","Filling ViewPager");
		friendFeed=new FriendFeed(this);
		View friendFeedView=friendFeed.getFriendFeed();
	//	viewList.add(new ViewFragment(friendFeedView, "Лента"));
		Log.w("HomeScreen_Filling","Filling FriendFeed");

		Button button=new Button(this);
		friendFeedView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		button.setText("Button");
		
		LinearLayout justLayout=(LinearLayout) findViewById(R.id.just_layout);
		justLayout.removeAllViews();
		justLayout.addView(friendFeedView);
		
		
		psm=new PlacesSquareMenu(this);
		View squareMenu=psm.getSquareMenu();
		viewList.add(new ViewFragment(squareMenu, "Места"));
		Log.w("HomeScreen_Filling","Filling SquareMenu");
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
