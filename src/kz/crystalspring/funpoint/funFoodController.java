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
import kz.crystalspring.visualities.LoadingImageView;
import kz.crystalspring.funpoint.R;
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
import android.view.Gravity;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import android.widget.RelativeLayout.LayoutParams;

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
	LinearLayout commentsListLayout;
	LinearLayout mainInfoLayout;
	TableLayout galleryLayout;

	ImageView switchThirdBtn;
	ImageView switchPreviousBtn;
	ImageView switchNextBtn;

	ViewFlipper switcher;

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
		commentsListLayout = (LinearLayout) context
				.findViewById(R.id.comment_list_layout);
		mainInfoLayout = (LinearLayout) context
				.findViewById(R.id.main_info_layout);
		checkInBtn = (RelativeLayout) context.findViewById(R.id.checkin_block);
		mapInBtn = (RelativeLayout) context.findViewById(R.id.map_block);
		todoBtn = (RelativeLayout) context.findViewById(R.id.todo_block);
		kitchenTV = (TextView) context.findViewById(R.id.food_kitchen);
		hereNowTV = (TextView) context.findViewById(R.id.here_now_tv);
		galleryLayout = (TableLayout) context.findViewById(R.id.gallery_table);

		switcher = (ViewFlipper) context.findViewById(R.id.switcher);

		int[] arg = { R.id.checkin_block, R.id.map_block, R.id.herenow_block,
				R.id.todo_block, R.id.avg_price_block, R.id.address_block,
				R.id.phone_block };
		setBlocksAlpha(MainApplication.ALPHA, arg);

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

		switchPreviousBtn = (ImageView) context
				.findViewById(R.id.switch_back_btn);
		switchPreviousBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switchPrevious();
			}
		});
		switchNextBtn = (ImageView) context.findViewById(R.id.switch_btn);
		switchNextBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switchNext();
			}
		});
		showFood(itemFood);
	}

	private void onSwitch()
	{
		int id = switcher.indexOfChild(switcher.getCurrentView());
		System.out.println(id);
		if (id == 0)
		{
			switchPreviousBtn.setVisibility(View.GONE);
			switchNextBtn.setVisibility(View.VISIBLE);
			LinearLayout headerLayout = (LinearLayout) context
					.findViewById(R.id.minor_header_layout);
			// headerLayout.setGravity(Gravity.RIGHT);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			lp.setMargins(0, 0, Math.round(33 * MainApplication.mDensity), 0);

			headerLayout.setLayoutParams(lp);
			// uncollapseView(commentsListLayout);
			// collapseView(mainInfoLayout);
		} else if (id == 2)
		{
			switchPreviousBtn.setVisibility(View.VISIBLE);
			switchNextBtn.setVisibility(View.GONE);
			LinearLayout headerLayout = (LinearLayout) context
					.findViewById(R.id.minor_header_layout);
			// headerLayout.setGravity(Gravity.LEFT);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(Math.round(33 * MainApplication.mDensity), 0, 0, 0);
			headerLayout.setLayoutParams(lp);
			// collapseView(commentsListLayout);
			// uncollapseView(mainInfoLayout);
		} else
		{
			switchPreviousBtn.setVisibility(View.VISIBLE);
			switchNextBtn.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(Math.round(33 * MainApplication.mDensity), 0, 0, 0);
			LinearLayout headerLayout = (LinearLayout) context
					.findViewById(R.id.minor_header_layout);
			headerLayout.setLayoutParams(lp);
		}

		for (int i = 0; i < switcher.getChildCount(); i++)
		{
			View v = switcher.getChildAt(i);
			if (i != id)
				collapseView(v);
			else
				uncollapseView(v);
		}
	}

	private void switchNext()
	{
		switcher.setInAnimation(context, R.anim.slide_in_right);
		switcher.setOutAnimation(context, R.anim.slide_out_left);
		switcher.showNext();
		onSwitch();

	}

	private void switchPrevious()
	{
		switcher.setInAnimation(context, R.anim.slide_in_left);
		switcher.setOutAnimation(context, R.anim.slide_out_right);
		switcher.showPrevious();
		onSwitch();
	}

	//
	// private void switchPageToThird()
	// {
	// // switcher.setInAnimation(context, R.anim.slide_in_right);
	// // switcher.setOutAnimation(context, R.anim.slide_out_left);
	// // switcher.showNext();
	// // switchSecondBtn.setVisibility(View.GONE);
	// // switchFirstBtn.setVisibility(View.VISIBLE);
	// // LinearLayout headerLayout = (LinearLayout) context
	// // .findViewById(R.id.minor_header_layout);
	// // // headerLayout.setGravity(Gravity.RIGHT);
	// // RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	// // RelativeLayout.LayoutParams.WRAP_CONTENT,
	// // RelativeLayout.LayoutParams.WRAP_CONTENT);
	// // lp.setMargins(Math.round(33 * MainApplication.mDensity), 0, 0, 0);
	// // headerLayout.setLayoutParams(lp);
	// // uncollapseView(commentsListLayout);
	// // collapseView(mainInfoLayout);
	// }

	private void collapseView(View v)
	{
		v.getLayoutParams().height = 0;
		v.getLayoutParams().width = 0;
	}

	private void uncollapseView(View v)
	{
		v.getLayoutParams().height = v.getLayoutParams().WRAP_CONTENT;
		v.getLayoutParams().width = v.getLayoutParams().FILL_PARENT;
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
			adapter.fillLayout(commentsListLayout);
		}
		lunchPriceTV.setText(food.getLunchPrice());
		kitchenTV.setText(food.getCategoriesString());
		// kitchenTV.setVisibility(View.VISIBLE);
		if (itemFood.getFoodOptions() != null)
		{
			avgPriceTV.setText(food.getAvgPrice() + "тг");
			avgPriceTV.setVisibility(View.VISIBLE);
		} else
		{
			// kitchenTV.setVisibility(View.INVISIBLE);
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
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + phoneTV.getPhone()));
					context.startActivity(intent);
				}
			});
			phoneLayout.addView(phoneTV);
		}

		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				TableRow.LayoutParams.FILL_PARENT,
				Math.round(80 * MainApplication.mDensity));
		lp.weight = 1;

		TableRow currentRow = null;
		for (int i = 0; i < itemFood.getPhotosCount(); i++)
		{
			final LoadingImageView iv = new LoadingImageView(context);
			iv.setLayoutParams(lp);
			// iv.setImageDrawable(itemFood.getPhotos(i));
			if (i % 3 == 0)
			{
				TableRow tr = new TableRow(context);
				tr.setLayoutParams(new TableLayout.LayoutParams(Math
						.round(80 * MainApplication.mDensity), Math
						.round(80 * MainApplication.mDensity)));
				galleryLayout.addView(tr);

			}
			currentRow = (TableRow) galleryLayout.getChildAt(galleryLayout
					.getChildCount() - 1);
			if (currentRow != null)
				currentRow.addView(iv);
			if (itemFood.getUrlAndPhoto(i).getSmallDrawable() == null)
			{
				FSQConnector.loadImageAsync(iv, itemFood.getUrlAndPhoto(i),
						UrlDrawable.SMALL_URL, false);
			} else
			{
				iv.setDrawable(itemFood.getUrlAndPhoto(i).getSmallDrawable());
				final int ii = i;
				iv.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Toast.makeText(iv.getContext(), "On Click",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(iv.getContext(),
								FullScrLoadingImageActivity.class);
						MainApplication.selectedItemPhoto = itemFood
								.getUrlAndPhoto(ii);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						iv.getContext().startActivity(intent);
					}
				});
			}
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
	}

	private void goToMap()
	{
		MainApplication.mapItemContainer.setSelectedItem(itemFood);
		MainMenu.goToObjectMap();
		context.finish();
	}

}

class VenueCommentsAdapter
{
	List<VenueComment> data;
	private LayoutInflater mInflater;

	VenueCommentsAdapter(Context context, List<VenueComment> commentList)
	{
		this.data = commentList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount()
	{
		return data.size();
	}

	public Object getItem(int position)
	{
		return data.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position)
	{
		final ViewHolder holder;
		View convertView = null;
		convertView = mInflater.inflate(R.layout.comment_list_item, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView.findViewById(R.id.text);
		holder.author = (TextView) convertView.findViewById(R.id.author);
		holder.layout = (View) convertView.findViewById(R.id.comment_layout);
		holder.openMoreButton = (Button) convertView
				.findViewById(R.id.more_button);
		// convertView.setMinimumHeight(60);
		convertView.setTag(holder);
		holder.author.setText(data.get(position).getAuthor());
		holder.layout.getBackground().setAlpha(MainApplication.ALPHA);

		final String fullText = data.get(position).getText();
		final String shortText;
		if (!data.get(position).isLongText())
		{
			shortText = null;
			holder.text.setText(fullText);
			
		} else
		{
			shortText = data.get(position).getShortText();
			holder.text.setText(shortText);
		}
		if (data.get(position).isLongText())
		holder.openMoreButton.setOnClickListener(new OnClickListener()
		{
			boolean shrt=false;
			@Override
			public void onClick(View v)
			{
				if (shrt)
					holder.text.setText(shortText);
				else 
					holder.text.setText(fullText);
				shrt=!shrt;
			}
		});
		else 
			holder.openMoreButton.setVisibility(View.GONE);
		return convertView;
	}

	public void fillLayout(LinearLayout layout)
	{
		layout.removeAllViews();
		for (int i = 0; i < getCount(); i++)
			layout.addView(getView(i));
	}

	static class ViewHolder
	{
		TextView author;
		TextView text;
		Button openMoreButton;
		View layout;
	}

}
