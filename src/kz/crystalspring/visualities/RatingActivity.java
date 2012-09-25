package kz.crystalspring.visualities;

import org.json.JSONException;
import org.json.JSONObject;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.RefreshableMapList;
import kz.crystalspring.funpoint.venues.FSQConnector;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RatingActivity extends Activity implements RefreshableMapList
{
	View progressBar;
	JSONObject jCheckin;
	TextView checkinPoints;
	TextView checkinMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_page);
		progressBar= findViewById(R.id.progress_bar);
		MainApplication.refreshable=this;
		checkinPoints=(TextView) findViewById(R.id.points_view);
		checkinMessage=(TextView) findViewById(R.id.message);
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
					board=new LeaderBoard(jNot);
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
			
		}
		
	}
}

class LeaderBoard 
{
	public LeaderBoard(JSONObject jNot)
	{
		
	}
}
