package kz.crystalspring.android_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {
	static final String C_TAG = "OnBootReceiver";
	
	/**
	 * Запуск фонового процесса после перезагрузки девайса 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		C_Log.v(3, C_TAG, "onReceive - start");
	    if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			context.startService(new Intent(context, C_ServiceHelper.class));
	    }
	    C_Log.v(2, C_TAG, "onReceive - end");
	}
}