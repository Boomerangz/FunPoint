package kz.crystalspring.funpoint.venues;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.sbeyer.atmpoint1.types.ItemCinema;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FileConnector
{
	private static final String FOOD_FILE = "json_rest_1_ru_fsq_zip";

	private static final String JAM_CINEMA_URL = "http://www.jam.kz/api/foursquare/cinema";
	private static final String JAM_EVENT_URL = "http://www.jam.kz/api/foursquare/event";
	private static final String JAM_EVENTS_LIST_URL = "http://www.jam.kz/api/foursquare/event_all";
	private static final String JAM_PLACES_LIST_URL = "http://www.jam.kz/api/foursquare/cinema_all";
	private static final String JAM_KEY = "YADGMVO5M5QJTZXXIDEIIDOYTRS5KLI5QHUQKB5DZ22ADROO";
	private static final BasicNameValuePair key_pair = new BasicNameValuePair(
			"key", JAM_KEY);

	private static Context context;

	public FileConnector(Context context)
	{
		this.context = context;
	}

	public static String getFoodFileString()
	{
		return FOOD_FILE;
	}

	public static JSONObject getFoodInfoFromFile(String id)
	{
		return findObjectInJSONZIP(id, getFoodFileString());
	}

	public static JSONObject getJSONObjFromText(String wantedID,
			String JSONString)
	{
		JSONObject returnObject = null;
		try
		{
			JSONArray entries = new JSONArray(JSONString);
			try
			{
				String currID = "-1";
				int i = 0;
				JSONObject jObject = null;
				while (!currID.equals(wantedID) && i < entries.length())
				{
					jObject = entries.getJSONObject(i);
					currID = jObject.getString("fsqid");
					i++;
				}
				if (currID.equals(wantedID))
				{
					returnObject = jObject;
				} else
					returnObject = null;
			} catch (Exception e)
			{
				returnObject = null;
			}
		} catch (Exception je)
		{
			Toast.makeText(context.getApplicationContext(), "ошибка парсинга",
					Toast.LENGTH_SHORT).show();
			returnObject = null;
		}
		return returnObject;
	}

	public static JSONObject findObjectInJSON(String wantedID, String fileName)
	{
		try
		{
			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			return getJSONObjFromText(wantedID, text);
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject findObjectInJSONZIP(String wantedID,
			String fileName)
	{
		try
		{
			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			byte[] vIconBytesUnzipped = C_FileHelper.decompress(vIconBytes);
			String text = new String(vIconBytesUnzipped, "UTF-8");
			return getJSONObjFromText(wantedID, text);
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static HashSet<String> getJamCinemaList()
	{
		HashSet<String> set = new HashSet<String>();
		try
		{
			// InputStream fis=context.getAssets().open("cinema_list.json");
			byte[] vBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + "cinema_list.json"));
			String st = new String(vBytes);
			JSONArray jArray = new JSONArray(st);
			for (int i = 0; i < jArray.length(); i++)
			{
				JSONObject jObject = jArray.getJSONObject(i);
				String fsq_id = jObject.getString("fsq_id");
				set.add(fsq_id);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return set;
	}

	public static JSONObject loadCinemaInfo(String FsqId)
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("fsq_id", FsqId));
		params.add(key_pair);
		String sResponse = HttpHelper.loadPostByUrl(JAM_CINEMA_URL, params);
		try
		{
			return new JSONObject(sResponse);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject loadJSONEventById(String id)
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("events_id", id));
		params.add(key_pair);
		String sResponse = HttpHelper.loadPostByUrl(JAM_EVENT_URL, params);
		try
		{
			return new JSONObject(sResponse);
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray loadJSONCinemaEventsList()
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("rubr_id", "2"));
		params.add(key_pair);
		String sResponse = HttpHelper
				.loadPostByUrl(JAM_EVENTS_LIST_URL, params);
//		Log.w("cinema", "Длинна ответа -"
//				+ Long.valueOf(sResponse.length()).toString());
		try
		{
			return new JSONArray(sResponse);
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static final DateFormat date_formatter = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static JSONArray loadJSONEventsList()
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("rubr_id", "0"));
		params.add(key_pair);

		GregorianCalendar start = new GregorianCalendar();
		GregorianCalendar end = new GregorianCalendar();
		end.add(Calendar.DAY_OF_MONTH, 7);

		params.add(new BasicNameValuePair("date_start", date_formatter
				.format(start.getTime())));
		params.add(new BasicNameValuePair("date_end", date_formatter.format(end
				.getTime())));
		String sResponse = HttpHelper
				.loadPostByUrl(JAM_EVENTS_LIST_URL, params);
//		Log.w("cinema", "Длинна ответа -"
//				+ Long.valueOf(sResponse.length()).toString());
		try
		{
			return new JSONArray(sResponse);
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray loadJSONPlaceList(String event_id)
	{
		List<BasicNameValuePair> params = new ArrayList();
		params.add(new BasicNameValuePair("events_id", event_id));
		params.add(key_pair);
		String sResponse = HttpHelper
				.loadPostByUrl(JAM_PLACES_LIST_URL, params);
		// sResponse="{\"events_id\":\"10937\",\"title\":\"\u041f\u0440\u043e\u043c\u0435\u0442\u0435\u0439\",\"places\":[{\"fsq_id\":\"4ee9f98229c220d20e69e8f4\",\"name\":\"\u0418\u0441\u043a\u0440\u0430\",\"ts\":\"2012-06-25 09-40-00\"},{\"fsq_id\":\"4bd2e47b41b9ef3b4bd4fee5\",\"name\":\"Silk Way City\",\"ts\":\"2012-06-24 22-25-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"4dea39f2e4cdc079f47d37ca\",\"name\":\"Kinopark 8\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":\"50051c06e4b0fb593261df3b\",\"name\":\"\u041f\u0440\u043e\u043c\u0435\u043d\u0430\u0434 (\u0423)\",\"ts\":\"2012-06-25 11-00-00\"},{\"fsq_id\":\"50052341e4b03a9a758a7d92\",\"name\":\"Kinopark 5\",\"ts\":\"2012-06-25 11-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-25 11-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4d55fa2a9e508cfa0e71079b\",\"name\":\"Kinopark 7\",\"ts\":\"2012-06-24 22-20-00\"},{\"fsq_id\":\"4ccd62d054f0b1f74a5b1cca\",\"name\":\"KinoPark 6 \u0421\u043f\u0443\u0442\u043d\u0438\u043a\",\"ts\":\"2012-06-25 10-30-00\"},{\"fsq_id\":null,\"name\":\"Kinopark 7 \u0410\u043a\u0442\u043e\u0431\u0435\",\"ts\":\"2012-06-24 22-10-00\"},{\"fsq_id\":null,\"name\":\"Kinopark 7 \u0410\u043a\u0442\u043e\u0431\u0435\",\"ts\":\"2012-06-25 10-20-00\"},{\"fsq_id\":\"4da1af6fd686b60c62b0a928\",\"name\":\"Star Cinema (\u0425\u0430\u043d \u0428\u0430\u0442\u044b\u0440)\",\"ts\":\"2012-06-25 08-50-00\"},{\"fsq_id\":\"4de398fbfa7651589f19c030\",\"name\":\"KinoPlexx\",\"ts\":\"2012-06-25 11-20-00\"},{\"fsq_id\":\"4f487f46e4b0db1b351c85e5\",\"name\":\"Chaplin ADK\",\"ts\":\"2012-06-25 11-05-00\"},{\"fsq_id\":\"4f487f46e4b0db1b351c85e5\",\"name\":\"Chaplin ADK\",\"ts\":\"2012-06-25 08-30-00\"}]}";
		try
		{
			JSONObject jResponse = new JSONObject(sResponse);
			return jResponse.getJSONArray("places");
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
