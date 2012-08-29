package kz.crystalspring.visualities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.boomerang.jam_menu.JamTextImageSwitcher;
import com.boomerang.jam_menu.SwitcherDescription;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.RefreshableMapList;
import kz.crystalspring.visualities.homescreen.PlacesSquareMenu;
import android.R.layout;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

public class HomeScreen extends FragmentActivity implements
RefreshableMapList
{
	
	ViewPager viewPager;
	TabPageIndicator tabIndicator;
	
	View placesMenu;
	
	
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
		// TODO Auto-generated method stub
	}
	
	
	private List<ViewFragment> fillObjectAndEventLists()
	{
		List<ViewFragment> viewList = new ArrayList();

		ListView eventListView2 = new ListView(getBaseContext());
		eventListView2.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.WRAP_CONTENT));
		eventListView2.setDivider(getResources().getDrawable(R.drawable.transperent_color));
		eventListView2.setDividerHeight(0);
		eventListView2.setCacheColorHint(0);

		viewList.add(new ViewFragment(eventListView2, "Лента"));
		
		PlacesSquareMenu psm=new PlacesSquareMenu(this);
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

		viewList.add(new ViewFragment(eventListView1, "Рекомендации"));
		return viewList;
	}
	
}
