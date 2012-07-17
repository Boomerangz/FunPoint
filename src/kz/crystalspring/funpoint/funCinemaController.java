package kz.crystalspring.funpoint;

import com.boomerang.metromenu.MetromenuActivity;

import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.CinemaTimeTable.CinemaTime;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemCinema;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class funCinemaController extends ActivityController
{
	final String CINEMA_TIME_FILE = "json_cinema_info_zip";
	TextView tv1;
	// TextView timeTable;
	LinearLayout hallLayout;
	ListView timeList;
	ItemCinema cinema;
	Activity activitycontext;

	funCinemaController(FragmentActivity context)
	{
		super(context);
		activitycontext=context;
	}

	@Override
	protected void onCreate()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume()
	{
		context.setContentView(R.layout.fun_cinema_detail);
		tv1 = (TextView) context.findViewById(R.id.object_name);
		// timeTable = (TextView) context.findViewById(R.id.timetable);
		hallLayout = (LinearLayout) context.findViewById(R.id.hall_layout);
		timeList = (ListView) context.findViewById(R.id.time_list_view);
		hallLayout.removeAllViews();
		String sObjID = Prefs.getSelObjId(context.getApplicationContext());
		cinema = (ItemCinema) MainApplication.mapItemContainer
				.getSelectedItem();
		cinema.loadAdditionalInfo();
		showCinema(cinema);
	}

	private void showCinema(ItemCinema cinema2)
	{
		tv1.setText(cinema.getName());
		if (cinema.isHallInfoFilled())
		{
			timeList.setAdapter(new CinemaTimeTableAdapter(cinema.getTimeTable(), activitycontext));
		} else
		{
		}
	}

	@Override
	protected void onPause()
	{

	}
}



class CinemaTimeTableAdapter extends BaseAdapter
{
	CinemaTimeTable table;
	Context context;
	
	public CinemaTimeTableAdapter(CinemaTimeTable table,Context context)
	{
		this.table=table;
		this.context=context;
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.object_list_item_cinema, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView.findViewById(R.id.text);
		holder.tableLayout=(TableLayout) convertView.findViewById(R.id.table);
		
		convertView.setMinimumHeight(60);
		convertView.setTag(holder);
		String st = Integer.toString(position) + ". " + toString();

		holder.text.setText(table.getTimeLines().get(position).filmName+" "+table.getTimeLines().get(position).getStrDate());
		holder.text.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainApplication.selectedEventId=table.getTimeLines().get(position).filmId;
				Intent i = new Intent(context, funEventActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		});
		
		int i=0;
		TableRow row=null;
		
		for (final CinemaTime time:table.getTimeLines().get(position).times)
		{
		
			TextView timeView;
			if (!table.getTimeLines().get(position).ticketable)
				timeView=new TextView(context);
			else
			{
				Button btn=new Button(context);
				btn.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						String url="http://m.ticketon.kz/hallplan/"+time.getHash();
						String lUrl="http://www.google.kz";
						Dialog dialog = new Dialog(context);
			            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				timeView=btn;
			}
			
			timeView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.FILL_PARENT));
			timeView.setText(time.getStringTime()+" ");
			
			if (i%4==0)
			{
				row=new TableRow(context);
				row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT));
				holder.tableLayout.addView(row);
			}
			row.addView(timeView);
			i++;
		}
		return convertView;
	}
	
	class ViewHolder
	{
		TextView text;
		TableLayout tableLayout;
	}
}
