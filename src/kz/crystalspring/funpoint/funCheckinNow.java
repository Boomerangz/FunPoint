package kz.crystalspring.funpoint;

import kz.com.pack.jam.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class funCheckinNow extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkin_list);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		ListView list = (ListView)findViewById(R.id.listView1);
//		list.setAdapter(new ObjectAdapter(this, MainApplication.mapItemContainer.getUnFilteredItemList()));
//		list.setOnItemClickListener(new OnItemClickListener()
//		{
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3)
//			{
//				Toast toast = Toast.makeText(getApplicationContext(), "Item clicked", Toast.LENGTH_SHORT);
//				toast.show();
//			}
//		});
	}
}


