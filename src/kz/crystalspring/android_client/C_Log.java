package kz.crystalspring.android_client;


import android.util.Log;

public class C_Log {
	static private int fCurrentLogLevel = C_Vars.C_LOG_LEVEL;
	static private String fLog = "";

	/**
	* Установка уровня журналирования, до перезагрузки приложения или следующей установки
	*/	
	public static void SetLogLevel(int pLogLevel) {
		fCurrentLogLevel = pLogLevel;
	}

	/**
	* Получение буфера с журналом. При получении производится очистка буфера.
	*/	
	public static String GetErrLog() {
		String vResult = fLog;
		fLog = "";
		return vResult;
	}
	
	/**
	* Журналирование строки данных. Если значение pLevel больше или арвно текущему уровню 
	* журналирования, то запись сохраняется в буфер журнала, и позже будет передана на сервер для анализа
	* Если буфер вырастет больше 100кБ, то сохранение новых данных не производится. 
	* @param pLevel - уровернь критичности (0 - errors, 1 - critical mess, 2 - mess, 3 - spam)
	* @param pType - тип
	* @param pText - текст
	*/
	public static void v (int pLevel, String pType, String pText) {
		if (fCurrentLogLevel >= pLevel) {
			String vMessage = pLevel + " " + pType + ":" + pText; // CS_Utils.IntToDate(0) + 
			Log.v("CS_INF", vMessage);			
			if (fLog.length() < C_Vars.C_LOG_MAX_LENGTH) {
				fLog = fLog + "||" + C_Utils.IntToDate(0) + "," + vMessage;
			}
		}
	}
	
	/**
	 * Выдача текущего списка логов (для тестирования)
	 * @return
	 */
	public static String GetLogs () {
		return fLog;
	}

}
