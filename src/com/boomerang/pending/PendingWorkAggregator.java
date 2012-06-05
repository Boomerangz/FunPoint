package com.boomerang.pending;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class PendingWorkAggregator
{
	List workList=new ArrayList(0);
	boolean runningNow=false;
	
	
	public PendingWorkAggregator()
	{
		AsyncTask iterator=new AsyncTask()
		{
			@Override
			protected Object doInBackground(Object... params)
			{
				while (true)
				{
					if (!getRunningNow())
						runNextTask1();
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}	
			}
		};
		iterator.execute();
	}
	
	
	public void addTaskToQueue(PendingWork work)
	{
		prAddTask(work);
	}
	
	public void addTaskToQueue(AsyncTask work)
	{
		prAddTask(work);
	}
	
	private synchronized void prAddTask(Object work)
	{
		workList.add(work);
	}
	
	public void addBackroundTaskToQueue(Runnable backgroundWork)
	{
		addTaskToQueue(new PendingWork(backgroundWork, null));
	}
	
	
	private void runNextTask1()
	{
		if (workList.size()>0)
		{
			 setRunningNow(true);
			Object oTask=workList.get(0);
			if (PendingWork.class.isInstance(oTask))
			{
				QueueAsyncTask queueTask=new QueueAsyncTask((PendingWork)oTask);
				queueTask.execute();
			}
			else
			if (AsyncTask.class.isInstance(oTask))
			{
				AsyncTask task=(AsyncTask)oTask;
				task.execute();
			}
			workList.remove(0);
		}
		else
			 setRunningNow(false);
	}
	
	private synchronized void setRunningNow(boolean running)
	{
		runningNow=running;
	}
	
	private synchronized boolean getRunningNow()
	{
		return runningNow;
	}
	
	class QueueAsyncTask extends AsyncTask
	{
		PendingWork work;
		QueueAsyncTask(PendingWork work)
		{
			this.work=work;
		}

		@Override
		protected Object doInBackground(Object... params)
		{
			if (work!=null)
				work.runBackgroundTask();
			return null;
		}
		
		@Override
		protected void onPostExecute(Object object)
		{
			if (work!=null)
				work.runPostTask();
		}
	}
	
}



