package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.venues.Event;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class funEventActivity extends Activity
{
	Event event;
	TextView eventNameText;
	TextView eventDescriptionText;
	
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
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		eventNameText.setText(event.getName());
		eventDescriptionText.setText(event.getDescription());
	}
}
