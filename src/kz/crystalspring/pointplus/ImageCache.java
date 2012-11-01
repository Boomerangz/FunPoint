package kz.crystalspring.pointplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

public class ImageCache
{
	Context context;
	static ImageCache singletone;
	final File directory;

	final int BUFFER_SIZE = 40;
	Map<String, Bitmap> mapBit;
	LinkedList<String> listBit;
	SharedPreferences mPrefs;

	public ImageCache(Context context)
	{
		this.context = context;
		singletone = this;
		File nDirectory = new File(Environment.getExternalStorageDirectory(), "jamkz/");

		nDirectory.mkdirs();
		if (!nDirectory.exists())
		{
			directory = context.getFilesDir();
		} else
		{
			directory = nDirectory;
		}
		mapBit = new HashMap<String, Bitmap>(BUFFER_SIZE);
		listBit = new LinkedList<String>();
		mPrefs = context.getSharedPreferences("title_photos", Context.MODE_PRIVATE);
	}

	public static ImageCache getInstance()
	{
		return singletone;
	}

	public boolean hasImage(String sUrl)
	{
		if (sUrl != null)
		{
			File imageFile = new File(fileName(sUrl));
			return imageFile.exists();
		}
		return false;
	}

	public synchronized void addToCache(String sUrl, Bitmap bitmap)
	{
		if (sUrl != null)
		{
			File imageFile = new File(fileName(sUrl));
			if (imageFile.exists())
				imageFile.delete();
			if (!imageFile.exists() && bitmap != null)
			{
				try
				{
					imageFile.createNewFile();
					FileOutputStream stream = new FileOutputStream(imageFile);
					if (sUrl.contains(".png"))
						bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
					else
						bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
					stream.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private String fileName(String sUrl)
	{
		String imageName = Integer.toString(sUrl.hashCode());
		String fullName = directory + "/" + imageName + ".jpg";
		return fullName;
	}

	public synchronized Bitmap getImage(String sUrl)
	{
		if (mapBit.containsKey(sUrl))
		{
			Log.w("ImageCache", "load from RAM");
			Bitmap bit = mapBit.get(sUrl);
			return bit;
		} else
		{
			Log.w("ImageCache", "load from sd");
			File imageFile = new File(fileName(sUrl));
			if (imageFile.exists() && imageFile.canRead())
			{
				String fName = imageFile.getAbsolutePath();
				int i = 0;
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeFile(fName);
				pushBitmapToBuffer(sUrl, bitmap);
				return bitmap;
			} else
				return null;
		}
	}

	private void pushBitmapToBuffer(String sUrl, Bitmap bitmap)
	{
		Log.w("ImageCache", "Added to buffer");
		if (listBit.contains(sUrl))
			listBit.remove(sUrl);
		listBit.add(0, sUrl);
		mapBit.put(sUrl, bitmap);
		if (listBit.size() > BUFFER_SIZE)
		{
			String st = listBit.get(BUFFER_SIZE);
			// mapBit.get(st).
			mapBit.remove(st);
			listBit.remove(st);
			Log.w("ImageCache", "Removed from buffer");
		}
		Log.w("ImageCache", "Buffer size now=" + Integer.toString(listBit.size()));
		System.gc();
	}

	public byte[] getBytes(String sUrl)
	{
		return null;
	}

	public String getTitlePhotoUrlIfHave(String fsqId)
	{
		String str = mPrefs.getString(fsqId, null);
		if (str != null)
		{
			JSONObject jObject;
			try
			{
				jObject = new JSONObject(str);
				if (jObject.getLong("date") > getRandomExpireDate())//случайная дата "протухания" кэша. от 3 до 6 дней
					return jObject.getString("url");
				else
					return null;
			} catch (JSONException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	private long getRandomExpireDate()
	{
		final int MAX_EXPIRE_DAYS = 6;
		final int MIN_EXPIRE_DAYS = 3;
		int days = (int) Math.round(Math.random() * (MAX_EXPIRE_DAYS - MIN_EXPIRE_DAYS) + MIN_EXPIRE_DAYS);
		long expireTime = days * 24 * 60 * 60 * 1000;
		long expirationDate = new Date().getTime() - expireTime;
		return expirationDate;
	}

	public void addPhotoUrl(String key, String url)
	{
		Editor editor = mPrefs.edit();
		JSONObject jObject = new JSONObject();
		try
		{
			jObject.put("url", url);
			jObject.put("date", Long.toString(new Date().getTime()));
			editor.putString(key, jObject.toString());
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		editor.commit();
	}
}
