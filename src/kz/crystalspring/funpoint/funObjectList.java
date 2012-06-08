package kz.crystalspring.funpoint;

import java.util.List;

import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class funObjectList extends Activity implements RefreshableMapList
{
	LinearLayout list;
	List<MapItem> itemsList;
	Button mapBtn;
	
	
	@Override 
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.object_list);
		list=(LinearLayout) findViewById(R.id.objects_list);
		mapBtn=(Button) findViewById(R.id.mapBtn);
		mapBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				MainMenu.goToObjectMap();
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		MainApplication.refreshable=this;
		refreshList();
	}
	

	private void refreshList()
	{
		itemsList=MainApplication.mapItemContainer.getFilteredItemList();
		ObjectAdapter adapter=new ObjectAdapter(this, itemsList);
		adapter.fillLayout(list);
//		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new OnItemClickListener()
//		{
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3)
//			{
//				MainApplication.mapItemContainer.setSelectedItem(itemsList.get(arg2));
//				Intent intent=new Intent(funObjectList.this,funObjectDetail.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			//	Toast.makeText(funObjectList.this, Integer.toString(arg2), Toast.LENGTH_SHORT).show();
//			}
//		});
	}

	@Override
	public void refreshMapItems()
	{
		refreshList();
	}
}

class ObjectAdapter
{
	    private List<MapItem> data;
	    private Context context;

	    public ObjectAdapter(Context context, List<MapItem> _data) 
	    {
	    	data=_data;
	    	this.context=context;
	    }

	    public int getCount() 
	    {
	        return data.size();
	    }
	    public Object getItem(int position) {
	        return data.get(position);
	    }

	    public long getItemId(int position) 
	    {
	        return position;
	    }

	    public View getView(int position) 
	    {
	    	return data.get(position).getView(null,position);
	    }
	    
	    public void fillLayout(LinearLayout l)
	    {
	    	l.removeAllViews();
	    	for (int i=0; i<getCount(); i++)
	    	{
	    		i++;
	    		View v=getView(i);
	    		final int itemIndex=i;
	    		v.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						MainApplication.mapItemContainer.setSelectedItem((MapItem)getItem(itemIndex));
						Intent intent=new Intent(context,funObjectDetail.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
						//Toast.makeText(context, Integer.toString(arg2), Toast.LENGTH_SHORT).show();
					}
				});
	    		v.setMinimumHeight(Math.round(70*MainApplication.mDensity));
	    		l.addView(v);
	    	}
	    }
}


