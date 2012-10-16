package kz.crystalspring.funpoint.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import kz.crystalspring.funpoint.CinemaTimeTable;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.funEventActivity;
import kz.crystalspring.funpoint.funObjectDetail;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.ListItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItem.ViewHolder;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.views.LoadingImageView;

import org.json.JSONArray;
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

public abstract class Event implements ListItem
{
	private static final String COLUMN_NAME = "title";
	private static final String COLUMN_DESC = "description";
	private static final String COLUMN_IMGURL = "image";
	static final Context context = MainApplication.context;
	protected static Map<Integer, String> JamCategoryMap;
	protected static Map<Integer, String> JamGenresMap;

	protected static final DateFormat date_formatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	protected static final DateFormat datetime_formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH-mm");
	public static final DateFormat time_formatter = new SimpleDateFormat(
			"HH-mm");

	private String name;
	private String description;
	private UrlDrawable imageUrl;
	private Drawable image;
	private String place_type;
	private Integer eventType;
	int id;

	public Event(JSONObject jEvent)
	{
		initCategoryMapIfNot();
		try
		{
			id = jEvent.getInt("events_id");
			setName(jEvent.getString("title"));
			setDescription(jEvent.getString("description"));
			setImageUrl(jEvent.getString("img_url"));
			setEventType(jEvent.getInt("id_route"));
			setPlace_type(getCatEquiv(getEventType()));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	Event(int id, String name, String description, String imageUrl,
			Integer rubrId)
	{
		initCategoryMapIfNot();

		this.id = id;
		setName(name);
		setDescription(description);
		setImageUrl(imageUrl);
		setPlace_type(getCatEquiv((rubrId)));
	}

	private void initCategoryMapIfNot()
	{
		if (JamCategoryMap == null)
		{
			JamCategoryMap = new HashMap<Integer, String>();
			JamCategoryMap.put(2, MapItem.FSQ_TYPE_CINEMA);
			JamCategoryMap.put(3, MapItem.FSQ_TYPE_FOOD);
			JamCategoryMap.put(4, MapItem.FSQ_TYPE_CLUB);
		}
		if (JamGenresMap == null)
		{
			JamGenresMap = new HashMap<Integer, String>();
			JamGenresMap.put(2, "Кино");
			JamGenresMap.put(3, "Концерт");
			JamGenresMap.put(4, "Вечеринка");
			JamGenresMap.put(5, "Спектакль");
			JamGenresMap.put(7, "Выставка");
		}
	}

	Event()
	{
	}

	public UrlDrawable getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		UrlDrawable urlDr = new UrlDrawable(null, imageUrl);
		this.imageUrl = urlDr;
	}

	Event(Cursor cursor) throws Exception
	{
		String[] columnNames = cursor.getColumnNames();
		String nm = null;
		String dsc = null;
		String imgurl = null;
		int i = 0;
		for (String columName : columnNames)
		{
			if (columName.trim().toUpperCase()
					.equals(COLUMN_NAME.toUpperCase().trim()))
			{
				nm = cursor.getString(i);
			}
			if (columName.trim().toUpperCase()
					.equals(COLUMN_DESC.toUpperCase().trim()))
			{
				dsc = cursor.getString(i);
			}
			if (columName.trim().toUpperCase()
					.equals(COLUMN_IMGURL.toUpperCase().trim()))
			{
				imgurl = cursor.getString(i);
			}
			i++;
		}
		if (nm != null && dsc != null && imgurl != null)
		{
			setName(nm);
			setDescription(dsc);
			setImageUrl(imgurl);
		} else
		{
			throw new Exception(
					"Name or Description or Image URI not found in cursor");
		}
	}

	private String getCatEquiv(int int1)
	{
		return JamCategoryMap.get(Integer.valueOf(int1));
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

	public String getPlace_type()
	{
		return place_type;
	}

	public void setPlace_type(String place_type)
	{
		this.place_type = place_type;
	}

	public Integer getEventType()
	{
		return eventType;
	}

	public void setEventType(Integer eventType)
	{
		this.eventType = eventType;
	}

	@Override
	public View getView(View convertView, int position)
	{
		convertView = null;
		ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(context);
		if (convertView==null)
			convertView = mInflater.inflate(R.layout.event_list_item, null);
		holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.event_name);
		holder.shortDescription = (TextView) convertView
				.findViewById(R.id.short_description);
		holder.background = (View) convertView.findViewById(R.id.list_block);
		holder.loadingImage = (LoadingImageView) convertView
				.findViewById(R.id.loading_imageview);
		
		convertView.setMinimumHeight(80);
		convertView.setTag(holder);
		String st = getName();

		holder.name.setText(st);
		holder.shortDescription.setText(getShortCharacteristic());
		holder.background.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainApplication.selectedEventId = Integer.toString(id);
				Intent intent = new Intent(context, funEventActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		if (getImageUrl().getSmallDrawable() != null)
			holder.loadingImage.setDrawable(getImageUrl().getSmallDrawable());
		else
		{
			holder.loadingImage.setDrawable(null);
			FSQConnector.loadImageAsync(holder.loadingImage, getImageUrl(),
					UrlDrawable.SMALL_URL, false, null);
		}
		System.gc();
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
		public LoadingImageView loadingImage;
		public View background;
	}

	@Override
	public boolean equals(Object o)
	{
		if (Event.class.isInstance(o) && this.getId() == ((Event) o).getId())
			return true;
		else
			return false;
	}

}
