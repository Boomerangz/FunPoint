package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
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


	
	
	class UrlDrawable 
	{
		String bigUrl="";
		String smallUrl="";
		Drawable bigDrawable=null;
		Drawable smallDrawable=null;
	}
	
	
	private void addCommentToList(VenueComment comment)
	{
		commentList.add(comment);
	}

	public List<VenueComment> getCommentsList()
	{
		return commentList;
	}

	public void loadComments(JSONObject jObject)
	{
		JSONArray tipItems;
		try
		{
			tipItems = jObject.getJSONArray("groups").getJSONObject(0)
					.getJSONArray("items");
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
					comment.loadFromJSON(tipItems.getJSONObject(i));
				} catch (JSONException e)
				{
					comment = null;
					e.printStackTrace();
				}
				if (comment != null)
					addCommentToList(comment);
			}
		}
	}

	public void loadPhones(JSONObject jsonObject)
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

	public void loadPhotos(JSONObject jObject)
	{
		try
		{
			JSONArray jPhotos = jObject.getJSONArray("groups");
			for (int i = 0; i < jPhotos.length(); i++)
			{
				JSONObject jPhoto = jPhotos.getJSONObject(i);
				if (jPhoto.getInt("count") > 0)
				{
					for (int j = 0; j < jPhoto.getJSONArray("items").length(); j++)
					{
						String sUrl = jPhoto.getJSONArray("items")
								.getJSONObject(j).getString("url");
						UrlDrawable urlDr=new UrlDrawable();
						urlDr.bigUrl=sUrl;
						
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

}