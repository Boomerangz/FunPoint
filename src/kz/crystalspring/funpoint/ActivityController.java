package kz.crystalspring.funpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.animation.ProjectAnimation;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ActivityController
{
	Activity context;

	ActivityController(Activity _context)
	{
		context = _context;
	}

	protected abstract void onCreate();
	
	protected abstract void onResume();

	protected abstract void onPause();

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// if (buttonsShowed)
			// {
			// buttonsShowed = false;
			// ProjectAnimation.hideLeft(btnFeedButtons);
			// objDetAllInfo.startAnimation(fadeInAnimationRight);
			// return true;
			// } else
			// {
			MainMenu.tabHost.setCurrentTab(Integer.valueOf(Prefs
					.getInitTab(context.getApplicationContext())));
			return true;
			// }
		}
		return context.onKeyDown(keyCode, event);
	}

	private JSONObject getJSONObjFromText(int wantedID,String JSONString)
	{
		JSONObject returnObject = null;
		try
		{
			JSONArray entries = new JSONArray(JSONString);
			try
			{
				int currID = -1;
				int i = 0;
				JSONObject jObject = null;
				while (currID != wantedID && i < entries.length())
				{
					jObject = entries.getJSONObject(i);
					currID = jObject.getInt("id");
					i++;
				}
				if (currID == wantedID)
				{
					returnObject = jObject;
				} else
					returnObject = null;
			} catch (Exception e)
			{
				Toast.makeText(context.getApplicationContext(),
						"ошибка парсинга", Toast.LENGTH_SHORT).show();
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

	protected JSONObject findObjectInJSON(int wantedID, String fileName)
	{
		try
		{
			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			return getJSONObjFromText(wantedID,text);
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	protected JSONObject findObjectInJSONZIP(int wantedID, String fileName)
	{
		try
		{
			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			byte[] vIconBytesUnzipped = C_FileHelper.decompress(vIconBytes);
			String text = new String(vIconBytesUnzipped, "UTF-8");
			return getJSONObjFromText(wantedID,text);
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
}