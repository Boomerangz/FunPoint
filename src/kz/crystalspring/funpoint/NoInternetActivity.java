package kz.crystalspring.funpoint;

import java.util.List;

import com.boomerang.metromenu.MetromenuActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

public class NoInternetActivity extends Activity implements RefreshableMapList
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nointernet_layout);
		MainApplication.refreshable=this;
		MainApplication.pwAggregator.stopQueue();
	}

	@Override
	public boolean onKeyDown(int i, KeyEvent event)
	{
		if (i==KeyEvent.KEYCODE_BACK)
		{
//			ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//		    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1); 
//		    ComponentName componentInfo = taskInfo.get(0).topActivity;
//		    am.restartPackage(componentInfo.getPackageName());
			finish();
		}
		return true;
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		finish();
	}
	
	@Override
	public void refreshMapItems()
	{
	}
}
