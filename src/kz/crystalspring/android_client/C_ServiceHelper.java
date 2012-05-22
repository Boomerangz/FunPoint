package kz.crystalspring.android_client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class C_ServiceHelper extends Service { // implements Runnable{
	static final String C_TAG = "CS_ServiceHelper";
  
	private AlarmManager alarmManager;
	static Context fContext =  null;
	

    @Override
	public void onCreate() {
    	C_Log.v(2, C_TAG, "onCreate - start");
		super.onCreate();
		Context fContext = this;
		
		// запуск периодического фонового процесса
		Intent intent = new Intent(fContext, OnAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(fContext, 0, intent, 0);
		this.alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		this.alarmManager.setRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + C_Vars.C_SERVICE_FIRST_RUN,
				C_Vars.C_SERVICE_INTERVAL, pendingIntent);
		C_Log.v(2, C_TAG, "onCreate - end");
	}
	  
	@Override
	public IBinder onBind(Intent arg0) {
		C_Log.v(3, C_TAG, "onBind");
		return null; 
	}
	
	@Override
	public boolean onUnbind(Intent arg0) {
		C_Log.v(3, C_TAG, "onUnbind");
		return true; 
	}
	
	@Override
	public void onDestroy() {
		C_Log.v(2, C_TAG, "onDestroy");
		if (alarmManager != null) {
			C_Log.v(2, C_TAG, "onDestroy - alarmManager - cancel");
			Intent intent = new Intent(fContext, OnAlarmReceiver.class);
			alarmManager.cancel(PendingIntent.getBroadcast(fContext, 0, intent, 0));
		}		   
		C_Log.v(2, C_TAG, "onDestroy");
	}
	   
};	