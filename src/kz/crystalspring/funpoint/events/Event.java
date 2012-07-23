package kz.crystalspring.funpoint.events;
import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.funEventActivity;
import kz.crystalspring.funpoint.funObjectDetail;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.ListItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItem.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
public class Event implements ListItem
{
	private static final String COLUMN_NAME="title";
	private static final String COLUMN_DESC="description";
	private static final String COLUMN_IMGURL="image";
	private static final Context context=MainApplication.context;
	
	
	private String name;
	private String description;
	private String imageUrl;
	private Drawable image;
	private String place_type;
	int id;
	
	CinemaTimeTable table=null;
	
	
	Event(int id,String name, String description, String imageUrl)
	{
		this.id=id;
		setName(name);
		setDescription(description);
		setImageUrl(imageUrl);
	}
	
	Event ()
	{
	}
	
	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	Event (Cursor cursor) throws Exception
	{
		String[] columnNames=cursor.getColumnNames();
		String nm=null;
		String dsc=null;
		String imgurl=null;
		int i=0;
		for (String columName:columnNames)
		{
			if (columName.trim().toUpperCase().equals(COLUMN_NAME.toUpperCase().trim()))
			{
				nm=cursor.getString(i);
			}
			if (columName.trim().toUpperCase().equals(COLUMN_DESC.toUpperCase().trim()))
			{
				dsc=cursor.getString(i);
			}
			if (columName.trim().toUpperCase().equals(COLUMN_IMGURL.toUpperCase().trim()))
			{
				imgurl=cursor.getString(i);
			}
			i++;
		}
		if (nm!=null&&dsc!=null&&imgurl!=null)
		{
			setName(nm);
			setDescription(dsc);
			setImageUrl(imgurl);
		}
		else 
		{
			throw new Exception("Name or Description or Image URI not found in cursor");
		}
	}

	public Event(JSONObject jEvent)
	{
		try
		{
			id=jEvent.getInt("events_id");
			setName(jEvent.getString("title"));
			setDescription(jEvent.getString("description"));
			setImageUrl(jEvent.getString("img_url"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Drawable getImage()
	{
		return image;
	}

	public void setImage(Drawable image)
	{
		this.image = image;
	}

	public int getId()
	{
		return id;
	}
	
	public int getItemColor()
	{
		return context.getResources().getColor(R.color.cinema);
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
		holder.itemColorView = (View) convertView.findViewById(R.id.item_color_view);

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
				MainApplication.selectedEventId=Integer.toString(id);
				Intent intent=new Intent(context,funEventActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		
		holder.itemColorView.setBackgroundColor(getItemColor());
		
		return convertView;
	}

	@Override
	public String getShortCharacteristic()
	{
		return getName();
	}
	
	
	public static class ViewHolder
	{
		public TextView name;
		public TextView shortDescription;
		public ImageView goIntoButton;
		public View background;
		public View itemColorView;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (Event.class.isInstance(o)&&this.getId()==((Event)o).getId())
			return true;
		else return false;
	}
	
	public void loadPlaceTable()
	{
		if (table==null)
		{
			table=new CinemaTimeTable();
			table.loadFromCinemaJSONArray(FileConnector.loadJSONPlaceList(Integer.toString(getId())));
		}	
	}

	public CinemaTimeTable getTimeTable()
	{
		return table;
	}

}
