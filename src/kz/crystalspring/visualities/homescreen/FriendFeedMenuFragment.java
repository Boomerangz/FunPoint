package kz.crystalspring.visualities.homescreen;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQFriendCheckin;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class FriendFeedMenuFragment extends TitleFragment
{
	public static FriendFeedMenuFragment newInstance()
	{
		FriendFeedMenuFragment fragment = new FriendFeedMenuFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	FriendFeed menu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		menu = new FriendFeed(getActivity());
		View v = menu.getFriendFeed();
		return v;
	}

	@Override
	public String getTitle()
	{
		return "Лента";
	}

	public void refresh()
	{
		if (menu != null)
			menu.refresh();
	}
}

class FriendFeed
{
	Activity context;
	View friendFeedList;

	public FriendFeed(Activity context)
	{
		this.context = context;
		friendFeedList = createFriendFeedList();
	}

	public View getFriendFeed()
	{
		friendFeedList = createFriendFeedList();
		refresh();
		return friendFeedList;
	}

	private View createFriendFeedList()
	{
		View friendFeed;
		LayoutInflater layoutInf = context.getLayoutInflater();

		friendFeed = layoutInf.inflate(R.layout.friend_feed, null);
		if (friendFeed == null)
			Log.w("HomeScree", "FriednFeed is null");
		return friendFeed;
	}

	public void refresh()
	{
		ListView listView = (ListView) friendFeedList.findViewById(R.id.listView1);
		FriendFeedAdapter adapter = new FriendFeedAdapter(FSQConnector.getFriendFeed());
		listView.setAdapter(adapter);
		listView.setMinimumHeight(Math.round(80 * MainApplication.mDensity));
		View progressBar = friendFeedList.findViewById(R.id.progressBar1);
		View loginView = friendFeedList.findViewById(R.id.login_view);
		if (FSQConnector.isFriendFeedLoaded())
		{
			progressBar.setVisibility(View.GONE);
			loginView.setVisibility(View.GONE);
		} else
		{
			if (MainApplication.FsqApp.hasAccessToken())
			{
				progressBar.setVisibility(View.VISIBLE);
				loginView.setVisibility(View.GONE);
			} else
			{

				loginView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}
		}
	}
}

class FriendFeedAdapter extends BaseAdapter
{
	List<FSQFriendCheckin> list;

	FriendFeedAdapter(List<FSQFriendCheckin> list)
	{
		this.list = list;
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
		return list.get(arg0).getView(arg1);
	}

}