package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.item_page.foodController;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

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
		controller.onCreate();
	}

	private void setcontroller()
	{
		MapItem selectedItem = MainApplication.mapItemContainer
				.getSelectedMapItem();
		String objType = ((FSQItem) selectedItem).getCategory();
		if (objType.equals(MapItem.FSQ_TYPE_HOTEL))
			controller = new funHotelController(this);
		else if (objType.equals(MapItem.FSQ_TYPE_FOOD))
			controller = new foodController(this);
		else if (objType.equals(MapItem.FSQ_TYPE_CINEMA))
			controller = new funCinemaController(this);
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
		controller.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
			return controller.onKeyDown(keyCode, event);
	}

}
