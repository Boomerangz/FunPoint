package kz.crystalspring.funpoint.venues;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.funObjectDetail;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.funpoint.R;

import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class MapItem implements Serializable
{
	String id;
	float longitude;
	float latitude;
	int valid;

	public static final int TYPE_HOTEL = 2;
	public static final int TYPE_FOOD = 3;
	public static final int TYPE_CINEMA = 4;
	public static final int TYPE_MARKET = 5;

	public static final String FSQ_TYPE_CINEMA = "4bf58dd8d48988d17f941735";
	public static final String FSQ_TYPE_CLUB =   "4bf58dd8d48988d11f941735";
	public static final String FSQ_TYPE_HOTEL =  FSQ_TYPE_CLUB;//"4bf58dd8d48988d1fa931735";
	public static final String FSQ_TYPE_FOOD =   "4d4b7105d754a06374d81259";// "4bf58dd8d48988d145941735";"4d4b7105d754a06374d81259;
	public static final String FSQ_TYPE_MARKET = "4d4b7105d754a06378d81259";

	
	

	public static final String[] TYPES_ARRAY = { FSQ_TYPE_CINEMA,
			FSQ_TYPE_HOTEL, FSQ_TYPE_FOOD, FSQ_TYPE_MARKET };

	public static Context context;
	private static Drawable icon;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public float getLongitude()
	{
		return longitude;
	}

	public int getLongitudeE6()
	{
		return (int) Math.round(getLongitude() * 1e6);
	}

	public void setLongitude(float longitude)
	{
		this.longitude = longitude;
	}

	public float getLatitude()
	{
		return latitude;
	}

	public int getLatitudeE6()
	{
		return (int) Math.round(getLatitude() * 1e6);
	}

	public void setLatitude(float latitude)
	{
		this.latitude = latitude;
	}

	public void setIsValid(int i)
	{
		valid = i;
	}

	public int getIsValid()
	{
		return valid;
	}

	public int getRating()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract String getObjTypeId();

	public abstract MapItem loadFromJSON(JSONObject jObject);

	@Override
	public String toString()
	{
		return "ID " + getId();
	}

	public abstract String getIconName();

	public Drawable getIcon()
	{

		int id = getImgId(getIconName());
		Drawable icon = context.getResources().getDrawable(id);
		icon.setBounds(-icon.getIntrinsicWidth() / 2,
				-icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);

		return icon;
	}

	public Bitmap getIconBM()
	{
		int id = getImgId(getIconName());
		return BitmapFactory.decodeResource(context.getResources(), id);
	}

	public String toStringLong()
	{
		// TODO Auto-generated method stub
		return "";
	}

	public GeoPoint getGeoPoint()
	{
		GeoPoint point = new GeoPoint(getLatitudeE6(), getLongitudeE6());
		return point;
	}

	public static int getImgId(String pImgSrcStr)
	{
		if (pImgSrcStr.equalsIgnoreCase("m_1"))
		{
			return R.drawable.m_1;
		} else if (pImgSrcStr.equalsIgnoreCase("m_13"))
		{
			return R.drawable.m_13;
		} else if (pImgSrcStr.equalsIgnoreCase("m_16"))
		{
			return R.drawable.m_16;
		} else if (pImgSrcStr.equalsIgnoreCase("m_2"))
		{
			return R.drawable.m_2;
		} else if (pImgSrcStr.equalsIgnoreCase("m_21"))
		{
			return R.drawable.m_21;
		} else if (pImgSrcStr.equalsIgnoreCase("m_22"))
		{
			return R.drawable.m_22;
		} else if (pImgSrcStr.equalsIgnoreCase("m_26"))
		{
			return R.drawable.m_26;
		} else if (pImgSrcStr.equalsIgnoreCase("m_27"))
		{
			return R.drawable.m_27;
		} else if (pImgSrcStr.equalsIgnoreCase("m_29"))
		{
			return R.drawable.m_29;
		} else if (pImgSrcStr.equalsIgnoreCase("m_3"))
		{
			return R.drawable.m_3;
		} else if (pImgSrcStr.equalsIgnoreCase("m_35"))
		{
			return R.drawable.m_35;
		} else if (pImgSrcStr.equalsIgnoreCase("m_37"))
		{
			return R.drawable.m_37;
		} else if (pImgSrcStr.equalsIgnoreCase("m_4"))
		{
			return R.drawable.m_4;
		} else if (pImgSrcStr.equalsIgnoreCase("m_43"))
		{
			return R.drawable.m_43;
		} else if (pImgSrcStr.equalsIgnoreCase("m_44"))
		{
			return R.drawable.m_44;
		} else if (pImgSrcStr.equalsIgnoreCase("m_48"))
		{
			return R.drawable.m_48;
		} else if (pImgSrcStr.equalsIgnoreCase("m_5"))
		{
			return R.drawable.m_5;
		} else if (pImgSrcStr.equalsIgnoreCase("m_8"))
		{
			return R.drawable.m_8;
		} else if (pImgSrcStr.equalsIgnoreCase("cinema"))
		{
			return R.drawable.m_notarius;
		} else if (pImgSrcStr.equalsIgnoreCase("exchange"))
		{
			return R.drawable.m_exchange;
		} else
		{
			return 0;
		}

	}

	public float distanceTo(GeoPoint gp)
	{
		return ProjectUtils.distance(getLatitude(), getLongitude(),
				(float) (gp.getLatitudeE6() / 1e6),
				(float) (gp.getLongitudeE6() / 1e6));
	}

	public static void sortMapItemList(List<MapItem> mapItemList,
			final GeoPoint point)
	{
		Collections.sort(mapItemList, new Comparator<MapItem>()
		{
			@Override
			public int compare(MapItem lhs, MapItem rhs)
			{
				if (lhs.distanceTo(point) > rhs.distanceTo(point))
				{
					return 1;
				} else if (lhs.distanceTo(point) < rhs.distanceTo(point))
				{
					return -1;
				} else
					return 0;
			}
		});
	}

	@Override
	public boolean equals(Object o)
	{
		return ((MapItem) o).id.equals(this.id);
	}

	public void loadInfoFromFile()
	{

	}

	public View getView(View convertView, int position)
	{
		convertView = null;
		ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.object_list_item, null);
		holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.name);
		holder.range = (TextView) convertView.findViewById(R.id.range);
		holder.shortDescription = (TextView) convertView
				.findViewById(R.id.short_description);
		holder.goIntoButton = (ImageView) convertView
				.findViewById(R.id.go_into_btn);
		holder.background = (View) convertView.findViewById(R.id.list_block);
		holder.itemColorView = (View) convertView.findViewById(R.id.item_color_view);

		convertView.setMinimumHeight(80);
		convertView.setTag(holder);
		String st = Integer.toString(position) + ". " + toString();

		holder.name.setText(st);
		if (MainApplication.getCurrentLocation() != null)
			st = Integer.toString(Math.round(distanceTo(MainApplication
					.getCurrentLocation()))) + " м";
		else
			st = "";
		holder.shortDescription.setText(getShortCharacteristic());
		//holder.background.getBackground().setAlpha(MainApplication.ALPHA);
		holder.range.setText(st);
		
		holder.background.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainApplication.mapItemContainer.setSelectedItem(MapItem.this);
				Intent intent=new Intent(context,funObjectDetail.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				//Toast.makeText(context, Integer.toString(arg2), Toast.LENGTH_SHORT).show();
			}
		});
		
		holder.itemColorView.setBackgroundColor(getItemColor());
		
		return convertView;
	}

	public static class ViewHolder
	{
		public TextView name;
		public TextView shortDescription;
		public TextView range;
		public ImageView goIntoButton;
		public View background;
		public View itemColorView;
	}

	public abstract String getShortCharacteristic();
	public abstract int getItemColor();
}
