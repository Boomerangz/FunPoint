package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class funObjectDetail extends Activity
{
	String pSelObjType;
	String pSelObjId;
	ActivityController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setcontroller();
		controller.onCreate();
	}

	private void setcontroller()
	{
		MapItem selectedItem = MainApplication.mapItemContainer
				.getSelectedItem();
		String objType = ((FSQItem) MainApplication.mapItemContainer
				.getSelectedItem()).getCategory();
		if (objType.equals(MapItem.FSQ_TYPE_HOTEL))
			controller = new funHotelController(this);
		else if (objType.equals(MapItem.FSQ_TYPE_FOOD))
			controller = new funFoodController(this);
		else if (objType.equals(MapItem.FSQ_TYPE_CINEMA))
			controller = new funCinemaController(this);
		else
			controller = new funFoodController(this);
		controller.onCreate();
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
	//	setcontroller();
		controller.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
//		if (keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			return controller.onKeyDown(keyCode, event);
//		} else
			return controller.onKeyDown(keyCode, event);
	}

}
