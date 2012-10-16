package kz.crystalspring.pointplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kz.crystalspring.android_client.C_FileHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageCache
{
	Context context;
	static ImageCache singletone;

	public ImageCache(Context context)
	{
		this.context = context;
		singletone = this;
	}

	public static ImageCache getInstance()
	{
		return singletone;
	}

	public boolean hasImage(String url)
	{
		File directory = context.getFilesDir();
		String imageName = Integer.toString(url.hashCode());
		File imageFile = new File(directory + "/" + imageName + ".jpg");
		return imageFile.exists();
	}

	public synchronized void addToCache(String sUrl, Bitmap bitmap)
	{
		File directory = context.getFilesDir();
		String imageName = Integer.toString(sUrl.hashCode());
		File imageFile = new File(directory + "/" + imageName + ".jpg");
		if (imageFile.exists())
			imageFile.delete();
		if (!imageFile.exists())
		{
			try
			{
				imageFile.createNewFile();
				FileOutputStream stream = new FileOutputStream(imageFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				stream.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public synchronized Bitmap getImage(String sUrl)
	{
		File directory = context.getFilesDir();
		String imageName = Integer.toString(sUrl.hashCode());
		File imageFile = new File(directory + "/" + imageName + ".jpg");
		if (imageFile.exists() && imageFile.canRead())
		{
			String fName = imageFile.getAbsolutePath();
			Bitmap bitmap = BitmapFactory.decodeFile(fName);
			// System.out.println(bitmap.toString());
			return bitmap;
		} else
			return null;
	}

	public byte[] getBytes(String sUrl)
	{
		return null;
	}
}
