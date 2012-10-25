package kz.crystalspring.funpoint.venues;

import java.io.Serializable;

import kz.crystalspring.pointplus.ImageCache;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class UrlDrawable implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -703381267268292002L;// generated
																		// serialable
																		// UID
	public static final int BIG_URL = 1;
	public static final int SMALL_URL = 2;

	public String bigUrl = "";
	public String smallUrl = "";
	private Drawable bigDrawable = null;
	private Drawable smallDrawable = null;

	public UrlDrawable(String smallUrl2, String bigUrl2)
	{
		this.smallUrl = smallUrl2;
		this.bigUrl = bigUrl2;
	}

	public UrlDrawable()
	{
	}

	public synchronized Drawable getBigDrawable()
	{
			return bigDrawable;
	}

	public synchronized void setBigDrawable(Drawable bigDrawable)
	{
		this.bigDrawable = bigDrawable;
	}

	public synchronized Drawable getSmallDrawable()
	{
		if (smallDrawable == null && getBigDrawable() != null)
			return getBigDrawable();
		else
			return smallDrawable;
	}

	public synchronized void setSmallDrawable(Drawable smallDrawable)
	{
		this.smallDrawable = smallDrawable;
	}
}
