package kz.crystalspring.android_client;

import java.io.File;


//import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StatFs;
//import android.provider.ContactsContract;
import android.provider.Settings;
import android.widget.Toast;

public class C_JavascriptInterface {
	static final String C_TAG = "CS_JavascriptInterface";
	private static Context fContext;
	static C_LocationHelper fLocationHelper = null;
	static Location fLastLocation;
	
	/** Instantiate the interface and set the context */
	C_JavascriptInterface(Context c) {
    	fContext = c;
	}

	/**
	* Отображение сообщения Toast 
	* @param toast - текст сообщения
	*/
	public void ShowToast(String toast) {
		C_Log.v(2, C_TAG, "showToast toast=" + toast);
		Toast.makeText(fContext, toast, Toast.LENGTH_SHORT).show();
    }
	
	/**
	* Проверка введенного пина по сохраненному ранее дайджесту, если ОК то расшифровка и 
	* сохранение в переменных класса на время сессии индивидуального и приватного ключей   
	* @param pPin - пин (пароль) доступа к приватному разделу приложения
	* @return результат проверки пина
	*/
	public boolean SetSessionPin(String pPin) {
		boolean vResult = false;
		C_Log.v(3, C_TAG, "SetSessionPin - start");
		try{
			vResult = C_SecurityHelper.SetSessionPin(fContext, pPin);
		}catch(Exception e){
			C_Log.v(0, C_TAG, "e:SetSessionPin err:"+e.getMessage());
		}
		C_Log.v(3, C_TAG, "SetSessionPin - res=" + vResult + " - end");	
		return vResult;
    }
	
	/**
	* Установка нового или замена пина (если указан pOldPin) 
	* Если пин ранее уже был введен, делается перешифровка и сохранение новго дайджеста пина, 
	* индивидуального и приватного ключа в БД. 
	* Проверка старого пина производится по сохраненному ранее дайджесту   
	* @param pOldPin - старый пин (пароль) для доступа к приватному разделу приложения (если новый пин, то уазывается "")
	* @param pNewPin - новый пин (пароль) доступа к приватному разделу приложения
	* @return результат операции
	*/	
	public boolean SaveNewPin (String pOldPin, String pNewPin) {
		C_Log.v(3, C_TAG, "SaveNewPin - start");	
		boolean vResult = false;
		try {
			vResult = C_SecurityHelper.SaveNewPin(fContext, pOldPin, pNewPin);
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "e:SaveNewPin err:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "SaveNewPin - res=" + vResult + " - end");		
		return vResult;
	}
	
	/**
	* Проверка был ли уже введен пин-код, т.е. доступны ли индивидуальный и приватный ключи. 
	* Вызывается со страниц приватного раздела приложения    
	* @return результат операции
	*/
	public boolean IsPinExists(){
		boolean vResult = C_SecurityHelper.IsKeysExists();
		C_Log.v(3, C_TAG, "IsPinExists res=" + vResult);
		return vResult;
	}

	/**
	* Щифрация данных индивидуальным секретным ключем
	* Может вызываться со страниц приватного раздела приложения  
	* @param pData - строка данных для шифрации
	* @return зашмфрованные и закодированные в Base64 данные
	*/
	public String Encrypt (String pData) {
		C_Log.v(3, C_TAG, "Encrypt - start");
		String vResult = null;
		try {
			byte[] vB = C_SecurityHelper.sym_device_encrypt(pData.getBytes("UTF-8"));
			vResult = Base64.encodeBytes(vB);
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "Encrypt err:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "Encrypt - end");
		return vResult;
	}

	/**
	* Расшифровка данных индивидуальным секретным ключем
	* Может вызываться со страниц приватного раздела приложения  
	* @param pData - строка данных для расшифровки (в Base64)
	* @return расшмфрованные
	*/
	public String Decrypt (String pData) {
		C_Log.v(3, C_TAG, "Decrypt - start");
		String vResult = null;
		try {
			byte[] vB = C_SecurityHelper.sym_device_decrypt(Base64.decode(pData));
			vResult = new String(vB, "UTF-8");
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "Decrypt err:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "Decrypt - end");
		return vResult;
	}

	/**
	* Установка результата в диалоге выбора виджета
	* @param pWId - ид выбранного виджета 
	*/
	public void SetWidgetResult(String pWId) {
		C_Log.v(3, C_TAG, "SetWidgetResult - start");
		C_WidgetConfigActivity t = (C_WidgetConfigActivity) C_WidgetConfigActivity.fContext;
		if (t != null){
			t.SelectWidget(Integer.parseInt(pWId));
			C_Log.v(1, C_TAG, "SetWidgetResult pWId=" + pWId);
		} else {
			C_Log.v(0, C_TAG, "e:SetWidgetResult - Activity not found, pWId=" + pWId);
		}
		C_Log.v(3, C_TAG, "SetWidgetResult - end");
    }
		
	/**
	* Запуск  процедуры синхранизации (отравки на сервер и получения данных с сервера)
	* @param pShowProgress - показывать окно ProgressBar 
	*/
	public void StartSync(boolean pShowProgress) {
   		C_Log.v(3, C_TAG, "startSync pShowProgress=" + pShowProgress + " - start");
   		C_NetHelper.SyncData(fContext, pShowProgress, false);
   		C_Log.v(3, C_TAG, "startSync - end");
	}
	
	/**
	* Процедура ожидания окончания процедуры синхранизации, запущенной ранее с помощью StartSync
	*/
	public void WaitForSync() {
   		C_Log.v(3, C_TAG, "WaitForSync - start");
   		try {
   			Thread.sleep(1000);
   			while (C_NetHelper.fIsWorking) {
   				Thread.sleep(500);
   			}
		} catch(Exception e){
			C_Log.v(0, C_TAG, "WaitForSync err:" + e.getMessage());
		}
   		C_Log.v(3, C_TAG, "WaitForSync - stop");
	}
	
	/**
	* Процедура проверки окончания процедуры синхранизации, запущенной ранее с помощью StartSync
	* @return результат операции - если true то идет синхронизация
	*/
	public boolean IsSyncWaiting() {
   		C_Log.v(3, C_TAG, "isSyncWaiting");
		return C_NetHelper.fIsWorking;
	}
	
	/**
	* Добавление или обновление пакета в очередм загрузки
	* @param pPackId - ид пакета
	* @param pUrl - адрес пакета
	* @return результат добавления (если false то нужно повторить добавление позже)
	*/
	public boolean AddPackURL(int pPackId, String pUrl) {
   		C_Log.v(1, C_TAG, "DonnloadPack pPackId=" + pPackId + ", pUrl=" + pUrl + " - start");
    	boolean vResult = false;
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "AddPackURL getWritableDatabase is null! pUrl=" + pUrl + " - end");
		} else {
			try {
				dbHelper.UpdateOrInsert_T_PACK(vDb, pPackId, "N", pUrl);
				vResult = true;
			} catch (Exception e) {
				C_Log.v(0, C_TAG, "AddPackURL err:" + e.getMessage());
			}
		}
		dbHelper.close();
		C_Log.v(3, C_TAG, "AddPackURL pPackId=" + pPackId + " res=" + vResult + " - end");	
		return vResult;
	}

	/**
	* Удаление пакета из очередм загрузки
	* @param pPackId - ид пакета
	* @return результат удаления (если false то нужно повторить позже)
	*/
	public boolean DelPackURL(int pPackId) {
   		C_Log.v(1, C_TAG, "DelPackURL pPackId=" + pPackId + " - start");
    	boolean vResult = false;
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "DelPackURL getWritableDatabase is null! pPackId=" + pPackId + " - end");
		} else {
			try {
				dbHelper.Delete_T_PACK(vDb, pPackId);
				vResult = true;
			} catch (Exception e) {
				C_Log.v(0, C_TAG, "DelPackURL err:" + e.getMessage());
			}
		}
		dbHelper.close();
		C_Log.v(3, C_TAG, "DelPackURL pPackId=" + pPackId + " res=" + vResult + " - end");	
		return vResult;
	}
	
	/**
	* Получение статуса загрузки пакета из очередм загрузки
	* @param pPackId - ид пакета
	* @return результат "N"- не загружен, "P" - загружен, "E" - запись с таким ид не найдена
	*/
	public String GetPackStatus(int pPackId) {
   		C_Log.v(2, C_TAG, "GetPackStatus pPackId=" + pPackId + " - start");
    	String vResult = "E";
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getReadableDatabase();
		try {
			vResult = dbHelper.GetStatus_T_PACK(vDb, pPackId, "E");
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "GetPackStatus err:" + e.getMessage());
		}
		dbHelper.close();
		C_Log.v(3, C_TAG, "GetPackStatus pPackId=" + pPackId + " res=" + vResult + " - end");	
		return vResult;
	}	
	
	/**
	* Получение количества необработанных (незагруженных) пакетов в очереди загрузки
	* @return количество пакетов в статусе отличном от "P"
	*/
	public int GetCountOfUnprocessedPacks() {
   		C_Log.v(2, C_TAG, "GetCountOfUnprocessedPacks - start");
    	int vResult = 0;
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getReadableDatabase();
		try {
			vResult = dbHelper.GetCountOfUnprocessedPacks(vDb);
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "GetCountOfUnprocessedPacks err:" + e.getMessage());
		}
		dbHelper.close();
		C_Log.v(3, C_TAG, "GetCountOfUnprocessedPacks res=" + vResult + " - end");	
		return vResult;
	}
	
	/**
	 * Проверка наличия файла в папке Documents приложения
	 * @param pFileName - имя файла (косвенные пути запрещены!)
	 * @return boolean
	 */
	public boolean isFileExists(String pFileName) {
		File vFile = new File (fContext.getFilesDir() + "/" + pFileName);
		return vFile.exists();
	}
	
	/**
	* Процедура добавления в исходящую очередь данных для передачи на сервер
	* Может вызываться только со страниц приватного раздела приложения, после ввода пина 
	* Производится шифрация данных индивидуальным ключем и подпись индивидуальной цифровой подписью
	* @pqram pType - тип записи 
	* @pqram pOutData - данные
	* @return ид записи в исходящей очереди
	*/
	public long InsOutPrivateData(String pType, String pOutData) {
		C_Log.v(3, C_TAG, "InsOutPrivateData pType=" + pType + " pOutData len=" + pOutData.length() + " - start");
		long vRes = -1;
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "delVar InsOutPrivateData is null! pOutData=" + pOutData + " - end");
			return vRes;
		}
		try {
			vRes = dbHelper.AddDeviceOutDataRec(vDb, pType, pOutData.getBytes());
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "InsOutPrivateData err:" + e.getMessage());
		} finally {
			dbHelper.close();
		}		
		C_Log.v(3, C_TAG, "InsOutPrivateData pType=" + pType + " res=" + vRes + " - end");	
		return vRes;
	}
	
	/**
	* Процедура добавления в исходящую очередь данных для передачи на сервер
	* Производится шифрация данных общим ключем
	* @pqram pType - тип записи 
	* @pqram pOutData - данные
	* @return ид записи в исходящей очереди
	*/
	public static long InsOutData(String pType, String pOutData) {
		C_Log.v(3, C_TAG, "InsOutData pType=" + pType + " pOutData len=" + pOutData.length() + " - start");
		long vRes = -1;
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "delVar getWritableDatabase is null! pOutData=" + pOutData + " - end");
			return vRes;
		}
		try {
			vRes = dbHelper.AddOutDataRec(vDb, pType, pOutData.getBytes());
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "InsOutData err:" + e.getMessage());
		} finally {
			dbHelper.close();
		}		
		C_Log.v(3, C_TAG, "InsOutData pType=" + pType + " res" + vRes + " - end");	
		return vRes;
	}
	public static long InsOutDataNew(Context context,String pType, String pOutData) {
		C_Log.v(3, C_TAG, "InsOutData pType=" + pType + " pOutData len=" + pOutData.length() + " - start");
		long vRes = -1;
    	C_DBHelper dbHelper = new C_DBHelper(context);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "delVar getWritableDatabase is null! pOutData=" + pOutData + " - end");
			return vRes;
		}
		try {
			vRes = dbHelper.AddOutDataRec(vDb, pType, pOutData.getBytes());
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "InsOutData err:" + e.getMessage());
		} finally {
			dbHelper.close();
		}		
		C_Log.v(3, C_TAG, "InsOutData pType=" + pType + " res" + vRes + " - end");	
		return vRes;
	}
	
	
	/**
	* Процедура принудительного закрытия главного окна приложения
	*/	
	public void CloseApp() {
		C_Log.v(3, C_TAG, "CloseWindow - start");
		CancelLocationListener(); // завершить процесс прослушвателя если он был запущен
		Activity vA = (Activity) fContext;
		vA.finish();
		C_Log.v(3, C_TAG, "CloseWindow - end");
	}
	
	/**
	* Получение значения переменной по имени
	* @param pName -  имя переменной
	* @return значение переменной или "" если переменная не найдена
	*/
	public String getVar(String pName) {
		C_Log.v(3, C_TAG, "getVar pName=" + pName + " - start");
    	String vRes = "";
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try{
			vRes = dbHelper.GetVar(db, pName, "");
		}finally{
			dbHelper.close();
		}
		C_Log.v(3, C_TAG, "getVar pName=" + pName + " - end");
    	return vRes;
	}
	
	/**
	* Получение списка переменных и их значений по маске имен
	* @param pMask -  маска имен переменных, может включать % _
	* @return строка JSON-массива наименований и значений переменных
	*/	
	public String getVars(String pMask) {
		C_Log.v(3, C_TAG, "getNotifs - start");
    	String vRes = "";
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try{
			vRes = dbHelper.GetVars(db, pMask);
		}finally{
			dbHelper.close();
		}
		C_Log.v(3, C_TAG, "getNotifs - end");
    	return vRes;
	}	
    
	/**
	* Установка значения переменной
	* @param pName - наименование переменной
	* @param pValue - значение переменной
	*/	
	public boolean setVar(String pName, String pValue) {
		C_Log.v(3, C_TAG, "setVar pName=" + pName + " pValue=" + pValue + " - start");

    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db == null) {
			C_Log.v(0, C_TAG, "setVar getWritableDatabase is null! pName=" + pName + " pValue=" + pValue + " - end");
			return false;
		}
		try{
			dbHelper.SetVar_AT(db, pName, pValue);
			C_Log.v(2, C_TAG, "setVar pName=" + pName + " pValue=" + pValue + " - end");
		}finally{
			dbHelper.close();
		}
		C_Log.v(3, C_TAG, "setVar - end");
		return true;
	}
    
	/**
	* Удаление переменной
	* @param pName - наименование переменной
	*/	
    public boolean delVar(String pName) {
		C_Log.v(3, C_TAG, "delVar pName=" + pName + " - start");
    	C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db == null) {
			C_Log.v(0, C_TAG, "delVar getWritableDatabase is null! pName=" + pName + " - end");
			return false;
		}		
		try{
			dbHelper.DelVar_AT(db, pName);
			// ставим флаг того, что значение одной из переменных изменилось, и нужно будет обновить их на сервер
			dbHelper.SetVar_AT(db, C_Vars.C_VAR_DB_IS_CHANGED, "T");
			C_Log.v(3, C_TAG, "delVar pName=" + pName + " - end");
		}finally{
			dbHelper.close();
		}
		C_Log.v(3, C_TAG, "delVar - end");
		return true;
	}
    
	/**
	* Запуск строки javascript на текущей веб-страцице браузера (internal)
	* @param pCommand - командная строка
	*/	
    private static void ExecJS (String pCommand) {
		C_Log.v(3, C_TAG, "ExecJS :" + pCommand);
    	C_MainActivity.fWebView.loadUrl("javascript:"+pCommand);
    }
    
	/**
	* Фомирование JSON-строки с данными указаного местополжения pLocation (internal)
	* @param pLocation - местополжение
	* @return - строка JSON 
	*/	
    private String GetLocationResult (Location pLocation) {  
		C_Log.v(3, C_TAG, "GetLocationResult");
		fLastLocation = pLocation;
    	if (pLocation == null) {
    		return "{\"RESULT\":\"ERROR\",\"ERROR_MSG\":\"Location is null\"}";
    	} 
    	return "{\"RESULT\":\"OK\","+
			"\"LATITUDE\":\""+String.valueOf(pLocation.getLatitude())+"\","+
			"\"LONGITUDE\":\""+String.valueOf(pLocation.getLongitude())+"\","+
			"\"ACCURACY\":\""+String.valueOf(pLocation.getAccuracy())+"\","+
			"\"ALTITUDE\":\""+String.valueOf(pLocation.getAltitude())+"\","+
			"\"BEARING\":\""+String.valueOf(pLocation.getBearing())+"\","+
			"\"SPEED\":\""+String.valueOf(pLocation.getSpeed())+"\","+
			"\"TIME\":\""+String.valueOf(pLocation.getTime())+"\","+
			"\"PROVIDER\":\"" + pLocation.getProvider()+"\"}";
	}

	/**
	* Получение текущего местоположения по заданным условиям.
	* Если для заданных условия точности и срока устаревания уже имеются готовые данные, то 
	* процедура pCallBackFunctionName с результатом вызывается сразу. Иначе выбирается
	* провайдер на одно обновление, после получения результата запускается pCallBackFunctionName
	* @param pCallBackFunctionName - наименование процедуры, которая вызывается с оденим параметром - строкой JSON c данными местоположения
	* @param pMinAccuracy - минимальная точность (метров, обычно 100-200)
	* @param pMaxTimeAgo - минимальное время определения (секунд, обычно 20 - 60)
	* @param pProvider - наименвоание провайдера, от которого нужно получать данные. 
	* Если указана пустая строка, то выбирается лучший по потреблению энергии провайдер  
	* @return - если false значит нет доступных провайдеров, и вызова pCallBackFunctionName не будет. Если true то будет вызов pCallBackFunctionName.
	* 	В функцию pCallBackFunctionName передается один параметр - объект JSON с 
	* результатом и координатами. Варианты ответа:
	* {"RESULT":"ERROR","ERROR_MSG":"текст ошибки"}
    * {"RESULT":"OK",
		"LATITUDE":"latitude",
		"LONGITUDE":"longitude",
		"ACCURACY":"accuracy",
		"ALTITUDE":"altitude",
		"BEARING":"bearing",
		"TIME":"time",
		"PROVIDER":"provider_name"
	   }
	*/	    
	public boolean GetLocation (String pCallBackFunctionName, int pMinAccuracy, int pMaxTimeAgo, String pProviderName) {
		C_Log.v(3, C_TAG, "GetLocation - end");
		CancelLocationListener(); // завершить предыдущий процесс прослушвателя из предыдущего вызова
		fLocationHelper = new C_LocationHelper(fContext, pProviderName, false);
		final String fCallBackFunctionName = pCallBackFunctionName;
		boolean vResult = false;
		fLocationHelper.setChangedLocationListener(new LocationListener() {
			@Override
			public void onLocationChanged(Location vLocation) {
				if (vLocation != null) {
					String vRes = GetLocationResult(vLocation); 
					C_Log.v(2, C_TAG, "getLocation -- " + vRes);
					ExecJS (fCallBackFunctionName + "('" + vRes + "');");
					fLocationHelper.cancel();
					fLocationHelper = null;
//					String Text = "My current location is: " + "Latitud = " + vLocation.getLatitude() + "Longitud = " + vLocation.getLongitude(); 
//					Toast.makeText(fContext, Text, Toast.LENGTH_SHORT).show();
//					CS_Log.v(2, C_TAG, "onLocationChanged  lat=" + vLocation.getLatitude() + " lng=" + vLocation.getLongitude());
				} else {
					C_Log.v(1, C_TAG, "onLocationChanged : Location id null !");
				}
			}
			@Override
			public void onProviderDisabled(String provider){
//				Toast.makeText(fContext, "Provider " + provider + " is Disabled",Toast.LENGTH_SHORT ).show();
				C_Log.v(2, C_TAG, "onProviderDisabled provider=" + provider);
				fLocationHelper.cancel();
				fLocationHelper = null;
			} 
			@Override 
			public void onProviderEnabled(String provider){ 
//				Toast.makeText(fContext,"Provider " + provider + " is Enabled",Toast.LENGTH_SHORT).show();
				C_Log.v(2, C_TAG, "onProviderEnabled provider=" + provider);
			} 
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras){ 
//				Toast.makeText(fContext,"Status of provider " + provider + " is  changed to "+ status, Toast.LENGTH_SHORT).show();
				C_Log.v(2, C_TAG, "onStatusChanged provider=" + provider + " status=" + status);
			} 
		});
			
		Location vLocation = fLocationHelper.getLocation(pMinAccuracy, pMaxTimeAgo);
		if (vLocation != null) {
			// имеется готовый результат:
			String vRes = GetLocationResult(vLocation); 
			C_Log.v(2, C_TAG, "GetLocation - " + vRes);
			ExecJS (fCallBackFunctionName + "('" + vRes + "');");
			vResult = true;
		} else if (!fLocationHelper.fIsProviderEnabled) {
			// нет готового результата и нет ни одного доступного провайдера:
			String vRes = "{\"RESULT\":\"ERROR\",\"ERROR_MSG\":\"All providers are disabled!\"}"; 
			C_Log.v(2, C_TAG, "GetLocation - " + vRes);
			ExecJS (fCallBackFunctionName + "('" + vRes + "');");
			vResult = true; // true - есть ответ (ошибка), false -  ждите ответа
		} // иначе готового результата нет но есть доступный провайдер, запущен запрос на обновление, ждем ответа
		C_Log.v(3, C_TAG, "GetLocation - vResult=" + vResult + " - end");
		return vResult;
	}
	
	/**
	* Принудительное завершение ожидания получения местоположения, запещенного с помощью GetLocation
	*/
	public void CancelLocationListener() {
		if (fLocationHelper != null) {
			fLocationHelper.cancel();
			fLocationHelper = null;
			C_Log.v(2, C_TAG, "CancelLocationListener - cancel");
		}
		C_Log.v(3, C_TAG, "CancelLocationListener");
	}
	
	/**
	* Получение ид устройства
	* @return - ид устройства (цифры и лат. символы)
	*/
	public String getDeviceId() {
		String vRes = C_Utils.GetDeviceID(fContext); 
		C_Log.v(3, C_TAG, "getDeviceId vRes=" + vRes);
		return vRes;
	}
    
	/**
	 * Закрыть окно прогресс-диалога
	 * @return результат, true - окно закрылось без ошибок
	 */
	public boolean HideProgressDialog() {
		boolean vRes = C_MainActivity.HideProgressDialog();
		C_Log.v(3, C_TAG, "HideProgressDialog vRes=" + vRes);
		return vRes;
	}
    
	/**
	 * Вывести окно прогресс-диалога с текстом pText
	 * @param pText
	 * @return результат, true - окно открылось без ошибок
	 */
	public boolean ShowProgressDialog(String pText) {
		boolean vRes = C_MainActivity.ShowProgressDialog(pText);
		C_Log.v(3, C_TAG, "HideProgressDialog vRes=" + vRes);
		return vRes;
	}
	
	/**
	 * передача события об активации главного окна в процедуру JS OnAppShowProc()
	 */
	public static void OnShowMainWin() {
		ExecJS (C_Vars.C_ONSHOW_JS_PROCNAME);
		C_Log.v(3, C_TAG, "OnShowMainWin");
	}
	
	/**
	 * передача события об деактивации главного окна в процедуру JS OnAppHideProc()
	 */
	public static void OnHideMainWin() {
		ExecJS (C_Vars.C_ONHIDE_JS_PROCNAME);
		C_Log.v(3, C_TAG, "OnHideMainWin");
	}
	
	/**
	 * передача события о нажатии на кнопку back в процедуру JS OnPressKeyBackProc()
	 */
	public static void OnPressKeyBack() {
		ExecJS (C_Vars.C_ONBACK_JS_PROCNAME);
		C_Log.v(3, C_TAG, "OnPressKeyBack");
	}
	
	
	
	/**
	 * открыть диалог настроек определения местоположения (можено использовать при предложении пользователю включить GPS)
	 */
	public void OpenGPSSettings() {
		C_Log.v(3, C_TAG, "OpenGPSSettings");
		Intent vIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
	    fContext.startActivity(vIntent);
	}
	
	/**
	 * Проверка доступности провайдера получения текущешл местоположения с указанным именем
	 * @param pProviderName - имя провайдера (gps, network, wifi)
	 * @return boolean
	 */
	public boolean IsLocationProviderEnabled(String pProviderName) {
		C_Log.v(3, C_TAG, "IsLocationProviderEnabled " + pProviderName);
		LocationManager vLocationManager = (LocationManager)fContext.getSystemService(Context.LOCATION_SERVICE);
		boolean vResult = vLocationManager.isProviderEnabled(pProviderName);
		C_Log.v(3, C_TAG, "IsLocationProviderEnabled res=" + vResult);
		return vResult;
	}
	
	
	/**
	 * Получение количества свободного места в том разделе где установлено приложение
	 * @return long размер доступного места в байтах
	 */
	public long GetFreeSpace() {
		C_Log.v(3, C_TAG, "GetFreeSpace");
		StatFs stat = new StatFs(fContext.getFilesDir().getPath());
		long vResult = stat.getBlockSize() * stat.getAvailableBlocks();
		C_Log.v(3, C_TAG, "GetFreeSpace res=" + vResult);
		return vResult;
	}
	
	
	/**
	 * Сохранение строки данных в указанном файле. Если файл существует, то он будет перезаписан.
	 * @param pFileName имя файла (без сказания пути!)
	 * @param pFileData строка UTF8 данных для записи в файл
	 * @return boolean true - успешно, false - ошибка
	 */
	public boolean SaveDataToFile(String pFileName, String pFileData) {
		C_Log.v(3, C_TAG, "SaveDataToFile pFileName=" + pFileName + " pFileData_len=" + pFileData.length());
		try {
			C_FileHelper.SaveFile(new File(fContext.getFilesDir() + "/" + pFileName), pFileData.getBytes("UTF-8"), true);
			return true;
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "e:SaveDataToFile pFileName=" + pFileName + " pFileData_len=" + pFileData.length() + " err:" + e.getMessage());
			return false;
		}
	}
	

	/**
	 * Считывание содержимого указанного файла. Если файл не существет или возникла ошибка, вернется пустая строка.
	 * @param pFileName имя файла (без сказания пути!)
	 * @return String строка UTF8 данных, считанных из файла
	 */
	public String LoadDataFromFile (String pFileName) {
		String vResult = "";
		C_Log.v(3, C_TAG, "LoadDataFromFile pFileName=" + pFileName);
		try {
			byte[] vData = C_FileHelper.ReadFile(new File(fContext.getFilesDir() + "/" + pFileName));
			vResult = new String(vData, "UTF8");
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "e:LoadDataFromFile pFileName=" + pFileName + " pFileData_len=" + vResult.length() + " err:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "LoadDataFromFile pFileName=" + pFileName + " pFileData_len=" + vResult.length());
		return vResult;
	}
	
	/**
	 * Выполнение любого SQL-запроса без возврата результата
	 * @param pSQL текст запроса
	 * @return int -2 = ошибка получения дескриптора БД, -1 = ошибка выполнения запроса, =0 - без ошибок 
     * Примеры запросов: 
     * create table cc (f1 integer, f2 datetime, f3 varchar(20))
     * insert into cc values (1, now(), 'ssss')
     * delete from cc where f1=1
     * drop table cc
	 */
	public int ExecSQL(String pSQL) {
		C_Log.v(2, C_TAG, "ExecSQL pSQL=" + pSQL);
		C_DBHelper dbHelper = new C_DBHelper(fContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db == null) {
			C_Log.v(0, C_TAG, "ExecSQL getWritableDatabase is null! pSQL=" + pSQL + " - end");
			return -2;
		}		
		int vResult = -1;
		try {
			vResult = dbHelper.ExecSQL(db, pSQL);
		} finally {
			db.close();
			dbHelper.close();
		}
		C_Log.v(3, C_TAG, "ExecSQL pSQL=" + pSQL + " res=" + vResult + " - end");
		return vResult;
	}
	
    /**
     * Выполнение любого SQL-запроса c возвращением результата одной строкой JSON. Select должен возвращать одну строковую колонку! 
     * @param pSQL - текст SQL запроса SELECT
     * @return string - результат выполнения запроса в виде JSON. 
     * в формате {"R":[a1,a2,...,aN]} где a1..aN - значения колонки для записей 1..N
     * select '{"f1":"' || f1 || '","f2":"' || datetime(f2,'localtime') || '"}' from cc where f3 like '%'
     * Пример запроса: 
     * select '{"fname1":"' || field1 || '","fname2":"' || datetime(field2_date,'localtime') || '"}' from table_name where field1 > 12
     * 
     * Если при выполнении запроса возникает ошибка, результат выдается в виде {"E":["текст ошибки"]}. 
     * Пустой результат возвращается в виде {"R":[]}
     *  
     * Документация по SQLLite: 
     * http://www.sqlite.org/docs.html  
     * http://www.sqlite.org/lang.html 
     */
    public String ExecSQL_Select(String pSQL) {
    	C_Log.v(2, C_TAG, "ExecSQL_Select pSQL=" + pSQL);
   		C_DBHelper dbHelper = new C_DBHelper(fContext);
   		SQLiteDatabase db = dbHelper.getReadableDatabase();
   		String vResult = dbHelper.ExecSQL_Select(db, pSQL);
   		db.close();    	
   		dbHelper.close();
    	C_Log.v(3, C_TAG, "ExecSQL_Select pSQL=" + pSQL + " res_len=" + vResult.length() + " - end");
		return vResult;
    }
    
    /**
     * Открыть диалог дополненной реальности (видео с камеры с наложенными значками из массива pMarkers)
     * Выход из диалога - по кномпе Back или при нажатии на значек. В случае нажатия на значек возвращается код 
     * @param pUseCollisionDetection - проверка на наложение значков, если true то значек при наложении переносится выше
     * @param pMarkers - массив JSON с координатами и иконками значков, пример:
     * {"Markers\":[
     * {"Lat":12.571,"Lon\":99.921,"Alt":0.0,"Text":\"Test банкомат 01","Icon":"26_m.png","id":"11"},
     * {"Lat":12.581,"Lon\":99.931,"Alt":110.0,"Text":\"Test банкомат 02","Icon":"26_m.png","id":"ss1"},
     * {"Lat":12.591,"Lon\":99.941,"Alt":1223.0,"Text":\"Test банкомат 31","Icon":"26_m.png","id":"adf1"}
     * ]}
     * Lat - широта (град)
     * Lon - долгота (град)
     * Alt - высота над уровнем моря (м)
     * Text - текст (не должен быть пустым!)
     * Icon - наименование файла картинки в формате png, без указания пути
     * Если размеры картинки по ширине или высоте больше 48, то картинка рисуется над боксом с надписью. 
     * Если оба размера меньше или равно 48 то картинка рисуется внутри бокса слева от надписи.
     * id -  ид записи. При нажатии пользователем на значек на текущей странице вызывается 
     * javascript-функция OnARClickProc(pId) с параметром id нажатого значка 
     * При завершении диалога функция OnARClickProc вызывается с пустым параметром.
     * 
     */
    public void ShowARDialog (Boolean pUseCollisionDetection, String pMarkers) {
//    	try {
//    		C_Log.v(3, C_TAG, "ShowARDialog");
//    		Intent vIntent = new Intent(fContext, C_ARActivity.class);
//    		vIntent.putExtra("Markers", pMarkers);
//    		vIntent.putExtra("UseCollisionDetection", pUseCollisionDetection);
//    		fContext.startActivity(vIntent);
//    	} catch (Exception e) {
//    		C_Log.v(0, C_TAG, "AddMarkers err:" + e.getMessage());
//    	}
    }
    
    /**
     * То же что и ShowARDialog только добавились параметры boolean pUpdateLocation, double pLongitude, double pLatitude, double pAltitude
     * @param pUseCollisionDetection
     * @param pMarkers
     * @param pUpdateLocation - нужно ли обновлять текущую позицию, если false то позиция центра не меняется
     * @param pLatitude - широта позиции центра
     * @param pLongitude - долгота позиции центра
     * @param pAltitude - высота позиции центра
     */
    public void ShowARDialog1 (Boolean pUseCollisionDetection, String pMarkers, boolean pUpdateLocation, double pLatitude, double pLongitude, double pAltitude) {
//    	try {
//    		C_Log.v(3, C_TAG, "ShowARDialog");
//    		Intent vIntent = new Intent(fContext, C_ARActivity.class);
//    		vIntent.putExtra("Markers", pMarkers);
//    		vIntent.putExtra("UseCollisionDetection", pUseCollisionDetection);
//    		vIntent.putExtra("UpdateLocation", pUpdateLocation);
//    		vIntent.putExtra("Latitude", pLatitude);
//    		vIntent.putExtra("Longitude", pLongitude);
//    		vIntent.putExtra("Altitude", pAltitude);
//    		fContext.startActivity(vIntent);
//    	} catch (Exception e) {
//    		C_Log.v(0, C_TAG, "AddMarkers err:" + e.getMessage());
//    	}
    }

	/**
	 * передача события о нажатии на значек в диалоге AR в процедуру JS OnARClickProc(pId)
	 * @param pId - id нажатого значка
	 */
	public static void OnARClick(String pId) {
		C_Log.v(3, C_TAG, "OnARClick pId=" + pId);
		ExecJS (C_Vars.C_ONARCLICK_JS_PROCNAME.replace(":1", pId));
		// восстановить ориентацию экрана:
		C_MainActivity.fMainActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
	}
	
	
	/**
	 * Запуск генератора 2D-barcode c передачей строки
	 * @param type
	 * @param data
	 */
	private void encodeBarcode(String type, String data) {
		Intent intent = new Intent("com.google.zxing.client.android.ENCODE");
		intent.putExtra("ENCODE_TYPE", type);
		intent.putExtra("ENCODE_DATA", data);
		fContext.startActivity(intent);
	}
	/**
	 * Запуск генератора 2D-barcode c передачей структуры Bundle
	 * @param type
	 * @param data
	 */
	private void encodeBarcode(String type, Bundle data) {
		Intent intent = new Intent("com.google.zxing.client.android.ENCODE");
		intent.putExtra("ENCODE_TYPE", type);
		intent.putExtra("ENCODE_DATA", data);
		fContext.startActivity(intent);
	}
	 
	/**
	 * Генерация QR-кода на экране
	 * Для работы функции необходима установка приложения ZXing Barcode Scanner (если не установлено, то 
	 * запрашивается и производится установка)
	 * @param pType - строка Тип данных
	 * @param pText - строка Данные
	 * Примеры (тип, данные):
	 * "TEXT_TYPE", "http://www.homeplus.kz"
	 * "EMAIL_TYPE", "info@homeplus.com"
	 * "PHONE_TYPE", "12335609826"
	 * "SMS_TYPE", "2125551212" 
	 * "BUNDLE", "{\"TYPE\":\"LOCATION_TYPE\",\"LAT\":12.23445,\"LONG\":-74.234}"
	 * "BUNDLE", "{\"TYPE\":\"CONTACT_TYPE\",\"name\":\"_Name_\",\"phone\":\"_phone_\",\"email\":\"_email_\",\"postal\":\"_postal_\"}"
	 */
	public void EncodeBarCode(String pType, String pText) {
		try {
			C_Log.v(1, C_TAG, "EncodeBarCode pType=" + pType + " pText=" + pText);
			if (pType.equals("BUNDLE")) {
				Bundle bundle = new Bundle();
				JSONObject vObj = new JSONObject(pText);
				String vType = vObj.getString("TYPE");
				if (vType.equals("LOCATION_TYPE")) {
					bundle.putFloat("LAT", (float) vObj.getDouble("LAT"));
					bundle.putFloat("LONG", (float) vObj.getDouble("LONG"));
				}
				if (vType.equals("CONTACT_TYPE")) {
					bundle.putString("name", vObj.getString("name"));
					bundle.putString("phone", vObj.getString("phone"));
					bundle.putString("email", vObj.getString("email"));
					bundle.putString("postal", vObj.getString("postal"));
				}
				encodeBarcode(vType, bundle);
			} else {
				encodeBarcode(pType, pText);
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * запуск диалога сканирования бар-кода
	 * Для работы функции необходима установка приложения ZXing Barcode Scanner (если не установлено, 
	 * то запрашивается и производится установка)
	 * после сканирования на текущей странице запускается javascript-функция OnScanBarcodeProc(pFormat,pContent) 
	 * с параметрами - 'форматом кода' и 'считанным кодом или текстом'
	 * (или если ошибка считывания - 'error', 'текст ошибки'
	 */
	public void ExecScannerDialog () {
		C_Log.v(1, C_TAG, "ExecScannerDialog");
    	IntentIntegrator integrator = new IntentIntegrator(C_MainActivity.fMainActivityInstance);
    	integrator.initiateScan();
	}
	

	/**
	 * передача события о сканировании бар-кода в процедуру JS OnScanBarcodeProc(pId)
	 * @param pFormat - формат считанного бар-кода
	 * @param pContent - считанный текст или код
	 */
	public static void OnScanBarCode(String pFormat, String pContent) {
		C_Log.v(1, C_TAG, "OnScanBarCode");
		String vCommand = C_Vars.C_ONSCANBARCODE_JS_PROCNAME.replace(":1", pFormat).replace(":2", pContent);
		ExecJS (vCommand);
	}
	
	/**
	 * Открыть страницу по адресу pUrl в браузере по умолчанию
	 * @param pUrl - адрес страницы (обязательно включая "http://"!)
	 */
	public void ExecBrowserWithUrl(String pUrl) {
		C_Log.v(1, C_TAG, "ExecBrowser pUrl="+pUrl);
		Intent vIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pUrl));
		fContext.startActivity(vIntent);
	}

	/**
	 * Принудительная установка ориентации экрана основной формы  
	 * @param pOrientation - может быть:
	 * "portrait" - портретная, 
	 * "landscape" - горизонтальная, 
	 * "auto" - автоматическая (в зависимости от показаний датчика наклона), 
	 * "user" - согласно установкам пользвоателя (желательно испозьовать этот вариант!)
	 * 
	 */
	public void SetDisplayOrientation(String pOrientation) {
		if (pOrientation.equals("portrait")) {
			C_MainActivity.fMainActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (pOrientation.equals("landscape")) {
			C_MainActivity.fMainActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		if (pOrientation.equals("auto")) {
			C_MainActivity.fMainActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		if (pOrientation.equals("user")) {
			C_MainActivity.fMainActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		} 
	}

	/**
	 * Возвращается текущая версия мобильного приложения
	 * может быть:
	 * Android:
	 * "A_0.9" - 17.11.2011 
	 * "A_0.91" - 12.12.2011
	 * "A_0.92" - 16.01.2011
	 * iOS:
	 * "i_0.9" - ..
	 * @return
	 */
	public String GetAppVer() {
		return C_Vars.C_VERSION;
	}
	
	/**
	 * Получение логов (для тестирования)
	 * @return строка с логами
	 */
	public String GetLogs() {
		return C_Log.GetLogs();
	}
	
}