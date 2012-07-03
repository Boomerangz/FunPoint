package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class funObjectList extends Activity implements RefreshableMapList
{
	LinearLayout list;
	List<MapItem> itemsList;
	Button mapBtn;
	ImageView openSearchButton;
	EditText searchEdit;
	ObjectAdapter adapter;
	
	@Override 
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.object_list);
		list=(LinearLayout) findViewById(R.id.objects_list);
		mapBtn=(Button) findViewById(R.id.mapBtn);
		adapter=new ObjectAdapter(this);
		mapBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainMenu.goToObjectMap();
			}
		});
		
		searchEdit=(EditText) findViewById(R.id.search_edit);
		searchEdit.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				filterByString(s.toString());
			}
		});
		
		openSearchButton=(ImageView)findViewById(R.id.search_btn);
		openSearchButton.setOnClickListener(new OnClickListener()
		{
			boolean searchVisible=false;
			@Override
			public void onClick(View v)
			{
				searchVisible=!searchVisible;
				if (searchVisible)
					searchEdit.setVisibility(View.VISIBLE);
				else 
					searchEdit.setVisibility(View.GONE);
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
	
	private void filterByString(String filter)
	{
		adapter.setStringFilter(filter);
		refreshList();
	}
	

	private void refreshList()
	{
		itemsList=MainApplication.mapItemContainer.getFilteredItemList();
		adapter.setData(itemsList);
		adapter.refreshState();
		adapter.fillLayout(list);
	}

	@Override
	public void refreshMapItems()
	{
		refreshList();
	}
}

class ObjectAdapter
{
	    private List<MapItem> data=new ArrayList();
	    private Context context;
	    private String filterString="";
	    
	    private List<MapItem> filteredData;
	    
	    public ObjectAdapter(Context context) 
	    {
	    	this.context=context;
	    }
	    
	    public void setData(List _data)
	    {
	    	data=_data;
	    }

	    public int getCount() 
	    {
	        return filteredData.size();
	    }
	    public Object getItem(int position) {
	        return filteredData.get(position);
	    }

	    public long getItemId(int position) 
	    {
	        return position;
	    }

	    public View getView(int position) 
	    {
	    	return filteredData.get(position).getView(null,position);
	    }
	    
	    public void fillLayout(LinearLayout l)
	    {
	    	
	    	ArrayList<View> viewList=new ArrayList<View>(getCount());
	    	for (int i=0; i<getCount(); i++)
	    	{
	    		View v=getView(i);
	    		
	    		v.setMinimumHeight(Math.round(70*MainApplication.mDensity));
	    		viewList.add(v);
	    	}
	    	l.removeAllViews();
	    	for (View v:viewList)
	    	{
	    		l.addView(v);
	    	}
	    }
	    
	    public void setStringFilter(String s)
	    {
	    	filterString=s;
	    	refreshState();
	    }
	    
	    public void refreshState()
	    {
	    	filteredData=new ArrayList();
	    	if (!filterString.trim().equals(""))
	    	for (MapItem item:data)
	    	{
	    		if (item.toString().toUpperCase().contains(filterString.toUpperCase().trim()))
	    		filteredData.add(item);
	    	}
	    	else
	    		filteredData.addAll(data);
	    }
}


