package kz.crystalspring.funpoint;

import java.io.File;

import kz.crystalspring.funpoint.TimeTableAdapter.ViewHolder;
import kz.crystalspring.funpoint.venues.*;
import java.io.IOException;
import java.util.List;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.atmpoint1.types.ItemFood;
import kz.sbeyer.atmpoint1.types.ItemCinema.CinemaTimeLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class funFoodController extends ActivityController
{
	ItemFood itemFood;
	TextView titleTV;
	TextView addressTV;
	TextView lunchPriceTV;
	TextView avgPriceTV;
	ListView commentsList;

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
		commentsList=(ListView)context.findViewById(R.id.comment_list);
		int iObjID=Integer.parseInt(sObjID);
		
		itemFood=(ItemFood)MainApplication.mapItemContainer.getSelectedItem();
		
		JSONObject jObject=FSQConnector.getVenueInformation(itemFood.getId());
		itemFood.itemFoodLoadOptionalInfo(jObject);
		
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
		VenueCommentsAdapter adapter=new VenueCommentsAdapter(context, itemFood.getOptionalInfo().getCommentsList());
		commentsList.setAdapter(adapter);
	}
	
	
	private ItemFood getFoodFromJSON(JSONObject jObject)
	{
		ItemFood food=new ItemFood();
		return food.loadFromJSON(jObject);
	}
}

class VenueCommentsAdapter extends BaseAdapter
{
	List<VenueComment> data;
	private LayoutInflater mInflater;

	VenueCommentsAdapter(Context context,List<VenueComment> commentList)
	{
		this.data=commentList;
		 mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public Object getItem(int position)
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.comment_list_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.author = (TextView) convertView.findViewById(R.id.author);
         
           // convertView.setMinimumHeight(60);
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(data.get(position).getText());
        holder.author.setText(data.get(position).getAuthor());
        return convertView;
    }

    static class ViewHolder 
    {
        TextView author;
        TextView text;
    }
	
}
