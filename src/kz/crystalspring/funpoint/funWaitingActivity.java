package kz.crystalspring.funpoint;

import com.boomerang.metromenu.MetromenuActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class funWaitingActivity extends Activity implements RefreshableMapList
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_layout);
		MainApplication.refreshable=this;
		if (MainApplication.mapItemContainer.getFilteredItemList().size()>0)
		{
			refreshMapItems();
		}
		
//		AsyncTask task=new AsyncTask<Object, Object,Boolean>()
//				{
//
//					@Override
//					protected Boolean doInBackground(Object... params)
//					{
//						return ; 
//					}
//					
//					@Override 
//					protected void onPostExecute(Boolean result)
//					{
//						if (result)
//						{
//							refreshMapItems();
//						}
//					}
//					
//				};
//		task.execute();		
	}
	
	

	@Override
	public void refreshMapItems()
	{
		Intent intent = new Intent(funWaitingActivity.this, MainMenu.class);
		startActivity(intent);
		finish();
	}
}
