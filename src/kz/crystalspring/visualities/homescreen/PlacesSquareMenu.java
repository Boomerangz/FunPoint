package kz.crystalspring.visualities.homescreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.ProfilePage;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.funCheckinNow;
import kz.crystalspring.funpoint.funWaitingActivity;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.Helpdesk;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.boomerang.jam_menu.JamMenuActivity;
import com.boomerang.jam_menu.JamTextImageSwitcher;
import com.boomerang.jam_menu.SwitcherDescription;

public class PlacesSquareMenu
{
	View squareMenu;
	Activity context;
	boolean isMoving;
	public PlacesSquareMenu(Activity context)
	{
		this.context = context;
		squareMenu = createSquareMenu();
	}
	
	public View getSquareMenu()
	{
		if (squareMenu!=null)
			return squareMenu;
		else
			squareMenu = createSquareMenu();
		return getSquareMenu();
	}

	List<JamTextImageSwitcher> switchers = new ArrayList<JamTextImageSwitcher>();

	private View createSquareMenu()
	{
		View squareMenu;
		LayoutInflater layoutInf = context.getLayoutInflater();

		squareMenu = layoutInf.inflate(R.layout.jam_menu, null);
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher1));
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher2));
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher3));
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher4));
		
		
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher5));
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher6));
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher7));
		switchers.add((JamTextImageSwitcher) squareMenu
				.findViewById(R.id.switcher8));
		SwitcherDescription[] swRest = {
				new SwitcherDescription(R.drawable.rest0),
				new SwitcherDescription(R.drawable.rest1),
				new SwitcherDescription(R.drawable.rest2) };

		SwitcherDescription[] swCinema = {
				new SwitcherDescription(R.drawable.cinema1),
				new SwitcherDescription(R.drawable.cinema2),
				new SwitcherDescription(R.drawable.cinema3) };

		SwitcherDescription[] swShopping = {
				new SwitcherDescription(R.drawable.shopping1),
				new SwitcherDescription(R.drawable.shopping2),
				new SwitcherDescription(R.drawable.shopping3) };

		SwitcherDescription[] swHotel = {
				new SwitcherDescription(R.drawable.hotel1),
				new SwitcherDescription(R.drawable.hotel2),
				new SwitcherDescription(R.drawable.hotel3) };
		
		SwitcherDescription[] swСlub = {
				new SwitcherDescription(R.drawable.club1),
				new SwitcherDescription(R.drawable.club2),
				new SwitcherDescription(R.drawable.club3) };
		
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
		switchers.get(4).setImageSource(Arrays.asList(swСlub));

		switchers.get(0).setText("Рестораны");

		switchers.get(1).setText("Кино");

		switchers.get(3).setText("Отели");

		switchers.get(2).setText("Магазины");
		
		switchers.get(4).setText("Ночные клубы");

		for (int i = 0; i < switchers.size(); i++)
		{
			JamTextImageSwitcher switcher;
			switcher = switchers.get(i);

			if (i > 4)
			{
				if (i % 2 == 0)
					switcher.setImageSource(Arrays.asList(a));
				else
					switcher.setImageSource(Arrays.asList(b));
			}
			switcher.updateImage();
		}
		updateNext();
		
		
		
		switchers.get(0).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_FOOD);
			}
		});
		switchers.get(1).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_CINEMA);
			}
		});
		switchers.get(2).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_MARKET);
			}
		});
		switchers.get(3).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_HOTEL);
			}
		});
		switchers.get(4).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_CLUB);//openCheckIn();
			}
		});
		switchers.get(6).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				openUserInfo();
			}
		});
		switchers.get(7).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				openHelpdesk();
			}
		});
		
		
		return squareMenu;
	}
	
	static final int UPDATE_DELAY = 1000 + JamTextImageSwitcher.durationTime;
	Handler mHandler = new Handler();

	private void updateNext()
	{
		// if (continueUpdating)
		// {
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
				// if (!isMoving)
				// {
				result.updateImage();
				// }
			}
		};
		task.execute();
		// }
	}

	private void runItemActivityWithFilter(String visibleFilter)
	{
		MainApplication.mapItemContainer.setVisibleFilter(visibleFilter);
		Intent intent = new Intent(context,
				funWaitingActivity.class);
		context.startActivity(intent);
		MainMenu.currentListTab = MainMenu.OBJECT_LIST_TAB;
		//continueUpdating = false;
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
		Intent intent = new Intent(context, class1);
		context.startActivity(intent);
	}

	private void openUserInfo()
	{
		openActivity(ProfilePage.class);
	}
	
	
	private Runnable mUpdateTimeTask = new Runnable()
	{
		public void run()
		{
			updateNext();
		}
	};
	
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
			return squareMenu.onTouchEvent(arg1);
		}

		public abstract void onClick();
	}

}


