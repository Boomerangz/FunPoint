package kz.crystalspring.visualities.homescreen;

import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQFriendCheckin;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class FriendFeed
{
	Activity context;
	View friendFeedList;
	
	
	public FriendFeed(Activity context)
	{
		this.context=context;
		friendFeedList=createFriendFeedList();
	}
	
	public View getFriendFeed()
	{
		if (friendFeedList!=null)
			return friendFeedList;
		else
		{
			friendFeedList=createFriendFeedList();
			return getFriendFeed();
		}
	}

	private View createFriendFeedList()
	{
		View friendFeed;
		LayoutInflater layoutInf = context.getLayoutInflater();

		friendFeed = layoutInf.inflate(R.layout.friend_feed, null);
				
		return friendFeed;
	}

	public void refresh()
	{
		ListView listView=(ListView) friendFeedList.findViewById(R.id.listView1);
			listView.setAdapter(new FriendFeedAdapter(FSQConnector.getFriendFeed()));
			listView.setMinimumHeight(Math.round(80*MainApplication.mDensity));
		View progressBar=friendFeedList.findViewById(R.id.progressBar1);
		if (listView.getAdapter().getCount()>0)
			progressBar.setVisibility(View.GONE);
		else
			progressBar.setVisibility(View.VISIBLE);
	}
}


class FriendFeedAdapter extends BaseAdapter
{
	List<FSQFriendCheckin> list;
	FriendFeedAdapter(List<FSQFriendCheckin> list)
	{
		this.list=list;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		return list.get(arg0).getView();
	}
	
}
