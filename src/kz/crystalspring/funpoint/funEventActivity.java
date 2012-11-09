package kz.crystalspring.funpoint;

import kz.com.pack.jam.R;
import kz.crystalspring.cinema.CinemaTimeTable2;
import kz.crystalspring.cinema.FilmLine;
import kz.crystalspring.funpoint.events.Event;
import kz.crystalspring.funpoint.events.FilmEvent;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.LoadingImageView;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class funEventActivity extends Activity
{
	Event event;
	TextView eventNameText;
	TextView eventDescriptionText;
	LoadingImageView lImageView;
	LinearLayout listView;

	View mainView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setContentView(R.layout.waiting_layout);

		AsyncTask task = new AsyncTask()
		{
			@Override
			protected Object doInBackground(Object... params)
			{
				mainView = getLayoutInflater().inflate(R.layout.fun_event_layout, null);

				if (event == null)
				{
					event = MainApplication.getEventContainer().getEventById(MainApplication.selectedEventId);
				}
				eventNameText = (TextView) mainView.findViewById(R.id.event_name);
				eventDescriptionText = (TextView) mainView.findViewById(R.id.event_desc);
				lImageView = (LoadingImageView) mainView.findViewById(R.id.loading_imageview);
				listView = (LinearLayout) mainView.findViewById(R.id.listView1);

				if (MainApplication.getInstance().checkInternetConnection())
				{
					eventNameText.setText(event.getName());
					eventDescriptionText.setText(Html.fromHtml((String)ProjectUtils.ifnull(event.getDescription(),"")));

					lImageView.setTag(Integer.toString(event.getId()));
					FSQConnector.loadImageAsync(lImageView, event.getImageUrl(), UrlDrawable.BIG_URL, false, null);
					if (FilmEvent.class.isInstance(event))
					{
						FilmEvent fEvent = (FilmEvent) event;
						fEvent.loadPlaceTable();
					}
				} else
				{
					MainApplication.loadNoInternetPage();
					finish();
				}
				// FileConnector.loadJSONPlaceList(Integer.toString(event.getId()));
				return null;
			}

			@Override
			protected void onPostExecute(Object result)
			{
				super.onPostExecute(result);
				setContentView(mainView);
				MainApplication.tracker.trackPageView("Event_Category="+event.getShortCharacteristic()+", Eventname="+event.getName());
				if (FilmEvent.class.isInstance(event))
				{
					FilmEvent fEvent = (FilmEvent) event;
					EventTimeTableAdapter adapter = new EventTimeTableAdapter(fEvent.getTimeTable(), funEventActivity.this);
					adapter.fillLayout(listView);
				}
			}
		};
		task.execute();
	}
}

class EventTimeTableAdapter extends BaseAdapter
{
	CinemaTimeTable2 table;
	Activity context;

	public EventTimeTableAdapter(CinemaTimeTable2 table, Activity context)
	{
		this.table = table;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return table.getFilmsCount();
	}

	@Override
	public Object getItem(int position)
	{
		return table.getFilmStr(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public View getView(final int position, View convertView)
	{
		return ((FilmLine) getItem(position)).getView(context);
	}

	public void fillLayout(LinearLayout layout)
	{
		layout.removeAllViews();
		for (int i = 0; i < getCount(); i++)
		{
			View v = getView(i, null, null);
			layout.addView(v);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView);
	}

	class ViewHolder
	{
		TextView text;
		TableLayout tableLayout;
	}

}
