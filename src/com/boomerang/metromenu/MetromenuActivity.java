package com.boomerang.metromenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.pointplus.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MetromenuActivity extends Activity {

	List<TextImageSwitcher> switchers = new ArrayList();
	int currButton=-1;
	Handler mHandler=new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metromenu);

		switchers.add((TextImageSwitcher) findViewById(R.id.switcher1));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher2));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher4));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher3));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher5));
		switchers.add((TextImageSwitcher) findViewById(R.id.switcher6));

		Integer[] a = { R.drawable.red, R.drawable.blue };
		Integer[] b = { R.drawable.blue, R.drawable.red };

		for (int i = 0; i < switchers.size(); i++) {
			TextImageSwitcher switcher;
			switcher = switchers.get(i);

			if (i % 2 == 0)
				switcher.ImageSource = (Arrays.asList(a));
			else
				switcher.ImageSource = (Arrays.asList(b));
			switcher.updateImage();
			switcher.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					Toast toast = Toast.makeText(getBaseContext(), " нопка нажата", 100);
					toast.show();
				}
			});
		}
		((TextImageSwitcher)switchers.get(0)).setText("–естораны");
		((TextImageSwitcher)switchers.get(0)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent=new Intent(MetromenuActivity.this,MainMenu.class);
				startActivity(intent);
			}
		});
		mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 1000);
	}

	private void beginUpdating() 
	{

	}
	
	private void updateNext()
	{
		currButton=(int) Math.round((Math.random()*switchers.size()-1));
		if (currButton>=switchers.size())
			currButton=0;
		if (currButton<0)
			currButton=switchers.size()-1;
		switchers.get(currButton).updateImage();
		mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 1000);
	}

	private Runnable mUpdateTimeTask = new Runnable() 
	{
		public void run() 
		{
			updateNext();
		}
	};
}