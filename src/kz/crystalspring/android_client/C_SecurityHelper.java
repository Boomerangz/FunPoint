package kz.crystalspring.android_client;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class C_SecurityHelper {
	static final String C_TAG = "CS_SecurityHelper";

	/**
	 * Получение ранее полученного индивидулаьного ключа шифрования из перменной класса
	 * хранится в глобальной переменной главного класса
	 * на время активности веб-сессии пользователя
	 */
	static private SecretKey GetDeviceSectetKey() {
    	try {
    		C_Log.v(3, C_TAG, "GetDeviceSectetKey");
    		return C_MainActivity.fDeviceSecretKey;
    	} catch (Exception e) {
    		C_Log.v(0, C_TAG, "GetDeviceSectetKey err:" + e.getMessage());
    	}
    	return null;
	}
    
	/**
	 * Установка в переменые класса нидивидуального секретного и приватного ключей 
	 * на время активности веб-сессии пользователя 
	 * @param pDeviceKey - индивидуальный секретный ключ
	 * @param pPrivateKey - приватный ключ
	 */
    static private void SetSessionKeys(SecretKey pDeviceKey, PrivateKey pPrivateKey) {
      	try {
    		C_Log.v(3, C_TAG, "SetSessionKeys");
      		C_MainActivity.fDeviceSecretKey = pDeviceKey;
      		C_MainActivity.fDevicePrivateKey = pPrivateKey;
      	} catch (Exception e) {
      		C_Log.v(0, C_TAG, "SetSessionKeys err:" + e.getMessage());
      	}
    }
    
    /**
     * Получение общего секретного ключа 
     * @return - ключ из константы C_COMMON_SECRET_KEY
     */
	public static SecretKey GetCommonSecretKey() {
		C_Log.v(3, C_TAG, "GetCommonSecretKey");
		SecretKey vResult = new SecretKeySpec(C_Vars.C_COMMON_SECRET_KEY, "AES");
		return vResult;
	}
	
	/**
	 * Поулучение секретного пользовательского ключа из пина (пароля) паользователя 
	 * @param pPassDigest - дайджест пароля пользователя
	 * @return секретный ключ
	 */
	static private SecretKey sym_getDeviceSecretKey(byte[] pPassDigest) {
		C_Log.v(3, C_TAG, "sym_getDeviceSecretKey");
    	SecretKey vResult = new SecretKeySpec(pPassDigest, "AES");
	    return vResult;
	}
	
	/**
	 * Проверка наличия установленного индивидуального секретного ключа
	 * @return результат
	 */
	static public boolean IsKeysExists() {		
		boolean vResult = (C_MainActivity.fDeviceSecretKey != null) & (C_MainActivity.fDevicePrivateKey != null);
		C_Log.v(3, C_TAG, "IsPinExists res=" + vResult);
		return vResult;
	}
	
	/*
	public static void test() {
		try {
			byte[] pub = Base64.decode("MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgExKLzdjg8qWjbeIR83GPvGADDubCnPVMeD4tzXPTi0H28dHBuvyyNdOxlDbrRfAe4YPJLiXL6usDmvivZYjlpFerXBfV49rLWD7zSRWCWn9R6jSOBaRK5TgzPs1UjzGDoiHDnDl5sJQTYMY5/RGJmsIYJ+K1JZ/A6rsh2VLLFJFAgMBAAE=");
			byte[] vData = Base64.decode("LrFLB25SqNEONGF4bIdUGCIg3FK2h+0yOg71qT4f6Xvudktvdk6L+w+DzFz9g9FK24kq1qP0VJdilo9AfXSDtoTZAI60N/97JEX/1reg5WKBNJisFaCZtZmoFTw9cS74c9v62vnCn8QKvWD4OF5FAC2JupbjG3erKqo1JSZB1hdvU5zN8PFd9M4seFJkrFGGZPX+rX3Iy2cKT0MqDzRosZt1KZjaBpxw2DD5bDiW/9tkp/8wRNAqcdhnepwIP2rR");
//			byte[] vSign = Base64.decode("NqQawIA6ofC9IwtX1DwjyJyIZnuwKKYIJft797tOyDu+1NCmdYkHtaCgxhc+QGXOzEieUwZDfSxEG59Z1508Wn+B1OOqQwdqVs9E6LF+BzS2gswjM7+cB7uHtrWm+Y7u7Q64XzSPECv9N295w8Q078wTwOqqXWYrlQFm1vBlPNU=");
			byte[] vSign = Base64.decode("K3HHmxB/Nph7RYaXudAmqI6GZixueN/JWGhjVTANaT/ttYx0uns7qC82AHhiZYN0riGVlyi4iZYI1U2b4nIpy/EhWyDlwhdMFXD+nThP31E6lpvwLY7AtsTuIVGhI12V4IqjJ/SAYZBcX/1mPr2O0VCqJquHXu/dqReMR6Qvzbc=");
			
			X509EncodedKeySpec publicSrvKeySpec = new X509EncodedKeySpec(pub);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey kPub = keyFactory.generatePublic(publicSrvKeySpec);
			
			if (!asym_verify(kPub, vData, vSign)) {
				C_Log.v(0, C_TAG, "IsPinExists res=err");
			}
			C_Log.v(0, C_TAG, "IsPinExists res=ok");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	*/
/*	
	public void test() { 
		try {
//			String pass = "1234";
			byte[] passwordKey = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16};
	     
			fSessionKey = new SecretKeySpec(passwordKey, "AES");
			fIV = new IvParameterSpec(C_IV_KEY);		
		
			String msg = " test 5678901 234 50 we test проверка на прочность 12";
		
			byte[] b_msg = msg.getBytes(); // Base64.decode(msg);	
			byte[] b_enc = sym_encrypt(b_msg);
			String b64_enc = Base64.encodeBytes(b_enc); // = WYZRqPLA5/bPkIKgH665gw==
			CS_Log.v(1,C_TAG, "enc = " +b64_enc);

			String de;
			b_enc = sym_decrypt(b_enc);
			de = new String(b_enc, "UTF8");
			CS_Log.v(1,C_TAG, "enc = " +de);
						
			String php_en = "44PH1wKdfrcVHX70TiLMtvvnsEhK8Do0qO90RHfc9o5OsAcyYWHDV7YuJKVDpZin";
			b_msg = Base64.decode(php_en);
			b_enc = sym_decrypt(b_msg);
			de = new String(b_enc, "UTF8");
			CS_Log.v(1,C_TAG, "enc = " +de);
			
			
			String pub2 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgbCxONoEOWQHkos2vid0MImLIQhk7xUkV8XrF1ozcgQDNpdy2VB02K8Rw5y0ZAvK1ZVMHTlEAaA/+tDiaeN1qaVwChRmWNz5x3Ofuzf0Kr0SRacopTWk8x7Yznrhxj7Y4AEmyDxvOIIoQMCo+k0BeKc4MZr5Ocon4OzwRSW89aQIDAQAB";
			String prv2 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOBsLE42gQ5ZAeSiza+J3QwiYshCGTvFSRXxesXWjNyBAM2l3LZUHTYrxHDnLRkC8rVlUwdOUQBoD/60OJp43WppXAKFGZY3PnHc5+7N/QqvRJFpyilNaTzHtjOeuHGPtjgASbIPG84gihAwKj6TQF4pzgxmvk5yifg7PBFJbz1pAgMBAAECgYBV0kgnKMS1negRbQjHRitYBpZMCqrnpoxKbewLV/7KHDMYcYMz3nrFSnt2yZD/bZcvMy5LLvHe++yR9I1o1SumXD9K/WN4yNlKUCxmdpSQ+rrGzzJKC9rKkNk65K3SbA9PGxsN/wNUvgoa3xh5+JQ7jmXlFoQ3TeliUXLNOiYj4QJBAP3pSCuBecx4AsVM31KGpQTMOylgEdJWPdCKMV5oAjAPEPmR4lurnskiSnXlHDjz5spVWsJnB5e37HHLrOVCH6UCQQDiRMpHJEubB0kEdMT3asw6dWEezARrMSpxXKIcmK6vc+IiYLB79YJfZ65tCIzhhLT2ABrUEiqJ1OIktLNxcPt1AkEAtDfD1b1Maeg0bz2ktLqEnecnhrJ9c8Fqln2/lLZV9y56aek1tMMXLMN9/jfj1DipcqsqhBkEtpcA57Qcv4wzUQJAFo8RM8KUnyI8z2rs8FX/gpyNUEq67gz61E6OAxfd4rRFCXRmZBV4KBboNwqxMRLQD+62vgoJn7BP06UIdvjyuQJBAOYug1swqKiRoYRSlv6UfGXfQ86aEqmJVu11/vR2hvy63OrO7Vf6Nt9N+LIv3rLzzk6/b17Q4nI0cP9gGxEMQ1c=";
			String mess2 = "MTIzNDU2Nzg5MDEyMzQ1NnFxcXFxcnJycnIxMjM0NTY3ODkwdGVzdGVlZXd3dzEyMzQ1Njc4OTB0ZXN0ZWVld3d3MTIzNDU2Nzg5MHRlc3RlZWV3d3cxMjM0NTY3ODkwdGVzdGVlZXd3dzEyMzQ1Njc4OTA=";
			String msig2 = "b6iJjI+fRTEA5wbxhxPAFAZSyqCJCjOmz9CYREzzp9L+4+FzT7eAua9/t9bTFETMo13Ccf080w/p+xZ+uQyhZfWgWKr04mqsCF1VNfGFU5ln6I8h/vPaJTQ5r2+ha2zh8cGD8NIbXePiLmW7OGQYdbFwmR2+PMmov/19zAFK71o=";
			String menc2 = "tF1uQB74Vh1TEbXX0XVe+N34ul8oO2O5U11L8AJWfhv0sxbGtUmh1xDjS3kwphORB7qNumuW5lDO43MNuIYYzFY556/Xxt+s4NH2iwxf+2f3NK9H1UTsKSbjtYmnIsKo8W67xthF24R8HxNK+EdTSAPRacoPuAGmIgXFwaIZKPw=";
					

			byte[] mess__ = Base64.decode(mess2);
			byte[] sign__ = Base64.decode(msig2);
			byte[] menc__ = Base64.decode(menc2);
			byte[] mdec__;		
			byte[] me;
		
			String sPublicKey = pub2;
			String sPrivateKey = prv2;		
			try {
				byte[] encodedPublicKey = Base64.decode(sPublicKey);
				byte[] encodedPrivateKey = Base64.decode(sPrivateKey);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
				fPublicKey = keyFactory.generatePublic(publicKeySpec);
				PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
				fPrivateKey = keyFactory.generatePrivate(privateKeySpec);

				byte[] encodedSrvPublicKey = Base64.decode(sPublicKey);
				X509EncodedKeySpec publicSrvKeySpec = new X509EncodedKeySpec(encodedSrvPublicKey);
				fSrvPublicKey = keyFactory.generatePublic(publicSrvKeySpec);
				
//				fSecretKey = new SecretKeySpec(passwordKey, C_CIPHER_ALGORITHM);
//		        fIvParameterSpec = new IvParameterSpec(C_RawSecretKey);
			} catch (Exception e) {
				CS_Log.v(0, C_TAG, "LoadKeys err:" + e.getMessage());
			}
			
		
			byte[] bs = asym_sign(mess__); 
			if (asym_verify(mess__, bs)) {
				me = asym_encrypt(mess__);
				mdec__ = asym_decrypt(me);
				CS_Log.v(2, C_TAG, "Test ok" + mdec__);
			}
		
			if (asym_verify(mess__, sign__)) {
				mdec__ = asym_decrypt(menc__);
				CS_Log.v(2, C_TAG, "Test ok" + mdec__);
			}
		
			if (bs.equals(sign__)){
				CS_Log.v(2, C_TAG, "Test - sign verify ok");
			}
					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
*/	


    
	/**
	 * получение дайджеста по паролю
	 * @param pPassword - пароль
	 * @return - дайджест SHA2-512 (64 байта)
	 */
	static byte[] getDigestSHA512(String pPassword) {
		C_Log.v(3, C_TAG, "getDigestSHA512 - start");
	    byte[] passwordMessageDigest = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-512"); // 64 bytes
		    passwordMessageDigest = messageDigest.digest(pPassword.getBytes());		    
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "getDigestSHA512 err:" + e.getMessage());
		}
		return passwordMessageDigest;
	}

	/**
	 * Получение дайджеста по паролю пользователя с солью
	 * Отдельная процедура, специально для вычислеия и сравнения дафджество пароля пользовател
	 * т к дайджест пароля используется для проверки пароля и лежит в открытом виде в базе! 
	 * @param pPassword - пароль (пин)
	 * @return - дайджест пароля SHA2-512 (64 байта)
	 */
	static byte[] getDigestOfPasswordForComparing(String pPassword) {
		C_Log.v(3, C_TAG, "getDigestOfPasswordForComparing");
		String vS = "2ad78rg" + pPassword + "asdlfk;#$i7v2";
		return getDigestSHA512(vS);
	}
	
	
	
	/**
	 * Создание секретного пользовательского ключа из дайджеста пароля (пина)
	 * используется для шифрации индивидуального ключа и приватного ключа
	 * @param pPassDigest - дайджест пароля (пина)
	 * @return - секретный ключ
	 */
	static private SecretKey sym_createUsersSecretKey(byte[] pPassDigest) {
		C_Log.v(3, C_TAG, "sym_createUsersSecretKey");
	    SecretKey vSecretKey = null;
	    try{
	    	SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	    	secureRandom.setSeed(pPassDigest);
	    	KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    	keyGenerator.init(256, secureRandom);
	    	vSecretKey = keyGenerator.generateKey();
	    } catch (Exception e) {
	    	C_Log.v(0, C_TAG, "sym_createUsersSecretKey err:" + e.getMessage());
	    }
	    return vSecretKey;
	}

	/**
	 * Создание секретного пользовательского ключа из строкового пароля (пина)
	 * используется для шифрации индивидуального ключа и приватного ключа
	 * @param pPassword - пароль (пин)
	 * @return - секретный ключ
	 */
	static private SecretKey sym_createUsersSecretKey(String pPassword) {
	    C_Log.v(3, C_TAG, "sym_createUsersSecretKey (pass)");
	    byte[] vPassDigest = getDigestSHA512(pPassword);
	    return sym_createUsersSecretKey(vPassDigest);
	}

	/**
	 * Шифрация данных секретным ключем (AES256)
	 * @param pSecretKey - секретный ключ
	 * @param pData - байтовый массив входящих данных
	 * @return - зашифрованные данные
	 */
	static public byte[] sym_encrypt (SecretKey pSecretKey, byte[] pData) {
		C_Log.v(3, C_TAG, "sym_encrypt - start");		
		byte[] vData = null;
		try {
			Cipher vCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");	
			IvParameterSpec vIV = new IvParameterSpec(C_Vars.C_IV_KEY);
			vCipher.init(Cipher.ENCRYPT_MODE, pSecretKey, vIV);
		    vData = vCipher.doFinal(pData);
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "sym_encrypt err:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "sym_encrypt - end");
	    return vData;
	}

	/**
	 * Шифрация данных секретным ключем (AES256), полученным из пароля pPassword
	 * @param pPassword - пароль (пин)
	 * @param pData - байтовый массив входящих данных
	 * @return - зашифрованные данные
	 */
	static public byte[] sym_encrypt (String pPassword, byte[] pData) {
		C_Log.v(3, C_TAG, "sym_encrypt (pass)");
    	SecretKey vSecretKey = sym_createUsersSecretKey(pPassword);
    	return sym_encrypt(vSecretKey, pData);
	}
	
	/**
	 * Расшифровка данных секретным ключем (AES256)
	 * @param pSecretKey - секретный ключ
	 * @param pData - байтовый массив входящих данных
	 * @return - расшифрованные данные
	 */
	static public byte[] sym_decrypt (SecretKey pSecretKey, byte[] pData){
		C_Log.v(3, C_TAG, "sym_decrypt - start");
		byte[] vData = null;
		try {
			Cipher vCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec vIV = new IvParameterSpec(C_Vars.C_IV_KEY);
			vCipher.init(Cipher.DECRYPT_MODE, pSecretKey, vIV);
	    	vData = vCipher.doFinal(pData);
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "sym_decrypt err:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "sym_decrypt - end");
	    return vData;
	}
	
	/**
	 * Расшифровка данных секретным ключем (AES256), полученным из пароля pPassword
	 * @param pPassword - пароль (пин)
	 * @param pData - байтовый массив входящих данных
	 * @return - расшифрованные данные
	 */
	static public byte[] sym_decrypt (String pPassword, byte[] pData) {
		C_Log.v(3, C_TAG, "sym_decrypt (pass)");
		SecretKey vSecretKey = sym_createUsersSecretKey(pPassword);
    	return sym_decrypt(vSecretKey, pData);
	}
	
	/**
	 * Шифрация данных секретным индивидуальный ключем (AES256)
	 * ключ берется из переменной класса с помощью GetDeviceSectetKey
	 * Использовать только во время активности веб-сессии пользователя!
	 * @param pData - байтовый массив входящих данных
	 * @return - зашифрованные данные
	 */
	static public byte[] sym_device_encrypt (byte[] pData) {
		C_Log.v(3, C_TAG, "sym_device_encrypt");
		return sym_encrypt(GetDeviceSectetKey(), pData);
	}
	
	/**
	 * Дешифрация данных секретным индивидуальный ключем (AES256)
	 * ключ берется из переменной класса с помощью GetDeviceSectetKey
	 * Использовать только во время активности веб-сессии пользователя!
	 * @param pData - байтовый массив входящих данных
	 * @return - душифрованные данные
	 */
	static public byte[] sym_device_decrypt (byte[] pData) {
		C_Log.v(3, C_TAG, "sym_device_decrypt");
		return sym_decrypt(GetDeviceSectetKey(), pData);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Генерация пары ключей при регистрации пользователя или при сбросе пароля после утери  
	 * Также производится регистрация публичного и индивидуального секретного ключей на сервере 
	 * Использовать только во время активности веб-сессии пользователя!
	 * Должен быть доступен deviceSecretKey для шифровани приватного ключа
	 * @param pDbHelper
	 * @param pDb
	 * @param pPin - пин пользователя
	 * @return - результат
	 */
	static private boolean CreateNewAsymKeys(C_DBHelper pDbHelper, SQLiteDatabase pDb, String pPin) {
		try {
			C_Log.v(2, C_TAG, "CreateNewAsymKeys - start");	
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024, new SecureRandom());
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey vPublicKey = keyPair.getPublic();
			PrivateKey vPrivateKey = keyPair.getPrivate();
			C_Log.v(2, C_TAG, "CreateNewAsymKeys - gen ok");
	
			X509EncodedKeySpec v509EncodedKeySpec = new X509EncodedKeySpec(vPublicKey.getEncoded());
			byte[] vBPublicKey = v509EncodedKeySpec.getEncoded();
			String sPublicKey = Base64.encodeBytes(vBPublicKey);
			
			PKCS8EncodedKeySpec vPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(vPrivateKey.getEncoded());
			// шифрование приватного ключа 
			byte[] vBPKCS8EncodedKeySpec = sym_encrypt(pPin, vPKCS8EncodedKeySpec.getEncoded()); 
			String sPrivateKey = Base64.encodeBytes(vBPKCS8EncodedKeySpec);			
			pDbHelper.SetVar(pDb, C_Vars.C_VAR_PRIVATE_KEY, sPrivateKey);
			pDbHelper.SetVar(pDb, C_Vars.C_VAR_PUBLIC_KEY, sPublicKey);
			C_Log.v(2, C_TAG, "CreateNewAsymKeys - end");	
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "CreateNewAsymKeys err:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Создание индивидуального секретного ключа AES256
	 * При регистрации или перерегистрации пользвоателя
	 * @return байтовый массив 32 байба
	 */
	static private byte[] CreateNewDeviceBKey() {
		C_Log.v(2, C_TAG, "CreateNewDeviceBKey");	
		Random ranGen = new SecureRandom();
		byte[] vB = new byte[32]; //!128 = 16
		ranGen.nextBytes(vB);
		return vB;
	}
	
	/**
	 * Получение индивидульаного секретного ключа из БД
	 * Использовать только во время активности веб-сессии пользователя!
	 * @param pContext
	 * @return секретный ключ
	 * @throws Exception
	 */
//	public static SecretKey DB_GetDiveceSecretKey1(Context pContext) throws Exception {
//		C_Log.v(3, C_TAG, "DB_GetDiveceSecretKey - start");
//		SecretKey vResult = null;
//		C_DBHelper dbHelper = new C_DBHelper(pContext);
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {			
//			String vSKey = dbHelper.GetVar(db, C_Vars.C_VAR_DEVICE_KEY, null);
//			if (vSKey != null) {
//				byte[] vBKey = Base64.decode(vSKey);
//				vResult = new SecretKeySpec(vBKey, "AES");
//			}
//		} finally {
//			dbHelper.close();
//		}
//		C_Log.v(3, C_TAG, "DB_GetDiveceSecretKey - end");
//		return vResult;
//	}

	/**
	 * Получение приватного ключа из БД
	 * Использовать только во время активности веб-сессии пользователя!
	 * @param pDbHelper
	 * @param pDb
	 * @param pPin - пин (пароль)
	 * @return секретный ключ
	 */
	static public PrivateKey GetDevicePrivateKey(C_DBHelper pDbHelper, SQLiteDatabase pDb, String pPin) throws Exception {
		C_Log.v(3, C_TAG, "GetDevicePrivateKey - set");
		PrivateKey vResult = null;
		String sPrivateKey = pDbHelper.GetVar(pDb, C_Vars.C_VAR_PRIVATE_KEY, null);
		byte[] vBPrivateKey = Base64.decode(sPrivateKey);
		vBPrivateKey = sym_decrypt(pPin, vBPrivateKey);
		PKCS8EncodedKeySpec vPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(vBPrivateKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		vResult = keyFactory.generatePrivate(vPKCS8EncodedKeySpec);
		C_Log.v(3, C_TAG, "GetDevicePrivateKey - end");
		return vResult;
	}
	
	/**
	 * Получение индивидуального публичного ключа из БД
	 * @param pContext
	 * @return 
	 * @throws Exception
	 */
	static public PublicKey GetDevicePublicKey(Context pContext) throws Exception {
		C_Log.v(3, C_TAG, "GetDevicePublicKey - set");
		PublicKey vResult = null;
		C_DBHelper dbHelper = new C_DBHelper(pContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try{
			String sSrvPublicKey = dbHelper.GetVar(db, C_Vars.C_VAR_PUBLIC_KEY, null);
			if (sSrvPublicKey == null) throw new Exception("GetDevicePublicKey: public key is not found!");
			byte[] vBSrvPublicKey = Base64.decode(sSrvPublicKey);
			X509EncodedKeySpec vX509EncodedSrvKeySpec = new X509EncodedKeySpec(vBSrvPublicKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			vResult = keyFactory.generatePublic(vX509EncodedSrvKeySpec);
		}finally{
			dbHelper.close();
		}
		C_Log.v(3, C_TAG, "GetDevicePublicKey - end");
		return vResult;
	}	
	
	/**
	 * Получение серверного публичного ключа из БД или из константы C_VAR_SRV_PUBLIC_KEY
	 * @param pDbHelper
	 * @param pDb
	 * @return
	 * @throws Exception
	 */
	static public PublicKey GetSrvPublicKey(C_DBHelper pDbHelper, SQLiteDatabase pDb) throws Exception {
		C_Log.v(3, C_TAG, "GetSrvPublicKey - start");
		PublicKey vResult = null;
		String sSrvPublicKey = pDbHelper.GetVar(pDb, C_Vars.C_VAR_SRV_PUBLIC_KEY, null);
		if (sSrvPublicKey == null) {
			sSrvPublicKey = C_Vars.C_SRV_PUBLIC_KEY;
		}
		byte[] vBSrvPublicKey = Base64.decode(sSrvPublicKey);
		X509EncodedKeySpec vX509EncodedSrvKeySpec = new X509EncodedKeySpec(vBSrvPublicKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		vResult = keyFactory.generatePublic(vX509EncodedSrvKeySpec);
		C_Log.v(3, C_TAG, "GetSrvPublicKey - end");
		return vResult;
	}

	/**
	 * Получение серверного публичного ключа из БД или из константы C_VAR_SRV_PUBLIC_KEY
	 * @param pDbHelper
	 * @param pDb
	 * @return
	 * @throws Exception
	 */
	static public PublicKey GetSrvPublicKey(Context pContext) throws Exception {
		C_Log.v(3, C_TAG, "GetSrvPublicKey (db) - start");
		PublicKey vResult = null;
		C_DBHelper dbHelper = new C_DBHelper(pContext);
		SQLiteDatabase vDb = dbHelper.getReadableDatabase();
		try{
			vResult = GetSrvPublicKey(dbHelper, vDb);
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "SetSessionPin (db) err:" + e.getMessage());
		} finally {
			dbHelper.close();
		}		
		C_Log.v(3, C_TAG, "GetSrvPublicKey (db) - end");
		return vResult;
	}
	
	/**
	 * Сохранение серверного публичного ключа в БД
	 * @param pDbHelper
	 * @param pDb
	 * @param pSrvPubKey
	 * @throws Exception
	 */
	static public void SaveNewSrvPublicKey(C_DBHelper pDbHelper, SQLiteDatabase pDb, String pSrvPubKey) throws Exception {
		C_Log.v(1, C_TAG, "SaveNewSrvPublicKey");
		pDbHelper.SetVar(pDb, C_Vars.C_VAR_SRV_PUBLIC_KEY, pSrvPubKey);
	}
	
	/**
	 * Проверка введенного пина (пароля0 по хэшу и в случае успеха расшифровка 
	 * и сохранение в переменых класса индивидульаного ключа deviceSecretKey и приватного ключа
	 * @param pContext
	 * @param pPin - пин (пароль) пользователя
	 * @return - результат проверки пароля по ранее созраненному дайджесту
	 */
	static public boolean SetSessionPin (Context pContext, String pPin) {		
		C_Log.v(3, C_TAG, "SetSessionPin - start");
		boolean vResult = false;
		C_DBHelper dbHelper = new C_DBHelper(pContext);
		SQLiteDatabase vDb = dbHelper.getReadableDatabase();
		try{
			
//String vSDeviceKey1 = dbHelper.GetVar(db, CS_Vars.C_VAR_DEVICE_KEY, null);
//byte[] vBDeviceKey1 = sym_decrypt(pPin, Base64.decode(vSDeviceKey1));				
//PublicKey vPublicKey = GetSrvPublicKey(pContext);
//byte[] pEncryptedDeviceKey = asym_encrypt(vPublicKey, vBDeviceKey1);
//dbHelper.AddOutDataRec(db, CS_Vars.C_REGISTRATION_DEVICE_KEY, "T", "", pEncryptedDeviceKey);
			
//dbHelper.DelVar_AT(db, CS_Vars.C_VAR_USER_DIGEST_KEY);			
			// извлечение из базы созраненного дайджеста
			String vSUserDigest = dbHelper.GetVar(vDb, C_Vars.C_VAR_USER_DIGEST_KEY, null);
			if (vSUserDigest != null) {
				// получение дайджеста для текущего пароля
				byte[] vUserDigest = Base64.decode(vSUserDigest);
				
				byte[] vPinDigest = getDigestOfPasswordForComparing(pPin);
				if (Arrays.equals(vPinDigest, vUserDigest)) {
					// 	получение, расшифровка и установка ключа шифрования локальных данных
					String vDeviceKey = dbHelper.GetVar(vDb, C_Vars.C_VAR_DEVICE_KEY, null);
					byte[] vBDeviceKey = Base64.decode(vDeviceKey);						
					vBDeviceKey = sym_decrypt(pPin, vBDeviceKey);
					SetSessionKeys(sym_getDeviceSecretKey(vBDeviceKey), GetDevicePrivateKey(dbHelper, vDb, pPin));
					vResult = true;
				}
			} else {
				C_Log.v(0, C_TAG, "SetSessionPin err: digest not found");
			}
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "SetSessionPin err:" + e.getMessage());
		} finally {
			dbHelper.close();
		}
   		C_Log.v(2, C_TAG, "SetSessionPin - res=" + vResult + " - end");
		return vResult;
	}
	
	/**
	 * Регистрация пароля пользователя, а также геренация кючей и регистрация их на сервере 
	 * Если ранее уже был сохранен дайджест пина, то необзодимо значение старого пина pOldPin дл проверки
	 * В этом случае (после проверки) производится перешифровка и сохранение нвого дайджеста, сиссионного и приватного ключа в БД
	 * @param pContext
	 * @param pOldPin - старый пароль (если не указан, значит первоначальный ввод)
	 * @param pNewPin - новый пароль
	 * @return - результат (проверки старого пина или регистрации)
	 * @throws Exception
	 */
	static public boolean SaveNewPin (Context pContext, String pOldPin, String pNewPin) throws Exception {
		C_Log.v(2, C_TAG, "SaveNewPin - start");
		boolean vResult = false;
		if (pNewPin.length() < 4) {
			throw new Exception("SaveNewPin err: length of new pin < 4");
		}

		C_DBHelper dbHelper = new C_DBHelper(pContext);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();

		if (vDb == null) {
			C_Log.v(0, C_TAG, "SaveNewPin getWritableDatabase is null! - end");
			return false;
		}			
		
		try {
//dbHelper.DelVar_AT(db, CS_Vars.C_VAR_USER_DIGEST_KEY);			
			// извлечение из базы дайджеста старого пароля
			vDb.beginTransaction();
			String vSOldDBDigest = dbHelper.GetVar(vDb, C_Vars.C_VAR_USER_DIGEST_KEY, null);
//vSOldDBDigest = null; // debug			
			if (vSOldDBDigest != null){
				// изменение существующего пароля
			
				// проверка ввода старого пароля: 
				byte[] vBOldDBDigest = Base64.decode(vSOldDBDigest);
				byte[] vBOldPSDigest = getDigestOfPasswordForComparing(pOldPin);				
				if (!Arrays.equals(vBOldDBDigest, vBOldPSDigest)) {
					throw new Exception("SaveNewPin err: old pin is invalid");
				}
				
				// перешифрование индивидуального и приватного ключей
				String vSDeviceKey = dbHelper.GetVar(vDb, C_Vars.C_VAR_DEVICE_KEY, null);
				String vSPrivatelKey = dbHelper.GetVar(vDb, C_Vars.C_VAR_PRIVATE_KEY, null);
				byte[] vBDeviceKey = sym_decrypt(pOldPin, Base64.decode(vSDeviceKey));
				byte[] vBPrivateKey = sym_decrypt(pOldPin, Base64.decode(vSPrivatelKey));
				dbHelper.SetVar(vDb, C_Vars.C_VAR_DEVICE_KEY, Base64.encodeBytes(sym_encrypt(pNewPin, vBDeviceKey)));
				dbHelper.SetVar(vDb, C_Vars.C_VAR_PRIVATE_KEY, Base64.encodeBytes(sym_encrypt(pNewPin, vBPrivateKey)));

				// сохранение в базу нового даджеста:
				byte[] vBNewDigest = getDigestOfPasswordForComparing(pNewPin);
				String vSNewDigest = Base64.encodeBytes(vBNewDigest);
				dbHelper.SetVar(vDb, C_Vars.C_VAR_USER_DIGEST_KEY, vSNewDigest);
//				регистрация на сервере нового дайджеста: ???
				byte[] vEncryptedDigest = asym_encrypt(GetSrvPublicKey(dbHelper, vDb), vBNewDigest);
				dbHelper.AddOutDataRec(vDb, C_Vars.C_REGISTRATION_DIGEST, vEncryptedDigest); // регистрация дайджеста клиентского пароля на сервере				
				vResult = true;
			} else {
				// первоначальный ввод пароля
				
				// создание дайджеста нового пароля:
				byte[] vBNewDigest = getDigestOfPasswordForComparing(pNewPin);
				String vSNewDigest = Base64.encodeBytes(vBNewDigest);
				PublicKey vSrvPublicKey = GetSrvPublicKey(dbHelper, vDb);

				//  создание и сохранение в базе пары асимметричных ключей (приватный ключ зашифрован с паролем pNewPin)
				CreateNewAsymKeys(dbHelper, vDb, pNewPin);
				
				//  создание нового ключа шифрования локальных данных:
				byte[] vNewDeviceBKey = CreateNewDeviceBKey();
				String vSNewDeviceKey = Base64.encodeBytes(sym_encrypt(pNewPin, vNewDeviceBKey));
				dbHelper.SetVar(vDb, C_Vars.C_VAR_DEVICE_KEY, vSNewDeviceKey);
				
				// отправка индивидуального ключа на сервер в зашифрованном серверным ключем виде:
				byte[] pEncryptedDeviceKey = asym_encrypt(vSrvPublicKey, vNewDeviceBKey);
				dbHelper.AddOutDataRec(vDb, C_Vars.C_REGISTRATION_DEVICE_KEY, pEncryptedDeviceKey);

				// отправка индивидуального секретного ключа на сервер:
				String vDevicePublicKey = dbHelper.GetVar(vDb, C_Vars.C_VAR_PUBLIC_KEY, null);
				dbHelper.AddOutDataRec(vDb, C_Vars.C_REGISTRATION_PUB_KEY, Base64.decode(vDevicePublicKey)); // регистрация публичного клиентского ключа на сервере

				// сохранение в базу дайджеста нового пароля:
				dbHelper.SetVar(vDb, C_Vars.C_VAR_USER_DIGEST_KEY, vSNewDigest);
//				регистрация на сервере нового дайджеста: ???
				byte[] vEncryptedDigest = asym_encrypt(vSrvPublicKey, vBNewDigest);
				dbHelper.AddOutDataRec(vDb, C_Vars.C_REGISTRATION_DIGEST, vEncryptedDigest);
				
				// установка в сессионной переменной индивидуального ключа:
				SetSessionKeys(sym_getDeviceSecretKey(vNewDeviceBKey), GetDevicePrivateKey(dbHelper, vDb, pNewPin));

				vResult = true;
			}
			C_Log.v(2, C_TAG, "SaveNewPin");
			vDb.setTransactionSuccessful();
		}finally{
			vDb.endTransaction();
			vDb.close();
			dbHelper.close();
		}
		C_Log.v(2, C_TAG, "SaveNewPin - res=" + vResult + " - end");
		return vResult;
	}
	

	
	/**
	 * Подпись сообщения клиентским приватным ключем
	 * @param pPrivateKey
	 * @param pMessage - сообщение
	 * @return - подпись
	 * @throws Exception
	 */
	static public byte[] asym_sign(PrivateKey pPrivateKey, byte[] pMessage) throws Exception{
		C_Log.v(3, C_TAG, "asym_sign - start");
		if (pMessage == null) {
			throw new Exception("asym_sign err: pMessage is null!");
		}
		byte[] vRes = null;
		if(pPrivateKey == null) throw new Exception("Private key is null!");
		Signature vSignature = Signature.getInstance("SHA1withRSA");
		vSignature.initSign(pPrivateKey);			
		vSignature.update(pMessage);
		vRes = vSignature.sign();
		C_Log.v(3, C_TAG, "asym_sign - res len=" + vRes.length + " - end");
		return vRes;
	}

	/**
	 * проверка подписи публичным ключем
	 * @param pPublicKey 
	 * @param pMessage - сообщение
	 * @param pSignature - подпись
	 * @return - результат
	 * @throws Exception
	 */
	static public boolean asym_verify(PublicKey pPublicKey, byte[] pMessage, byte[] pSignature) throws Exception{
		C_Log.v(3, C_TAG, "asym_verify - start");
		if (pMessage == null) {
			throw new Exception("asym_verify err: pMessage is null!");
		}
		if (pSignature == null) {
			throw new Exception("asym_verify err: pSignature is null!");
		}
		if (pPublicKey == null) {
			throw new Exception("asym_verify err: Srv public key is null!");
		}
		boolean vRes = false;
		Signature vSignature = Signature.getInstance("SHA1withRSA");
		vSignature.initVerify(pPublicKey);
		vSignature.update(pMessage);
		vRes = vSignature.verify(pSignature);
		C_Log.v(2, C_TAG, "asym_verify - res=" + vRes + " - end");
		return vRes;
	}

	
	/**
	 * шифрование публичным ключем
	 * @param pPublicKey
	 * @param pData - данные (до 140 байт!)
	 * @return - зашифрованные данные
	 * @throws Exception
	 */
	static public byte[] asym_encrypt(PublicKey pPublicKey, byte[] pData) throws Exception {
		C_Log.v(3, C_TAG, "asym_encrypt - start");
		byte[] vData = null;
		if (pData == null) {
			throw new Exception("asym_encrypt: pData is null!");
		}
		if(pPublicKey == null) {
			throw new Exception("asym_encrypt: Public key is null!");
		}
		Cipher cipher = Cipher.getInstance("RSA"); //"RSA" RSA/ECB/PKCS1Padding
		cipher.init(Cipher.ENCRYPT_MODE, pPublicKey);
		vData = cipher.doFinal(pData);
		C_Log.v(3, C_TAG, "asym_encrypt - end");
		return vData;
    }
	
	/**
	 * расшифровка приватным (клиентским) ключем
	 * @param pPrivateKey
	 * @param pData - данные
	 * @return - расшифрованные данные
	 * @throws Exception
	 */
	static public byte[] asym_decrypt(PrivateKey pPrivateKey, byte[] pData) throws Exception {
		C_Log.v(2, C_TAG, "asym_decrypt - start");
		byte[] vData = null;
		if (pData == null) {
			throw new Exception("asym_decrypt: pData is null!");
		}
		if(pPrivateKey == null) {
			throw new Exception("asym_decrypt err: Private key is null!");
		}
		Cipher cipher = Cipher.getInstance("RSA"); //"RSA"  RSA/ECB/PKCS1Padding
		cipher.init(Cipher.DECRYPT_MODE, pPrivateKey);
		vData = cipher.doFinal(pData);
		C_Log.v(3, C_TAG, "asym_decrypt - end");
		return vData;
	}
	
	
}
