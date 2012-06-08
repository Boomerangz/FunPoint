package kz.crystalspring.funpoint;

import com.boomerang.metromenu.MetromenuActivity;

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
		MainApplication.refreshable=this;
		if (MainApplication.mapItemContainer.getFilteredItemList().size()>0)
		{
			refreshMapItems();
		}
	}

	@Override
	public void refreshMapItems()
	{
		Intent intent = new Intent(funWaitingActivity.this, MainMenu.class);
		startActivity(intent);
		finish();
	}
}
