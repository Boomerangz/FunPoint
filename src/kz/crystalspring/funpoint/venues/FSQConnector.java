package kz.crystalspring.funpoint.venues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class FSQConnector
{
	public static final String CLIENT_ID = "ATCDKP1BI3F1YDPOHVOWI2UCEXIUFWGPR0GF3DOVSLJFRFBM";
	public static final String CLIENT_SECRET = "YADGMVO5M5QJTZXXIDEIIDOYTRS5KLI5QHUQKB5DZ22ADROO";
	private static final String API_URL = "https://api.foursquare.com/v2";
	private static final String TAG = "FoursquareApi";

	public static ArrayList<MapItem> loadItems(GeoPoint point, String category)
	{
		if (point != null)
		{
			try
			{
				return getNearby(point.getLatitudeE6() / 1e6,
						point.getLongitudeE6() / 1e6,category);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ArrayList();
			}
		}
		else return new ArrayList();
	}

	public static ArrayList<MapItem> getNearby(double latitude, double longitude, String category)
			throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();

		try
		{
			String ll = String.valueOf(latitude) + ","
					+ String.valueOf(longitude);
			String sUrl = API_URL + "/venues/search?ll=" + ll;
			
			if (category!=null)
				sUrl+="&categoryId="+category;
			
			sUrl += "&client_id=" + CLIENT_ID + "&client_secret="
					+ CLIENT_SECRET;
			URL url = new URL(sUrl);

			Log.d(TAG, "Opening URL " + url.toString());

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			urlConnection.connect();

			String response = streamToString(urlConnection.getInputStream());
			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			JSONArray groups = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONArray("groups");

			int length = groups.length();

			if (length > 0)
			{
				for (int i = 0; i < length; i++)
				{
					JSONObject group = (JSONObject) groups.get(i);
					JSONArray items = (JSONArray) group.getJSONArray("items");

					int ilength = items.length();

					for (int j = 0; j < ilength; j++)
					{
						JSONObject item = (JSONObject) items.get(j);

						FSQItem venue = new FSQItem();
						venue.loadFromJSON(item);
						venue.setCategory(category);
						if (venue != null)
							venueList.add(venue);
					}
				}
			}
		} catch (Exception ex)
		{
			throw ex;
		}

		return venueList;
	}

	private static String streamToString(InputStream is) throws IOException
	{
		String str = "";

		if (is != null)
		{
			StringBuilder sb = new StringBuilder();
			String line;

			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null)
				{
					sb.append(line);
				}

				reader.close();
			} finally
			{
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}
}
