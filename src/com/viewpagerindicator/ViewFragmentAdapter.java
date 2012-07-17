package com.viewpagerindicator;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class ViewFragmentAdapter extends FragmentPagerAdapter
{
List<ViewFragment> viewList;
	
	
	public ViewFragmentAdapter(FragmentManager fm, List<ViewFragment> viewList)
	{
		super(fm);
		this.viewList=viewList;
	}

	@Override
	public ViewFragment getItem(int position)
	{
		return viewList.get(position);
	}

	@Override
	public int getCount()
	{
		return viewList.size();
	}
	
	@Override
	public String getPageTitle(int position)
	{
		return getItem(position).getTitle();
	}
	
}