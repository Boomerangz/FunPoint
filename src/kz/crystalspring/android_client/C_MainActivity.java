package kz.crystalspring.android_client;

import java.security.PrivateKey;
import javax.crypto.SecretKey;

import kz.crystalspring.funpoint.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class C_MainActivity extends Activity{
	static final String C_TAG = "CS_MainActivity";
	public static WebView fWebView;
	public static Context fContext;
	public static Context fThis;
//	private String fLastPage = null;
	private boolean fIsStartApp = true;
	private static ProgressDialog fProgressDialog = null;
//	private static RelativeLayout fProgressBar;
	public static C_MainActivity fMainActivityInstance;
	public static SecretKey fDeviceSecretKey; // ключ под которым лежат пользовательские данные
	public static PrivateKey fDevicePrivateKey; // ключ для подписывания пользовательских данных


	/**
	 * Очистка ключей и удаление кэшированных данных
	 * Вызывается при завершении сессии пользвоателя (вызод из приватной части приложения) 
	 */
	public static void ClearKeys() {
		C_Log.v(2, C_TAG, "ClearKeys");
		fDeviceSecretKey = null;
		fDevicePrivateKey = null;
		C_FileHelper.DeleteCachedDecryptedFiles(fContext);
	}
	
	
    /**
     * Проверка на первый запуск  
     * Если это первый запуск, то производится копирование файлов из Assets в Documents
     * и отправка информации об устройстве на сервер
     */
	private void CheckForFirstStart() {
		C_Log.v(3, C_TAG, "CheckForFirstStart - start");		
		C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "CheckForFirstStart getWritableDatabase is null! - end");
			return;
		}
		try{
			if (! dbHelper.GetVar(vDb, C_Vars.C_VAR_VERSION, "-").equals(C_Vars.C_VERSION)) {
				C_Log.v(1, C_TAG, "CheckForFirstStart: first start");
				C_FileHelper.CopyAssetFiles(fContext);
//				C_FileHelper.UnzipAssetFiles(fContext, C_Vars.C_ZIP_ASSET_FILES);
				dbHelper.SetVar(vDb, C_Vars.C_VAR_VERSION, C_Vars.C_VERSION);
				dbHelper.SetVar(vDb, C_Vars.C_VAR_SERVICE_STATE, "ON");				
				dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_DEVICE, C_Utils.GetDeviceInfo().getBytes());
				dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_VERSION, C_Vars.C_VERSION.getBytes());
		   	}			
//			dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_DEVICE_TYPE, "A".getBytes());
			vDb.close();
		} catch (Exception e) {
    		C_Log.v(0, C_TAG, "CheckForFirstStart err:" + e.getMessage());
    	}
		finally {
			dbHelper.close();
		}
		
//		if (vIsFirstStart) {
//			// первая синхронизация:
//			здесь нельзя запускать синхронизацию, запуск идет позже из страницы вывода ошибки
//			CS_NetHelper.SyncData(fContext, true, false);
//			Intent v = new Intent();		
//			v.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -2);
//			setResult(RESULT_OK, v);
//		}
	
		C_Log.v(3, C_TAG, "CheckForFirstStart - end");
	}
	
	@Override
	public void onStop() {
		C_Log.v(3, C_TAG, "onStop");
		ClearKeys(); // сброс сессионных ключей
	    super.onStop();
	}

//	@Override
//	public void onPause() {
//		CS_Log.v(3, C_TAG, "onPause");
//	    super.onPause();
//	}
	
	@Override
	public void onResume() {
		C_Log.v(3, C_TAG, "onResume");
	    super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		C_Log.v(3, C_TAG, "onCreate - start");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		fContext = getApplicationContext();
		fThis = this;
		fMainActivityInstance = this;
		fWebView = (WebView) findViewById(R.id.webview);
		
//		fProgressBar = (RelativeLayout)  (R.id.main_ProgressBar);
		
		fWebView.setWebViewClient(new WebViewClient() {
		    @Override  
		    public void onPageFinished(WebView view, String url){
				super.onPageFinished(view, url);
				C_Log.v(2, C_TAG, "onPageFinished url=" + url);
//				fLastPage = url;
				if (C_Vars.C_SHOW_PROGRESS_BAR_IN_MAIN_WIN) {
					HideProgressDialog(); 
				}
	        }  
		});
		fWebView.getSettings().setJavaScriptEnabled(true);
		fWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); //?
		fWebView.addJavascriptInterface(new C_JavascriptInterface(this), "JSHnd");
		
//		C_JavascriptInterface c = new C_JavascriptInterface(this);
//		
//		c.ExecSQL("drop table cc");
//		c.ExecSQL("create table cc (f1 integer, f2 varchar(20))");
//		c.ExecSQL("insert into cc values (1,'ssss');");		
//		c.ExecSQL("insert into cc values (2,'sыуар лдцу рдлцрудлцтадл д лвтд2333лллаа');");
//		String s = c.ExecSQL_Select("select '+' || f1 || '=' || f2 from cc");
		
//		c.SaveDataToFile("fname", "проверка");
//		String s = c.LoadDataFromFile("fname1");
//		C_Log.v(1, C_TAG, "onCreate =" + s + "=");
//		s = c.LoadDataFromFile("fname");
//		C_Log.v(1, C_TAG, "onCreate =" + s + "=");
		
		
		CheckForFirstStart();
		this.startService(new Intent(fContext, C_ServiceHelper.class)); //  повторный запуск не запускает дополнительный инстанс 
		C_Log.v(3, C_TAG, "onCreate - end");
	}

	/**
	 * Перехват нажатий клавишей, по клавиже Back делается переход баружера на предыдущую страницу
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		C_Log.v(3, C_TAG, "onKeyDown keyCode=" + keyCode);
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			C_JavascriptInterface.OnPressKeyBack();
			C_Log.v(3, C_TAG, "onKeyDown goBack");
			return true;
		}
//		if ((keyCode == KeyEvent.KEYCODE_BACK) && fWebView.canGoBack()) {
//			fWebView.goBack();
//			C_Log.v(2, C_TAG, "onKeyDown goBack");
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Переход браужера на указанную страницу
	 * @param pPageName - имя страницы (имя файла)
	 */
	public void GotoPage(String pPageName){
		C_Log.v(3, C_TAG, "GotoPage pPageName=" + pPageName + " - start");
		String vURL = C_Vars.C_PROVIDER_ROOT_URL;
		if(pPageName == null){
			vURL = vURL + C_Vars.C_START_PAGE;
		}else{
			vURL = vURL + pPageName;
		}
		HideProgressDialog(); // на случай если из javascript забыли закрыть
		if (C_Vars.C_SHOW_PROGRESS_BAR_IN_MAIN_WIN) {
			ShowProgressDialog(null);
		}
		fWebView.loadUrl(vURL);	
		C_Log.v(2, C_TAG, "GotoPage pPageName=" + pPageName + " Url=" + vURL);
	}

	/**
	 * Получеие адреса перехода по полученному событию Intent (из БД)
	 * (при получении события от виджета или от уведомления)
	 * @return - адрес страницы (обычно имя файла)
	 */
	public String GetPageNameByIntent() {
		C_Log.v(3, C_TAG, "GotoPage GetPageNameByIntent - start");
		String vUrl = null;
		int vNotifyID = C_MainActivity.this.getIntent().getIntExtra(C_Vars.C_NOTIFY_EXTRA_NAME, -1);
		if(vNotifyID != -1) {
			// open from notification or widget
			C_DBHelper dbHelper = new C_DBHelper(fContext);
			SQLiteDatabase vDb = dbHelper.getWritableDatabase();
			if (vDb == null) {
				C_Log.v(0, C_TAG, "GetPageNameByIntent getWritableDatabase is null! vNotifyID=" + vNotifyID + " - end");
				return C_Vars.C_START_PAGE;
			}
			try{
				vUrl = dbHelper.GetWidgetUrlByNotifyID(vDb, vNotifyID, C_Vars.C_START_PAGE);
			} finally{
				dbHelper.close();
			}
		}
		C_Log.v(2, C_TAG, "GetPageNameByIntent vNotifyID=" + vNotifyID + " vUrl=" + vUrl);
		return vUrl;
	}
	
	@Override
	public void onStart(){
		C_Log.v(3, C_TAG, "onStart");
		super.onStart();	
		ClearKeys(); // сброс сессионных ключей
		String vUrl = GetPageNameByIntent();
		if ((vUrl != null) | fIsStartApp) {
			fIsStartApp = false;
			GotoPage(vUrl);
		} else {
			/* - это делается в пользовательской процедуре по событию открытия окна 
			if (fLastPage != null) {
				if (fLastPage.contains(C_Vars.C_CABINET_PREFIX)) {
					C_Log.v(2, C_TAG, "onResume - page " + fLastPage + " is expired, go to login page");
					GotoPage(C_Vars.C_CABINET_PAGE);
				}
			}
			*/
			// выхывает на текущей странице процедуру try{OnAppShowProc();}catch(e){} :
			C_JavascriptInterface.OnShowMainWin();
		}
	}

	@Override
	protected void onPause() {
		C_Log.v(3, C_TAG, "onPause");
		super.onPause();
		// выхывает на текущей странице процедуру try{OnAppHideProc();}catch(e){} :
		C_JavascriptInterface.OnHideMainWin();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		C_Log.v(3, C_TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult vResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (vResult != null) {
			String vContent = vResult.getContents();
			if (vContent != null) {
				C_Log.v(1, C_TAG, "onScanResult: Format: " + vResult.getFormatName() + " Contents: " + vContent);
				C_JavascriptInterface.OnScanBarCode(vResult.getFormatName(), vContent);
			} else {
				C_Log.v(0, C_TAG, "onScanResult: error: Content is null!");
				C_JavascriptInterface.OnScanBarCode("error", "code is null!");
			}
		}
	}
	
	
	 
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.m_quit:
    		C_Log.v(2, C_TAG, "onOptionsItemSelected item=Quit");
    		// закрыть процесс прослушивателя gps если запущен
    		if (C_JavascriptInterface.fLocationHelper != null) {
    			C_JavascriptInterface.fLocationHelper.cancel();
    		}
    		finish();
            return true;
        case R.id.m_home:
        	C_Log.v(2, C_TAG, "onOptionsItemSelected item=Home");
        	GotoPage(C_Vars.C_START_PAGE);
            return true;
        case R.id.m_sync:
        	C_Log.v(2, C_TAG, "onOptionsItemSelected item=Sync");
   	      	C_NetHelper.SyncData(this, true, false);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	
	public static boolean ShowProgressDialog(String pText) {
		if (fProgressDialog == null) {
			String vS = fThis.getString(R.string.loading_msg);
			if (pText != null) {
				if (pText.length() > 0) {
					vS = pText;
				}
			}
			fProgressDialog = ProgressDialog.show(fThis, "", vS, true);
			return true;
		}
		return false;
		
//		if (fProgressBar != null) fProgressBar.setVisibility(View.VISIBLE);
//		return true;
	}
	
	public static boolean HideProgressDialog() {
		if (fProgressDialog != null){
			fProgressDialog.dismiss();
			fProgressDialog = null;
			return true;
		}
		return false;
//		if (fProgressBar != null) fProgressBar.setVisibility(View.INVISIBLE);		
//		return true;
	}
}
	   
	   
