package kz.crystalspring.android_client;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

public class C_Utils {
	
	/**
	 * полчение даты в строковом форате yyyy-MM-dd HH:mm:ss по числу
	 * Если указано pTime = 0 то выдается текущая дата
	 * @param pTime - дата в формате "микросекунды от 1970"
	 * @return
	 */
    public static String IntToDate(long pTime){
    	Date vD = new Date();
    	if (pTime != 0) vD.setTime(pTime);   	
    	SimpleDateFormat vDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return vDF.format(vD);
//    	return (String) DateFormat.format("yyyy-MM-dd hh:mm:ss", vTime);
    }

    /**
     * проверка доступности сети
     * @param pContext
     * @return
     */
    public static boolean isNetworkEnabled (Context pContext) {
    	boolean vResult = false;
    	ConnectivityManager cm = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo vNetworkInfo = cm.getActiveNetworkInfo();
    	if (vNetworkInfo != null) {
    		vResult = vNetworkInfo.isConnectedOrConnecting();
    	}
    	return vResult;
    }

//    private static String byte2Hex(byte[] bytes) {
//        StringBuilder string = new StringBuilder();
//        for (byte b: bytes) {
//                String hexString = Integer.toHexString(0x00FF & b);
//                string.append(hexString.length() == 1 ? "0" + hexString : hexString);
//        }
//        return string.toString();
//    }
    
    /**
	 * получение уникального DeviceID = IMEI+":"+Android_ID 
	 * @param context - контекст приложения
	 * @return возвращает сроку - ид устройства
	 */
	public static String GetDeviceID(Context context) {
//		String srvcName = Context.TELEPHONY_SERVICE;
//		TelephonyManager operator = (TelephonyManager)context.getSystemService(srvcName);
//		String vResult = operator.getDeviceId();
//		vResult = vResult+":"+Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		String vResult = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
// return "78e9fe73a57d4960"; //!!!!
		return vResult;
	}
	
	/**
	 * Поулчение инормации об устройстве
	 * @return Строка JSON с параметрами устройства
	 */
    public static String GetDeviceInfo() {
    	String vResult = "{\"BOARD\":\"" + Build.BOARD +
		"\",\"BRAND\":\"" + Build.BRAND +
		"\",\"DEVICE\":\"" + Build.DEVICE +
		"\",\"DISPLAY\":\"" + Build.DISPLAY +
		"\",\"ID\":\"" + Build.ID +
		"\",\"FINGERPRINT\":\"" + Build.FINGERPRINT +
		"\",\"HOST\":\"" + Build.HOST +
		"\",\"TAGS\":\"" + Build.TAGS +
		"\",\"TYPE\":\"" + Build.TYPE +
		"\",\"MODEL\":\"" + Build.MODEL +
		"\",\"PRODUCT\":\"" + Build.PRODUCT +
		"\",\"USER\":\"" + Build.USER +
		"\",\"V_SDK\":\"" + Build.VERSION.SDK + 
		"\",\"V_RELEASE\":\"" + Build.VERSION.RELEASE + "\"}";
    	return vResult;
    }

    // преобразование градусов в радианы
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	// преобразование радиан в градусы
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
	
	// вычисление расстояния между двумя точками, заданных генграфическими координатами, в метрах
	public static double GetDistance (double pLatitude1, double pLongitude1, double pLatitude2, double pLongitude2) {
		double vDist = Math.sin(deg2rad(pLatitude1)) * Math.sin(deg2rad(pLatitude2)) + Math.cos(deg2rad(pLatitude1)) * Math.cos(deg2rad(pLatitude2)) * Math.cos(deg2rad(pLongitude1 - pLongitude2));
		vDist = Math.acos(vDist);
		vDist = rad2deg(vDist);
		vDist = vDist * 111189.57696;// * 60 * 1.1515 * 1.609344 * 1000;
		return vDist;
	}
	
	/**
	 * Перевод sygned байта в unsigned число 
	 * @param pByte
	 * @return
	 */
	public static int Byte2UnsignedInt(byte pByte) {
	    return pByte & 0xFF;
	}
	
	/**
	 * Извлечение unsigned int из  байтового массива
	 * Число записано в массив в формате 32 bit, big endian byte order
	 * @param vData - байтовый массив
	 * @return
	 */
	public static int Bytes2long(byte[] vData) {
		return Byte2UnsignedInt(vData[3]) + Byte2UnsignedInt(vData[2]) * 0x100 + Byte2UnsignedInt(vData[1]) * 0x1000 + Byte2UnsignedInt(vData[0]) * 0x10000;
	}
	
}
