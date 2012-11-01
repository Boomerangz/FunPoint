package kz.crystalspring.android_client;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.crypto.SecretKey;

import kz.crystalspring.funpoint.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class C_NetHelper extends android.app.Application{
	private static String C_TAG = "NetHelper";
	public static boolean fIsWorking = false;
//	private static Context fContext;
	
	/**
	 * Загрузка данных с адреса pUrl, с передачей методом post данных из pOutData (если pOutData  is not null)
	 * @param pUrl - адрес
	 * @param pOutData - данные исходящей очереди
	 * @return - строка JSON входящих данных или ошибка. Возврат ""  - тоже ошибка!
	 */
	public static byte[] DownloadFromUrl(String pUrl, String pOutData) throws ClientProtocolException, IOException, IllegalAccessException{
		C_Log.v(3, C_TAG, "DownloadFromUrl pUrl=" + pUrl);
		HttpClient httpClient = new DefaultHttpClient();
/*		
		String proxyHost = android.net.Proxy.getDefaultHost();
		int proxyPort = android.net.Proxy.getDefaultPort();
		if (proxyPort > 0) {
			C_Log.v(2, C_TAG, "DownloadFromUrl proxy =" + proxyHost + ":" + proxyPort);
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
*/		
		HttpPost vHttpPost = new HttpPost(pUrl);
		vHttpPost.setHeader("Accept", "application/json");
		vHttpPost.setHeader("Content-type", "application/json;charset=UTF-8");
		if (pOutData != null) {
			
			C_Log.v(2, C_TAG, "DownloadFromUrl set pOutData len=" + pOutData.length());
			StringEntity vStringEntity = new StringEntity(pOutData, HTTP.UTF_8);
			vHttpPost.setEntity(vStringEntity);
		}
		
		HttpResponse response = httpClient.execute(vHttpPost);
		int vStCode = response.getStatusLine().getStatusCode();
		if (vStCode == HttpStatus.SC_OK) {
			C_Log.v(2, C_TAG, "DownloadFromUrl responce=ok");
			HttpEntity entity = response.getEntity();
			byte[] vInData = EntityUtils.toByteArray(entity);
			return vInData;
		} else {
			C_Log.v(0, C_TAG, "e:DownloadFromUrl responce=" + vStCode);
			return null;
//			throw new IllegalAccessException("HttpStatus="+VStCode);	
		}
	
	}


	/**
	 * Внутренняя процедура загрузки пакета данных 
	 * Пакет представляет собой байтовцый массив в формате
	 * Sign(126)_FileName1Len(4)_FileName1(..)_FileData1Len(4)_FileData1(..)_ ... _FileNameNLen(4)_FileNameN(..)_FileDataNLen(4)_FileDataN(..)_
	 * @param pContext 
	 * @param pUrl - урл файла пакета
	 * @return
	 */
	private static boolean DownloadPack (Context pContext, String pUrl) {
		try {
			byte[] vData = DownloadFromUrl(pUrl, null);
		
			if (vData == null ) {
				C_Log.v(1, C_TAG, "DownloadPack responce = null");
				return false;
			} else {
				C_Log.v(2, C_TAG, "DownloadPack - url=" + pUrl + " len=" + vData.length);
			}
			String vDocsDir = pContext.getFilesDir() + "/";
			// расшифровываем общим ключем
			vData = C_SecurityHelper.sym_decrypt(C_SecurityHelper.GetCommonSecretKey(), vData);
			byte[] vSign = new byte[128];
			byte[] vBLen = new byte[4];

			// извлекаем цифровую подпись (первые 128 байт)
			int vPos = 128;
			System.arraycopy(vData, 0, vSign, 0, vPos);
			int vPackLen = vData.length - vPos;
			byte[] vPackData = new byte[vPackLen];
			System.arraycopy(vData, vPos, vPackData, 0, vPackLen);

			// проверяем подпись
			PublicKey vSrvPublicKey = C_SecurityHelper.GetSrvPublicKey(pContext);
			if ( !C_SecurityHelper.asym_verify(vSrvPublicKey, vPackData, vSign) ) {
				C_Log.v(0, C_TAG, "DownloadPack - signature is invalid!");
				return false;
			}
			vPos = 0;
			while (vPos < vPackLen) {
				// получаем длину имени файла
				System.arraycopy(vPackData, vPos, vBLen, 0, 4);
				int vFNLen = C_Utils.Bytes2long(vBLen); 
				vPos = vPos + 4;
				if (vFNLen > 100 | vFNLen < 1) {
					C_Log.v(0, C_TAG, "DownloadPack - filename length is too big! len=" + vFNLen);
					return false;
				}
				// поличаем имя файла
				byte[] vFNData = new byte[vFNLen]; 
				System.arraycopy(vPackData, vPos, vFNData, 0, vFNLen);
				String vFileName = new String(vFNData, "UTF8");
				vPos = vPos + vFNLen;
				// получаем длину содержимого файла
				System.arraycopy(vPackData, vPos, vBLen, 0, 4);				
				int vFDLen = C_Utils.Bytes2long(vBLen); 
				vPos = vPos + 4;
				if (vFNLen > 1000000 | vFNLen < 0) {
					C_Log.v(0, C_TAG, "DownloadPack - file sixe is too big! namw=" + vFileName + " len=" + vFDLen);
					return false;
				}
				// получаем данные файла
				byte[] vFData = new byte[vFDLen]; 
				System.arraycopy(vPackData, vPos, vFData, 0, vFDLen);
				vPos = vPos + vFDLen;				
				C_FileHelper.SaveFile(new File(vDocsDir + vFileName), vFData, true);
				C_Log.v(2, C_TAG, "DownloadPack - save file " + vFileName + " len=" + vFDLen);
			}
			return true;
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "e:DownloadPack pUrl=" + pUrl + " err:" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Запрос данных с одного их кэширующих серверов
	 * Адреса серверов берутся из исписка адресов в БД, причем в порядке убывания даты последнего успешного обращения
	 * Перед отправкой формируется массив JSON исходящих даных, в соучае успешной отправки отправленные 
	 * данные из исходящей очереди удаляются 
	 * @param context
	 * @return Возвращает строку JSON ответа или "" = ошибка
	 */
	private static String RequestData(Context context) throws Exception{
		C_Log.v(3, C_TAG, "RequestData - start");
		// возвращает строку с новыми обновлениями
		C_DBHelper dbHelper = new C_DBHelper(context);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "RequestData getWritableDatabase is null! - end");
			return "";
		}	
//		CS_SecurityHelper vSH = new CS_SecurityHelper(context);
		try{
			String vResult = null;
			String vJLastID = dbHelper.GetVar(vDb, C_Vars.C_VAR_SYNC_ID, "0");
			long vLastOutId = dbHelper.GetLastId_T_Out(vDb);
			String vOutData = null;
			if (vLastOutId != -1) {
				vOutData = dbHelper.GetOutData(vDb, vLastOutId);
			}
			String vUrls = dbHelper.GetArrayOfUrl(vDb);
			String vUrlCode = null;
			String vUrl = null;
			String[] vAUrls = vUrls.split("::");
			int i = 0;
			Log.i( C_TAG, "RequestData - prestart"+String.valueOf(vAUrls.length));
			while ( (i < vAUrls.length) & (vResult == null) ) {
				try {
					Log.i( C_TAG, "RequestData - start");
					String vRec = vAUrls[i];
					int vPos = vRec.indexOf(",");
					vUrlCode = vRec.substring(0, vPos);
					vUrl = vRec.substring(vPos + 1);
//vJLastID = "13699";
//vUrl = "http://109.233.240.226/cs/sync.php&r=1id=78e9fe73a57d4960"; //!!!!	

					vUrl = vUrl + "?d=Android&app=Pointplus&r=" + vJLastID + "&id=" + C_Utils.GetDeviceID(context) + "&n=" + new Date().getTime();
//vUrl = "http://www.homeplus.kz/cs/sync.php?d=Android&r=36540&id=78e9fe73a57d4960&n=1329726894417"; //!!!!

					Log.i( C_TAG, "RequestData - try id=" + vUrlCode + " url=" + vUrl);
					C_Log.v(3, C_TAG, "RequestData - try id=" + vUrlCode + " url=" + vUrl);
					byte[] vData = DownloadFromUrl(vUrl, vOutData);
					if (vData != null) { 
						vResult = new String(vData, "UTF8");
						C_Log.v(2, C_TAG, "RequestData - ok, id=" + vUrlCode + " result len=" + vResult.length());
						if ( (vResult.length() > 0) & (vOutData != null) & (vLastOutId != -1) ) {
							dbHelper.Delete_t_out(vDb, vLastOutId);
						}
						if (! (vUrlCode.equals("-1")||vUrlCode.equals("-2")||vUrlCode.equals("-3") )) {
							// Если загрузка завершилась удачно, и если это не дефолтовый URL, 
							// то ставим этот URL первым в списке для использования в следующий	раз
							C_Log.v(2, C_TAG, "RequestData - set last valid proc id=" + vUrlCode);
							dbHelper.SetAccessDate_T_URLS(vDb, Integer.parseInt(vUrlCode));
						}
					}
				} catch (Exception e) {
					C_Log.v(0, C_TAG, "e:RequestData id=" + vUrlCode + " url=" + vUrl + " err:" + e.getMessage());
				}
				i++;
			}
			C_Log.v(2, C_TAG, "RequestData - end");
			return vResult;
		}finally{
			dbHelper.close();
		}
	}
	
	/**
	 * Обработка принятой записи типа NU - добавление или обновление данных уведомления (канала)
	 * @param dbHelper
	 * @param db
	 * @param pRec строка в формате 100002,N,start_date,stop_date,latitude,longitude,url,text_in_base64,pic_in_base64
	 * @throws Exception 
	 */
	static private void ProcessRec_NU (C_DBHelper dbHelper, SQLiteDatabase db, String pRec) throws Exception {
//		"100002","N","","","","","Q1NJbmZNi4yNw==","rates.html?r0=NB.kz&r1=UCH&r2=KZT&r3=USD",""
//		100002,N,start_date,stop_date,latitude,longitude,text_in_base64,url,pic_in_base64
		C_Log.v(3, C_TAG, "ProcessRec_NU - start");
		int vFPos = 0;
		int vFE = pRec.indexOf(",", vFPos + 1);
		int vFId = Integer.parseInt(pRec.substring(vFPos + 1, vFE - 1));
	
		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFType = pRec.substring(vFPos + 1, vFE - 1);

		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFStartDate = pRec.substring(vFPos + 1, vFE - 1);

		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFStopDate = pRec.substring(vFPos + 1, vFE - 1);

		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFLatitude = pRec.substring(vFPos + 1, vFE - 1);

		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFLongitude = pRec.substring(vFPos + 1, vFE - 1);

		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFText = pRec.substring(vFPos + 1, vFE - 1);
		vFText = new String(Base64.decode(vFText), "UTF8");

		vFPos = vFE + 1;
		vFE = pRec.indexOf(",", vFPos + 1);
		String vFUrl = pRec.substring(vFPos + 1, vFE - 1);

		vFPos = vFE + 1;
		vFE = pRec.length();
		String vFData = pRec.substring(vFPos + 1, vFE - 1);
		byte[] vFBlob = null;
		if (vFData.length() > 0) {
			vFBlob = Base64.decode(vFData);
		}
		
		C_Log.v(2, C_TAG, "ProcessData - update notif, vId=" + vFId + " vType=" + vFType + " vStartDate=" + vFStartDate + 
			" vStopDate=" + vFStopDate + " vLatitude=" + vFLatitude + " vLongitude=" + vFLongitude + " vText=" + vFText + 
			" vUrl=" + vFUrl);
		dbHelper.UpdateOrInsert_T_NOTIFS(db, vFId, vFType, vFStartDate, vFStopDate, vFLatitude, vFLongitude, vFText, vFUrl,	vFBlob);
	}
	
	/**
	 * Обработка принятой записи типа CD - удаление файла
	 * @param context
	 * @param pRec - имя файла
	 */
	private static void ProcessRec_CD (Context context, String pRec) {
		C_Log.v(3, C_TAG, "ProcessRec_CD - start");
		String vFileName = pRec.replace("/","..");
		if (context.deleteFile(vFileName) ) {
			C_Log.v(2, C_TAG, "ProcessData - delete file name=" + vFileName + " - ok");							
		} else {
			C_Log.v(0, C_TAG, "ProcessData - delete file name=" + vFileName + " - err");
		};
	}
	
	/**
	 * Обработка принятой записи типа CU - добавление или обновление файла
	 * @param context
	 * @param pCrypted - если T то файл зашифрован индивидуальным ключем, ему добавляется расширение ".crypted"
	 * @param pRec - строка в формате fileName,fileDataB64
	 * @throws Exception
	 */
	private static void ProcessRec_CU (Context context, String pCrypted, String pRec) throws Exception {
		C_Log.v(3, C_TAG, "ProcessRec_CU - start");
		
		if (pRec.length()==0) {
			C_Log.v(0, C_TAG, "ProcessRec_CU - empty record!");
			return;
		}
		
		int vP = pRec.indexOf(",");
		if (vP < 0) {
			C_Log.v(0, C_TAG, "ProcessRec_CU - no ',' in record!");
			return;
		}

		String vFileName = pRec.substring(0, vP).replace("/", "..");
		if (vFileName.length()==0) {
			C_Log.v(0, C_TAG, "ProcessRec_CU - empty FileName");
			return;
		}
		if (pCrypted.equals("T")) vFileName = vFileName + ".crypted";
		byte[] vBFileData = Base64.decode(pRec.substring(vP + 1));
	
		C_Log.v(2, C_TAG, "ProcessRec_CU - update file, name=" + vFileName );
		C_FileHelper.SaveFile(new File(context.getFilesDir() + "/" + vFileName), vBFileData, true);
	}
	
	/**
	 * Обработка принятой записи типа VD - удаление переменной
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec -  имя переменной
	 * @throws Exception
	 */
	static private void ProcessRec_VD (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		String vVarName = pRec;
		C_Log.v(2, C_TAG, "ProcessRec_VD name=" + vVarName);
		pDbHelper.DelVar(pDb, vVarName);
	}
	
	/**
	 * Обработка принятой записи типа VU - добавление или обновление переменной
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec - строка в формате VarName,VarValue
	 * @throws Exception
	 */
	static private void ProcessRec_VU (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		int vP = pRec.indexOf(",");
		String vVarName = pRec.substring(0, vP);
		String vVarValue = pRec.substring(vP + 1);		
		C_Log.v(2, C_TAG, "ProcessRec_VU name=" + vVarName + " val=" + vVarValue);
		pDbHelper.SetVar(pDb, vVarName, vVarValue);
	}

	/**
	 * Обработка принятой записи типа UD - удаление URL из списка адресов серверов
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec - ид сервера
	 * @throws Exception
	 */
	static private void ProcessRec_UD (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		int vUrlId = Integer.parseInt(pRec);
		C_Log.v(2, C_TAG, "ProcessRec_UD id=" + vUrlId);
		pDbHelper.Delete_T_URLS(pDb, vUrlId);
	}

	/**
	 * Обработка принятой записи типа UU - добавление или обновление адреса кэширующего сервера
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec строка в формате ID,URL
	 * @throws Exception
	 */
	static private void ProcessRec_UU (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		int vP = pRec.indexOf(",");
		int vUrlId = Integer.parseInt(pRec.substring(0, vP));
		String vUrl = pRec.substring(vP + 1);		
		C_Log.v(2, C_TAG, "ProcessRec_UU id=" + vUrlId + " url=" + vUrl);
		pDbHelper.UpdateOrInsert_T_URLS(pDb, vUrlId, vUrl);
	}
	
	/**
	 * Обработка принятой записи типа ND - удаление информации увдеомления
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec -  ид уведомления
	 * @throws Exception
	 */
	static private void ProcessRec_ND (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		int vNId = Integer.parseInt(pRec);
		C_Log.v(2, C_TAG, "ProcessRec_ND id=" + vNId);
		pDbHelper.Delete_T_NOTIFS(pDb, vNId);
	}

	/**
	 * Обработка принятой записи типа PR - добавление или обновление публичного клча сервера 
	 * (публичным ключом проверяются подписи принимаемых сообщений)
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec - публичный ключ в Base64
	 * @throws Exception
	 */
	static private void ProcessRec_PK (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		C_Log.v(2, C_TAG, "ProcessRec_PK");
		C_SecurityHelper.SaveNewSrvPublicKey(pDbHelper, pDb, pRec);
//		dbHelper.SetVar(db, CS_Vars.C_VAR_SRV_PUBLIC_KEY, pRec);
	}
	
	/**
	 * Обработка принятой записи типа LL - установка уровня журналирования
	 * С помощью этой возможности можно получить подробные журналы работы приложения с устройства  
	 * @param pDbHelper
	 * @param pDb
	 * @param pRec - уровень журналирования
	 * @throws Exception
	 */
	static private void ProcessRec_LL (C_DBHelper pDbHelper, SQLiteDatabase pDb, String pRec) throws Exception {
		int vLogLevel = Integer.parseInt(pRec);
		C_Log.v(2, C_TAG, "ProcessRec_LL vLogLevel=" + vLogLevel);
		C_Log.SetLogLevel(vLogLevel);
	}

	/**
	 * Обработка полученной строки JSON входящей очереди
	 * Обрабатывается каждая запись в отдельной транзакции БД, после обработки 
	 * устанавливается ид последней обработанной записи
	 * @param context
	 * @param pJSON - строка JSON
	 * @throws Exception
	 */
	private static void ProcessData (Context context, String pJSON) throws Exception {
		C_Log.v(3, C_TAG, "ProcessData - start");
//		String vTestJSON = "{\"S\":[{\"J\":[1,\"Ss\",\"T\",\"F\",\"vQ_SIGNsdf asdf sd asf\",\"\"]},
//		{\"J\":[221,\"TYPE2\",\"Q_ZIPPED2\",\"Q_CRYPTED2\",\"vQ_SIGN2\",\"vQ_DATA2\"]},
//		{\"J\":[3,\"TYPE3\",\"Q_ZIPPED3\",\"Q_CRYPTED3\",\"vQ_SIGN3\",\"vQ_DATA3\"]}]}";	
		if(pJSON == null){
			C_Log.v(1, C_TAG, "ProcessData: pJSON is empty! - end");
			return;
		}
		boolean vIsWidgetsUpdated = false;
		C_DBHelper dbHelper = new C_DBHelper(context);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "ProcessData getWritableDatabase is null! - end");
			return;
		}
		PublicKey vSrvPubKey = null;
		SecretKey vSecretKey = null;
		try{
			C_Log.v(3, C_TAG, "ProcessData: start parsing of JSON");
			int vPos = pJSON.indexOf("[") + 1;
			int vLen = pJSON.length();
			int vCount = 0;
			int vE = 0;
			do {
				vPos = pJSON.indexOf("[", vPos); 
				if (vPos == -1) break;
				
				vE = pJSON.indexOf(",", vPos); 
				String vRecID = pJSON.substring(vPos + 1, vE);
				
				vPos = vE + 1;
				vE = pJSON.indexOf(",", vPos + 1);			
				String vRecType = pJSON.substring(vPos + 1, vE - 1);
				
				vPos = vE + 1;
				vE = pJSON.indexOf(",", vPos + 1);			
				String vZipped = pJSON.substring(vPos + 1, vE - 1);

				vPos = vE + 1;
				vE = pJSON.indexOf(",", vPos + 1);			
				String vCrypted = pJSON.substring(vPos + 1, vE - 1);

				vPos = vE + 1;
				vE = pJSON.indexOf(",", vPos + 1);			
				String vSign = pJSON.substring(vPos + 1, vE - 1);

				vPos = vE + 1;
				vE = pJSON.indexOf("]", vPos + 1);			
				String vData = pJSON.substring(vPos + 1, vE - 1);
				
				byte[] vRecData = Base64.decode(vData);
				byte[] vRecSign = Base64.decode(vSign); 

				// проверка серверной подписи:
				C_Log.v(3, C_TAG, "ProcessData: verify sign of record id=" + vRecID + " type=" + vRecType);
				if (vSrvPubKey == null) {
					vSrvPubKey = C_SecurityHelper.GetSrvPublicKey(dbHelper, vDb);
				}
				if ( !C_SecurityHelper.asym_verify(vSrvPubKey, vRecData, vRecSign)) {
					throw new Exception("Sign is invalid! J=" + vRecID);
				}
				
				// это дешифрация общим ключем,
				// если vCrypted="T" то приватные данные в файле дополнительно зашифрованы DeviceSecretKey, 
				// расшифровка файлов производится только при их открытии, хранятся в зашифрованном виде 
				// признак шифрации файла приватных данных - в имени файла: filename.js.crypted
				if (vSecretKey == null) {
					vSecretKey = C_SecurityHelper.GetCommonSecretKey();
				}
				vRecData = C_SecurityHelper.sym_decrypt(vSecretKey, vRecData);
				if (vRecData.length == 0) {
					throw new Exception("Decrypt error! J=" + vRecID);
				}
				
				if (vZipped.equals("T")) {
					vRecData = C_FileHelper.decompress(vRecData);
				}
				
				String vRec = new String(vRecData, "UTF8");
				
				C_Log.v(2, C_TAG, "ProcessData: process record id=" + vRecID + " type=" + vRecType);
				vDb.beginTransaction();
				try{
					if (vRecType.equals("CD")) {
						ProcessRec_CD(context, vRec);
					} else if(vRecType.equals("CU")){
						ProcessRec_CU(context, vCrypted, vRec);					
					}else if (vRecType.equals("VD")){
						ProcessRec_VD(dbHelper, vDb, vRec);
					}else if(vRecType.equals("VU")){
						ProcessRec_VU(dbHelper, vDb, vRec);
					}else if (vRecType.equals("UD")){
						ProcessRec_UD(dbHelper, vDb, vRec);
					}else if (vRecType.equals("UU")){
						ProcessRec_UU(dbHelper, vDb, vRec);
					}else if (vRecType.equals("ND")){
						ProcessRec_ND(dbHelper, vDb, vRec);
					}else if (vRecType.equals("NU") | vRecType.equals("WU")){
						ProcessRec_NU(dbHelper, vDb, vRec);
						vIsWidgetsUpdated = true;					
					} else if(vRecType.equals("PK")){
						ProcessRec_PK(dbHelper, vDb, vRec);					
					} else if(vRecType.equals("LL")){
						ProcessRec_LL(dbHelper, vDb, vRec);					
					} else if(vRecType.equals("VACUUM")){
						dbHelper.Vacuum(vDb);					
					}
					C_Log.v(1, C_TAG, "ProcessData: set last rec, id=" + vRecID+ " type=" + vRecType);
					dbHelper.SetVar(vDb, C_Vars.C_VAR_SYNC_ID, vRecID);
					vDb.setTransactionSuccessful();
					vCount ++;
				}finally{
					vDb.endTransaction();
				}
				dbHelper.SetVar_AT(vDb, C_Vars.C_VAR_SYNC_RES, vCount + ", len=" + vLen);
				dbHelper.SetVar_AT(vDb, C_Vars.C_VAR_SYNC_DATE, C_Utils.IntToDate(0));
			} while (vPos <= vLen);

			if (vIsWidgetsUpdated) {
				C_Log.v(2, C_TAG, "ProcessData: update widgets, check notifs");
				C_WidgetProvider.UpdateAllWidgets(context);
				dbHelper.FireNotifs(vDb, context, 1, null);	
			}
		}finally{;
			dbHelper.close();
		}
		C_Log.v(2, C_TAG, "ProcessData - end");
	}

	public static void DownloadPackages (Context pContext) {
		C_DBHelper dbHelper = new C_DBHelper(pContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null) {
			C_Log.v(0, C_TAG, "DownloadPackages getWritableDatabase is null! - end");
			return;
		}	
		try{
			Properties vUrls = new Properties();
			dbHelper.GetArrayOfPackages(vDb, vUrls);
			String vUrlCode = null;
			String vUrl;
			@SuppressWarnings("rawtypes") // это объявление для удаления предупреждения
			Enumeration en = vUrls.keys();
			while (en.hasMoreElements()) {
				vUrlCode = (String) en.nextElement();
				int vPackId = Integer.parseInt(vUrlCode);
				vUrl = vUrls.getProperty(vUrlCode);
				try{
					if (DownloadPack(pContext, vUrl)) {
						dbHelper.SetStatus_T_PACK(vDb, vPackId, "P");
					}
				} catch (Exception e) {
					C_Log.v(0, C_TAG, "DownloadPackages id=" + vUrlCode + " url=" + vUrl + " err:" + e.getMessage());
				}
			}
			C_Log.v(2, C_TAG, "RequestData - end");
		}finally{
			dbHelper.close();
		}
	}
	
	/**
	 * Процедура синхронизации (отправка и прием данных с одного их кэширующих серверов)
	 * @param context
	 * @param pShowProgress - показывать ли ProgressBar
	 * @param pOnlyOnline - работа только в случае наличия активного интернет-соединения
	 */
	public static void SyncData (Context context, final Boolean pShowProgress, Boolean pOnlyOnline){
		C_Log.v(3, C_TAG, "SyncData - pShowProgress=" + pShowProgress + " pOnlyOnline=" + pOnlyOnline + " - start");	
		if (fIsWorking) {
			C_Log.v(1, C_TAG, "SyncData: is working! - end");
			return;
		}
		Boolean vAllowStartConnection = true;
		final Context vContext = context;
		
		if (pOnlyOnline){
			// проверка наличия коннекции
			vAllowStartConnection = C_Utils.isNetworkEnabled(vContext);
		}

		if (!vAllowStartConnection) {
			// нет коннекции - нет обновления
			C_Log.v(2, C_TAG, "SyncData: no connections - end");	
			return;
		}

		if (pShowProgress) {
		}
		C_Log.v(3, C_TAG, "SyncData - exec thread");	
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fIsWorking = true;
					 
					String s = RequestData(vContext);
					if (s != null) {
						ProcessData(vContext, s);
					}
					
					DownloadPackages(vContext);
					
					C_Log.v(3, C_TAG, "SyncData - end thread");	
				} catch (Exception e) {
					C_Log.v(0, C_TAG, "e:SyncData - err:" + e.getMessage());	
				} finally {
					if (pShowProgress) {
					}
					fIsWorking = false;
// при обращении к CS_Vars.fMainActivityInstance - ошибка при вызове из сервиса!					
				}
			}
		}).start();
		C_Log.v(3, C_TAG, "SyncData - end");	
	}
	
}
