package com.boomerang.pending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;
import android.util.Log;

public class PendingWorkAggregator
{
	List workList = new ArrayList(0);
	private boolean ableToDo;


	private AtomicInteger runningNowCount;
	
	
	public PendingWorkAggregator()
	{
		runningNowCount=new AtomicInteger(0);
		// AsyncTask iterator=new AsyncTask()
		// {
		// @Override
		// protected Object doInBackground(Object... params)
		// {
		// while (true)
		// {
		// if (!getRunningNow())
		// runNextTask();
		// try
		// {
		// Thread.sleep(1000);
		// } catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// }
		// }
		// };
		// iterator.execute();
	}

	public void addTaskToQueue(PendingWork work)
	{
		prAddTask(work);
	}

	public void addTaskToQueue(Runnable task, Runnable postTask)
	{
		prAddTask(new PendingWork(task, postTask));
	}

	public void addPriorityTask(Runnable task, Runnable postTask)
	{
		prAddPriorityTask(new PendingWork(task, postTask));
	}

	private synchronized void prAddTask(Object work)
	{
		workList.add(work);
		if (!getRunningNow())
			runNextTask();
	}

	private synchronized void prAddPriorityTask(Object work)
	{
		ArrayList newWorkList = new ArrayList();
		newWorkList.add(work);
		newWorkList.addAll(workList);
		workList = newWorkList;

		if (!getRunningNow())
			runNextTask();
	}

	public void addBackroundTaskToQueue(Runnable backgroundWork)
	{
		addTaskToQueue(new PendingWork(backgroundWork, null));
	}

	private static final int THREADS_COUNT=100;
	private void runNextTask()
	{
		if (ableToDo&&runningNowCount.get()<THREADS_COUNT)
		{
			Object oTask = null;
			synchronized (workList)
			{
				if (workList.size() > 0)
				{
					oTask = workList.get(0);
					workList.remove(0);
				} 
			}
			if (oTask != null && PendingWork.class.isInstance(oTask))
			{
				QueueAsyncTask queueTask = new QueueAsyncTask((PendingWork) oTask);
				queueTask.execute();
				runNextTask();
			}
		}
	}

	private synchronized boolean getRunningNow()
	{
		return runningNowCount.get()>0;
	}

	class QueueAsyncTask extends AsyncTask
	{
		PendingWork work;

		QueueAsyncTask(PendingWork work)
		{
			this.work = work;
		}

		@Override
		protected Object doInBackground(Object... params)
		{
			runningNowCount.incrementAndGet();
			Log.w("PendingWork", runningNowCount.toString());
			if (work != null)
				work.runBackgroundTask();
			return null;
		}

		@Override
		protected void onPostExecute(Object object)
		{
			if (work != null)
				work.runPostTask();
			runningNowCount.decrementAndGet();
			Log.w("PendingWork_", runningNowCount.toString());
			runNextTask();
		}
	}
	
	public boolean isAbleToDo()
	{
		return ableToDo;
	}

	public void setAbleToDo(boolean ableToDo)
	{
		this.ableToDo = ableToDo;
	}

	public void stopQueue()
	{
		setAbleToDo(false);
	}
}
