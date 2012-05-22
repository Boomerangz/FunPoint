package kz.crystalspring.android_client;

import kz.crystalspring.pointplus.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class C_WidgetConfigActivity extends Activity {
	static final String C_TAG = "CS_WidgetConfigActivity";

	int fAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	public static Context fContext = null; 
	private WebView fWebView;
	protected ProgressDialog fPD = null;
//	private RelativeLayout fProgressBar;
 
	/**
	 * Выбор нового виджета, вызывается из диалога выбора виджета через javascript 
	 * @param pNotifyId - ид выбранного виджета
	 */
	public void SelectWidget(int pNotifyId) {
		C_Log.v(2, C_TAG, "SelectWidget - pNotifyId=" + pNotifyId + " - start");
 		//сохранение связки widget_id к notify_id: 
		C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb != null) {
			try{
				dbHelper.UpdateOrInsert_T_WIDGETS_AT(vDb,fAppWidgetId, pNotifyId);
				String vSData = "{\"N\":" + Integer.toString(pNotifyId) + ",\"D\":" + C_Utils.IntToDate(0) + "\"}";
				dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_SELECT_WIDGET, vSData.getBytes());
				vDb.close();
			}finally{
				dbHelper.close();
			}
		} else {
			C_Log.v(0, C_TAG, "SelectWidget: getWritableDatabase is null!");
		}
		
		// обновление нового виджета:
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(fContext);
        C_WidgetProvider.UpdateAppWidget(fContext, appWidgetManager, fAppWidgetId);
        
        // устанавливаем результат диалога выбора виджета: 
		Intent resultValue = new Intent();		
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, fAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
		C_Log.v(2, C_TAG, "SelectWidget - mAppWidgetId=" + fAppWidgetId + " - end");	
	}
		
	/**
	 * создание диалога выбора нового виджета
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		C_Log.v(2, C_TAG, "onCreate - start");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.widget_config);
		fContext = this;
		
		setResult(RESULT_CANCELED); // RESULT_CANCELED результат диалога по умолчанию
				
		fWebView = (WebView) findViewById(R.id.wc_webview);  
		fWebView.setWebViewClient(new WebViewClient() {  
		    @Override  
		    public void onPageFinished(WebView view, String url){
		    	C_Log.v(2, C_TAG, "onCreate - onPageFinished url=" + url);
		    	if (C_Vars.C_SHOW_PROGRESS_BAR_IN_MAIN_WIN) {	
		    		if (fPD != null){
		    			fPD.dismiss();
		    			fPD = null;
		    		}
		    	}
	        }  
		});
		
		fWebView.getSettings().setJavaScriptEnabled(true);
		fWebView.addJavascriptInterface(new C_JavascriptInterface(this), "JSHnd");
		if (C_Vars.C_SHOW_PROGRESS_BAR_IN_MAIN_WIN) {	
			fPD = ProgressDialog.show(this, "", this.getString(R.string.loading_msg), true);
		}
		C_Log.v(2, C_TAG, "onCreate - load config dialog");
		fWebView.loadUrl(C_Vars.C_WIDGET_CONFIG_URL);		
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			fAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
		} else {
			C_Log.v(0, C_TAG, "e:onCreate - intent extras is null");
		}
		if (fAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
			C_Log.v(0, C_TAG, "e:onCreate - fAppWidgetId is invalid, finish");
		}
		C_Log.v(2, C_TAG, "onCreate - end");

	}
}