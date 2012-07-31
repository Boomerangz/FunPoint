package kz.crystalspring.android_client;



import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;


public class C_DBHelper extends SQLiteOpenHelper {
	static final String C_TAG = "CS_DBHelper";
	
    public C_DBHelper(Context context) {
        super(context, C_Vars.C_DATABASE_NAME, null, C_Vars.C_DATABASE_VERSION);
    	C_Log.v(3, C_TAG, "CS_DBHelper constructor");
    }

    
    @Override
    public void onCreate(SQLiteDatabase db) {
        C_Log.v(1, C_TAG, "onCreate - start");
        db.execSQL(C_Vars.C_SQL_T_VARS_CREATE);
        db.execSQL(C_Vars.C_SQL_T_URLS_CREATE);
        db.execSQL(C_Vars.C_SQL_T_WIDGETS_CREATE);
        db.execSQL(C_Vars.C_SQL_T_NOTIFS_CREATE);
        db.execSQL(C_Vars.C_SQL_T_OUT_CREATE);
        db.execSQL(C_Vars.C_SQL_T_PACK_CREATE);
        C_Log.v(1, C_TAG, "onCreate - end");
    }

    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// метод вызывается при изменении версии базы в C_DATABASE_VERSION!
        C_Log.v(1, C_TAG, "onUpgrade - start");
    	db.execSQL("DROP TABLE IF EXISTS "+C_Vars.C_T_VARS_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+C_Vars.C_T_URLS_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+C_Vars.C_T_WIDGETS_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+C_Vars.C_T_NOTIFS_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+C_Vars.C_T_OUT_NAME);		
		db.execSQL("DROP TABLE IF EXISTS "+C_Vars.C_T_PACK_NAME);		
		C_Log.v(1, C_TAG, "onUpgrade - tables are dropped");
		onCreate(db);
		C_Log.v(1, C_TAG, "onUpgrade - end");
	}

    /**
    * метод может вернуть null если база уже заблокирована из другого потока,
    * обязательно проверять  результат на null!
    * @see android.database.sqlite.SQLiteOpenHelper#getWritableDatabase()
	* @param pDb - дексриптор базы данных
    */
    @Override
	public SQLiteDatabase getWritableDatabase() {
    	C_Log.v(3, C_TAG, "getWritableDatabase - start");
    	int vCount = 0;
		SQLiteDatabase vDb = null;
		while (vDb == null) {
			try {
				vDb = super.getWritableDatabase();	
			}catch(SQLiteException e) {
				C_Log.v(0, C_TAG, "getWritableDatabase err:"+e.getMessage());
				try {
					vCount ++;
					if (vCount > 10) 
						break;
					Thread.sleep(100);
				} catch (InterruptedException e1) {	
				}
			}
		}
    	C_Log.v(3, C_TAG, "getWritableDatabase - end");
		return vDb;
	}

    /**
    * Сжатие базы (по команде с сервера)
	* @param pDb - дексриптор базы данных
    */
    public void Vacuum(SQLiteDatabase pDb) {
    	C_Log.v(1, C_TAG, "Vacuum");
		pDb.execSQL("VACUUM");        
	}

	/**
	* Получение значения переменной 
	* @param pDb - дексриптор базы данных
	* @param pName - наименование переменной 
	* @param pDefValue - значение по умолчанию (возвращается если переменная с таким именем не найдена)
	* @return - значение переменной или значение по умолчанию из pDefValue
	*/
	public String GetVar(SQLiteDatabase pDb, String pName, String pDefValue){
		C_Log.v(3, C_TAG, "GetVar - pName=" + pName + " pDefValue=" + pDefValue + " - start");
		Cursor vCursor = pDb.query(C_Vars.C_T_VARS_NAME,new String[] {"V_VALUE"}, "V_NAME=:s", new String[] {pName}, null,null,null);
		String vResult = pDefValue;
	    if(vCursor.moveToFirst()){
	    	C_Log.v(2, C_TAG, "GetVar - pName=" + pName + " - found");
	    	vResult = vCursor.getString(0);
	    }
	    vCursor.close();
	    C_Log.v(3, C_TAG, "GetVar - pName=" + pName + " res=" + vResult + " - end");
	    return vResult;
	}

	/**
	* Установка значения переменной 
	* @param pDb - дексриптор базы данных
	* @param pName - наименование переменной 
	* @param pValue - значение
	*/	
    public void SetVar(SQLiteDatabase pDb, String pName, String pValue){
   		C_Log.v(3, C_TAG, "SetVar - pName=" + pName + " pValue=" + pValue + " - start");
    	ContentValues vCV = new ContentValues();
    	vCV.put("V_NAME", pName);
    	vCV.put("V_VALUE", pValue);
   		Integer i = pDb.update(C_Vars.C_T_VARS_NAME, vCV, "V_NAME=:s", new String[] {pName});
   		if (i != 0){
   			C_Log.v(2, C_TAG, "SetVar - pName=" + pName + " - update");
   		} else {
   	   		C_Log.v(1, C_TAG, "SetVar - pName=" + pName + " - insert");
   			pDb.insert(C_Vars.C_T_VARS_NAME, null, vCV);
   		}
   		C_Log.v(3, C_TAG, "SetVar - pName=" + pName + " - end");
    }
   
	/**
	* Установка значения переменной в автономной транзакции
	* @param pDb - дексриптор базы данных
	* @param pName - наименование переменной 
	* @param pValue - значение
	*/	
    public void SetVar_AT(SQLiteDatabase pDb, String pName, String pValue){
    	C_Log.v(3, C_TAG, "SetVar_AT - pName=" + pName + " pValue=" + pValue + " - end");
    	pDb.beginTransaction();
    	try{
    		SetVar(pDb, pName, pValue);
    		pDb.setTransactionSuccessful();
    	}finally{
    		pDb.endTransaction();
    	}
    }


	/**
	* Удаление переменной
	* @param pDb - дексриптор базы данных
	* @param pName - наименование переменной 
	*/	
    public void DelVar(SQLiteDatabase pDb, String pName){
    	C_Log.v(3, C_TAG, "DelVar - pName=" + pName + " - end");
   		Delete_T_VARS(pDb, pName);
    }

	/**
	* Удаление переменной в автономной транзакции
	* @param pDb - дексриптор базы данных
	* @param pName - наименование переменной 
	*/	
    public void DelVar_AT(SQLiteDatabase pDb, String pName){
    	C_Log.v(3, C_TAG, "DelVar_AT - pName=" + pName + " - end");
    	pDb.beginTransaction();
    	try{
    		Delete_T_VARS(pDb, pName);
    		pDb.setTransactionSuccessful();
    	}finally{
    		pDb.endTransaction();
    	}
    }
    
	/**
	* Удаление переменной (internal)
	* @param pDb - дексриптор базы данных
	* @param pName - наименование переменной 
	*/    
    private void Delete_T_VARS (SQLiteDatabase db, String pName){
    	C_Log.v(1, C_TAG, "Delete_T_VARS - name=" + pName + " - start");
    	db.delete(C_Vars.C_T_VARS_NAME, "V_NAME=:s", new String[] {pName});
    	C_Log.v(2, C_TAG, "Delete_T_VARS - name=" + pName + " - end");
    }
    
	/**
	* Добавление виджета или обновление информации об установленом виджете 
	* @param pDb - дексриптор базы данных
	* @param pWidgetId - ид виджета 
	* @param pNotifyId - ид уведомления (канала) виджета 
	*/   
    public void UpdateOrInsert_T_WIDGETS_AT(SQLiteDatabase pDb, int pWidgetId, int pNotifyId){
    	C_Log.v(3, C_TAG, "UpdateOrInsert_T_WIDGETS_AT - pWidgetId=" + pWidgetId + " pNotifyId=" + pNotifyId + " - start");
    	pDb.beginTransaction();
    	try{
        	ContentValues vCV = new ContentValues();
        	vCV.put("W_ID", pWidgetId);
        	vCV.put("N_ID", pNotifyId);
       		Integer i = pDb.update(C_Vars.C_T_WIDGETS_NAME, vCV, "W_ID="+pWidgetId, null);
       		if (i == 0){
        		C_Log.v(2, C_TAG, "UpdateOrInsert_T_WIDGETS_AT w_id=" + pWidgetId + " n_id=" + pNotifyId + " - insert");
       			pDb.insert(C_Vars.C_T_WIDGETS_NAME, null, vCV);
       		}
    		pDb.setTransactionSuccessful();
    	}finally{
    		pDb.endTransaction();
    	}
    	C_Log.v(3, C_TAG, "UpdateOrInsert_T_WIDGETS_AT -pWidgetId=" + pWidgetId + " pNotifyId=" + pNotifyId + " - end");
    }    
    
	/**
	* Удаление информации виджета в автономной транзакции 
	* @param pDb - дексриптор базы данных
	* @param pWidgetId - ид виджета 
	*/
    public void Delete_T_WIDGETS_AT (SQLiteDatabase pDb, int pWidgetId){
    	C_Log.v(3, C_TAG, "Delete_T_WIDGETS_AT w_id=" + pWidgetId + " - start");
    	pDb.beginTransaction();
    	try{
    		pDb.delete(C_Vars.C_T_WIDGETS_NAME, "W_ID="+pWidgetId, null);
    		pDb.setTransactionSuccessful();
    	}finally{
    		pDb.endTransaction();
    	}
    	C_Log.v(3, C_TAG, "Delete_T_WIDGETS_AT w_id=" + pWidgetId + " - end");
    }
    
	/**
	* Вставка записи в исходящую очередь для отправки на сервер при слеюущем сеаксе связи  (internal)
	* @param pDb - дексриптор базы данных
	* @param pType - тип записи (см. описание в formats.rtf)
	* @param pZipped - признак того что инфомрация сжата, T  или F
	* @param pCrypted - признак того что инфомрация зашифрована индивидуальным ключем, T  или F 
	* @param pSign - цифровая подпись (имеется только в пользовательских сообщениях)
	* @param pData - данные в Base64
	* @return - ид вставленной записи
	*/    
    private long Insert_T_OUT(SQLiteDatabase pDb, String pType, String pZipped, String pCrypted, String pSign, String pData) {
    	C_Log.v(3, C_TAG, "Insert_T_OUT - pType=" + pType + " - start");
    	long vRes = -1;
    	ContentValues vCV = new ContentValues();
    	vCV.put("OUT_TYPE", pType);
        vCV.put("OUT_ZIPPED", pZipped);
        vCV.put("OUT_CRYPTED", pCrypted); // данные всегда зашифрованы! pCrypted - признак дополнитеьной шифрации индивидуальным ключем! 
        vCV.put("OUT_SIGN", pSign);
    	vCV.put("OUT_DATA", pData);
    	vRes = pDb.insert(C_Vars.C_T_OUT_NAME, null, vCV);
    	C_Log.v(3, C_TAG, "Insert_T_OUT - pType=" + pType + " res=" + vRes + " - end");
    	return vRes;
    }
    
	/**
	* Вставка записи в исходящую очередь для отправки на сервер (системные, служебные сообщения)
	* Данные шифруются общим ключем
	* @param pDb - дексриптор базы данных
	* @param pType - тип записи (см. описание в formats.rtf)
	* @param pOutData - данные в Base64
	* @return - ид вставленной записи
	*/    
    public long AddOutDataRec(SQLiteDatabase pDb, String pType, byte[] pOutData) {
    	C_Log.v(3, C_TAG, "AddOutDataRec - pType=" + pType +  " - start");
    	long vRes = -1;
   		try {
   			if (pOutData !=  null) { 
   				byte[] vBData = pOutData;
   				String vZipped = "F";
   				if (pOutData.length > 200) {
   					vBData = C_FileHelper.compress(vBData);
   					vZipped = "T";
   				}
   				vBData = C_SecurityHelper.sym_encrypt(C_SecurityHelper.GetCommonSecretKey(), vBData);
   				String vSData = Base64.encodeBytes(vBData);			
   				vRes = Insert_T_OUT(pDb, pType, vZipped, "F", "", vSData);
   			}
		} catch(Exception e){
			C_Log.v(0, C_TAG, "AddOutDataRec err:" + e.getMessage());
		}
    	return vRes;
    }
    
	/**
	* Вставка записи в исходящую очередь для отправки на сервер (пользовательские сообщения)
	* Данные шифруются индивидуальным ключем и подписываются пользовательским сертификатом
	* Внимание! Должен быть доступен индивидуальный секретный ключ и приватный ключ, т.е. отправка 
	* производится только из активной пользовательской web-сессии!
	* @param pDb - дексриптор базы данных
	* @param pType - тип записи (см. описание в formats.rtf)
	* @param pOutData - данные в Base64
	* @return - ид вставленной записи
	*/    
    public long AddDeviceOutDataRec(SQLiteDatabase pDb, String pType, byte[] pOutData) {
    	C_Log.v(3, C_TAG, "AddDeviceOutDataRec - pType=" + pType +  " - start");
    	long vRes = -1;
   		try {
   			if (pOutData !=  null) { 
   				byte[] vBData = pOutData;
   				String vZipped = "F";
   				if (pOutData.length > 200) {
   					vBData = C_FileHelper.compress(vBData);
   					vZipped = "T";
   				}
   				if (!C_SecurityHelper.IsKeysExists()) {
   					return -1;
   				}
   				vBData = C_SecurityHelper.sym_device_encrypt(vBData);
   				String vSData = Base64.encodeBytes(vBData);
   				String vSSign = Base64.encodeBytes( C_SecurityHelper.asym_sign( C_MainActivity.fDevicePrivateKey, vBData) );
   				vRes = Insert_T_OUT(pDb, pType, vZipped, "T", vSSign, vSData);
   			}
		} catch(Exception e){
			C_Log.v(0, C_TAG, "AddDeviceOutDataRec err:" + e.getMessage());
		}
    	return vRes;
    }    
    
    /**
     * Выполнение любого SQL-запроса без возврата результата
     * @param pDb - дексриптор базы данных
     * @param pSQL - текст запроса
     * @return int -2 = ошибка получения дескриптора БД, -1 = ошибка выполнения запроса, =0 - без ошибок
     */
    public int ExecSQL(SQLiteDatabase pDb, String pSQL) {
    	C_Log.v(3, C_TAG, "ExecSQL - pSQL=" + pSQL +  " - start");
    	int vRes = -1;
   		try {
   			pDb.execSQL(pSQL);
   			vRes = 0;
		} catch(Exception e) {
			C_Log.v(0, C_TAG, "ExecSQL err:" + e.getMessage());
		}
    	return vRes;
    }

    /**
     * Выполнение любого SQL-запроса c возвращением результата одной строкой JSON. Select должен возвращать одну строковую колонку! 
     * @param pDb - дексриптор базы данных
     * @param pSQL - текст SQL запроса SELECT
     * @return string - результат выполнения запроса в виде JSON. 
     * в формате {"R":[a1,a2,...,aN]} где a1..aN - значения колонки для записей 1..N
     * Примеры запросов: 
     * create table cc (f1 integer, f2 datetime, f3 varchar(20))
     * insert into cc values (1, now(), 'ssss')
     * select '{"f1":"' || f1 || '","f2":"' || datetime(f2,'localtime') || '"}' from cc where f3 like '%'
     * drop table cc
     * 
     * Если при выполнении запроса возникает ошибка, результат выдается в виде {"E":["текст ошибки"]}. 
     * Пустой результат возвращается в виде {"R":[]}
     * 
     * Документация по SQLLite: 
     * http://www.sqlite.org/docs.html  
     * http://www.sqlite.org/lang.html 
     */
    public String ExecSQL_Select(SQLiteDatabase pDb, String pSQL) {
    	C_Log.v(3, C_TAG, "ExecSQL_Select - pSQL=" + pSQL +  " - start");
    	String vResult = "{\"R\":[";
   		try {
   			Cursor cursor = pDb.rawQuery(pSQL, null);
   			try {
   	       		while (cursor.moveToNext()) {
   	    			if (vResult.length() > 6) {
   	   					vResult = vResult + ",";
   	    			}
   	   				vResult = vResult + cursor.getString(0);
   	    		}
   	    	} finally {	
   	    		cursor.close();
   	    	}
   	    	vResult =vResult + "]}"; 
		} catch(Exception e) {
			C_Log.v(0, C_TAG, "ExecSQL_Select err:" + e.getMessage());
			vResult = "{\"E\":\"" + e.getMessage() +"\"}";
		}
    	return vResult;
    }
 
	/**
	* Получение строки JSON-массива записей исходящей очереди (см. описание в formats.rtf)
	* @param pDb - дексриптор базы данных
	* @param pLastId - ид последней записи (получается предварительно с помощью метода GetLastId_T_Out)
	* @return - строка JSON-массива записей исходящей очереди
	*/
    public String GetOutData(SQLiteDatabase pDb, long pLastId) throws UnsupportedEncodingException{
    	C_Log.v(3, C_TAG, "GetOutData pLastId=" + pLastId + " - start");
    	String vResult = null;
    	Cursor cursor = pDb.query(C_Vars.C_T_OUT_NAME, 
    			new String[] {"OUT_ID","OUT_TYPE","OUT_ZIPPED","OUT_CRYPTED","datetime(OUT_DATE,'localtime') OUT_DATE","OUT_SIGN","OUT_DATA"}, 
    	    	"OUT_ID <= " + pLastId, null, null, null, "OUT_ID");
    	try {
       		while (cursor.moveToNext()) {
    			if (vResult == null) {
    				vResult = "{\"O\":[";
   				} else {
   					vResult = vResult + ",";
    			}
   				vResult = vResult + 
   					"{\"R\":[" + cursor.getString(0) + 
   					",\"" +	cursor.getString(1) + 
   					"\",\"" +	cursor.getString(2) + 
   					"\",\"" +	cursor.getString(3) + 
   					"\",\"" +	cursor.getString(4) + 
   					"\",\"" +	cursor.getString(5) + 
   					"\",\"" +	cursor.getString(6) + 
					"\"]}";
    		}
    	} finally {	
    		cursor.close();
    	}
    	if (vResult != null) {
			vResult = vResult + "]}";
	    	C_Log.v(2, C_TAG, "GetOutData res len=" + vResult.length() + " - end");
		} else {
	    	C_Log.v(3, C_TAG, "GetOutData - end");
		}
    	return vResult;
	}
    
	/**
	* Получение ид последней записи, используется перед вызовом метода GetOutData
	* для исключения потери данных при параллельной обработке
	* @param pDb - дексриптор базы данных
	* @return - ид последней записи
	*/    
    public long GetLastId_T_Out(SQLiteDatabase pDb){
    	C_Log.v(3, C_TAG, "GetLastId_T_Out - start");
    	long vResult = -1;
    	Cursor cursor = pDb.query(C_Vars.C_T_OUT_NAME, 
    			new String[] {"OUT_ID"}, null, null, null, null, "OUT_ID desc");
    	try {
    		if (cursor.moveToNext())
    			vResult = cursor.getLong(0);
    	} finally {	
    		cursor.close();
    	}
		C_Log.v(3, C_TAG, "GetLastId_T_Out vResult=" + vResult + " - end");
    	return vResult;
	}
    
	/**
	* Удаление отправленных записей из исходящей очереди
	* @param pDb - дексриптор базы данных
	* @param pLastId - ид последней отправленной записи (получается перед отправкой с помощью метода GetLastId_T_Out)
	*/    
    public void Delete_t_out(SQLiteDatabase pDb, long pLastId){
   		C_Log.v(3, C_TAG, "Delete_t_out - pLastId=" + pLastId + " - start");
   		Integer i = pDb.delete(C_Vars.C_T_OUT_NAME, "OUT_ID <= " + pLastId, null);
   		C_Log.v(3, C_TAG, "Delete_t_out rows=" + i + " - end");
    }

	/**
	* Солучение списка всех переменных  
	* @param pDb - дексриптор базы данных
	* @param pVars - возвращаемый список переменных и их значений
	*/    
    public void GetArrayOfVars(SQLiteDatabase pDb, Properties pVars){
    	C_Log.v(3, C_TAG, "GetArrayOfVars - start");
	   	Cursor cursor = pDb.query(C_Vars.C_T_VARS_NAME, new String[] {"V_NAME","V_VALUE"}, null, null, null, null, null);
	   	try {
   			do{
   				String vName = cursor.getString(cursor.getColumnIndex("V_NAME"));
   				String vValue = cursor.getString(cursor.getColumnIndex("V_VALUE"));
   				pVars.put(vName,vValue);	    						
   			} while (cursor.moveToNext());
    	} finally {	
    		cursor.close();
    	}
    	C_Log.v(3, C_TAG, "GetArrayOfVars pVars len=" + pVars.values().size() + " - end");
    }
	    
	    
	/**
	* Добавление или обновление адреса пакета для загрузки
	* @param pDb - дексриптор базы данных
	* @param pId - ид пакета
	* @param pUrl - адрес пакета
	* @return результат
	*/
    public void UpdateOrInsert_T_PACK(SQLiteDatabase pDb, Integer pId, String pStatus, String pUrl){
   		C_Log.v(3, C_TAG, "UpdateOrInsert_T_PACK pId=" + pId + " pStatus=" + pStatus + " url=" + pUrl + " - start");
    	ContentValues vCV = new ContentValues();
    	vCV.put("P_ID", pId);
    	vCV.put("P_STATUS", pStatus);
    	vCV.put("P_URL", pUrl);
   		Integer i = pDb.update(C_Vars.C_T_PACK_NAME, vCV, "P_ID=" + pId, null);
   		if (i != 0){
   			C_Log.v(2, C_TAG, "UpdateOrInsert_T_PACK id=" + pId + " - updated");
   		} else {
   			pDb.insert(C_Vars.C_T_PACK_NAME, null, vCV);
   			C_Log.v(1, C_TAG, "UpdateOrInsert_T_PACK id=" + pId + " - inserted");
   		}
   		C_Log.v(3, C_TAG, "UpdateOrInsert_T_PACK pId=" + pId + " - end");
    }
    
	/**
	* Обновление статуса загрузки пакета для указанного ид пакета
	* @param pDb - дексриптор базы данных
	* @param pId - ид пакета
	*/
    public void SetStatus_T_PACK(SQLiteDatabase pDb, Integer pId, String pStatus){
    	C_Log.v(3, C_TAG, "SetStatus_T_PACK pId=" + pId + " - start");
    	ContentValues cv = new ContentValues();
    	cv.put("P_STATUS", pStatus);
   		pDb.update(C_Vars.C_T_PACK_NAME, cv, "P_ID=" + pId, null);
   		C_Log.v(3, C_TAG, "SetStatus_T_PACK pId=" + pId + " - end");
    }
    
	/**
	* Получение статуса загрузки пакета для указанного ид пакета
	* @param pDb - дексриптор базы данных
	* @param pId - ид пакета
	* @param pDefValue - дефолтовое значение
	* @return результат или ошибка
	*/
    public String GetStatus_T_PACK(SQLiteDatabase pDb, Integer pId, String pDefValue){
    	C_Log.v(3, C_TAG, "SetStatus_T_PACK pId=" + pId + " - start");
     	Cursor vCursor = pDb.query(C_Vars.C_T_PACK_NAME, new String[] {"P_STATUS"}, "P_ID="+pId, null,null,null,null);
    	String vResult = pDefValue;
	    if(vCursor.moveToFirst()){
	    	vResult = vCursor.getString(0);
	    	C_Log.v(2, C_TAG, "GetNotifyIdByWidgetId pId=" + pId + " res=" + vResult + " - found");
	    }
	    vCursor.close();     	
   		C_Log.v(3, C_TAG, "SetStatus_T_PACK pId=" + pId + " - end");
   		return vResult;
    }    
	
    /**
	* Получение количества необработанных (незагруженных) пакетов в очереди загрузки
	* @param pDb - дексриптор базы данных
	* @return  количество пакетов в статусе отличном от "P"
	*/
    public int GetCountOfUnprocessedPacks(SQLiteDatabase pDb){
    	C_Log.v(3, C_TAG, "GetCountOfUnprocessedPacks - start");
     	Cursor vCursor = pDb.query(C_Vars.C_T_PACK_NAME, new String[] {"count(1)"}, "P_STATUS != 'P'", null,null,null,null);
    	int vResult = 0;
	    if(vCursor.moveToFirst()){
	    	vResult = vCursor.getInt(0);
	    	C_Log.v(2, C_TAG, "GetCountOfUnprocessedPacks res=" + vResult + " - found");
	    }
	    vCursor.close();     	
   		C_Log.v(3, C_TAG, "GetCountOfUnprocessedPacks - end");
   		return vResult;
    }

	/**
	* Удаление записи из списка пакетов для загрузки
	* @param pDb - дексриптор базы данных
	* @param pId - ид пакета
	*/
    public void Delete_T_PACK (SQLiteDatabase pDb, Integer pId){
    	C_Log.v(3, C_TAG, "Delete_T_PACK id=" + pId+ " - start");
   		pDb.delete(C_Vars.C_T_PACK_NAME, "P_ID=" + pId, null);
   		C_Log.v(3, C_TAG, "Delete_T_PACK id=" + pId+ " - end");
    }    
    
	/**
	* Получение списка адресов незагруженных пакетов 
	* @param pDb - дексриптор базы данных
	* @param pUrls - возвращаемый список
	*/    
    public void GetArrayOfPackages(SQLiteDatabase pDb, Properties pUrls){
    	C_Log.v(3, C_TAG, "GetArrayOfPackages - start");
    	Cursor vCursor = pDb.query(C_Vars.C_T_PACK_NAME, new String[] {"P_ID","P_URL"}, "P_STATUS = 'N'", null, null, null, "P_ID");
    	try {
   			while (vCursor.moveToNext()){
    			int vId = vCursor.getInt(vCursor.getColumnIndex("P_ID"));
    			String vUrl = vCursor.getString(vCursor.getColumnIndex("P_URL"));
    			pUrls.put(String.valueOf(vId),vUrl);	    						
    			C_Log.v(2, C_TAG, "GetArrayOfPackages add id=" +vId + " url=" + vUrl);
    		}
    	} finally {
    		vCursor.close();
    	}
    	C_Log.v(3, C_TAG, "GetArrayOfPackages - end");
    }
    
    
	/**
	* Солучение списка адресов кжширующих серверов  
	* @param pDb - дексриптор базы данных
	* @param pUrls - возвращаемый список
	*/    
    public String GetArrayOfUrl(SQLiteDatabase pDb){
    	C_Log.v(3, C_TAG, "GetArrayOfUrl - start");
    	String vResult = "";
    	Cursor vCursor = pDb.query(C_Vars.C_T_URLS_NAME, new String[] {"U_ID","U_URL"}, null, null, null, null, "U_ACCESS_DATE DESC");
    	try {
       		if (vCursor.isAfterLast()){
   				// первоначальная загрузка
        		vResult = "-1," + C_Vars.C_DEFAULT_SYNC_PROC_URL1 + "::" + "-2," + C_Vars.C_DEFAULT_SYNC_PROC_URL2+"::" +
        			"-3," + C_Vars.C_DEFAULT_SYNC_PROC_URL3;
    			C_Log.v(2, C_TAG, "GetArrayOfUrl add default");
    		} else {	
    			while (vCursor.moveToNext()){
    				int vId = vCursor.getInt(vCursor.getColumnIndex("U_ID"));
    				String vUrl = vCursor.getString(vCursor.getColumnIndex("U_URL"));
    				if (vResult.length() != 0) vResult = vResult + "::"; 
    				vResult = vResult + vId + ","+vUrl;
    				C_Log.v(2, C_TAG, "GetArrayOfUrl add id=" +vId + " url=" + vUrl);
    			} ;
    		}	
    	} finally {
    		vCursor.close();
    	}
    	C_Log.v(3, C_TAG, "GetArrayOfUrl - end");
    	return vResult;
    }
	    
	/**
	* Добавление или обновление адреса кэширующего сервера
	* @param pDb - дексриптор базы данных
	* @param pId - ид адреса
	* @param pUrl - URL процедуры обмена
	*/
    public void UpdateOrInsert_T_URLS(SQLiteDatabase pDb, Integer pId, String pUrl){
   		C_Log.v(3, C_TAG, "UpdateOrInsert_T_URLS pId=" + pId + " url=" + pUrl + " - start");
    	ContentValues vCV = new ContentValues();
    	vCV.put("U_ID", pId);
    	vCV.put("U_URL", pUrl);
    	vCV.put("U_ACCESS_DATE", new Date().getTime());
   		Integer i = pDb.update(C_Vars.C_T_URLS_NAME, vCV, "U_ID=" + pId, null);
   		if (i != 0){
   			C_Log.v(2, C_TAG, "UpdateOrInsert_T_URLS id=" + pId + " - update");
   		} else {
   			pDb.insert(C_Vars.C_T_URLS_NAME, null, vCV);
   			C_Log.v(1, C_TAG, "UpdateOrInsert_T_URLS id=" + pId + " - insert");
   		}
   		C_Log.v(3, C_TAG, "UpdateOrInsert_T_URLS pId=" + pId + " - end");
    }

	/**
	* Обновление даты последнего успешного обращения к указанному кэширующему серверу
	* @param pDb - дексриптор базы данных
	* @param pId - ид адреса кэширующего сервера
	*/
    public void SetAccessDate_T_URLS(SQLiteDatabase pDb, Integer pId){
    	C_Log.v(3, C_TAG, "SetAccessDate_T_URLS pId=" + pId + " - start");
    	ContentValues cv = new ContentValues();
    	cv.put("U_ACCESS_DATE", new Date().getTime());
   		pDb.update(C_Vars.C_T_URLS_NAME, cv, "U_ID=" + pId, null);
   		C_Log.v(3, C_TAG, "SetAccessDate_T_URLS pId=" + pId + " - end");
    }
	    
	/**
	* Удаление из списка серверов указанного адреса кэширующего сервера
	* @param pDb - дексриптор базы данных
	* @param pId - ид адреса кэширующего сервера
	*/
    public void Delete_T_URLS (SQLiteDatabase pDb, Integer pId){
    	C_Log.v(3, C_TAG, "Delete_T_URLS id=" + pId+ " - start");
   		pDb.delete(C_Vars.C_T_URLS_NAME, "U_ID=" + pId, null);
   		C_Log.v(3, C_TAG, "Delete_T_URLS id=" + pId+ " - end");
    }

	/**
	* Добавление или обновление информации уведомления (канала)
	* @param pDb - дексриптор базы данных
	* @param pId - ид уведомления (канала)
	* @param pType - тип: N - уведомление, W - виджет (канал)
	* @param pStartDate - дата начала действия уведомления
	* @param pStopDate - дата окончания действия уведомления
	* @param pLatitude - географическая широта точки действия
	* @param pLongitude - географическая долгота точки действия
	* @param pText - текст уведомления (канала)
	* @param pUrl - адрес страницы, используемый при переходе с виджета или уведолмения
	* @param pImgData - данные картинки увдеомления или виджета в Base64
	*/
	public void UpdateOrInsert_T_NOTIFS (SQLiteDatabase pDb, Integer pId, String pType, String pStartDate, 
			String pStopDate, String pLatitude, String pLongitude, String pText, String pUrl, byte[] pImgData) {
		C_Log.v(3, C_TAG, "UpdateOrInsert_T_NOTIFS pId=" + pId + " - start");
    	ContentValues vCV = new ContentValues();
    	vCV.put("N_ID", pId);
    	vCV.put("N_TYPE", pType);
    	vCV.put("N_START_DATE", pStartDate);
    	vCV.put("N_STOP_DATE", pStopDate);
    	vCV.put("N_LATITUDE", pLatitude);
    	vCV.put("N_LONGITUDE", pLongitude);
    	vCV.put("N_TEXT", pText);
    	vCV.put("N_URL", pUrl);
    	vCV.put("N_IMAGE", pImgData);
    	vCV.put("N_SHOW", 0);
    	Integer i = pDb.update(C_Vars.C_T_NOTIFS_NAME, vCV, "N_ID=" + pId.toString(), null);
    	if (i != 0){
    		C_Log.v(2, C_TAG, "UpdateOrInsert_T_NOTIFS pId=" + pId + " - updated");
    	} else {
    		pDb.insert(C_Vars.C_T_NOTIFS_NAME, null, vCV);
   			C_Log.v(1, C_TAG, "UpdateOrInsert_T_NOTIFS pId=" + pId + " - inserted");
   		}
    	C_Log.v(3, C_TAG, "UpdateOrInsert_T_NOTIFS pId=" + pId + " - end");
    }

	/**
	* Удаление информации указанного уведомления (канала)
	* @param pDb - дексриптор базы данных
	* @param pId - ид уведомления (канала)
	*/
    public void Delete_T_NOTIFS (SQLiteDatabase pDb, Integer pId){
    	C_Log.v(3, C_TAG, "Delete_T_NOTIFS id=" + pId + " - start");
    	pDb.delete(C_Vars.C_T_NOTIFS_NAME, "N_ID="+pId.toString(), null);
    	C_Log.v(3, C_TAG, "Delete_T_NOTIFS id=" + pId + " - end");
	}
	
	/**
	* Обновление количества отображений уведомления
	* @param pDb - дексриптор базы данных
	* @param pId - ид уведомления
	*/    
    public void AddNotifyShowCount(SQLiteDatabase pDb, Integer pNId){
    	C_Log.v(3, C_TAG, "AddNotifyShowCount pId=" + pNId + " - start");
    	pDb.execSQL("update " + C_Vars.C_T_NOTIFS_NAME + " set N_SHOW=N_SHOW+1 where N_ID=" + pNId);
   		C_Log.v(3, C_TAG, "AddNotifyShowCount pId=" + pNId + " - end");
    }
    
    
	/**
	* Получение ид канала по ид виджета
	* @param pDb - дексриптор базы данных
	* @param pWidgetId - ид виджета
	* @param pDefValue - ид канала по умолчанию (на случай если канал удален, а виджет у пользователя еще остался)
	* @return - ид уведомления 
	*/    
    public int GetNotifyIdByWidgetId (SQLiteDatabase pDb, int pWidgetId, int pDefValue){
    	C_Log.v(3, C_TAG, "GetNotifyIdByWidgetId pWidgetId=" + pWidgetId + " pDefValue=" + pDefValue + " - start");
    	Cursor vCursor = pDb.query(C_Vars.C_T_WIDGETS_NAME,new String[] {"N_ID"}, "W_ID="+pWidgetId, null,null,null,null);
    	int vResult = pDefValue;
	    if(vCursor.moveToFirst()){
	    	vResult = vCursor.getInt(0);
	    	C_Log.v(2, C_TAG, "GetNotifyIdByWidgetId pWidgetId=" + pWidgetId + " res=" + vResult + " - found");
	    }
	    vCursor.close();
	    C_Log.v(3, C_TAG, "GetNotifyIdByWidgetId pWidgetId=" + pWidgetId + " res=" + vResult + " - end");
	    return vResult;
    }
    
    
    /**
	* Получение текущего местоположения в фоновом режиме, затем проверка и запуск уведомлений с указанным местоположением
	* вызывается только после проверки наличия уведомлений с указанным местоположением!
	* @param pDb - дексриптор базы данных
	* @param pContext - контекст вывода уведомлений
	* @param pShowCountEdge - максимальное количество отображений уведомления (обычно = 1)
	*/	    
	public void GetLocationAndExecCheckNotifs (final SQLiteDatabase pDb, final Context pContext, final int pShowCountEdge) {
		C_Log.v(3, C_TAG, "GetLocationAndExecCheckNotifs - end");
		// определение координат местоположения в фоновом режиме:
		final C_LocationHelper vLocationHelper = new C_LocationHelper(pContext, null, true);
		vLocationHelper.setChangedLocationListener(new LocationListener() {
			@Override
			public void onLocationChanged(Location vLocation) {
				if (vLocation != null) {
					// завершаем сервис обновления местоположения
					vLocationHelper.cancel();
					//  повторяем вызов с полученным местоположением
					FireNotifs(pDb, pContext, pShowCountEdge, vLocation);
					C_Log.v(2, C_TAG, "onLocationChanged - ok");
				} else {
					C_Log.v(1, C_TAG, "onLocationChanged : Location is null !");
				}
			}
			@Override
			public void onProviderDisabled(String provider){
				C_Log.v(2, C_TAG, "onProviderDisabled provider=" + provider);
				vLocationHelper.cancel();
			} 
			@Override 
			public void onProviderEnabled(String provider){ 
				C_Log.v(2, C_TAG, "onProviderEnabled provider=" + provider);
			} 
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras){ 
				C_Log.v(2, C_TAG, "onStatusChanged provider=" + provider + " status=" + status);
			} 
		});

		Location vLocation = vLocationHelper.getLocation(C_Vars.C_NOTIF_LOCATION_ACCURACY, C_Vars.C_NOTIF_LOCATION_EXPIRE);
		if (vLocation != null) {
			// имеется готовый результат - сразу повторяем вызов с полученным местоположением:
			FireNotifs(pDb, pContext, pShowCountEdge, vLocation);					
			C_Log.v(2, C_TAG, "GetLocationAndExecCheckNotifs - exec");
		} else {
			if (!vLocationHelper.fIsProviderEnabled) {
				// нет готового результата и нет ни одного доступного провайдера - завершаем вызов:
				C_Log.v(1, C_TAG, "GetLocationAndExecCheckNotifs -  no providers");
			} else {
				// иначе готового результата нет но есть доступный провайдер, запущен запрос на обновление, 
				// ждем ответа и вызываем FireNotifs в onLocationChanged 
				C_Log.v(2, C_TAG, "GetLocationAndExecCheckNotifs - wait");
			}
		}
	}
	
	/**
	* Проверка условий срабатывания отображение уведомлений и запуск отобраения уведомлений
	* @param pDb - дексриптор базы данных
	* @param pContext - контекст вывода уведомлений
	* @param pShowCountEdge - максимальное количество отображений уведомления (обычно = 1)
	*/       
    public void FireNotifs (SQLiteDatabase pDb, Context pContext, int pShowCountEdge, Location pLocation) {
    	C_Log.v(3, C_TAG, "FireNotifs - start");
    	boolean vIsLocationNeed = false;
    	Cursor cursor = pDb.query(C_Vars.C_T_NOTIFS_NAME, new String[] {"N_ID","N_LATITUDE","N_LONGITUDE"}, 
    		"N_SHOW < " + pShowCountEdge +
    		" and (N_START_DATE='' or N_START_DATE <= datetime(CURRENT_TIMESTAMP,'localtime'))"+
    		" and (N_STOP_DATE='' or N_STOP_DATE >= datetime(CURRENT_TIMESTAMP,'localtime'))"+
       		" and N_TYPE='N'", null, null, null, "N_ID");
        try {
        	int vNCount = 0;
        	while (cursor.moveToNext()) {
    			boolean vShowNotifNeeded = true;
    			String vSLatitude = cursor.getString(1);
    			String vSLongitude = cursor.getString(2);
    			if (vSLatitude == null) vSLatitude = "";
    			if (vSLongitude == null) vSLongitude = "";
   				// проверка  - если нужно учитывать текущее местоположение, но оно неизвестно, 
    			// то отложить вывод до получения местоположения
				if (vSLatitude.length()>0 | vSLongitude.length()>0) {
					if (pLocation == null) {
	        			vShowNotifNeeded = false;
	        			vIsLocationNeed = true;
					} else {
						double vLongitude = Double.parseDouble(vSLongitude);
						double vLatitude = Double.parseDouble(vSLatitude);
						double vDistance = C_Utils.GetDistance(pLocation.getLatitude(), pLocation.getLongitude(), vLatitude, vLongitude);
	        			vShowNotifNeeded = vDistance <= C_Vars.C_NOTIF_DISTANCE;
					}
        		}
				// проверка - за один раз выводить не больше C_MAX_NOTIF_COUNT уведомлений! 
        		vNCount ++;
        		if ( vNCount > C_Vars.C_MAX_NOTIF_COUNT ) {
        			vShowNotifNeeded = false;
        		}
        		if (vShowNotifNeeded) {
        			// получение данных увдеомления:
        			int vNId = cursor.getInt(0);
        			String vCText = "";
        			String vTText = C_Vars.C_ALERT_TITLE;
       				String vNotifyText = GetNotifyTextByNotifyID(pDb, vNId);

       				if (vNotifyText == null) vNotifyText = "|";
       				String[] vLines = vNotifyText.split("[|]");
       				if(vLines.length == 0) vCText = vNotifyText;
       				if(vLines.length > 0) vTText = vLines[0]; //+" ("+pNId+")"; 
        			if(vLines.length > 1) vCText = vLines[1];			
        			if(vLines.length > 2) vCText = vCText + "\n" + vLines[2];		
        			String vSData = "{\"N\":" + Integer.toString(vNId) + ",\"D\":" + C_Utils.IntToDate(0) + "\"}";
       				// вывод увдеомления:
       				if ( C_NotificationHelper.CreateNotify(pContext, vTText, vCText, vNId) ) {
       				// 		отметка о том что уведомление обработано и отправка записи на сервер:
       					AddNotifyShowCount(pDb, vNId);
       					AddOutDataRec(pDb, C_Vars.C_INFO_SHOW_NOTIFICATION, vSData.getBytes());
       				}
        		}
       		}
       	} finally {	
       		cursor.close();
       	}
       	C_Log.v(3, C_TAG, "FireNotifs - end");
       	// если имеются готовые уведомления в которых указано местоположение, а местоположение неизвество, 
       	// то запускаем GetLocationAndCheckNotifs для получения текушего местоположения, из которого 
       	// повторяется запуск FireNotifs с указанным местоположением
       	if (vIsLocationNeed & (pLocation == null)) {
       		GetLocationAndExecCheckNotifs(pDb, pContext, pShowCountEdge);
       	}
    }
    
	/**
	* Получение данных виджета по ид виджета
	* @param pDb - дексриптор базы данных
	* @param pWidgetId - ид виджета
	* @param pNotifyData - возвращаемые строковые данные виджета
	* @return - картинка виджета (может быть null)
	*/
    public byte[] GetWidgetDataByWidgetID(SQLiteDatabase pDb, int pWidgetId, String[] pNotifyData) {
    	C_Log.v(3, C_TAG, "GetWidgetDataByWidgetID pWidgetId=" + pWidgetId + " - start");
    	int vNotifyId = GetNotifyIdByWidgetId(pDb, pWidgetId, -1); // -1 - error!
    	byte[] vResult = GetNotifyDataByNotifyID(pDb, vNotifyId, pNotifyData);
    	C_Log.v(3, C_TAG, "GetWidgetDataByWidgetID pWidgetId=" + pWidgetId + " - end");
    	return vResult;
    }    
    
	/**
	* Получение данных виджета по ид уведомления
	* @param pDb - дексриптор базы данных
	* @param pNotifyId - ид уведомления
	* @param pNotifyData - возвращаемые строковые данные виджета
	* @return - картинка виджета (может быть null)
	*/
    public byte[] GetNotifyDataByNotifyID(SQLiteDatabase pDb, int pNotifyId, String[] pNotifyData) {
    	C_Log.v(3, C_TAG, "GetNotifyDataByNotifyID pNotifyId=" + pNotifyId + " - start");
    	Cursor vCursor = pDb.query(C_Vars.C_T_NOTIFS_NAME, new String[] {"N_TEXT","N_IMAGE"}, "N_ID="+pNotifyId, null, null, null, null);
    	byte[] vResult = null; 
    	try{
    		if(vCursor.moveToFirst()) {
    			pNotifyData[0] = String.valueOf(pNotifyId);
    			pNotifyData[1] = vCursor.getString(vCursor.getColumnIndex("N_TEXT"));
    			vResult = vCursor.getBlob(vCursor.getColumnIndex("N_IMAGE"));
    			C_Log.v(3, C_TAG, "GetNotifyDataByNotifyID - ok");
    		} else {
//!!! - ошибка в SDK!			C_Log.v(0, C_TAG, "GetNotifyDataByNotifyID n_id=" + pNotifyId + " - data not found"); 
    		}
    	}finally{
    	    vCursor.close();    		
    	}
    	C_Log.v(3, C_TAG, "GetNotifyDataByNotifyID pNotifyId=" + pNotifyId + " - end");
    	return vResult; 
    }
    
	/**
	* Получение текста сообщения виджета по ид канала
	* @param pDb - дексриптор базы данных
	* @param pNotifyId - ид уведомления
	* @return - строка текста сообщения или "" если ид не наыден 
	*/
    public String GetNotifyTextByNotifyID(SQLiteDatabase pDb, int pNotifyId) {
    	C_Log.v(3, C_TAG, "GetNotifyTextByNotifyID pNotifyId=" + pNotifyId + " - start");
    	Cursor vCursor = pDb.query(C_Vars.C_T_NOTIFS_NAME, new String[] {"N_TEXT"}, "N_ID="+pNotifyId, null, null, null, null);
    	String vResult = ""; 
    	try{
    		if(vCursor.moveToFirst()) {
    			vResult = vCursor.getString(vCursor.getColumnIndex("N_TEXT"));
    		}
    	}finally{
    	    vCursor.close();    		
    	}
    	C_Log.v(3, C_TAG, "GetNotifyTextByNotifyID pNotifyId=" + pNotifyId + " - end");
    	return vResult; 
    }
    
	/**
	* Получение URL страницы перехода при тапе по виджету
	* @param pDb - дексриптор базы данных
	* @param pNotifyId - ид уведомления
	* @param pDefUrl - URL по умолчанию (для случая если данные виджета не найдены)
	* @return - URL из данных виджета или pDefUrl если данные виджета не найдены 
	*/ 
    public String GetWidgetUrlByNotifyID (SQLiteDatabase pDb, int pNotifyId, String pDefUrl){
    	C_Log.v(3, C_TAG, "GetWidgetUrlByNotifyID pNotifyId=" + pNotifyId + " pDefUrl=" + pDefUrl + " - end");
    	String vResult = pDefUrl;
    	Cursor vCursor = pDb.query(C_Vars.C_T_NOTIFS_NAME,new String[] {"N_URL"}, "N_ID="+pNotifyId, null, null,null,null);
    	try{
    		if (vCursor.moveToFirst()){
    			vResult = vCursor.getString(vCursor.getColumnIndex("N_URL"));
    			C_Log.v(2, C_TAG, "GetWidgetUrlByNotifyID n_id=" + pNotifyId + " res=" + vResult);
    		} else {
    			C_Log.v(0, C_TAG, "e:GetWidgetUrlByNotifyID n_id=" + pNotifyId + " - data not found, def=" + pDefUrl);	
    		}
    	}finally{
    		vCursor.close();    		
    	}
    	C_Log.v(3, C_TAG, "GetWidgetUrlByNotifyID pNotifyId=" + pNotifyId + " - end");
    	return vResult;
    }
    
	/**
	* Получение строки JSON-массива наименований и значений переменных по заданому условия
	* @param pDb - дексриптор базы данных
	* @param pMask - значение условия выбора переменный (like pMask)
	* @return - строка JSON-массива наименований и значений переменных
	*/
    public String GetVars(SQLiteDatabase pDb, String pMask){
    	C_Log.v(3, C_TAG, "GetNotifs - start");
    	String vResult = "{\"V\":[";
    	//{"V":[["n1","v1"],["n2","v2"],["n2","v2"]]}
    	Cursor cursor = pDb.query(C_Vars.C_T_VARS_NAME, new String[] {"V_NAME","V_VALUE"}, 
    			"V_NAME like '" + pMask + "'", null, null, null, null);
    	try {
   			while (cursor.moveToNext()) {
   				if (vResult.length() > 6) {
   					vResult = vResult + ",";
   				}
   				vResult = vResult +"[\"" + 
   					cursor.getString(0).replace("\"", "`") + "\",\"" + 
   					cursor.getString(1).replace("\"", "`") + "\"]";
    		}
			vResult = vResult +"]}";   			
    	} finally {	
    		cursor.close();
    	}
    	C_Log.v(3, C_TAG, "GetNotifs - end");
    	return vResult;
    }
 
}


