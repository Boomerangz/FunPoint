package kz.crystalspring.funpoint;

import com.google.analytics.tracking.android.EasyTracker;

import kz.crystalspring.funpoint.item_page.foodController;
import kz.crystalspring.funpoint.item_page.simpleController;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class funObjectDetail extends FragmentActivity
{
	String pSelObjType;
	String pSelObjId;
	ActivityController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setcontroller();
		EasyTracker.getInstance().activityStart(this);
	}

	private void setcontroller()
	{
		try
		{
			FSQItem selectedItem = (FSQItem) MainApplication.getMapItemContainer().getSelectedMapItem();
			String objType = selectedItem.getCategory();
			if (objType.equals(MapItem.FSQ_TYPE_FOOD))
				controller = new foodController(this);
			else if (objType.equals(MapItem.FSQ_TYPE_CINEMA))
				controller = new funCinemaController(this);
			else
				controller = new simpleController(this);
			controller.onCreate();
		} catch (Exception e)
		{
			e.printStackTrace();
			finish();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		controller.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		controller.onResume();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		EasyTracker.getInstance().activityStart(this);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return controller.onKeyDown(keyCode, event);
	}

}
