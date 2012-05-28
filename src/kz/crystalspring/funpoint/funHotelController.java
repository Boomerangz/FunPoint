package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.atmpoint1.types.ItemHotel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class funHotelController extends ActivityController implements ViewFactory
{
	ItemHotel itemHotel;
	TextView hotelName;
	TextView hotelDesc;
	ImageSwitcher iSwitcher;
	Button btn;
	

	funHotelController(Activity context)
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
		context.setContentView(R.layout.fun_hotel_detail);
		hotelName=(TextView)context.findViewById(R.id.hotel_title);
		hotelDesc=(TextView)context.findViewById(R.id.hotel_description);
		iSwitcher=(ImageSwitcher)context.findViewById(R.id.ImageSwitcher01);
		btn=(Button)context.findViewById(R.id.button1);
		
		String sObjID=Prefs.getSelObjId(context.getApplicationContext());
		itemHotel=(ItemHotel) MainApplication.mapItemContainer.getSelectedItem();
		showHotel(itemHotel);
		
	}
	
	@Override 
	protected void onPause()
	{
		
	}
	
	private void showHotel(ItemHotel hotel)
	{
		hotelName.setText(itemHotel.toString());
		iSwitcher.setFactory(this);
		iSwitcher.setInAnimation(AnimationUtils.loadAnimation(context,
				android.R.anim.fade_in));
		iSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context,
				android.R.anim.fade_out));
        
        
        btn.setOnClickListener(new OnClickListener()
		{
			boolean img=true;
			@Override
			public void onClick(View v)
			{
				img=!img;
				if (img)
					iSwitcher.setImageResource(R.drawable.c_26);
				else
					iSwitcher.setImageResource(R.drawable.c_27);
			}
		});
		
	}

	private ItemHotel getHotel(String wantedID)
	{
		ItemHotel currHotel=null;
		String fileName = "json_hotels_1_ru_zip";
		currHotel=getHotelFromJSON(FileConnector.findObjectInJSONZIP(wantedID,fileName));
		return currHotel;
	}
	
	
	
	private ItemHotel getHotelFromJSON(JSONObject jObject)
	{
		ItemHotel hotel=new ItemHotel();
		return hotel.loadFromJSON(jObject);
	}

	@Override
	public View makeView() {
		ImageView iView = new ImageView(context);
		iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iView.setLayoutParams(new 
				ImageSwitcher.LayoutParams(
						LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		iView.setBackgroundColor(0xFF000000);
		return iView;
	}
}
