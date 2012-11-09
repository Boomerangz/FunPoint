package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.ImageCache;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.LoadingImageView;
import kz.crystalspring.visualities.gallery.ImageContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class FSQItem extends MapItem implements ImageContainer
{
	String name;
	String address;
	String category = FSQ_UNDEFINED;
	int hereNow;
	List<String> categoryList = new ArrayList<String>();

	OptionalInfo optInfo;

	@Override
	public String getObjTypeId()
	{
		int i;
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	@Override
	public MapItem loadFromJSON(JSONObject jObject)
	{
		try
		{
			setName(jObject.getString("name"));
			setId(jObject.getString("id"));
			JSONObject location = jObject.getJSONObject("location");
			float lat = (float) location.getDouble("lat");
			float lng = (float) location.getDouble("lng");
			if (!location.isNull("address"))
				setAddress(location.getString("address"));
			setLatitude(lat);
			setLongitude(lng);
			loadCategories(jObject.getJSONArray("categories"));
			if (jObject.has("hereNow"))
				setHereNow(jObject.getJSONObject("hereNow").getInt("count"));
			getCategory();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public String getIconName()
	{
		return "m_4";
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCategory()
	{
		return category;
	}

	public void loadSimpleOptionalInfo()
	{
		if (optInfo == null)
			optInfo = new OptionalInfo();
		if (optInfo.getLoadingStatus() != optInfo.LOADED_SUCCES)
			optInfo.loadById(getId());
	}

	public void loadOptionalInfo()
	{
		loadSimpleOptionalInfo();
	}

	public OptionalInfo getOptionalInfo()
	{
		return optInfo;
	}

	public int getHereNow()
	{
		return hereNow;
	}

	public void setHereNow(int hereNow)
	{
		this.hereNow = hereNow;
	}

	public List<String> getPhones()
	{
		if (optInfo != null)
			return optInfo.getFSQPhonesList();
		else
			return null;
	}

	public boolean isCheckedIn()
	{
		return FSQConnector.isInCheckList(getId());
	}

	public boolean isCheckedToDo()
	{
		return FSQConnector.isInTodoList(getId());
	}

	public void loadCategories(JSONArray jArray)
	{
		for (int i = 0; i < jArray.length(); i++)
		{
			try
			{
				JSONObject jCateg = jArray.getJSONObject(i);
				String sCateg = jCateg.getString("name");
				categoryList.add(sCateg);
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		if (ArrayList.class.isInstance(categoryList))
			((ArrayList<String>) categoryList).trimToSize();
	}

	public String getCategoriesString()
	{
		String st = "";
		for (String s : categoryList)
		{
			st += ", " + s;
		}
		if (st.length() > 2)
			st = st.substring(2).trim();
		return st;
	}

	@Override
	public String getShortCharacteristic()
	{
		return getCategoriesString();
	}

	public int getPhotosCount()
	{
		return optInfo.getPhotosCount();
	}

	public UrlDrawable getUrlAndPhoto(int i)
	{
		return optInfo.getUrlAndPhoto(i);
	}

	@Override
	public int getItemColor()
	{
		if (getObjTypeId().equals(FSQ_TYPE_MARKET))
			return context.getResources().getColor(R.color.shop);
		return context.getResources().getColor(R.color.selected_blue);
	}

	@Override
	public Drawable getIconDrawable()
	{
		int id;
		if (getCategory().equals(FSQ_TYPE_MARKET))
			id = R.drawable.icon_markt;
		else if (getCategory().equals(FSQ_TYPE_CLUB))
			id = R.drawable.icon_disc;
		else
			id = R.drawable.icon_drug;
		return context.getResources().getDrawable(id);
	}

	Bitmap localBtm = null;

	@Override
	protected void loadImageToView(final LoadingImageView loadingImageView)
	{
		if (optInfo == null)
			optInfo = new OptionalInfo();
		Log.w("FSQItem", "loadImage begin");
		final Drawable photo;
		final ImageCache imageCache = ImageCache.getInstance();
		String photoUrl = imageCache.getTitlePhotoUrlIfHave(getId());
		loadingImageView.setTag(null);
		if (photoUrl != null && photoUrl.equals(""))
		{
			loadingImageView.setDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
		} else
		{
			if (photoUrl != null && imageCache.hasImage(photoUrl))
				photo = new BitmapDrawable(imageCache.getImage(photoUrl));
			else
				photo = null;
			loadingImageView.setTag(this.hashCode());
			Runnable preTask = new Runnable()
			{

				@Override
				public void run()
				{
					optInfo.loadById(getId());
				}

			};

			if (photo == null)
			{
				Runnable postTask = new Runnable()
				{
					@Override
					public void run()
					{
						if (optInfo.getLoadingStatus() == optInfo.LOADED_SUCCES && optInfo.getPhotosCount() > 0)
						{
							UrlDrawable urlDr = optInfo.getUrlAndPhoto(0);
							if (Integer.valueOf(FSQItem.this.hashCode()).equals(loadingImageView.getTag()))
								FSQConnector.loadImageAsync(loadingImageView, urlDr, UrlDrawable.SMALL_URL, false, null);
							else
								FSQConnector.loadImageAsync(null, urlDr, UrlDrawable.SMALL_URL, false, null);
							imageCache.addPhotoUrl(getId(), (String) ProjectUtils.ifnull(urlDr.smallUrl, urlDr.bigUrl));
						} else
						{
							imageCache.addPhotoUrl(getId(), "");
							loadingImageView.setDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
						}
					}
				};
				loadingImageView.setDrawable(null);
				MainApplication.pwAggregator.addPriorityTask(preTask, postTask);
			} else
			{
				if (optInfo.getLoadingStatus() != optInfo.LOADED_SUCCES && optInfo.getLoadingStatus() != optInfo.LOADING_NOW)
					MainApplication.pwAggregator.addBackroundTaskToQueue(preTask);
				Log.w("FSQItem", "loadImage ended");
				loadingImageView.setDrawable(photo);
				Log.w("FSQItem", "loadImage draw ended");
			}
			Log.w("FSQItem", "loadImage gc ended");
		}
	}
}