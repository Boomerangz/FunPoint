package kz.crystalspring.funpoint;

import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemCinema;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class funCinemaController extends ActivityController
{
	final String CINEMA_TIME_FILE = "json_cinema_info_zip";
	TextView tv1;
	// TextView timeTable;
	LinearLayout hallLayout;
	ListView timeList;
	ItemCinema cinema;

	funCinemaController(Activity context)
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
			timeList.setAdapter(new CinemaTimeTableAdapter(cinema.getTimeTable(), context.getApplicationContext()));
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
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.object_list_item_cinema, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView.findViewById(R.id.text);
		
		convertView.setMinimumHeight(60);
		convertView.setTag(holder);
		String st = Integer.toString(position) + ". " + toString();

		holder.text.setText(table.getTimeLines().get(position).filmName+" "+table.getTimeLines().get(position).date);
		return convertView;
	}
	
	class ViewHolder
	{
		TextView text;
	}
}
