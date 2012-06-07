package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.pointplus.ProjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OptionalInfo
{
	List<VenueComment> commentList = new ArrayList<VenueComment>();
	List<String> FSQPhonesList = new ArrayList();
	
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
			tipItems = jObject.getJSONArray("groups").getJSONObject(0).getJSONArray("items");
		} catch (JSONException e)
		{
			e.printStackTrace();
			tipItems = null;
		}
		if (tipItems != null)
		{
			for (int i=0; i < tipItems.length(); i++)
			{
				VenueComment comment = new VenueComment();
				try
				{
					comment.loadFromJSON(tipItems.getJSONObject(i));
				} catch (JSONException e)
				{
					comment=null;
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
			String phones=jsonObject.getString("formattedPhone");
			setFSQPhonesList(ProjectUtils.separateStrings(phones, ","));
		} catch (JSONException e)
		{
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
	
	

	
}