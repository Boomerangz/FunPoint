package kz.crystalspring.funpoint;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class CityManager
{
	Context context;
	List<City> cityList;
	Map<Integer, City> cityMap;
	SharedPreferences prefs;

	CityManager(Context context)
	{
		this.context = context;
		cityList = new ArrayList<City>();
		cityMap = new HashMap<Integer, City>();
		try
		{
			File file = new File(context.getFilesDir() + "/city_list.json");
			String st;
			if (file.exists())
			{
				byte[] bytes = C_FileHelper.ReadFile(file);
				st = new String(bytes);
			} else
			{
				InputStream is = context.getAssets().open("city_list.json");
				st = HttpHelper.getInstance().streamToString(is);
			}

			JSONArray jCityList = new JSONArray(st);
			for (int i = 0; i < jCityList.length(); i++)
			{
				JSONObject jObject = jCityList.getJSONObject(i);
				City city = new City(jObject);
				cityList.add(city);
				cityMap.put(city.getId(), city);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public List<City> getCityList()
	{
		return cityList;
	}

	private final static String SELECTED_CITY = "selected_city";

	public void selectCity(City city)
	{
		Editor editor = prefs.edit();
		String st;
		if (city != null)
			st = city.getId().toString();
		else
			st = "null";
		editor.putString(SELECTED_CITY, st);
		editor.commit();
	}

	public City getSelectedCity()
	{
		if (prefs.contains(SELECTED_CITY))
		{
			String s = prefs.getString(SELECTED_CITY, null);
			if (s.equals("null"))
				return null;
			else
			{
				try
				{
					Integer cityId = Integer.parseInt(s);
					return getCityById(cityId);
				} catch (Exception e)
				{
					return null;
				}
			}
		} else
			return null;
	}

	private City getCityById(Integer cityId)
	{
		return cityMap.get(cityId);
	}

	public City getSelectedCityIfnull()
	{
		return (City) ProjectUtils.ifnull(getSelectedCity(), cityMap.get(1));
	}

}
