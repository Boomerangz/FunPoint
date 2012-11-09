package kz.crystalspring.visualities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.RefreshableMapList;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQFriend;
import kz.crystalspring.funpoint.venues.FSQUser;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.views.LoadingImageView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
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
		progressBar = findViewById(R.id.progress_bar);
		MainApplication.refreshable = this;
		checkinPoints = (TextView) findViewById(R.id.points_view);
		checkinMessage = (TextView) findViewById(R.id.message);
		ratingLayout = (LinearLayout) findViewById(R.id.rating_layout);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		jCheckin = null;
		String checkinResponse = getIntent().getExtras().getString("checkinResponse");
		if (checkinResponse != null)
			try
			{
				jCheckin = new JSONObject(checkinResponse);
			} catch (JSONException e)
			{
				jCheckin = null;
				e.printStackTrace();
			}
		refreshMapItems();
	}

	@Override
	public void refreshMapItems()
	{
		String message = null;
		String scoreMessage = null;
		Integer currentScore = null;
		LeaderBoard board = null;
		try
		{
			for (int i = 0; i < jCheckin.getJSONArray("notifications").length(); i++)
			{
				JSONObject jNot = jCheckin.getJSONArray("notifications").getJSONObject(i);
				String type = jNot.getString("type");
				if (type.equals("message"))
				{
					message = jNot.getString("item");
				} else if (type.equals("leaderboard"))
				{
					JSONObject item = jNot.getJSONObject("item");
					scoreMessage = item.getString("message");
					currentScore = item.getInt("total");
					FSQUser.getInstance().addCheckin(currentScore);
					board = new LeaderBoard(jNot.getJSONObject("item").getJSONArray("leaderboard"));
				}
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		if (currentScore != null)
		{
			checkinPoints.setText("Вами получено " + currentScore.toString() + " баллов");
		}
		if (scoreMessage != null)
		{
			checkinMessage.setText(scoreMessage);
		}
		if (board != null)
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
		friends = new ArrayList<FSQFriend>();
		scoringList = new HashMap<String, Integer>();
		try
		{
			for (int i = 0; i < jArray.length(); i++)
			{
				JSONObject jObject = jArray.getJSONObject(i);
				FSQFriend friend = new FSQFriend(jObject);
				friends.add(friend);
				Integer userScore = jObject.getJSONObject("scores").getInt("recent");
				scoringList.put(friend.getId(), userScore);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public View getView(Activity context)
	{
		LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		int MAX_RATING = 25;
		for (FSQFriend friend : friends)
		{
			if (scoringList.get(friend.getId()) > MAX_RATING)
				MAX_RATING = scoringList.get(friend.getId());
		}
		MAX_RATING*=1.1;
		for (FSQFriend friend : friends)
		{
			// TextView tv=new TextView(context);
			// tv.setLayoutParams(new
			// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			// LinearLayout.LayoutParams.WRAP_CONTENT));
			// tv.setText(friend.getName()+" "+scoringList.get(friend.getId()).toString());
			// ll.addView(tv);
			LayoutInflater infalter = context.getLayoutInflater();
			View v = infalter.inflate(R.layout.leader_list_item, null);
			TextView friendNameTV = (TextView) v.findViewById(R.id.user_name);
			TextView friendPoints = (TextView) v.findViewById(R.id.user_place);
			View filler = (View) v.findViewById(R.id.filler);

			LoadingImageView liv = (LoadingImageView) v.findViewById(R.id.loading_imageview);

			friendNameTV.setText(friend.getName());
			friendPoints.setText(scoringList.get(friend.getId()).toString());
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) (friendPoints.getLayoutParams());
			LinearLayout.LayoutParams vlp = (LinearLayout.LayoutParams) (filler.getLayoutParams());

			lp.weight = MAX_RATING - scoringList.get(friend.getId());
			vlp.weight = scoringList.get(friend.getId());

			liv.setTag(friend.getId());
			FSQConnector.loadImageAsync(liv, friend.getUrlDrawable(), UrlDrawable.BIG_URL, false, null);
			ll.addView(v);
		}
		return ll;
	}
}
