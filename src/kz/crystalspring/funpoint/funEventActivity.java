package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.venues.Event;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.visualities.LoadingImageView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class funEventActivity extends Activity
{
	Event event;
	TextView eventNameText;
	TextView eventDescriptionText;
	LoadingImageView lImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_event_layout);
		if (event==null)
		{
			event=MainApplication.eventContainer.getEventById(MainApplication.selectedEventId);
		}
		eventNameText=(TextView) findViewById(R.id.event_name);
		eventDescriptionText=(TextView) findViewById(R.id.event_desc);
		lImageView=(LoadingImageView) findViewById(R.id.loading_imageview);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		eventNameText.setText(event.getName());
		eventDescriptionText.setText(Html.fromHtml(event.getDescription()));
		
		if (event.getImage()!=null)
		{
			lImageView.setDrawable(event.getImage());
		}
		else
		{
			final Event e=event;
			Runnable preTask=new Runnable()
			{
				@Override
				public void run()
				{
						Drawable dr=HttpHelper.loadPictureByUrl(e.getImageUrl());
						e.setImage(dr);
				}
			};
			Runnable postTask=new Runnable()
			{
				@Override
				public void run()
				{
					lImageView.setDrawable(e.getImage());	
				}
			};
			
			MainApplication.pwAggregator.addPriorityTask(preTask, postTask);
		}
	}
}
