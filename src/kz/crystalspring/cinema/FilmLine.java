package kz.crystalspring.cinema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.ProjectUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FilmLine
{
	String title;
	String filmID;
	Map<Date, DateFilmLine> dateList;

	FilmLine(String title, String filmID)
	{
		this.title = title;
		this.filmID = filmID;
		dateList = new HashMap<Date, DateFilmLine>();
	}

	public void addCinemaTime(CinemaTime ct, Date clearDate)
	{
		DateFilmLine dfl;
		if (dateList.containsKey(clearDate))
			dfl = dateList.get(clearDate);
		else
		{
			dfl = new DateFilmLine(clearDate);
			dateList.put(clearDate, dfl);
		}
		dfl.addCinemaTime(ct);
	}

	@Override
	public String toString()
	{
		return title;
	}


	public View getView(final Activity context)
	{
		View v;
		LayoutInflater inflater = context.getLayoutInflater();
		v = inflater.inflate(R.layout.cinema_line, null);
		TextView filmTitle = (TextView) v.findViewById(R.id.text);
		final LinearLayout dateLayout = (LinearLayout) v.findViewById(R.id.date_list);
		final ViewFlipper switcher = (ViewFlipper) v.findViewById(R.id.switcher);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		Date nowDate = new Date();
		ViewPager.LayoutParams vlp = new ViewPager.LayoutParams();
		vlp.width = ViewPager.LayoutParams.FILL_PARENT;
		vlp.height = ViewPager.LayoutParams.WRAP_CONTENT;

		Object[] array = dateList.keySet().toArray();
		List<Date> dateArray = new ArrayList();
		for (int i = 0; i < array.length; i++)
		{
			dateArray.add((Date) array[i]);
		}

		Collections.sort(dateArray);
	//	Collections.reverse(dateArray);
		for (int i = 0; i < dateArray.size(); i++)
		{
			if (i < 2)
			{
				Date dt = dateArray.get(i);
				final TextView tv = new TextView(context);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setLayoutParams(lp);
				tv.setText(ProjectUtils.dateToTomorrow(nowDate, dt));
				dateLayout.addView(tv);

				TableLayout tl = new TableLayout(context);
				tl.setLayoutParams(vlp);
				dateList.get(dt).fillLayout(tl,context);

				if (i == 0)
				{
					tv.setTypeface(null, Typeface.BOLD);
					tv.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View arg0)
						{
							if (curr_position > 0)
							{
								switcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left));
								switcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_right));
								switcher.showNext();
								curr_position--;
								minimizeTexts(dateLayout);
								tv.setTypeface(null, Typeface.BOLD);
							}
						}
					});
				} else
					tv.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View arg0)
						{
							if (curr_position == 0)
							{
								switcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
								switcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left));
								switcher.showPrevious();
								curr_position++;
								minimizeTexts(dateLayout);
								tv.setTypeface(null, Typeface.BOLD);
							}
						}
					});
				switcher.addView(tl);
			}
		}
		filmTitle.setText(title);
		return v;
	}
	
	private void minimizeTexts(LinearLayout layout)
	{
		for (int i=0;i<layout.getChildCount();i++)
		{
			TextView v=(TextView) layout.getChildAt(i);
			v.setTypeface(null,Typeface.NORMAL);
		}
	}

	int curr_position = 0;
}

class DateFilmLine
{
	Date date;
	List<CinemaTime> timeList;

	public DateFilmLine(Date clearDate)
	{
		date = clearDate;
		timeList = new ArrayList<CinemaTime>();
	}

	public void fillLayout(TableLayout tableLayout, Activity context)
	{
		tableLayout.removeAllViews();
		TableRow tr = new TableRow(context);
		TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(tlp);
		tableLayout.addView(tr);
		int i = 0;
		TableRow.LayoutParams ll = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.FILL_PARENT);
		int margin = Math.round(6 * MainApplication.mDensity);
		ll.setMargins(margin, 0, 0, margin);
		for (CinemaTime ct : timeList)
		{
			if (i == 4)
			{
				tr = new TableRow(tableLayout.getContext());
				tr.setLayoutParams(tlp);
				tableLayout.addView(tr);
				i = 0;
			}
			View v = ct.getView(context);
			v.setLayoutParams(ll);
			tr.addView(v);
			i++;
		}
	}

	public void addCinemaTime(CinemaTime ct)
	{
		timeList.add(ct);
		sort();
	}
	void sort()
	{
		Collections.sort(timeList, new Comparator<CinemaTime>()
				{

					@Override
					public int compare(CinemaTime lhs, CinemaTime rhs)
					{
						return lhs.getTime().compareTo(rhs.getTime());
					}
				});
	}
}

class CinemaTime
{
	private String time;
	private String hash;

	CinemaTime(String time, String hash)
	{
		setTime(time);
		this.hash = hash;
	}

	public View getView(final Activity context)
	{
		TextView timeView;
		if (ProjectUtils.ifnull(hash, "").equals(""))
		{
			timeView = new Button(context);
			timeView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
		} else
		{
			Button btn = (Button) context.getLayoutInflater().inflate(R.layout.cinema_button, null);
			btn.getBackground().setAlpha(MainApplication.ALPHA);
			btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String url = hash;
					Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		timeView.setText(time);
		return timeView;
	}

	public String getTime()
	{
		return time;
	}

	public String getStringTime()
	{
		return CinemaTimeTable2.time_formatter.format(time);// time.toLocaleString();
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getHash()
	{
		return hash;
	}

	public void setHash(String hash)
	{
		this.hash = hash;
	}
}
