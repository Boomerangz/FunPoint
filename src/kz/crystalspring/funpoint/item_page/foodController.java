package kz.crystalspring.funpoint.item_page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.crystalspring.funpoint.ActivityController;
import kz.crystalspring.funpoint.FullScrLoadingImageActivity;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.PhoneTextView;
import kz.crystalspring.funpoint.ProfilePage;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.WriteCommentActivity;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.VenueComment;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.visualities.LoadingImageView;
import kz.sbeyer.atmpoint1.types.ItemFood;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class foodController extends ActivityController
{
	private static final String[] CONTENT_TABS = new String[] { "Инфо",
			"Отзывы", "Фото" };

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
	LinearLayout phoneLayout;

	ImageView switchThirdBtn;
	ImageView switchPreviousBtn;
	ImageView switchNextBtn;
	View addCommentBtn;

	LayoutInflater inflater = context.getLayoutInflater();

	public foodController(FragmentActivity _context)
	{
		super(_context);

		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate()
	{
		// context.setTheme(R.style.StyledIndicators);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (!MainApplication.mapItemContainer.getSelectedItem().equals(itemFood))
		{
			itemFood = (ItemFood) MainApplication.mapItemContainer
					.getSelectedItem();
		
		context.setContentView(R.layout.controller_food);

		
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
		
		final int count=CONTENT_TABS.length;
		List<ViewFragment> viewList=new ArrayList(count);
		
		View page1=loadTitlePage();
		viewList.add(new ViewFragment(page1, CONTENT_TABS[0]));
		
		View page2=loadCommentPage();
		viewList.add(new ViewFragment(page2, CONTENT_TABS[1]));
		
		View page3=loadGalleryPage();
		viewList.add(new ViewFragment(page3, CONTENT_TABS[2]));
		
		ViewFragmentAdapter pagerAdapter = new ViewFragmentAdapter(
				context.getSupportFragmentManager(),viewList);
		ViewPager viewPager = (ViewPager) context.findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);

		TabPageIndicator indicator = (TabPageIndicator) context
				.findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);
		
		showFood(itemFood);
		}
	}

	private View loadTitlePage()
	{
		View v = inflater.inflate(R.layout.controller_food_page1, null);
		titleTV = (TextView) v.findViewById(R.id.food_title);
		addressTV = (TextView) v.findViewById(R.id.food_address);
		lunchPriceTV = (TextView) v.findViewById(R.id.food_lunch_price);
		avgPriceTV = (TextView) v.findViewById(R.id.food_avg_price);
		mainInfoLayout = (LinearLayout) v.findViewById(R.id.main_info_layout);
		checkInBtn = (RelativeLayout) v.findViewById(R.id.checkin_block);
		mapInBtn = (RelativeLayout) v.findViewById(R.id.map_block);
		todoBtn = (RelativeLayout) v.findViewById(R.id.todo_block);
		kitchenTV = (TextView) v.findViewById(R.id.food_kitchen);
		hereNowTV = (TextView) v.findViewById(R.id.here_now_tv);
		phoneLayout = (LinearLayout) v.findViewById(R.id.phone_block);
		
		checkInBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (MainApplication.FsqApp.hasAccessToken())
					checkInHere();
				else
					showNeedLogin();

			}
		});
		todoBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (MainApplication.FsqApp.hasAccessToken())
					checkToDo();
				else
					showNeedLogin();
			}
		});

		mapInBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToMap();
			}
		});
		
		return v;
	}

	private View loadCommentPage()
	{
		View v = inflater.inflate(R.layout.controller_food_page2, null);
		commentsListLayout = (LinearLayout) v
				.findViewById(R.id.comment_list_layout);
		addCommentBtn = (View)  v.findViewById(R.id.add_comment);
		addCommentBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openAddCommentActivity();
			}
		});

		return v;
	}
	

	private View loadGalleryPage()
	{
		View v = inflater.inflate(R.layout.controller_food_page3, null);
		galleryLayout = (TableLayout) v.findViewById(R.id.gallery_table);
		return v;
	}
	
	

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
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

		phoneLayout.removeAllViews();
		List<String> phones = food.getPhones();
		for (String phone : phones)
		{
			final PhoneTextView phoneTV = new PhoneTextView(context);
			phoneTV.setPhone(ProjectUtils.formatPhone(phone));
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

		if (itemFood.getOptionalInfo() != null)
		{
			VenueCommentsAdapter adapter = new VenueCommentsAdapter(context,
					itemFood.getOptionalInfo().getCommentsList());
			adapter.fillLayout(commentsListLayout);
		}

		loadPhotosToGallery();
	}

	private void loadPhotosToGallery()
	{
		galleryLayout.removeAllViews();
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
	
    @Override
	public void setStateChecked()
	{
		checkInBtn.setEnabled(false);
		checkInBtn.setBackgroundColor(Color.parseColor("#00A859"));
	}
    @Override
    public void setStateTodo()
	{
		todoBtn.setEnabled(false);
		todoBtn.setBackgroundColor(Color.parseColor("#00A859"));
	}
}

