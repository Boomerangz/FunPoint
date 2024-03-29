package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.ViewFragment;
import com.viewpagerindicator.ViewFragmentAdapter;

import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.CommentsWrapper;
import kz.crystalspring.visualities.homescreen.TitleFragment;
import kz.crystalspring.funpoint.CinemaTimeTable.CinemaTime;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemCinema;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class funCinemaController extends ActivityController
{

	private static final String[] CONTENT_TABS = new String[] { "Расписание",
			"Инфо", "Комментарии" };
	TextView tv1;
	LinearLayout timeList;
	ItemCinema cinema;
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
	View addCommentBtn;

	View contexView;

	Activity activitycontext;

	LayoutInflater inflater = context.getLayoutInflater();

	funCinemaController(FragmentActivity context)
	{
		super(context);
		activitycontext = context;
	}

	@Override
	protected void onCreate()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (MainApplication.getInstance().checkInternetConnection())
		{
			AsyncTask loadingTask = new AsyncTask()
			{
				@Override
				protected Object doInBackground(Object... params)
				{
					contexView = inflater.inflate(R.layout.controller_cinema,
							null);
					cinema = (ItemCinema) MainApplication.mapItemContainer
							.getSelectedItem();

					final int count = CONTENT_TABS.length;
					List<TitleFragment> viewList = new ArrayList<TitleFragment>(
							count);

					View page1 = loadTimePage();
					viewList.add(new ViewFragment(page1, CONTENT_TABS[0]));

					View page2 = loadTitlePage();
					viewList.add(new ViewFragment(page2, CONTENT_TABS[1]));

					View page3 = loadCommentPage();
					viewList.add(new ViewFragment(page3, CONTENT_TABS[2]));

					ViewFragmentAdapter pagerAdapter = new ViewFragmentAdapter(
							context.getSupportFragmentManager(), viewList);
					ViewPager viewPager = (ViewPager) contexView
							.findViewById(R.id.pager);
					viewPager.setAdapter(pagerAdapter);
					viewPager.setCurrentItem(0);

					TabPageIndicator indicator = (TabPageIndicator) contexView
							.findViewById(R.id.indicator);
					indicator.setViewPager(viewPager);

					cinema.loadAdditionalInfo();
					cinema.itemCinemaLoadOptionalInfo();
					return null;
				}

				@Override
				public void onPostExecute(Object result)
				{
					onContentLoaded();
				}
			};
			if (cinema == null
					|| !cinema.equals(MainApplication.mapItemContainer
							.getSelectedItem()))
			{
				context.setContentView(R.layout.waiting_layout);
				loadingTask.execute();
			} else
				onContentLoaded();
		} else
		{
			MainApplication.loadNoInternetPage();
			context.finish();
		}
	}

	private void onContentLoaded()
	{
		context.setContentView(contexView);
		showCinema(cinema);
	}

	private void showCinema(ItemCinema cinema2)
	{
		tv1.setText(cinema.getName());
		if (cinema.isHallInfoFilled())
		{
			new CinemaTimeTableAdapter(cinema.getTimeTable(), activitycontext)
					.fillLayout(timeList);
		} else
		{
		}
		titleTV.setText(cinema.getName());
		if (cinema.getAddress() != null && !cinema.getAddress().equals(""))
		{
			addressTV.setText(cinema.getAddress());
			addressTV.setVisibility(View.VISIBLE);
		} else
			addressTV.setVisibility(View.GONE);
		hereNowTV.setText(Integer.toString(cinema.getHereNow()));
		kitchenTV.setText(cinema.getCategoriesString());

		if (cinema.isCheckedIn())
			setStateChecked();

		if (cinema.isCheckedToDo())
			setStateTodo();

		phoneLayout.removeAllViews();
		List<String> phones = cinema.getPhones();
		if (phones != null)
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
		loadComments();
	}
	
	private void loadComments()
	{
		if (cinema.getOptionalInfo()!=null)
		{
			CommentsWrapper commentsWrapper=new CommentsWrapper(cinema,context);
		//	commentsListLayout.addView(commentsWrapper.getView());
		}
	}

	@Override
	protected void onPause()
	{

	}

	private View loadTimePage()
	{
		View v = inflater.inflate(R.layout.controller_cinema_page1, null);
		tv1 = (TextView) v.findViewById(R.id.object_name);
		// timeTable = (TextView) context.findViewById(R.id.timetable);
		timeList = (LinearLayout) v.findViewById(R.id.time_list_view);
		return v;
	}

	private View loadTitlePage()
	{
		View v = inflater.inflate(R.layout.controller_cinema_page2, null);
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
		addCommentBtn = (View) v.findViewById(R.id.add_comment);
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

class CinemaTimeTableAdapter extends BaseAdapter
{
	CinemaTimeTable table;
	Context context;

	public CinemaTimeTableAdapter(CinemaTimeTable table, Context context)
	{
		this.table = table;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return table.getTimeLines().size();
	}

	@Override
	public Object getItem(int position)
	{
		return table.getTimeLines().get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent1)
	{
		ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.object_list_item_cinema, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView.findViewById(R.id.text);
		holder.tableLayout = (TableLayout) convertView.findViewById(R.id.table);

		convertView.setMinimumHeight(60);
		convertView.setTag(holder);
		holder.text.setText(table.getTimeLines().get(position).getTitle() + " "
				+ table.getTimeLines().get(position).getStrDate());
		holder.text.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainApplication.selectedEventId = table.getTimeLines().get(
						position).filmId;
				Intent i = new Intent(context, funEventActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		});

		int i = 0;
		TableRow row = null;

		TableRow.LayoutParams ll=new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.FILL_PARENT);
		ll.setMargins(3, 0, 0, 3);
		for (final CinemaTime time : table.getTimeLines().get(position).times)
		{

			TextView timeView;
			if (ProjectUtils.ifnull(time.getHash(), "").equals(""))
			{
				timeView = new Button(context);
				timeView.setBackgroundColor(context.getResources().getColor(R.color.vpi__dark_theme));
			}
			else
			{
				Button btn = new Button(context);
				btn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						String url = time.getHash();
						Dialog dialog = new Dialog(context);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View vi = inflater.inflate(R.layout.webview, null);
						dialog.setContentView(vi);
						dialog.setCancelable(true);
						WebView wb = (WebView) vi.findViewById(R.id.webview);
						wb.getSettings().setJavaScriptEnabled(true);
						wb.setWebViewClient(new WebViewClient());
						wb.loadUrl(url);
						System.out.println("..loading url..");
						dialog.show();
					}
				});
				timeView = btn;
			}

			timeView.setLayoutParams(ll);
			timeView.setText(time.getStringTime() + " ");

			if (i % 4 == 0)
			{
				row = new TableRow(context);
				row.setLayoutParams(new TableLayout.LayoutParams(
						TableLayout.LayoutParams.WRAP_CONTENT,
						TableLayout.LayoutParams.WRAP_CONTENT));
				holder.tableLayout.addView(row);
			}
			row.addView(timeView);
			i++;
		}
		return convertView;
	}

	public void fillLayout(LinearLayout layout)
	{
		layout.removeAllViews();
		for (int i = 0; i < getCount(); i++)
		{
			View v = getView(i, null, null);
			layout.addView(v);
		}
	}

	class ViewHolder
	{
		TextView text;
		TableLayout tableLayout;
	}
}
