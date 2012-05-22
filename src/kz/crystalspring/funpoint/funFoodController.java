package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.atmpoint1.types.ItemFood;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class funFoodController extends ActivityController
{
	ItemFood itemFood;
	TextView titleTV;
	TextView addressTV;
	TextView lunchPriceTV;
	TextView avgPriceTV;

	funFoodController(Activity context)
	{
		super(context);
	}

	@Override
	protected void onCreate()
	{
		// TODO Auto-generated method stub

	}
	
	@Override 
	protected void onResume()
	{
		context.setContentView(R.layout.fun_food_detail);
		titleTV = (TextView) context.findViewById(R.id.food_title);
		addressTV = (TextView) context.findViewById(R.id.food_address);
		lunchPriceTV = (TextView) context.findViewById(R.id.food_lunch_price);
		avgPriceTV = (TextView) context.findViewById(R.id.food_avg_price);
		String sObjID=Prefs.getSelObjId(context.getApplicationContext());
		int iObjID=Integer.parseInt(sObjID);
		
		itemFood=(ItemFood)MainApplication.mapItemContainer.getSelectedItem();
		showFood(itemFood);
	}
	
	@Override 
	protected void onPause()
	{
		
	}
	
	private void showFood(ItemFood food)
	{
		titleTV.setText(food.getName());
		addressTV.setText(food.getAddress());
		lunchPriceTV.setText(food.getLunchPrice());
		//avgPriceTV.setText(food.getPriceInterval());
	}
	
	
	private ItemFood getFoodFromJSON(JSONObject jObject)
	{
		ItemFood food=new ItemFood();
		return food.loadFromJSON(jObject);
	}
}
