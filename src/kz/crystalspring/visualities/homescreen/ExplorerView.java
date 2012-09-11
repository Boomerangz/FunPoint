package kz.crystalspring.visualities.homescreen;

import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQFriendCheckin;
import kz.crystalspring.funpoint.venues.FSQItem;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ExplorerView
{
	Activity context;
	View exploreList;
	
	
	public ExplorerView(Activity context)
	{
		this.context=context;
		exploreList=createExplorerList();
	}
	
	public View getExplorer()
	{
		if (exploreList!=null)
			return exploreList;
		else
		{
			exploreList=createExplorerList();
			return getExplorer();
		}
	}

	private View createExplorerList()
	{
		View explorer;
		LayoutInflater layoutInf = context.getLayoutInflater();

		explorer = layoutInf.inflate(R.layout.friend_feed, null);
				
		return explorer;
	}

	public void refresh()
	{
		ListView listView=(ListView) exploreList.findViewById(R.id.listView1);
		if (listView.getAdapter()==null||listView.getAdapter().getCount()==0)
		{
			listView.setAdapter(new ExplorerAdapter(FSQConnector.getExplorer()));
			listView.setMinimumHeight(Math.round(1*MainApplication.mDensity));
		}
		else
		{
			listView.invalidate();
		}
		
		View progressBar=exploreList.findViewById(R.id.progressBar1);
		if (listView.getAdapter().getCount()>0)
			progressBar.setVisibility(View.GONE);
		else
			progressBar.setVisibility(View.VISIBLE);
	}
}


class ExplorerAdapter extends BaseAdapter
{
	List<FSQItem> list;
	ExplorerAdapter(List<FSQItem> list)
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
		return list.get(arg0).getView(arg1, arg0);
	}
	
}
