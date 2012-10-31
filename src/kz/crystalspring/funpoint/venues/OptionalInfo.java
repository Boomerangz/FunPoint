package kz.crystalspring.funpoint.venues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import kz.crystalspring.pointplus.ProjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class OptionalInfo
{
	List<VenueComment> commentList = new ArrayList<VenueComment>();
	List<String> FSQPhonesList = new ArrayList();
	List<UrlDrawable> FSQPhotosList = new ArrayList();
	public static final int NOT_LOADED = 0;
	public static final int LOADING_NOW = 1;
	public static final int LOADED_SUCCES = 2;
	public static final int LOADED_ERROE = 3;

	int loadingStatus = NOT_LOADED;

	public synchronized int getLoadingStatus()
	{
		return loadingStatus;
	}

	private void addCommentToList(VenueComment comment)
	{
		commentList.add(comment);
	}

	public List<VenueComment> getCommentsList()
	{
		return commentList;
	}

	private void loadComments(JSONObject jObject)
	{
		commentList.clear();
		JSONArray tipItems;
		try
		{
			tipItems = jObject.getJSONArray("groups").getJSONObject(0).getJSONArray("items");
		} catch (JSONException e)
		{
			e.printStackTrace();
			tipItems = null;
		}
		if (tipItems != null)
		{
			for (int i = 0; i < tipItems.length(); i++)
			{
				VenueComment comment = new VenueComment();
				try
				{
					comment = comment.loadFromJSON(tipItems.getJSONObject(i));
				} catch (JSONException e)
				{
					comment = null;
					e.printStackTrace();
				}
				if (comment != null)
					addCommentToList(comment);
			}
			Collections.sort(commentList);
			Collections.reverse(commentList);
		}
	}

	private void loadPhones(JSONObject jsonObject)
	{
		try
		{
			String phones = jsonObject.getString("formattedPhone");
			setFSQPhonesList(ProjectUtils.separateStrings(phones, ","));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private void loadPhotoUrls(JSONObject jObject)
	{
		FSQPhotosList.clear();
		try
		{
			System.gc();
			JSONArray jPhotos = jObject.getJSONArray("groups");
			for (int i = 0; i < jPhotos.length(); i++)
			{
				JSONObject jPhoto = jPhotos.getJSONObject(i);
				if (jPhoto.getInt("count") > 0)
				{
					for (int j = 0; j < jPhoto.getJSONArray("items").length(); j++)
					{
						int count = jPhoto.getJSONArray("items").getJSONObject(j).getJSONObject("sizes").getJSONArray("items").length();
						int smallIndex = (count - 2) > 0 ? (count - 2) : 0;

						String bigUrl = jPhoto.getJSONArray("items").getJSONObject(j).getJSONObject("sizes").getJSONArray("items")
								.getJSONObject(0).getString("url");
						String smallUrl = jPhoto.getJSONArray("items").getJSONObject(j).getJSONObject("sizes").getJSONArray("items")
								.getJSONObject(smallIndex).getString("url");

						UrlDrawable urlDr = new UrlDrawable();
						urlDr.bigUrl = bigUrl;
						urlDr.smallUrl = smallUrl;

						FSQPhotosList.add(urlDr);
					}
				}
			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getFSQPhonesList()
	{
		return FSQPhonesList;
	}

	public void setFSQPhonesList(List<String> fSQPhonesList)
	{
		FSQPhonesList = fSQPhonesList;
	}

	public int getPhotosCount()
	{
		return FSQPhotosList.size();
	}

	public UrlDrawable getUrlAndPhoto(int i)
	{
		return FSQPhotosList.get(i);
	}

	public synchronized void addCommentFromResponse(String st)
	{
		try
		{
			JSONObject jObject = new JSONObject(st);
			String status = jObject.getJSONObject("response").getJSONObject("tip").getString("status");
			if (status.equals("done"))
			{
				VenueComment comment = new VenueComment();
				comment = comment.loadFromJSON(jObject.getJSONObject("response").getJSONObject("tip"));
				ProjectUtils.addToBeginOfArrayList((ArrayList) commentList, comment).add(comment);
				GregorianCalendar date = null;
				date.add(Calendar.SECOND, (int) -date.getTimeInMillis());

			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadById(String fsqId)
	{
		if (loadingStatus != LOADING_NOW)
		{
			loadingStatus = LOADING_NOW;
			JSONObject jObject = FSQConnector.loadVenueInformation(fsqId);
			loadFromJSONObject(jObject);
			loadingStatus = LOADED_SUCCES;
		}
		else
		{
			while (loadingStatus == LOADING_NOW)
			{
				for (int i=0;i<65000;i++)
				{
				}
			}
		}
	}

	public void loadFromJSONObject(JSONObject fsqJObject)
	{
		try
		{
			loadPhotoUrls(fsqJObject.getJSONObject("photos"));
			loadComments(fsqJObject.getJSONObject("tips"));
			loadPhones(fsqJObject.getJSONObject("contact"));
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
