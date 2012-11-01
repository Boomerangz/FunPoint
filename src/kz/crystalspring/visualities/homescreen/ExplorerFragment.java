package kz.crystalspring.visualities.homescreen;

import java.util.List;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ExplorerFragment extends TitleFragment
{
	public static ExplorerFragment newInstance()
	{
		ExplorerFragment fragment = new ExplorerFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	ExplorerView menu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		menu = new ExplorerView(getActivity());
		refresh();
		View v = menu.getExplorer();
		return v;
	}

	public void refresh()
	{
		if (menu != null)
			menu.refresh();
	}

	@Override
	public String getTitle()
	{
		return "Рекомендации";
	}

}

class ExplorerView
{
	Activity context;
	View exploreList;

	public ExplorerView(Activity context)
	{
		this.context = context;
		exploreList = createExplorerList();
		refresh();
	}

	public View getExplorer()
	{
		if (exploreList != null)
			return exploreList;
		else
		{
			exploreList = createExplorerList();
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
		ListView listView = (ListView) exploreList.findViewById(R.id.listView1);
		if (listView.getAdapter() == null || listView.getAdapter().getCount() == 0)
		{
			ExplorerAdapter adapter = new ExplorerAdapter(FSQConnector.getExplorer());
			listView.setAdapter(adapter);
			listView.setMinimumHeight(Math.round(100 * MainApplication.mDensity));
			listView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
				{
					System.out.println("LISTCLICK!!!!");
				}
			});
		} else
		{
			listView.invalidate();
		}

		View progressBar = exploreList.findViewById(R.id.progressBar1);
		View loginView = exploreList.findViewById(R.id.login_view);
		if (FSQConnector.isExploringLoaded())
		{
			progressBar.setVisibility(View.GONE);
			loginView.setVisibility(View.GONE);
		} else
		{
			if (MainApplication.FsqApp.hasAccessToken())
			{
				loginView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
			} else
			{
				loginView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}
		}
	}
}

class ExplorerAdapter extends BaseAdapter
{
	List<FSQItem> list;

	ExplorerAdapter(List<FSQItem> list)
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
		return list.get(arg0).getView(arg1, arg0);
	}

}
