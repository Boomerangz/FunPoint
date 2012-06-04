package com.boomerang.pending;

public class PendingWork
{
	Runnable backgroundTask;
	Runnable postTask;
	
	PendingWork(Runnable btask, Runnable pstTask)
	{
		backgroundTask=btask;
		postTask=pstTask;
	}
	
	public void runBackgroundTask()
	{
		if (backgroundTask!=null)
			backgroundTask.run();
	}
	
	public void runPostTask()
	{
		if (backgroundTask!=null)
			backgroundTask.run();
	}
}
