package kz.crystalspring.funpoint.venues;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.visualities.gallery.ImageContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FSQUser implements ImageContainer
{
	String firstName;
	String lastName;
	String email;
	UrlDrawable photo;
	List<UrlDrawable> badges;
	Integer recentScore;
	Integer maxScore;
	Integer checkinCount;
	Integer friendCount;
	boolean isFilled;
	
	
	public Integer getCheckinCount()
	{
		return checkinCount;
	}

	public void setCheckinCount(Integer checkinCount)
	{
		this.checkinCount = checkinCount;
	}

	public Integer getFriendCount()
	{
		return friendCount;
	}

	public void setFriendCount(Integer friendCount)
	{
		this.friendCount = friendCount;
	}

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
		isFilled=false;
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
		while (i < 8)
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
					badges.clear();
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
				case 7:
					setCheckinCount(jUser.getJSONObject("checkins").getInt("count"));
					i++;
				case 8:
					setFriendCount(jUser.getJSONObject("friends").getInt("count"));
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

	public List<FSQBadge> getBadgesList()
	{
		return FSQConnector.getBadgesList();
	}

	public Integer getRecentScore()
	{
		return recentScore;
	}

	public void setRecentScore(Integer recentScore)
	{
		this.recentScore = recentScore;
		if (getMaxScore()!=null&&getRecentScore()>getMaxScore())
			setMaxScore(getRecentScore());
	}

	public Integer getMaxScore()
	{
		return maxScore;
	}

	public void setMaxScore(Integer maxScore)
	{
		this.maxScore = maxScore;
	}

//	public static void reInit()
//	{
//		singletone = new FSQUser();
//		MainApplication.loadUserActivity();
//	}

	@Override
	public int getPhotosCount()
	{
		return getBadgesList().size();
	}

	@Override
	public UrlDrawable getUrlAndPhoto(int i)
	{
		return getBadgesList().get(i);
	}

	public void addCheckin(int score)
	{
		if (checkinCount!=null)
			checkinCount++;
		setRecentScore((Integer)ProjectUtils.ifnull(getRecentScore(),0)+score);
	}
	
	

}
