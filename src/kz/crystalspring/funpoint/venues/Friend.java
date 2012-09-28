package kz.crystalspring.funpoint.venues;

import android.content.Context;
import android.view.View;

public abstract class Friend 
{
	String name;
	Friend(String name)
	{
		this.name=name;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public abstract View getView(Context context);
}
