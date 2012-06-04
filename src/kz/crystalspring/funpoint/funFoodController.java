package kz.crystalspring.funpoint;

import java.io.File;

import kz.crystalspring.funpoint.TimeTableAdapter.ViewHolder;
import kz.crystalspring.funpoint.venues.*;
import java.io.IOException;
import java.util.List;

import javax.security.auth.Destroyable;

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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class funFoodController extends ActivityController
{
	ItemFood itemFood;
	TextView titleTV;
	TextView addressTV;
	TextView lunchPriceTV;
	TextView avgPriceTV;
	TextView kitchenTV;
	TextView hereNowTV;
	RelativeLayout checkInBtn;
	RelativeLayout mapInBtn;
	RelativeLayout todoBtn;
	ListView commentsList;

	public static final int ALPHA = 100;

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
		String sObjID = Prefs.getSelObjId(context.getApplicationContext());
		commentsList = (ListView) context.findViewById(R.id.comment_list);
		checkInBtn = (RelativeLayout) context.findViewById(R.id.checkin_block);
		mapInBtn = (RelativeLayout) context.findViewById(R.id.map_block);
		todoBtn = (RelativeLayout) context.findViewById(R.id.todo_block);
		kitchenTV = (TextView) context.findViewById(R.id.food_kitchen);
		hereNowTV = (TextView) context.findViewById(R.id.here_now_tv);

		int[] arg = { R.id.checkin_block, R.id.map_block, R.id.herenow_block,
				R.id.todo_block, R.id.avg_price_block, R.id.address_block,
				R.id.phone_block };
		setBlocksAlpha(ALPHA, arg);

		if (MainApplication.FsqApp.hasAccessToken())
		{
			checkInBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					checkInHere();
				}
			});
			todoBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					checkToDo();
				}
			});
		}

		mapInBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToMap();
			}
		});
		
		int iObjID = Integer.parseInt(sObjID);

		itemFood = (ItemFood) MainApplication.mapItemContainer
				.getSelectedItem();

		if (itemFood.getOptionalInfo() == null)
		{
			JSONObject jObject = FSQConnector.getVenueInformation(itemFood
					.getId());
			itemFood.itemFoodLoadOptionalInfo(jObject);
		}

		if (itemFood.getFoodOptions() == null)
		{
			JSONObject jObject = FileConnector.getFoodInfoFromFile(itemFood
					.getId());
			itemFood.loadFoodOptions(jObject);
		}
		
		Button switchBtn=(Button) context.findViewById(R.id.switch_btn);
		switchBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switchPageToSecond();
			}
		});

		showFood(itemFood);
	}
	
	private void switchPageToSecond()
	{
		ViewSwitcher switcher=(ViewSwitcher)context.findViewById(R.id.switcher);
		switcher.showNext();
	}

	private void setBlocksAlpha(int alpha, int[] args)
	{
		for (int n : args)
		{
			View block = (View) context.findViewById(n);
			block.getBackground().setAlpha(alpha);
		}
	}

	@Override
	protected void onPause()
	{

	}

	private void showFood(ItemFood food)
	{
		titleTV.setText(food.getName());
		if (food.getAddress() != null && !food.getAddress().equals(""))
		{
			addressTV.setText(food.getAddress());
			addressTV.setVisibility(View.VISIBLE);
		} else
			addressTV.setVisibility(View.GONE);
		hereNowTV.setText(Integer.toString(food.getHereNow()));
		if (itemFood.getOptionalInfo() != null)
		{
			VenueCommentsAdapter adapter = new VenueCommentsAdapter(context,
					itemFood.getOptionalInfo().getCommentsList());
			commentsList.setAdapter(adapter);
		}
		lunchPriceTV.setText(food.getLunchPrice());
		if (itemFood.getFoodOptions() != null)
		{
			avgPriceTV.setText(food.getAvgPrice() + "тг");
			kitchenTV.setText(food.getKitchen());

			kitchenTV.setVisibility(View.VISIBLE);
			avgPriceTV.setVisibility(View.VISIBLE);
		} else
		{
			kitchenTV.setVisibility(View.INVISIBLE);
			avgPriceTV.setVisibility(View.INVISIBLE);
		}
		
		if (food.isCheckedIn())
			setStateChecked();
		
		if (food.isCheckedToDo())
			setStateTodo();
		

		LinearLayout phoneLayout = (LinearLayout) context
				.findViewById(R.id.phone_block);
		List<String> phones = food.getPhones();
		for (String phone : phones)
		{
			final PhoneTextView phoneTV = new PhoneTextView(context);
			phoneTV.setPhone(phone);
			phoneTV.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneTV.getPhone()));
					context.startActivity(intent);
				}
			});
			phoneLayout.addView(phoneTV);
		}
	}

	private ItemFood getFoodFromJSON(JSONObject jObject)
	{
		ItemFood food = new ItemFood();
		return food.loadFromJSON(jObject);
	}

	private void checkInHere()
	{
		FSQConnector.checkIn(itemFood.getId());
		setStateChecked();
	}
	
	private void checkToDo()
	{
		FSQConnector.addToTodos(itemFood.getId());
		setStateTodo();
	}
	
	private void setStateChecked()
	{
		checkInBtn.setEnabled(false);
		checkInBtn.setBackgroundColor(Color.parseColor("#00A859"));
	}
	
	private void setStateTodo()
	{
		todoBtn.setEnabled(false);
		todoBtn.setBackgroundColor(Color.parseColor("#00A859"));
		itemFood.setCheckedToDo(true);
	}
	
	private void goToMap()
	{
		MainApplication.mapItemContainer.setSelectedItem(itemFood);
		MainMenu.goToObjectMap();
		context.finish();
	}

}

class VenueCommentsAdapter extends BaseAdapter
{
	List<VenueComment> data;
	private LayoutInflater mInflater;

	VenueCommentsAdapter(Context context, List<VenueComment> commentList)
	{
		this.data = commentList;
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
		} else
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
