package com.viewpagerindicator;

import kz.crystalspring.funpoint.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ViewFragment extends Fragment
{
	View view;
	String title;

	public ViewFragment(View v, String title)
	{
		view = v;
		this.title = title;
	}

	public ViewFragment()
	{
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// return view;
//		TextView text = new TextView(getActivity());
//		text.setGravity(Gravity.CENTER);
//		text.setText("12312313");
//		text.setTextSize(20 * getResources().getDisplayMetrics().density);
//		text.setPadding(20, 20, 20, 20);
//		text.setTextColor(getResources().getColor(R.color.blue));
//
//		LinearLayout layout = new LinearLayout(getActivity());
//		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT));
//		layout.setGravity(Gravity.CENTER);
//		layout.addView(text);

		try
		{
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return view;
	}

	public String getTitle()
	{
		return title;
	}
}
