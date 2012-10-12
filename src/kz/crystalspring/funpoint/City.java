package kz.crystalspring.funpoint;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class City
{
	private Integer id;
	private String trueName;
	private String rusName;
	private GeoPoint centralPoint;

	public City(JSONObject jCity) throws JSONException
	{
		id = jCity.getInt("city_id");
		rusName = jCity.getString("name");
		trueName = jCity.getString("name_eng");
		Double latitude = jCity.getDouble("city_lat");
		Double longitude = jCity.getDouble("city_lng");
		centralPoint = new GeoPoint((int) Math.round(latitude * 1e6),
				(int) Math.round(longitude * 1e6));
		// "city_lng":"43.280810","city_lat":"76.912335"
	}

	@Override
	public boolean equals(Object o)
	{
		if (City.class.isInstance(o) && ((City) o).getId().equals(getId()))
			return true;
		else
			return false;
	}

	public String getTrueName()
	{
		return trueName;
	}

	public String getRusName()
	{
		return rusName;
	}

	public Integer getId()
	{
		return id;
	}

	public GeoPoint getPoint()
	{
		return centralPoint;
	}

	@Override
	public String toString()
	{
		return getTrueName();
	}

}