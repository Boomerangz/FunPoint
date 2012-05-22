package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
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
	}
	
	private void setcontroller()
	{
		MapItem selectedItem=MainApplication.mapItemContainer.getSelectedItem();
		int objType = MapItem.TYPE_FOOD;
		switch (objType)
		{
			case MapItem.TYPE_HOTEL: controller=new funHotelController(this); break;
			case MapItem.TYPE_FOOD: controller=new funFoodController(this); break;
			case MapItem.TYPE_CINEMA : controller=new funCinemaController(this);	
		}
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
		setcontroller();
		controller.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			return controller.onKeyDown(keyCode, event);
		}
		else 
			return super.onKeyDown(keyCode, event);
	}
	
}
