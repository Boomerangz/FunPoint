package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;
import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FoursquareApp.FsqAuthListener;
import kz.crystalspring.funpoint.venues.FSQBadge;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQUser;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.views.GalleryWrapper;
import kz.crystalspring.views.LoadingImageView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends Activity implements RefreshableMapList
{
	FSQUser user;
	Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(null);
		setContentView(R.layout.profile_page);

		spinner = (Spinner) findViewById(R.id.spinner_city);
		Button button1 = (Button) findViewById(R.id.button1);
		final FoursquareApp mFsqApp = MainApplication.FsqApp;
		FsqAuthListener listener = new FsqAuthListener()
		{
			@Override
			public void onSuccess()
			{
				Toast.makeText(ProfilePage.this,
						"Connected as " + mFsqApp.getUserName(),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFail(String error)
			{
				Toast.makeText(ProfilePage.this, error, Toast.LENGTH_SHORT)
						.show();
			}
		};

		mFsqApp.setListener(listener);
		button1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mFsqApp.authorize(ProfilePage.this);
			}
		});

		List<String> list = new ArrayList<String>();
		list.add("list 1");
		list.add("list 2");
		list.add("list 3");
		final CitySpinnerAdapter dataAdapter = new CitySpinnerAdapter(this,
				android.R.layout.simple_spinner_item);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				MainApplication.setCity(dataAdapter.getCity(arg2));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{

			}
		});
		City selectedCity = MainApplication.getCityManager().getSelectedCity();
		if (selectedCity != null)
			spinner.setSelection(dataAdapter.positionOf(MainApplication
					.getCityManager().getSelectedCity()) + 1);
		else
			spinner.setSelection(0);
	}

	class CitySpinnerAdapter extends ArrayAdapter<String>
	{
		Context context;
		List<City> cityNamesList;

		public CitySpinnerAdapter(Context context, int resource)
		{
			super(context, android.R.layout.simple_spinner_item,
					new ArrayList<String>());
			cityNamesList = MainApplication.getCityManager().getCityList();
			add("По местоположению");
			for (City st : cityNamesList)
			{
				add(st.getRusName());
			}
		}

		public City getCity(int arg2)
		{
			if (arg2 > 0)
				return cityNamesList.get(arg2 - 1);
			else
				return null;
		}

		public int positionOf(City selectedCity)
		{
			if (cityNamesList.contains(selectedCity))
				return cityNamesList.indexOf(selectedCity);
			else
				return 0;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		user = FSQUser.getInstance();
		user.fillIfNot();
		MainApplication.refreshable = this;
		refreshMapItems();
	}

	@Override
	public void refreshMapItems()
	{
		View pg = (View) findViewById(R.id.progress_bar);
		View infLayout = (View) findViewById(R.id.main_info_layout);
		if (user.isFilled())
		{
			TextView usernameTV = (TextView) findViewById(R.id.user_name);
			TextView usermailTV = (TextView) findViewById(R.id.user_email);
			TextView recentScoreTV = (TextView) findViewById(R.id.recent_score);
			TextView maxScoreTV = (TextView) findViewById(R.id.max_score);
			TextView checkinsTV = (TextView) findViewById(R.id.user_checkins);
			TextView friendsTV = (TextView) findViewById(R.id.user_friends);
			
			LoadingImageView loadingImage = (LoadingImageView) findViewById(R.id.loading_imageview);
			LinearLayout badgeGallery = (LinearLayout) findViewById(R.id.badge_gallery);

			usernameTV.setText(user.getName());
			usermailTV.setText(user.getEmail());
			loadingImage.setTag(user.getName());
			FSQConnector.loadImageAsync(loadingImage, user.getPhoto(),
					UrlDrawable.BIG_URL, false, null);
			loadBadgesGallery(badgeGallery);
			recentScoreTV.setText(user.getRecentScore().toString()+" баллов");
			maxScoreTV.setText(user.getMaxScore().toString());
			checkinsTV.setText(user.getCheckinCount().toString()+" чекинов");
			friendsTV.setText(user.getFriendCount().toString()+" друзей");
			

			pg.setVisibility(View.GONE);
			infLayout.setVisibility(View.VISIBLE);
		} else
		{
			pg.setVisibility(View.VISIBLE);
			infLayout.setVisibility(View.GONE);
		}
	}

	private void loadBadgesGallery(LinearLayout badgeGallery)
	{
		GalleryWrapper galleryWrapper = new GalleryWrapper(this,
				GalleryWrapper.MODE_BADGES);
		List<FSQBadge> badges=user.getBadgesList();
		List<UrlDrawable> drawable=new ArrayList<UrlDrawable>(badges.size());
		for(FSQBadge badge:badges)
		{
			drawable.add(badge);
		}
		galleryWrapper.addDrawableList(drawable);
		badgeGallery.removeAllViews();
		badgeGallery.addView(galleryWrapper.getView());
	}

}
