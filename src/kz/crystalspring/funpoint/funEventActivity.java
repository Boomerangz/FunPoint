package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.CinemaTimeTable.CinemaTime;
import kz.crystalspring.funpoint.CinemaTimeTableAdapter.ViewHolder;
import kz.crystalspring.funpoint.events.Event;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.visualities.LoadingImageView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class funEventActivity extends Activity
{
	Event event;
	TextView eventNameText;
	TextView eventDescriptionText;
	LoadingImageView lImageView;
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_event_layout);
		if (event==null)
		{
			event=MainApplication.eventContainer.getEventById(MainApplication.selectedEventId);
		}
		eventNameText=(TextView) findViewById(R.id.event_name);
		eventDescriptionText=(TextView) findViewById(R.id.event_desc);
		lImageView=(LoadingImageView) findViewById(R.id.loading_imageview);
		listView=(ListView) findViewById(R.id.listView1);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		eventNameText.setText(event.getName());
		eventDescriptionText.setText(Html.fromHtml(event.getDescription()));
		
		if (event.getImage()!=null)
		{
			lImageView.setDrawable(event.getImage());
		}
		else
		{
			final Event e=event;
			Runnable preTask=new Runnable()
			{
				@Override
				public void run()
				{
						Drawable dr=HttpHelper.loadPictureByUrl(e.getImageUrl());
						e.setImage(dr);
				}
			};
			Runnable postTask=new Runnable()
			{
				@Override
				public void run()
				{
					lImageView.setDrawable(e.getImage());	
				}
			};
			
			MainApplication.pwAggregator.addPriorityTask(preTask, postTask);
		}
		event.loadPlaceTable();
		EventTimeTableAdapter adapter=new EventTimeTableAdapter(event.getTimeTable(), this);
		listView.setAdapter(adapter);
	}
}


class EventTimeTableAdapter extends BaseAdapter
{
	CinemaTimeTable table;
	Context context;
	
	public EventTimeTableAdapter(CinemaTimeTable table,Context context)
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

		holder.text.setText(table.getTimeLines().get(position).getTitle()+" "+table.getTimeLines().get(position).getStrDate());
		holder.text.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MapItem item=MainApplication.mapItemContainer.getItemById(table.getTimeLines().get(position).filmId);
				MainApplication.mapItemContainer.setSelectedItem(item);
				Intent i = new Intent(context, funObjectDetail.class);
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
