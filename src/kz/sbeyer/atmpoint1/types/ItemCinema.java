package kz.sbeyer.atmpoint1.types;

import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.FileConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ItemCinema extends FSQItem
{
	boolean hallInfoFilled = false;
	CinemaTimeTable timeTable;

	public static final String CINEMA_IMG = "m_2";

	@Override
	public String getObjTypeId()
	{
		return FSQ_TYPE_CINEMA;
	}

	public ItemCinema loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);
		return this;
	}

	public void loadHallTableFromJSON(JSONArray jCinemaEvents,
			JSONArray jCinemaPlaces, JSONArray jCinemaSection)
	{
		try
		{
			JSONObject jPlace = jCinemaPlaces.getJSONObject(0);
			setAddress(jPlace.getString("address"));
			setName(jPlace.getString("title"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public void loadAdditionalInfo()
	{
		timeTable = new CinemaTimeTable();
		JSONObject jObject = FileConnector.loadCinemaInfo(getId());
		if (jObject != null)
		{
			try
			{
				timeTable.loadFromJSONArray(jObject.getJSONArray("events"));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			hallInfoFilled = true;
		}
	}

	public CinemaTimeTable getTimeTable()
	{
		return timeTable;
	}

	public void setTimeTable(CinemaTimeTable timeTable)
	{
		this.timeTable = timeTable;
	}

	public void setHallInfoNotFilled()
	{
		hallInfoFilled = false;
	}

	public boolean isHallInfoFilled()
	{
		return hallInfoFilled;
	}

	public String getIconName()
	{
		return CINEMA_IMG;
	}


	// @Override
	// public View getView1(View convertView, int position)
	// {
	// final ViewHolderCinema holder;
	// LayoutInflater mInflater = LayoutInflater.from(context);
	// if (convertView ==
	// null||convertView.getTag().getClass()!=ViewHolderCinema.class)
	// {
	// convertView = mInflater.inflate(R.layout.object_list_item_cinema, null);
	// holder = new ViewHolderCinema();
	// holder.name1 = (TextView) convertView.findViewById(R.id.name1);
	// holder.switcher = (ViewSwitcher) convertView.findViewById(R.id.switcher);
	// holder.cancelButton = (Button)
	// convertView.findViewById(R.id.cancelButton);
	// holder.okButton = (Button) convertView.findViewById(R.id.okButton);
	// convertView.setMinimumHeight(60);
	// convertView.setTag(holder);
	// }
	// else
	// {
	// holder = (ViewHolderCinema) convertView.getTag();
	// }
	//
	// String st=Integer.toString(-position)+". "+toString();
	// if (MainApplication.getCurrentLocation()!=null)
	// st+="   "+Float.toString(distanceTo(MainApplication.getCurrentLocation()));
	// holder.name1.setText(st);
	//
	// holder.name1.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// holder.switcher.showNext();
	// }
	// });
	//
	// holder.cancelButton.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// holder.switcher.showPrevious();
	// }
	// });
	//
	// return convertView;
	// }

	public static class ViewHolderCinema
	{
		public TextView name1;
		public ViewSwitcher switcher;
		public Button okButton;
		public Button cancelButton;
	}
	
	@Override
	public int getItemColor()
	{
		return context.getResources().getColor(R.color.cinema);
	}

}
