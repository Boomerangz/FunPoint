package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;
import java.util.List;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemCinema.CinemaHall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
		loadCinemaTimeTable();
		showCinema(cinema);
		if (cinema.getHallTable() != null && cinema.getHallTable().size() > 0)
		{
			setCurrentHall(0);
		}
	}

	private void showCinema(ItemCinema cinema2)
	{
		tv1.setText(cinema.getName());
		// timeTable.setText(cinema.getAddress());
		android.view.View.OnClickListener listner = new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setCurrentHall(Integer.parseInt((String) ((Button) v).getTag()));
			}
		};
		// отображение залов
		if (cinema.isHallInfoFilled())
		{
			for (int i = 0; i < cinema.getHallTable().size(); i++)
			{
				CinemaHall hall = cinema.getHallTable().get(i);
				Button cinemaName = new Button(context);
				cinemaName.setText(hall.getName());
				cinemaName.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				cinemaName.setTag(Integer.toString(i));
				cinemaName.setOnClickListener(listner);
				hallLayout.addView(cinemaName);
				hallLayout.refreshDrawableState();
			}
		} else
		{

		}
	}

	protected void setCurrentHall(int i)
	{
		timeList.setAdapter(new TimeTableAdapter(context, cinema.getHallTable()
				.get(i).getTimeTable()));
	}

	@Override
	protected void onPause()
	{

	}

	private void loadCinemaTimeTable()
	{
		try
		{
			JSONObject jCinemaInfo = findObjectInJSONZIP(cinema.getId(),
					CINEMA_TIME_FILE);
			if (jCinemaInfo != null)
			{
				cinema.loadHallTableFromJSON(jCinemaInfo
						.getJSONArray("halltable"));
			} else
				cinema.setHallInfoNotFilled();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

	}
}
