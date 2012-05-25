package kz.crystalspring.funpoint.venues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;
import kz.sbeyer.atmpoint1.types.ItemHotel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
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
	private static final String CHECK_IN_URL = "https://api.foursquare.com/v2/checkins/add";
	private static final String TAG = "FoursquareApi";
	private static final String API_VERSION = "&v=20120522";

	public static ArrayList<MapItem> loadItems(GeoPoint point, String category)
	{
		if (point != null)
		{
			try
			{
				return getNearby(point.getLatitudeE6() / 1e6,
						point.getLongitudeE6() / 1e6, category);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ArrayList();
			}
		} else
			return new ArrayList();
	}

	public static ArrayList<MapItem> getNearby(double latitude,
			double longitude, String category) throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();

		try
		{
			String ll = String.valueOf(latitude) + ","
					+ String.valueOf(longitude);
			String sUrl = API_URL + "/venues/search?ll=" + ll;

			if (category != null)
				sUrl += "&categoryId=" + category;

			sUrl += "&client_id=" + CLIENT_ID + "&client_secret="
					+ CLIENT_SECRET + API_VERSION;

			String response = loadByUrl(sUrl);
			JSONObject jsonObj = new JSONObject(response);// (JSONObject) new
															// JSONTokener(response).nextValue();

			JSONArray items = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONArray("venues");

			int length = items.length();

			if (length > 0)
			{
				for (int i = 0; i < length; i++)
				{
					JSONObject item = (JSONObject) items.get(i);

					FSQItem venue;
					if (category.equals(MapItem.FSQ_TYPE_FOOD))
						venue = new ItemFood();
					else if (category.equals(MapItem.FSQ_TYPE_HOTEL))
						venue = new ItemHotel();
					else if (category.equals(MapItem.FSQ_TYPE_CINEMA))
						venue = new ItemCinema();
					else
						venue = new FSQItem();
					venue.loadFromJSON(item);
					venue.setCategory(category);
					if (venue != null)
						venueList.add(venue);
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

	public static JSONObject getVenueInformation(String id)
	{
		String sUrl = API_URL + "/venues/" + id;

		sUrl += "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
				+ API_VERSION;

		String response = loadByUrl(sUrl);
		JSONObject jsonObj;
		try
		{
			jsonObj = (JSONObject) new JSONObject(response);
			return jsonObj.getJSONObject("response").getJSONObject("venue");
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	private static String loadByUrl(String sUrl)
	{

		try
		{
			URL url = new URL(sUrl);
			Log.d(TAG, "Opening URL " + url.toString());

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			urlConnection.connect();
			return streamToString(urlConnection.getInputStream());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void checkIn(String venueID)
	{
		String st="";
		try
		{
			String sUrl = CHECK_IN_URL + "?oauth_token="
					+ MainApplication.FsqApp.getAccesToken();
			URL url = new URL(sUrl);
			Log.d(TAG, "Opening URL " + url.toString());

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(sUrl);
			List pairs = new ArrayList();
			pairs.add(new BasicNameValuePair("venueId", venueID));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post);
			st = streamToString(response.getEntity().getContent());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(st);
	}
}
