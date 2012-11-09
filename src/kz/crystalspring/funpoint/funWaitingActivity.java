package kz.crystalspring.funpoint;

import kz.com.pack.jam.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class funWaitingActivity extends Activity implements RefreshableMapList
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_layout);
		MainApplication.refreshable = this;
		try
		{
			if (MainApplication.getMapItemContainer().getFilteredItemList().size() > 0)
			{
				refreshMapItems();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			finish();
		}
	}

	@Override
	public void refreshMapItems()
	{
		try
		{
			if (MainApplication.getMapItemContainer().getFilteredItemList().size() > 0)
			{
				Intent intent = new Intent(funWaitingActivity.this,
						funObjectList.class);
				startActivity(intent);
				finish();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			finish();
		}
	}
}
