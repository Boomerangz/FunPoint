package kz.crystalspring.visualities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.RefreshableMapList;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQFriend;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RatingActivity extends Activity implements RefreshableMapList
{
	View progressBar;
	JSONObject jCheckin;
	TextView checkinPoints;
	TextView checkinMessage;
	
	LinearLayout ratingLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_page);
		progressBar= findViewById(R.id.progress_bar);
		MainApplication.refreshable=this;
		checkinPoints=(TextView) findViewById(R.id.points_view);
		checkinMessage=(TextView) findViewById(R.id.message);
		ratingLayout= (LinearLayout) findViewById(R.id.rating_layout);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		jCheckin=null;
		String checkinResponse=getIntent().getExtras().getString("checkinResponse");
		if (checkinResponse!=null)
			try
			{
				jCheckin=new JSONObject(checkinResponse);
			} catch (JSONException e)
			{
				jCheckin=null;
				e.printStackTrace();
			}
		refreshMapItems();
	}

	@Override
	public void refreshMapItems()
	{
		String message=null;
		String scoreMessage=null;
		Integer currentScore=null;
		LeaderBoard board=null;
		try
		{
			for (int i=0; i<jCheckin.getJSONArray("notifications").length(); i++)
			{
				JSONObject jNot=jCheckin.getJSONArray("notifications").getJSONObject(i);
				String type=jNot.getString("type");
				if (type.equals("message"))
				{
					message=jNot.getString("item");
				}
				else
				if (type.equals("leaderboard"))
				{
					JSONObject item=jNot.getJSONObject("item");
					scoreMessage=item.getString("message");
					currentScore=item.getInt("total");
					board=new LeaderBoard(jNot.getJSONObject("item").getJSONArray("leaderboard"));
				}
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		if (currentScore!=null)
		{
			checkinPoints.setText(currentScore.toString());
		}
		if (scoreMessage!=null)
		{
			checkinMessage.setText(scoreMessage);
		}
		if (board!=null)
		{
			ratingLayout.removeAllViews();
			ratingLayout.addView(board.getView(this));
		}
	}
}

class LeaderBoard 
{
	List<FSQFriend> friends;
	Map<String, Integer> scoringList;
	public LeaderBoard(JSONArray jArray)
	{
		friends=new ArrayList<FSQFriend>();
		scoringList=new HashMap<String,Integer>();
		try
		{
			for (int i=0; i<jArray.length(); i++)
			{
				JSONObject jObject=jArray.getJSONObject(i);
				FSQFriend friend=new FSQFriend(jObject);
				friends.add(friend);
				Integer userScore=jObject.getJSONObject("scores").getInt("recent");
				scoringList.put(friend.getId(), userScore);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public View getView(Context context)
	{
		LinearLayout ll=new LinearLayout(context);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		for (FSQFriend friend:friends)
		{
			TextView tv=new TextView(context);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			tv.setText(friend.getName()+" "+scoringList.get(friend.getId()).toString());
			ll.addView(tv);
		}
		return ll;
	}
}
