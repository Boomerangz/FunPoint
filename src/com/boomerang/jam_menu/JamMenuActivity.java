package com.boomerang.jam_menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.ProfilePage;
import kz.crystalspring.funpoint.funCheckinNow;
import kz.crystalspring.funpoint.funWaitingActivity;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.pointplus.Helpdesk;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.aphidmobile.flip.FlipViewGroup;
import com.boomerang.jam_menu.JamTextImageSwitcher;

public class JamMenuActivity extends Activity
{

	List<JamTextImageSwitcher> switchers = new ArrayList<JamTextImageSwitcher>();
	// int currButton = -1;
	Handler mHandler = new Handler();
	boolean continueUpdating = true;
	static final int UPDATE_DELAY = 1000 + JamTextImageSwitcher.durationTime;
	private FlipViewGroup contentView;

	boolean isMoving = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = getLayoutInflater();

		View firstPage = inflater.inflate(R.layout.jam_menu, null);
		View secondPage = inflater.inflate(R.layout.jam_menu_page2, null);

		contentView = new FlipViewGroup(this);

		switchers.add((JamTextImageSwitcher) firstPage
				.findViewById(R.id.switcher1));
		switchers.add((JamTextImageSwitcher) firstPage
				.findViewById(R.id.switcher2));
		switchers.add((JamTextImageSwitcher) firstPage
				.findViewById(R.id.switcher3));
		switchers.add((JamTextImageSwitcher) firstPage
				.findViewById(R.id.switcher4));
		switchers.add((JamTextImageSwitcher) secondPage
				.findViewById(R.id.switcher5));
		switchers.add((JamTextImageSwitcher) secondPage
				.findViewById(R.id.switcher6));
		switchers.add((JamTextImageSwitcher) secondPage
				.findViewById(R.id.switcher7));
		switchers.add((JamTextImageSwitcher) secondPage
				.findViewById(R.id.switcher8));

		SwitcherDescription[] swRest = { new SwitcherDescription(R.drawable.rest0),
				new SwitcherDescription(R.drawable.rest1),
				new SwitcherDescription(R.drawable.rest2) };

		SwitcherDescription[] swCinema = { new SwitcherDescription(R.drawable.cinema1),
				new SwitcherDescription(R.drawable.cinema2),
				new SwitcherDescription(R.drawable.cinema3) };

		SwitcherDescription[] swShopping = { new SwitcherDescription(R.drawable.shopping1),
				new SwitcherDescription(R.drawable.shopping2),
				new SwitcherDescription(R.drawable.shopping3) };

		SwitcherDescription[] swHotel = { new SwitcherDescription(R.drawable.hotel1),
				new SwitcherDescription(R.drawable.hotel2),
				new SwitcherDescription(R.drawable.hotel3) };
		SwitcherDescription[] a = { new SwitcherDescription(R.color.blue),
				new SwitcherDescription(R.color.green),
				new SwitcherDescription(android.R.color.white) };
		SwitcherDescription[] b = { new SwitcherDescription(R.color.blue),
				new SwitcherDescription(R.color.green),
				new SwitcherDescription(android.R.color.white) };

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
		}
		switchers.get(0).setText("РЕСТОРАНЫ");
//		switchers.get(0).setTextBackground(
//				getResources().getColor(R.color.restaurant));

		switchers.get(1).setText("КИНО");
//		switchers.get(1).setTextBackground(
//				getResources().getColor(R.color.cinema));

		switchers.get(3).setText("ОТЕЛИ");
//		switchers.get(3).setTextBackground(
//				getResources().getColor(R.color.hotel));

		switchers.get(2).setText("МАГАЗИНЫ");
//		switchers.get(2).setTextBackground(
//				getResources().getColor(R.color.shop));

		switchers.get(4).setText("CheckIn Now");
		switchers.get(5).setText("Jam.KZ");
		switchers.get(6).setText("ПРОФИЛЬ");
		switchers.get(7).setText("СООБЩЕНИЯ");

		switchers.get(0).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_FOOD);
			}
		});
		switchers.get(1).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_CINEMA);
			}
		});
		switchers.get(2).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_MARKET);
			}
		});
		switchers.get(3).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_HOTEL);
			}
		});
		switchers.get(4).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				openCheckIn();
			}
		});
		switchers.get(6).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				openUserInfo();
			}
		});
		switchers.get(7).setOnTouchListener(new OnSwitcherTouchListener()
		{
			@Override
			public void onClick()
			{
				openHelpdesk();
			}
		});
		contentView.addFlipView(secondPage);
		contentView.addFlipView(firstPage);

		setContentView(contentView);
		contentView.startFlipping();
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		continueUpdating = true;
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, UPDATE_DELAY);
		contentView.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		contentView.onPause();
	}

	private void updateNext()
	{
		if (continueUpdating)
		{
			AsyncTask<Object, Object, JamTextImageSwitcher> task = new AsyncTask<Object, Object, JamTextImageSwitcher>()
			{
				@Override
				protected JamTextImageSwitcher doInBackground(Object... params)
				{

					int currButton = (int) Math.round((Math.random()
							* switchers.size() - 1));
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
		Intent intent = new Intent(JamMenuActivity.this,
				funWaitingActivity.class);
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

	abstract class OnSwitcherTouchListener implements OnTouchListener
	{
		boolean canceled = false;
		float x;
		float y;

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1)
		{
			System.out.println("ACTION____");
			if (arg1.getAction() == MotionEvent.ACTION_DOWN)
			{
				canceled = false;
				x = arg1.getRawX();
				y = arg1.getRawY();
				isMoving = true;
				System.out.println("ACTION_DOWN");
			}
			switch (arg1.getAction())
			{
			case MotionEvent.ACTION_MOVE:
				if (!canceled)
				{
					System.out.println("ACTION_MOVE");
					if (Math.abs(arg1.getRawX() - x) > 10
							|| Math.abs(arg1.getRawY() - y) > 10)
						canceled = true;
					if (!canceled)
						return true;
					System.out.println("ACTION_CANCELED");
				}
				break;
			case MotionEvent.ACTION_UP:
				System.out.println("ACTION_UP");
				isMoving = false;
				if (!canceled)
				{
					onClick();
					return true;
				}

			}
			System.out.println("ACTION_REMOVED");
			return contentView.onTouchEvent(arg1);
		}

		public abstract void onClick();
	}

}
