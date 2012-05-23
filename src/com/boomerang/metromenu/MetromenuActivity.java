package com.boomerang.metromenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MetromenuActivity extends Activity
{

	List<TextImageSwitcher> switchers = new ArrayList<TextImageSwitcher>();
	int currButton = -1;
	Handler mHandler = new Handler();
	boolean continueUpdating=true;
	static final int UPDATE_DELAY=1000+TextImageSwitcher.durationTime;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metromenu);

		switchers.add((TextImageSwitcher) findViewById(R.id.switcher1));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher2));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher4));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher3));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher5));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher6));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher7));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher8));

		Integer[] a1 = { R.drawable.rest0,R.drawable.rest1,R.drawable.rest2};
		Integer[] a = { R.drawable.red, R.drawable.blue };
		Integer[] b = { R.drawable.blue, R.drawable.red };

		for (int i = 0; i < switchers.size(); i++)
		{
			TextImageSwitcher switcher;
			switcher = switchers.get(i);

			if (i % 2 == 0)
				switcher.ImageSource = (Arrays.asList(a));
			else
				switcher.ImageSource = (Arrays.asList(b));
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
		switchers.get(0).setText("Рестораны");
		switchers.get(0).ImageSource=Arrays.asList(a1);
		switchers.get(1).setText("Кино");
		switchers.get(2).setText("Отели");
		switchers.get(3).setText("Магазины");
		switchers.get(4).setText("Акции и скидки");
		switchers.get(5).setText("Night.KZ");
		switchers.get(0)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						runItemActivityWithFilter(MapItem.FSQ_TYPE_FOOD);
					}
				});
		switchers.get(1)
		.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_CINEMA);
			}
		});
		switchers.get(2)
		.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_HOTEL);
			}
		});
		switchers.get(3)
		.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				runItemActivityWithFilter(MapItem.FSQ_TYPE_MARKET);
			}
		});

	}
	
	@Override 
	protected void onResume()
	{
		super.onResume();
		continueUpdating=true;
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, UPDATE_DELAY);
	}

	private void beginUpdating()
	{

	}

	private void updateNext()
	{
		if (continueUpdating)
		{
		currButton = (int) Math.round((Math.random() * switchers.size() - 1));
		if (currButton >= switchers.size())
			currButton = 0;
		if (currButton < 0)
			currButton = switchers.size() - 1;
		switchers.get(currButton).updateImage();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, UPDATE_DELAY);
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
		Intent intent = new Intent(MetromenuActivity.this,
				MainMenu.class);
		startActivity(intent);
		MainMenu.currentListTab=MainMenu.OBJECT_MAP_TAB;
		continueUpdating=false;
	}
}