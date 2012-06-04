package com.boomerang.pending;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class PendingWorkAggregator
{
	List<PendingWork> workList=new ArrayList<PendingWork>(0);
	boolean runningNow=false;
	
	public void addTaskToQueue(PendingWork work)
	{
		workList.add(work);
		if (!runningNow)
			runNextTask();
	}
	
	public void addBackroundTaskToQueue(Runnable backgroundWork)
	{
		addTaskToQueue(new PendingWork(backgroundWork, null));
	}
	
	
	private void runNextTask()
	{
		if (workList.size()>0)
		{
			runningNow=true;
			QueueAsyncTask queueTask=new QueueAsyncTask(workList.get(0));
			queueTask.execute();
			workList.remove(0);
		}
		else
			runningNow=false;
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
			runNextTask();
		}
	}
}



