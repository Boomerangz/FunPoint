package kz.crystalspring.funpoint.events;

import java.text.ParseException;
import java.util.Date;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.funEventActivity;
import kz.crystalspring.funpoint.events.Event.ViewHolder;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.visualities.EventActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleEvent extends Event
{
	Date eventDate;
	FSQItem item=null;
	boolean isAdditionInfoLoaded=false;
	
	SimpleEvent(JSONObject jObject)
	{
		super(jObject);
		try
		{
			Date date = datetime_formatter.parse(jObject.getJSONArray("dates")
					.getString(0));
			setEventDate(date);
		} catch (ParseException e)
		{
			e.printStackTrace();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public Date getEventDate()
	{
		return eventDate;
	}

	public void setEventDate(Date eventDate)
	{
		this.eventDate = eventDate;
	}

	@Override
	public String getShortCharacteristic()
	{
		String st=JamGenresMap.get(getEventType());
		if (st==null)
				st="Другое";
		return st;
	}
	
	@Override
	public View getView(View convertView, int position)
	{
		convertView = null;
		ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.event_list_item, null);
		holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.name);
		holder.shortDescription = (TextView) convertView
				.findViewById(R.id.short_description);
		holder.goIntoButton = (ImageView) convertView
				.findViewById(R.id.go_into_btn);
		holder.background = (View) convertView.findViewById(R.id.list_block);
		holder.itemColorView = (View) convertView
				.findViewById(R.id.item_color_view);
		holder.date= (TextView) convertView.findViewById(R.id.date);

		convertView.setMinimumHeight(80);
		convertView.setTag(holder);
		String st = Integer.toString(position) + ". " + getName();

		holder.name.setText(st);
		holder.shortDescription.setText(getShortCharacteristic());
		holder.background.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainApplication.selectedEventId = Integer.toString(id);
				Intent intent = new Intent(context, EventActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		holder.date.setText(getDateText());
		holder.itemColorView.setBackgroundColor(getItemColor());

		return convertView;
	}
	
	private String getDateText()
	{
		return date_formatter.format(getEventDate());
	}

	public static class ViewHolder
	{
		public TextView name;
		public TextView shortDescription;
		public TextView date;
		public ImageView goIntoButton;
		public View background;
		public View itemColorView;
	}
	
	
	
	
	public boolean isAdditionalInfoLoaded()
	{
		return isAdditionInfoLoaded;
	}
	
	public void loadAdditionalInfo()
	{
		JSONArray jObject=FileConnector.loadJSONPlaceList((Integer.toString(getId())));
		try
		{
			String place_id=jObject.getJSONObject(0).getString("fsq_id");
			item=(FSQItem) MainApplication.mapItemContainer.getItemById(place_id);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		setAdditionalInfoLoaded(true);
	}

	private void setAdditionalInfoLoaded(boolean b)
	{
		isAdditionInfoLoaded=true;
	}

	public boolean hasPlace()
	{
		return item!=null;
	}

	public FSQItem getPlace()
	{
		return item;
	}
}
