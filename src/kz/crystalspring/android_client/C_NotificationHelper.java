package kz.crystalspring.android_client;

import kz.crystalspring.funpoint.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class C_NotificationHelper {
	static final String C_TAG = "CS_NotificationHelper";
	
	/** 
	 * Создание уведомления в статус-панели 
	 * @param context
	 * @param pTText - титул
	 * @param pCText - основной текст
	 * @param pNId - ид уведомления
	 * @return - true если успешно
	 */
	public static boolean CreateNotify(Context context, String pTText, String pCText, int pNId) {
		boolean vResult = false;
		try {
			C_Log.v(3, C_TAG, "CreateNotify pNId=" + pNId + " - start");		
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
//		int icon = android.R.drawable. sym_action_email;
			int icon = R.drawable.icon;
			CharSequence tickerText = pTText;
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_LIGHTS; 
//		   notification.sound = Uri.parse(«file:///sdcard/notification/ringer.mp3»);
//		   notification.defaults |= Notification.DEFAULT_VIBRATE; 
			CharSequence contentTitle = pTText;
			CharSequence contentText = pCText; 
			Intent notificationIntent = new Intent(context, C_MainActivity.class);
			notificationIntent.setAction(C_Vars.C_NOTIFY_ACTION_NAME + pNId);
			notificationIntent.putExtra(C_Vars.C_NOTIFY_EXTRA_NAME, pNId);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.flags |= Notification.FLAG_AUTO_CANCEL; 
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			mNotificationManager.notify(pNId, notification);
			vResult = true;
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "e:CreateNotify :"+e.getLocalizedMessage());
		}
		C_Log.v(2, C_TAG, "CreateNotify pNId=" + pNId + " - end");
		return vResult;
	}		  
	
}

