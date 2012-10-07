package com.viewpagerindicator;

import java.util.List;
import java.util.Map;

import kz.crystalspring.visualities.homescreen.TitleFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class ViewFragmentAdapter extends FragmentPagerAdapter
{
List<TitleFragment> viewList;
	
	
	public ViewFragmentAdapter(FragmentManager fm, List<TitleFragment> viewList)
	{
		super(fm);
		this.viewList=viewList;
	}
	
	@Override 
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position)
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
		return viewList.get(position).getTitle();
	}
	
}