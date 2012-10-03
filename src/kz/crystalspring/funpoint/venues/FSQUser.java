package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FSQUser
{
	String firstName;
	String lastName;
	String email;
	UrlDrawable photo;
	List<UrlDrawable> badges;
	Integer recentScore;
	Integer maxScore;

	static FSQUser singletone;

	public static FSQUser getInstance()
	{
		if (singletone == null)
			singletone = new FSQUser();
		return singletone;
	}

	FSQUser()
	{
		badges = new ArrayList();
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public void modify(JSONObject jObject)
	{
		int i = 1;
		while (i < 6)
		{
			try
			{
				JSONObject jUser = jObject.getJSONObject("user");
				switch (i)
				{
				case 1:
					setLastName(jUser.getString("lastName"));
					i++;
				case 2:
					setFirstName(jUser.getString("firstName"));
					i++;
				case 3:
					setEmail(jUser.getJSONObject("contact").getString("email"));
					i++;
				case 4:
					photo = new UrlDrawable(null, jUser.getString("photo"));
					i++;
				case 5:
					JSONObject jBadges = jUser.getJSONObject("badges");
					JSONArray jBadgesArray = jBadges.getJSONArray("items");
					for (int j = 0; j < jBadgesArray.length(); j++)
					{
						JSONObject jBadge = jBadgesArray.getJSONObject(j);
						JSONObject jImage = jBadge.getJSONObject("image");
						Integer bigSize = jImage.getJSONArray("sizes").getInt(
								jImage.getJSONArray("sizes").length() - 1);
						Integer smallSize = jImage.getJSONArray("sizes")
								.getInt(0);
						String prefix = jImage.getString("prefix");
						String name = jImage.getString("name");
						String bigUrl = prefix + bigSize.toString() + name;
						String smallUrl = prefix + smallSize.toString() + name;
						UrlDrawable urlDr = new UrlDrawable(smallUrl, bigUrl);
						badges.add(urlDr);
					}
					i++;
				case 6:
					setRecentScore(jUser.getJSONObject("scores").getInt("recent"));
					setMaxScore(jUser.getJSONObject("scores").getInt("max"));
					i++;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
				i++;
			}
		}
		isFilled = true;
	}

	public void fillIfNot()
	{
		if (!isFilled())
		{
			fill();
		}
	}

	private void fill()
	{
		FSQConnector.loadUserInfo(this);
	}

	boolean isFilled = false;

	public boolean isFilled()
	{
		return isFilled;
	}

	public String getName()
	{
		String name = "";
		if (getFirstName() != null)
			name += getFirstName();
		name += " ";
		if (getLastName() != null)
			name += getLastName();
		return name;
	}

	public UrlDrawable getPhoto()
	{
		return photo;
	}

	public void setPhoto(UrlDrawable photo)
	{
		this.photo = photo;
	}

	public List<UrlDrawable> getBadgesList()
	{
		return badges;
	}

	public Integer getRecentScore()
	{
		return recentScore;
	}

	public void setRecentScore(Integer recentScore)
	{
		this.recentScore = recentScore;
	}

	public Integer getMaxScore()
	{
		return maxScore;
	}

	public void setMaxScore(Integer maxScore)
	{
		this.maxScore = maxScore;
	}
	
	

}
