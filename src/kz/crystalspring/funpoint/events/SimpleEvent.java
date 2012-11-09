package kz.crystalspring.funpoint.events;

import java.text.ParseException;
import java.util.Date;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
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
		View v=super.getView(convertView, position);
		TextView date=(TextView) v.findViewById(R.id.date);
		date.setText(getDateText());
		return v;
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
		public View background;
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
			item=(FSQItem) MainApplication.getMapItemContainer().getItemById(place_id);
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
