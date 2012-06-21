package kz.crystalspring.funpoint;

import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemCinema;

import android.app.Activity;
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
		showCinema(cinema);
	}

	private void showCinema(ItemCinema cinema2)
	{
		tv1.setText(cinema.getName());
		if (cinema.isHallInfoFilled())
		{
		} else
		{
		}
	}

	@Override
	protected void onPause()
	{

	}
}
