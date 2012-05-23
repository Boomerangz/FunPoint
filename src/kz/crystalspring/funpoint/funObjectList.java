package kz.crystalspring.funpoint;

import android.app.Activity;
import android.os.Bundle;

public class funObjectList extends Activity implements RefreshableMapList
{
	@Override 
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		MainApplication.refreshable=this;
	}

	@Override
	public void refreshMapItems()
	{
		
	}
}
