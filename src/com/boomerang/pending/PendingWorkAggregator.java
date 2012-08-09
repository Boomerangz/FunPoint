package com.boomerang.pending;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class PendingWorkAggregator
{
	List workList = new ArrayList(0);
	boolean runningNow = false;
	boolean ableToDo = true;
	
	public PendingWorkAggregator()
	{
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
		ArrayList newWorkList=new ArrayList();
		newWorkList.add(work);
		newWorkList.addAll(workList);
		workList=newWorkList;
		
		if (!getRunningNow())
			runNextTask();
	}

	public void addBackroundTaskToQueue(Runnable backgroundWork)
	{
		addTaskToQueue(new PendingWork(backgroundWork, null));
	}

	private void runNextTask()
	{   
		if (ableToDo)
		{
		Object oTask=null;
		synchronized (workList)
		{
			if (workList.size() > 0)
			{
					setRunningNow(true);
					oTask = workList.get(0);
					workList.remove(0);
			} else
				setRunningNow(false);
		}
		if (oTask!=null&&PendingWork.class.isInstance(oTask))
		{
			QueueAsyncTask queueTask = new QueueAsyncTask(
					(PendingWork) oTask);
			queueTask.execute();
			runNextTask();
		}
		}
	}

	private synchronized void setRunningNow(boolean running)
	{
		runningNow = running;
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
			this.work = work;
		}

		@Override
		protected Object doInBackground(Object... params)
		{
			if (work != null)
				work.runBackgroundTask();
			return null;
		}

		@Override
		protected void onPostExecute(Object object)
		{
			if (work != null)
				work.runPostTask();

		}
	}

	public void stopQueue()
	{
		ableToDo=false;
		setRunningNow(false);
	}

	public void setAbleToDo(boolean b)
	{
		ableToDo=true;
		if (!getRunningNow())
		{
			runNextTask();
		}
	}
}
