package kz.crystalspring.visualities;

import com.google.analytics.tracking.android.EasyTracker;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.events.Event;
import kz.crystalspring.funpoint.events.SimpleEvent;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.views.LoadingImageView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventActivity extends Activity
{
	SimpleEvent event;
	TextView eventNameText;
	TextView eventDescriptionText;
	LoadingImageView lImageView;
	LinearLayout listView;

	View mainView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_layout);
	}

	@Override
	protected void onResume()
	{
		super.onResume();


		if (MainApplication.getInstance().checkInternetConnection())
		{
			AsyncTask task = new AsyncTask()
			{

				@Override
				protected Object doInBackground(Object... params)
				{
					mainView = getLayoutInflater().inflate(R.layout.fun_event_layout, null);

					if (event == null)
					{
						event = (SimpleEvent) MainApplication.getEventContainer().getEventById(MainApplication.selectedEventId);
					}
					eventNameText = (TextView) mainView.findViewById(R.id.event_name);
					eventDescriptionText = (TextView) mainView.findViewById(R.id.event_desc);
					lImageView = (LoadingImageView) mainView.findViewById(R.id.loading_imageview);
					listView = (LinearLayout) mainView.findViewById(R.id.listView1);

					if (!event.isAdditionalInfoLoaded())
					{
						event.loadAdditionalInfo();
					}
					showAdditionalInfo();
					eventNameText.setText(event.getName());
					eventDescriptionText.setText(Html.fromHtml(event.getDescription()));
					FSQConnector.loadImageAsync(lImageView, event.getImageUrl(), UrlDrawable.BIG_URL, false, null);
					return null;
				}

				@Override
				protected void onPostExecute(Object result)
				{
					super.onPostExecute(result);
					setContentView(mainView);
				};
			};
			task.execute();
		} else
		{
			MainApplication.loadNoInternetPage();
			finish();
		}
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override 
	public void onStop()
	{
		super.onStop();
		EasyTracker.getInstance().activityStart(this);
	}

	private void showAdditionalInfo()
	{
		if (event.hasPlace())
		{
			listView.removeAllViews();
			FSQItem item = event.getPlace();
			listView.addView(item.getNewView());
		}
	}
}
