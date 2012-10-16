package kz.crystalspring.visualities;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
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
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventActivity extends Activity
{
	SimpleEvent event;
	TextView eventNameText;
	TextView eventDescriptionText;
	LoadingImageView lImageView;
	LinearLayout listView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_event_layout);
		if (event == null)
		{
			event = (SimpleEvent) MainApplication.eventContainer
					.getEventById(MainApplication.selectedEventId);
		}
		eventNameText = (TextView) findViewById(R.id.event_name);
		eventDescriptionText = (TextView) findViewById(R.id.event_desc);
		lImageView = (LoadingImageView) findViewById(R.id.loading_imageview);
		listView = (LinearLayout) findViewById(R.id.listView1);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (MainApplication.getInstance().checkInternetConnection())
		{
			eventNameText.setText(event.getName());
			eventDescriptionText.setText(Html.fromHtml(event.getDescription()));

			FSQConnector.loadImageAsync(lImageView, event.getImageUrl(), UrlDrawable.BIG_URL, false, null);
		} else
		{
			MainApplication.loadNoInternetPage();
			finish();
		}
		if (!event.isAdditionalInfoLoaded())
		{
			event.loadAdditionalInfo();
		}
		showAdditionalInfo();
	}

	private void showAdditionalInfo()
	{
		if	(event.hasPlace())
		{
			listView.removeAllViews();
			FSQItem item=event.getPlace();
			listView.addView(item.getNewView());
		}
	}
}
