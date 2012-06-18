package kz.crystalspring.funpoint.venues;

import java.io.Serializable;
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


	
	
	public class UrlDrawable implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -703381267268292002L;//generated serialable UID
		public static final int BIG_URL=1;
		public static final int SMALL_URL=2;
		
		public String bigUrl="";
		public String smallUrl="";
		private Drawable bigDrawable=null;
		private Drawable smallDrawable=null;
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
			return smallDrawable;
		}
		public synchronized void setSmallDrawable(Drawable smallDrawable)
		{
			this.smallDrawable = smallDrawable;
		}
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
					comment=comment.loadFromJSON(tipItems.getJSONObject(i));
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

	public void loadPhotoUrls(JSONObject jObject)
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
						int count=jPhoto.getJSONArray("items")
								.getJSONObject(j).getJSONObject("sizes").getJSONArray("items").length();
						String bigUrl = jPhoto.getJSONArray("items")
								.getJSONObject(j).getJSONObject("sizes").getJSONArray("items").getJSONObject(0).getString("url");
						String smallUrl = jPhoto.getJSONArray("items")
								.getJSONObject(j).getJSONObject("sizes").getJSONArray("items").getJSONObject(Math.round(count/2)).getString("url");
						
						UrlDrawable urlDr=new UrlDrawable();
						urlDr.bigUrl=bigUrl;
						urlDr.smallUrl=smallUrl;
						
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
			JSONObject jObject=new JSONObject(st);
			String status= jObject.getJSONObject("response").getJSONObject("tip").getString("status");
			if (status.equals("done"))
			{
				VenueComment comment=new VenueComment();
				comment=comment.loadFromJSON(jObject.getJSONObject("response").getJSONObject("tip"));
				ProjectUtils.addToBeginOfArrayList((ArrayList) commentList, comment).add(comment);
			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}