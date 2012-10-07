package com.viewpagerindicator;

import kz.crystalspring.visualities.homescreen.TitleFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ViewFragment extends TitleFragment
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
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
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

	@Override
	public View getView()
	{
		FrameLayout ll = (FrameLayout) super.getView();
		if (ll==null)
			Log.w("ViewFragment", "LL=null");
		return ll;
	}

	@Override
	public String getTitle()
	{
		return title;
	}
}
