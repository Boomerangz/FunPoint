package com.boomerang.jam_menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.ProfilePage;
import kz.crystalspring.funpoint.funCheckinNow;
import kz.crystalspring.funpoint.funWaitingActivity;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.pointplus.Helpdesk;
import kz.crystalspring.pointplus.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.boomerang.jam_menu.JamTextImageSwitcher;

public class JamMenuActivity extends Activity
{

	List<JamTextImageSwitcher> switchers = new ArrayList<JamTextImageSwitcher>();
	//int currButton = -1;
	Handler mHandler = new Handler();
	boolean continueUpdating = true;
	static final int UPDATE_DELAY = 1000 + JamTextImageSwitcher.durationTime;
	
	boolean isMoving=false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jam_menu);

		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher1));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher2));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher3));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher4));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher5));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher6));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher7));
		switchers.add((JamTextImageSwitcher) findViewById(R.id.switcher8));

		SwitcherDesc[] swRest = {
				new SwitcherDesc(R.drawable.rest0),
				new SwitcherDesc(R.drawable.rest1),
				new SwitcherDesc(R.drawable.rest2) };
		
		SwitcherDesc[] swCinema = {
				new SwitcherDesc(R.drawable.cinema1),
				new SwitcherDesc(R.drawable.cinema2),
				new SwitcherDesc(R.drawable.cinema3) };
		
		SwitcherDesc[] swShopping = {
				new SwitcherDesc(R.drawable.shopping1),
				new SwitcherDesc(R.drawable.shopping2),
				new SwitcherDesc(R.drawable.shopping3) };
		
		SwitcherDesc[] swHotel = {
				new SwitcherDesc(R.drawable.hotel1),
				new SwitcherDesc(R.drawable.hotel2),
				new SwitcherDesc(R.drawable.hotel3) };
		SwitcherDesc[] a = {
				new SwitcherDesc(R.color.blue),
				new SwitcherDesc(R.color.green),
				new SwitcherDesc(android.R.color.white) };
		SwitcherDesc[] b =  {
				new SwitcherDesc(R.color.blue),
				new SwitcherDesc(R.color.green),
				new SwitcherDesc(android.R.color.white) };
		
//		SwitcherDesc[] swRest = {
//				new SwitcherDesc(R.color.blue),
//				new SwitcherDesc(R.color.green),
//				new SwitcherDesc(R.color.blue) };
//		
//		SwitcherDesc[] swCinema = {
//				new SwitcherDesc(R.color.blue),
//				new SwitcherDesc(R.color.green),
//				new SwitcherDesc(R.color.blue) };
//		
//		SwitcherDesc[] swShopping = {
//				new SwitcherDesc(R.color.blue),
//				new SwitcherDesc(R.color.green),
//				new SwitcherDesc(R.color.blue) };
//		
//		SwitcherDesc[] swHotel = {
//				new SwitcherDesc(R.color.blue),
//				new SwitcherDesc(R.color.green),
//				new SwitcherDesc(R.color.blue) };
//		SwitcherDesc[] a = {
//				new SwitcherDesc(R.color.blue),
//				new SwitcherDesc(R.color.green),
//				new SwitcherDesc(R.color.blue) };
//		SwitcherDesc[] b = {
//				new SwitcherDesc(R.color.blue),
//				new SwitcherDesc(R.color.green),
//				new SwitcherDesc(R.color.blue) };

		switchers.get(0).setImageSource(Arrays.asList(swRest));
		switchers.get(1).setImageSource(Arrays.asList(swCinema));
		switchers.get(2).setImageSource(Arrays.asList(swShopping));
		switchers.get(3).setImageSource(Arrays.asList(swHotel));
		for (int i = 0; i < switchers.size(); i++)
		{
			JamTextImageSwitcher switcher;
			switcher = switchers.get(i);

			if (i > 3)
			{
				if (i % 2 == 0)
					switcher.setImageSource(Arrays.asList(a));
				else
					switcher.setImageSource(Arrays.asList(b));
			}
			switcher.updateImage();
			switcher.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Toast toast = Toast.makeText(getBaseContext(),
							"Coming soon", 100);
					toast.show();
				}
			});
		}
		switchers.get(0).setText("РЕСТОРАНЫ");
		switchers.get(0).setTextBackground(getResources().getColor(R.color.restaurant));
		
		switchers.get(1).setText("КИНО");
		switchers.get(1).setTextBackground(getResources().getColor(R.color.cinema));
		
		switchers.get(3).setText("ОТЕЛИ");
		switchers.get(3).setTextBackground(getResources().getColor(R.color.hotel));
		
		switchers.get(2).setText("МАГАЗИНЫ");
		switchers.get(2).setTextBackground(getResources().getColor(R.color.shop));
		
		switchers.get(4).setText("CheckIn Now");
		switchers.get(5).setText("Jam.KZ");
		switchers.get(6).setText("ПРОФИЛЬ");
		switchers.get(7).setText("СООБЩЕНИЯ");
		switchers.get(0).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_FOOD);
			}
		});
		switchers.get(1).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_CINEMA);
			}
		});
		switchers.get(2).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_MARKET);
			}
		});
		switchers.get(3).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_HOTEL);
			}
		});
		
		switchers.get(4).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				openCheckIn();
			}
		});

		switchers.get(6).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				openUserInfo();
			}
		});
		switchers.get(7).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				openHelpdesk();
			}
		});
		//MainApplication.loadAdditionalContent();
		
		
		
		
		ScrollView scrollV=(ScrollView) findViewById(R.id.jam_menu_scrollview);
		scrollV.setOnTouchListener(new OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
					isMoving=(event.getAction()==event.ACTION_MOVE);// TODO Auto-generated method stub
				return false;
			}
		});
		scrollV.setVerticalScrollBarEnabled(false);
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		continueUpdating = true;
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, UPDATE_DELAY);

	}

	private void updateNext()
	{
		if (continueUpdating)
		{
			AsyncTask<Object, Object, JamTextImageSwitcher> task=new AsyncTask<Object, Object, JamTextImageSwitcher>()
			{

				@Override
				protected JamTextImageSwitcher doInBackground(Object... params)
				{
	
					int currButton = (int) Math
							.round((Math.random() * switchers.size() - 1));
					if (currButton >= switchers.size())
						currButton = 0;
					if (currButton < 0)
						currButton = switchers.size() - 1;
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, UPDATE_DELAY);
					return switchers.get(currButton);
				}
				
				@Override
				public void onPostExecute(JamTextImageSwitcher result)
				{
					if (!isMoving)
					{
						result.updateImage();
					}
				}
			};
			task.execute();
		}
	}

	private Runnable mUpdateTimeTask = new Runnable()
	{
		public void run()
		{
			updateNext();
		}
	};

	private void runItemActivityWithFilter(String visibleFilter)
	{
		MainApplication.mapItemContainer.setVisibleFilter(visibleFilter);
		Intent intent = new Intent(JamMenuActivity.this, funWaitingActivity.class);
		startActivity(intent);
		MainMenu.currentListTab = MainMenu.OBJECT_LIST_TAB;
		continueUpdating = false;
	}

	private void openHelpdesk()
	{
		openActivity(Helpdesk.class);
	}

	private void openCheckIn()
	{
		openActivity(funCheckinNow.class);
	}
	
	private <E> void openActivity(Class<E> class1)
	{
		Intent intent = new Intent(this, class1);
		startActivity(intent);
	}

	private void openUserInfo()
	{
		openActivity(ProfilePage.class);
	}

}