package kz.crystalspring.funpoint.item_page;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.crystalspring.funpoint.ActivityController;
import kz.crystalspring.funpoint.FullScrLoadingImageActivity;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.PhoneTextView;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FileConnector;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.CommentsWrapper;
import kz.crystalspring.views.GalleryWrapper;
import kz.crystalspring.views.LoadingImageView;
import kz.crystalspring.visualities.homescreen.TitleFragment;
import kz.sbeyer.atmpoint1.types.ItemFood;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class foodController extends ActivityController {
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
	LinearLayout galleryLayout;
	LinearLayout phoneLayout;

	GalleryWrapper wrapper;
	
	ImageView switchThirdBtn;
	ImageView switchPreviousBtn;
	ImageView switchNextBtn;
	View addCommentBtn;

	View mainView;

	LayoutInflater inflater = context.getLayoutInflater();

	public foodController(FragmentActivity _context) {
		super(_context);

		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate() {
		if (!MainApplication.mapItemContainer.getSelectedItem()
				.equals(itemFood)) {
			itemFood = (ItemFood) MainApplication.mapItemContainer
					.getSelectedItem();

			context.setContentView(R.layout.waiting_layout);
			mainView = inflater.inflate(R.layout.controller_food, null);
			final int count = CONTENT_TABS.length;
			List<TitleFragment> viewList = new ArrayList<TitleFragment>(count);

			View page1 = loadTitlePage();
			viewList.add(new ViewFragment(page1, CONTENT_TABS[0]));

			View page2 = loadCommentPage();
			viewList.add(new ViewFragment(page2, CONTENT_TABS[1]));

//			View page3 = loadGalleryPage(page1);
//			viewList.add(new ViewFragment(page3, CONTENT_TABS[2]));

			ViewFragmentAdapter pagerAdapter = new ViewFragmentAdapter(
					context.getSupportFragmentManager(), viewList);
			ViewPager viewPager = (ViewPager) mainView.findViewById(R.id.pager);
			titleTV = (TextView) mainView.findViewById(R.id.food_title);
			viewPager.setAdapter(pagerAdapter);
			viewPager.setCurrentItem(0);

			TabPageIndicator indicator = (TabPageIndicator) mainView
					.findViewById(R.id.indicator);
			indicator.setViewPager(viewPager);

			if (MainApplication.getInstance().checkInternetConnection()) {
			} else {
				MainApplication.loadNoInternetPage();
				context.finish();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AsyncTask task = new AsyncTask() {

			@Override
			protected Object doInBackground(Object... params) {
				if (itemFood.getOptionalInfo() == null) {
					JSONObject jObject = FSQConnector
							.getVenueInformation(itemFood.getId());
					itemFood.itemFoodLoadOptionalInfo(jObject);
				}

				if (itemFood.getFoodOptions() == null) {
					JSONObject jObject = FileConnector
							.getFoodInfoFromFile(itemFood.getId());
					itemFood.loadFoodOptions(jObject);
				}
				System.out.println("Загрузка в фоне");
				return null;
			}

			@Override
			public void onPostExecute(Object result) {
				showFood(itemFood);
				context.setContentView(mainView);
				currentTask = null;
			}
		};
		currentTask = task;
		task.execute();

	}

	private View loadTitlePage() {
		View v = inflater.inflate(R.layout.controller_food_page1, null);
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
		commentsListLayout = (LinearLayout) v
				.findViewById(R.id.comment_list_layout);
		galleryLayout = (LinearLayout)v.findViewById(R.id.gallery_list_layout);
		createGallery();
		
		checkInBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainApplication.FsqApp.hasAccessToken())
					checkInHere();
				else
					showNeedLogin();
			}
		});
		todoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainApplication.FsqApp.hasAccessToken())
					checkToDo();
				else
					showNeedLogin();
			}
		});

		mapInBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToMap();
			}
		});

		return v;
	}

	private void createGallery() 
	{
		wrapper = new GalleryWrapper(context,GalleryWrapper.MODE_PLACE);
		View view = wrapper.getView();
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		galleryLayout.removeAllViews();
		galleryLayout.addView(view);
	}

	private View loadCommentPage() {
		View v = inflater.inflate(R.layout.controller_food_page2, null);

		addCommentBtn = (View) v.findViewById(R.id.add_comment);
		addCommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openAddCommentActivity();
			}
		});

		return v;
	}

	private View loadGalleryPage(View v) {
		//View v = inflater.inflate(R.layout.controller_food_page3, null);
		galleryLayout = (TableLayout) v.findViewById(R.id.gallery_table);
		return v;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
	}

	private void showFood(ItemFood food) {
		titleTV.setText(food.getName());
		if (food.getAddress() != null && !food.getAddress().equals("")) {
			addressTV.setText(food.getAddress());
			addressTV.setVisibility(View.VISIBLE);
		} else
			addressTV.setVisibility(View.GONE);
		hereNowTV.setText(Integer.toString(food.getHereNow()));
		lunchPriceTV.setText(food.getLunchPrice());
		kitchenTV.setText(food.getCategoriesString());
		// kitchenTV.setVisibility(View.VISIBLE);
		if (itemFood.getFoodOptions() != null) {
			avgPriceTV.setText(food.getAvgPrice() + "тг");
			avgPriceTV.setVisibility(View.VISIBLE);
		} else {
			// kitchenTV.setVisibility(View.INVISIBLE);
			avgPriceTV.setVisibility(View.INVISIBLE);
		}

		if (food.isCheckedIn())
			setStateChecked();

		if (food.isCheckedToDo())
			setStateTodo();

		phoneLayout.removeAllViews();
		List<String> phones = food.getPhones();
		for (String phone : phones) {
			final PhoneTextView phoneTV = new PhoneTextView(context);
			phoneTV.setPhone(ProjectUtils.formatPhone(phone));
			phoneTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + phoneTV.getPhone()));
					context.startActivity(intent);
				}
			});
			phoneLayout.addView(phoneTV);
		}

		loadComments();
		loadPhotosToGallery();
	}
	
	private void loadComments()
	{
		if (itemFood.getOptionalInfo()!=null)
		{
			commentsListLayout.removeAllViews();
			CommentsWrapper commentsWrapper=new CommentsWrapper(itemFood,context);
			commentsListLayout.addView(commentsWrapper.getView());
		}
	}

	private void loadPhotosToGallery() 
	{
		wrapper.clear();
		for (int i=0; i<itemFood.getPhotosCount();i++)
		{
			wrapper.addDrawable(itemFood.getUrlAndPhoto(i));
		}
	}

	@Override
	public void setStateChecked() {
		checkInBtn.setEnabled(false);
		checkInBtn.setBackgroundColor(Color.parseColor("#00A859"));
	}

	@Override
	public void setStateTodo() {
		todoBtn.setEnabled(false);
		todoBtn.setBackgroundColor(Color.parseColor("#00A859"));
	}
}
