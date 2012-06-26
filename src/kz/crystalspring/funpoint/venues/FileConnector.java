package kz.crystalspring.funpoint.venues;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.sbeyer.atmpoint1.types.ItemCinema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boomerang.database.JamDbAdapter;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class FileConnector
{
	private static final String FOOD_FILE = "json_rest_1_ru_fsq_zip";
	private static final String CINEMA_FILE = "";

	private static final String JAM_CINEMA_URL = "http://www.jamaica.jam.kz/jam_export.php?psw=2009ura";

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

	
	static JSONArray jCinemaCitys;
	static JSONArray jCinemaEvents;
	static JSONArray jCinemaPlaces;
	static JSONArray jCinemaSection;
	public static void loadCinemaTimeTables()
	{
		Runnable task=new Runnable()
		{
			@Override
			public void run()
			{
				String cinemaXML = ProjectUtils.loadByUrl(JAM_CINEMA_URL);
				System.out.println("ЗАГРУЗКА ЗАВЕРШЕНА");
				//Toast.makeText(context, "ЗАГРУЗКА ЗАВЕРШЕНА", Toast.LENGTH_SHORT).show();
				try
				{
					JSONObject jObject = ProjectUtils.XML2JSON(cinemaXML).getJSONObject("schedule");
					jCinemaCitys   = jObject.getJSONArray("city");
					jCinemaEvents  = jObject.getJSONArray("event");
					jCinemaPlaces  = jObject.getJSONArray("place");
					jCinemaSection = jObject.getJSONArray("section");
					
					AsyncTask task=new AsyncTask(){
						@Override
						protected Object doInBackground(Object... params)
						{
							JamDbAdapter dbAdapter=new JamDbAdapter(MainApplication.context);
							dbAdapter.open();
							dbAdapter.savePlacesToDB(jCinemaPlaces);
							dbAdapter.saveEventsToDB(jCinemaEvents);
							dbAdapter.saveSectionsToDB(jCinemaSection);
							dbAdapter.close();
							return null;
						}
					};
					task.execute();
					System.gc();
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, null);
	}

}
