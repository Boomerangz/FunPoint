package kz.crystalspring.android_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class OnAlarmReceiver extends BroadcastReceiver {
	static final String C_TAG = "OnAlarmReceiver";
	
	/**
	 * фоновая обработка - синхронизация, запуск уведомлений, обновление виджетов
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Boolean vEnabled = false;
		C_Log.v(3, C_TAG, "onReceive - start");
		try {
			C_DBHelper dbHelper = new C_DBHelper(context);
			SQLiteDatabase vDb = dbHelper.getWritableDatabase();
			if (vDb != null) {
				try {
					dbHelper.SetVar(vDb, C_Vars.C_VAR_SERVICE_A_DATE, C_Utils.IntToDate(0));
					String vS = dbHelper.GetVar(vDb, C_Vars.C_VAR_SERVICE_STATE, "ON");
					vEnabled = vS.equals("ON");
					// сохраняем ошбики из журнала в базу
					String vLogData = C_Log.GetErrLog();
					if (vLogData.length() > 0) {
						dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_LOG, vLogData.getBytes());
					}
					// проверка и запуск уведомлений
					dbHelper.FireNotifs(vDb, context, 1, null);
					vDb.close();
				} finally {
					dbHelper.close();
				}
			} else {
				C_Log.v(0, C_TAG, "onReceive: getWritableDatabase is null! - end");
			}
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "e:onReceive Db err:"+e.getLocalizedMessage());
		}
		if (vEnabled){
			C_Log.v(2, C_TAG, "onReceive - exec");
			try {
				// UpdateAllWidgets вызывается после синхронизации!!
				C_NetHelper.SyncData(context, false, true);
			} catch (Exception e) {
				C_Log.v(0, C_TAG, "e:onReceive Sync err:"+e.getLocalizedMessage());
			}
		} else {			
			C_Log.v(3, C_TAG, "onReceive - servce is OFF");
		}
		C_Log.v(3, C_TAG, "onReceive - end");
	}
	
}
