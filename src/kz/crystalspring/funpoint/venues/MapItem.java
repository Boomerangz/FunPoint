package kz.crystalspring.funpoint.venues;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.pointplus.R;

import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;

public abstract class MapItem
{
	String id;
	float longitude;
	float latitude;
	int valid;

	public static final int TYPE_HOTEL = 2;
	public static final int TYPE_FOOD = 3;
	public static final int TYPE_CINEMA = 4;
	public static final int TYPE_MARKET = 5;
	
	public static final String FSQ_TYPE_CINEMA="4bf58dd8d48988d17f941735";
	public static final String FSQ_TYPE_HOTEL="4bf58dd8d48988d1fa931735";
	public static final String FSQ_TYPE_FOOD = "4d4b7105d754a06374d81259";//"4bf58dd8d48988d145941735";"4d4b7105d754a06374d81259;
	public static final String FSQ_TYPE_MARKET = "4d4b7105d754a06378d81259";
	
	public static final String[] TYPES_ARRAY={FSQ_TYPE_CINEMA,FSQ_TYPE_HOTEL,FSQ_TYPE_FOOD,FSQ_TYPE_MARKET};
	
	
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
			icon.setBounds(-icon.getIntrinsicWidth()/2, -icon.getIntrinsicHeight(), icon.getIntrinsicWidth() /2, 0);

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

	public static void sortMapItemList(List<MapItem> mapItemList, final GeoPoint point)
	{
		Collections.sort(mapItemList, new Comparator<MapItem>()
		{
			@Override
			public int compare(MapItem lhs, MapItem rhs)
			{
				if (lhs.distanceTo(point)>rhs.distanceTo(point))
				{
					return 1;
				}
				else if (lhs.distanceTo(point)<rhs.distanceTo(point))
				{
					return -1;
				}
				else 
					return 0;
			}});
	}
	
	@Override 
	public boolean equals(Object o)
	{
		return ((MapItem)o).id.equals(this.id);
	}
	
	
	public void loadInfoFromFile()
	{
		
	}
	
}
