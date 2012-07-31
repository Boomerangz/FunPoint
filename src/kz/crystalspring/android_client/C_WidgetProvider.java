package kz.crystalspring.android_client;

import kz.crystalspring.funpoint.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class C_WidgetProvider extends AppWidgetProvider {
	static final String C_TAG = "CS_WidgetProvider";
	
	/**
	 * Обнволение данных одного виджета
	 * @param fContext
	 * @param appWidgetManager - виджет-менеджер
	 * @param pAppWidgetId - ид виджета
	 */
	public static void UpdateAppWidget(Context fContext, AppWidgetManager appWidgetManager, int pAppWidgetId) {
		C_Log.v(3, C_TAG, "UpdateAppWidget - pAppWidgetId=" + pAppWidgetId + " - start");
		int vNotyfyId = -1;
		String vNotifyText = null;
		byte[] vNotifyImg = null;
		C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try{
			String[] vNotifyData = new String[3];
			vNotifyImg = dbHelper.GetWidgetDataByWidgetID(db, pAppWidgetId, vNotifyData);
			if (vNotifyImg != null) {
				vNotyfyId = Integer.parseInt(vNotifyData[0]);
				vNotifyText = vNotifyData[1];				
			} else { // в базе нет данных для этого виджета
				vNotyfyId = -1;
//! - ошибка в SDK! Логировать не нужно	C_Log.v(0, C_TAG, "e: UpdateAppWidget - pAppWidgetId = " + pAppWidgetId + " - NotyfyId is not found in DB");//!!!
			}
		} finally {
			dbHelper.close();
		}
		
		if (vNotyfyId != -1) {
			C_Log.v(3, C_TAG, "UpdateAppWidget - pAppWidgetId=" + pAppWidgetId + " vNotyfyId=" + vNotyfyId); 
			RemoteViews vWidgetRemoteViews = new RemoteViews(fContext.getPackageName(), R.layout.widget);
			Intent vWidgetIntent = new Intent(fContext, C_MainActivity.class);
    	
			vWidgetIntent.putExtra(C_Vars.C_NOTIFY_EXTRA_NAME, vNotyfyId);
			vWidgetIntent.setAction(C_Vars.C_NOTIFY_ACTION_NAME + vNotyfyId);
			PendingIntent vPendingIntent = PendingIntent.getActivity(fContext, 0, vWidgetIntent, 0);
		
			vWidgetRemoteViews.setOnClickPendingIntent(R.id.widget_layout, vPendingIntent);
		
			String[] vLines = vNotifyText.split("[|]");
			if(vLines.length>0) vWidgetRemoteViews.setTextViewText(R.id.widget_line1, vLines[0]); 
			if(vLines.length>1) vWidgetRemoteViews.setTextViewText(R.id.widget_line2, String.valueOf(vLines[1])); 
			if(vLines.length>2) vWidgetRemoteViews.setTextViewText(R.id.widget_line3, String.valueOf(vLines[2])); 

			if(vNotifyImg!=null){
				Bitmap bmp = BitmapFactory.decodeByteArray(vNotifyImg, 0, vNotifyImg.length);
				if(bmp!=null){
					vWidgetRemoteViews.setImageViewBitmap(R.id.img, bmp);
				}
			}
			
			appWidgetManager.updateAppWidget(pAppWidgetId, vWidgetRemoteViews);
		}
		C_Log.v(3, C_TAG, "UpdateAppWidget - end");
	}	
	
	
	/**
	 * обновление данных всех виджетов
	 * @param context
	 */
	public static void UpdateAllWidgets(Context context) {
		C_Log.v(3, C_TAG, "UpdateAllWidgets - end");
		AppWidgetManager vAppWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName vWidgetProvider = new ComponentName(context, C_WidgetProvider.class);	  
        int[] vAppWidgetIds = vAppWidgetManager.getAppWidgetIds(vWidgetProvider);
//	    bug: vAppWidgetManager.getAppWidgetIds возвращает все vAppWidgetIds, включая отмененные
 	    for (int i=0; i<vAppWidgetIds.length; i++) { 	    	
 	    	UpdateAppWidget(context, vAppWidgetManager, vAppWidgetIds[i]);
 	    }
 	    C_Log.v(3, C_TAG, "UpdateAllWidgets - end");
	}	
	    
         	    
	@Override
	public void onUpdate(Context context,
						 AppWidgetManager appWidgetManager,
						 int[] appWidgetIds) {
		C_Log.v(3, C_TAG, "onUpdate appWidgetIds len=" + appWidgetIds.length);
	    for (int i=0; i<appWidgetIds.length; i++) {
 	    	UpdateAppWidget(context, appWidgetManager, appWidgetIds[i]);
 	    } 
	}
	
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    	C_Log.v(3, C_TAG, "onDeleted");
		C_DBHelper dbHelper = new C_DBHelper(context);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "onDeleted: getWritableDatabase is null! - end");
			return;
		}
		try {
			for (int i=0; i<appWidgetIds.length; i++) {
				int vAppWidgetId = appWidgetIds[i];
				dbHelper.Delete_T_WIDGETS_AT(vDb, vAppWidgetId);
				String vSData = "{\"N\":"+Integer.toString(vAppWidgetId)+",\"D\":"+C_Utils.IntToDate(0)+"\"}";
				dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_DELETE_WIDGET, vSData.getBytes());
				C_Log.v(2, C_TAG, "onDeleted delete appWidgetId=" + vAppWidgetId);
			}
			vDb.close();
		} finally {
			dbHelper.close();
		}
//		super.onDeleted(context, appWidgetIds);
    	C_Log.v(3, C_TAG, "onDeleted - end");
    }
	
	/**
	 * Для того чтобы при удалении виджета вызывался onDeleted
	 * SDK v1.5 fix that doesn't call onDelete Action
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
	// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		C_Log.v(3, C_TAG, "onReceive - action=" + action);
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			}
//		} else if (AppWidgetManager.ACTION_APPWIDGET_PICK.equals(action)) {
		} else {
			super.onReceive(context, intent);
		}
	}
    
	@Override
	public void onEnabled(Context context) {
		C_Log.v(3, C_TAG, "onEnabled");
		super.onEnabled(context);
	}
	
	
}
