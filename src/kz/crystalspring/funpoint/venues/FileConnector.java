package kz.crystalspring.funpoint.venues;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.ProjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thebuzzmedia.sjxp.XMLParser;
import com.thebuzzmedia.sjxp.rule.DefaultRule;
import com.thebuzzmedia.sjxp.rule.IRule.Type;

import android.content.Context;
import android.util.Xml;
import android.widget.Toast;
import java.io.InputStream;

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
	public static void getCinemaTimeTables()
	{
		System.out.println("Началась загрузка");
		String s=ProjectUtils.loadByUrl(JAM_CINEMA_URL);
		System.out.println("Началось преобразование");
		JSONObject jObject=XML2JSON(s);
		System.out.println("Преобразование закончилось");
	}

	public static JSONObject XML2JSON(String xml)
	{
		org.json.JSONObject xmlJSONObj = null;
		try
		{
			xmlJSONObj = org.json.XML.toJSONObject(xml);
			String jsonPrettyPrintString = xmlJSONObj.toString();
			JSONObject jObject=new JSONObject(jsonPrettyPrintString);
			return jObject;
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
