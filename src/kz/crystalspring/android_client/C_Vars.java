package kz.crystalspring.android_client;

public class C_Vars {
	public static final String C_VERSION = "A_0.9";
	// DB:
	
	public static final boolean C_SHOW_PROGRESS_BAR_IN_MAIN_WIN = false;
	public static final int C_LOG_MAX_LENGTH = 100 * 1024;
	public static final int C_MAX_NOTIF_COUNT = 5; //максимальное количество выводимых за один раз уведомлений
	
	public static final int C_NOTIF_DISTANCE = 100; //расстояние до объекта, при котором срабатывает уведомление, м
	public static final int C_NOTIF_LOCATION_ACCURACY = 100; //требуемая точность определения местоположения, м 
	public static final int C_NOTIF_LOCATION_EXPIRE = 60; //время устаревания определенного ранее местоположения, сек
	
	// публичный ключ сервера по умолчанию
	public static final String C_SRV_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDRNqo59dQQejYwPU4SxHGEVr864HHresvXLlqPVF/0+iQeMhT0u4fHptSyxlLK1Rg521ceLdBywfR4BqmYw7l2I0zaAnvZ4+chdRYnwn3TXVyZQ/KWOqkzSWykYGQYUWNnf7/zq/AMe1e9tuNq5rY5kuv0sQPw3x2sXQp/d1w9AwIDAQAB";
	// вектор инициклизации для AES256
	public static byte[] C_IV_KEY = {0x72, (byte) 0xF3, 0x23, (byte) 0x96, (byte) 0xA5, 0x52, (byte) 0x88, (byte) 0xE2, 0x42, 0x74, (byte) 0x91, 0x02, 0x17, 0x2E, (byte) 0x84, 0x19};
	// секретный общий ключ
	public static byte[] C_COMMON_SECRET_KEY = {(byte) 0xD8, 0x7F, (byte) 0x91, 0x3E, (byte) 0xA6, 0x59, 0x5B, 0x74, (byte) 0xBA, (byte) 0x83, (byte) 0xF7, (byte) 0x8A, 0x0E, (byte) 0x98, 0x23, 0x01,
		(byte) 0xE2, 0x04, (byte) 0xCE, 0x47, (byte) 0x9A, 0x18, 0x45, 0x65, (byte) 0xB1, (byte) 0xFA, (byte) 0xE4, (byte) 0x8F, 0x54, (byte) 0xC0, 0x06, 0x22};
		
	
	public static final int C_DATABASE_VERSION =20;
    public static final String C_DATABASE_NAME = "cs.db";

	public static final String C_T_VARS_NAME = "T_VARS";
	public static final String C_T_URLS_NAME = "T_URLS";
	public static final String C_T_WIDGETS_NAME = "T_WIDGETS";
	public static final String C_T_NOTIFS_NAME = "T_NOTIFS";
//	public static final String C_T_HISTORY_NAME = "T_HISTORY";
//	public static final String C_T_LOG_NAME = "T_LOG";
	public static final String C_T_OUT_NAME = "T_OUT";
//	public static final String C_T_IN_NAME = "T_IN";
	public static final String C_T_PACK_NAME = "T_PACK";

    public static final String C_SQL_T_VARS_CREATE = "CREATE TABLE "+C_T_VARS_NAME+
    	"(V_NAME VARCHAR(50) PRIMARY KEY, V_VALUE VARCHAR(250));";
    public static final String C_SQL_T_URLS_CREATE = "CREATE TABLE "+C_T_URLS_NAME+
    	"(U_ID INTEGER PRIMARY KEY, U_URL VARCHAR(256), U_ACCESS_DATE DATE);";
    public static final String C_SQL_T_NOTIFS_CREATE = "CREATE TABLE "+C_T_NOTIFS_NAME+
    	"(N_ID INTEGER PRIMARY KEY, N_TYPE VARCHAR(10), N_SHOW INTEGER DEFAULT 0, N_START_DATE DATE, " +
    	"N_STOP_DATE DATE, N_NEXT_DATE DATE, N_LATITUDE REAL, N_LONGITUDE REAL, N_TEXT VARCHAR(128), " +
    	"N_URL VARCHAR(256), N_IMAGE BLOB);";
    public static final String C_SQL_T_WIDGETS_CREATE = "CREATE TABLE "+C_T_WIDGETS_NAME+
    	"(W_ID INTEGER PRIMARY KEY, N_ID INTEGER);";
//    public static final String C_SQL_T_HISTORY_CREATE = "CREATE TABLE "+C_T_HISTORY_NAME+"(H_DATE DATE DEFAULT CURRENT_TIMESTAMP, H_TYPE VARCHAR(1), N_ID INTEGER);";
//    public static final String C_SQL_T_LOG_CREATE = "CREATE TABLE "+C_T_LOG_NAME+"(L_DATE DATE DEFAULT CURRENT_TIMESTAMP, L_TEXT VARCHAR(400));";
    public static final String C_SQL_T_OUT_CREATE = "CREATE TABLE "+C_T_OUT_NAME+
    	"(OUT_ID INTEGER PRIMARY KEY, OUT_TYPE VARCHAR(16), OUT_DATE DATE DEFAULT CURRENT_TIMESTAMP, " +
    	"OUT_ZIPPED VARCHAR(1), OUT_CRYPTED VARCHAR(1), OUT_SIGN VARCHAR(200), OUT_DATA VARCHAR(2000));"; // varchar in SQLite is unsizeble
//    public static final String C_SQL_T_IN_CREATE = "CREATE TABLE "+C_T_IN_NAME+"(IN_ID INTEGER PRIMARY KEY, OUT_ID INTEGER, IN_DATE DATE DEFAULT CURRENT_TIMESTAMP, IN_DATA VARCHAR(250));";

//    public static final String C_SQL_T_IN_INDEX_CREATE = "CREATE INDEX "+C_T_IN_NAME+"_I_DT ON "+C_T_IN_NAME+" (IN_DATE);";
//    public static final String C_SQL_T_OUT_INDEX_CREATE = "CREATE INDEX "+C_T_OUT_NAME+"_I_DT ON "+C_T_OUT_NAME+" (OUT_DATE);";
    public static final String C_SQL_T_PACK_CREATE = "CREATE TABLE "+C_T_PACK_NAME+
	"(P_ID INTEGER PRIMARY KEY, P_STATUS VARCHAR(1), P_URL VARCHAR(200));";    
    
	// DB vars names:
	public static final String C_VAR_DB_IS_CHANGED = "DB_IS_CHANGED"; // name of var in T_VARS = flag of change of any var
    public static final String C_VAR_VERSION = "VERSION"; 
//    public static final String C_VAR_DEVICE_ID = "DEVICE_ID"; 
    public static final String C_VAR_SERVICE_STATE = "SERVICE_STATE"; 
    public static final String C_VAR_SERVICE_DATE = "SERVICE_DT"; 
    public static final String C_VAR_SERVICE_D_DATE = "SERVICE_D_DT"; 
    public static final String C_VAR_SERVICE_A_DATE = "SERVICE_A_DT"; 
    public static final String C_VAR_SYNC_DATE = "SYNC_DT"; 
    public static final String C_VAR_SYNC_RES = "SYNC_RES"; 
    public static final String C_VAR_SYNC_ID = "SYNC_ID";
    
    public static final String C_VAR_PRIVATE_KEY = "PR_KEY"; 
    public static final String C_VAR_PUBLIC_KEY = "PB_KEY"; 
    public static final String C_VAR_SRV_PUBLIC_KEY = "S_PB_KEY"; 
    public static final String C_VAR_DEVICE_KEY = "DKEY"; // ключ под которым шифруются принимаемые с сервера и передаваемые на сервер данные
    public static final String C_VAR_USER_DIGEST_KEY = "HKEY"; // дайджест пароля пользователя
//    public static final String C_VAR_LOCAL_KEY = "LKEY"; // ключ под которым шифруются локальные данные
    
    // типы команд, отправляемых на сервер
    public static final String C_REGISTRATION_PUB_KEY = "REG_PUB_KEY"; // данные - публичн ключ der в Base64
//    public static final String C_REQUEST_FOR_NEW_SESSION_KEY = "REQ_SKEY"; // данные - локальная дата запроса в формате yyyy-MM-dd HH:mm:ss
    public static final String C_REGISTRATION_DEVICE_KEY = "REG_DKEY"; // данные - локальная дата запроса в формате yyyy-MM-dd HH:mm:ss
    public static final String C_REGISTRATION_DIGEST = "REG_DIGEST"; //формат отправки: {"N":"+NotificationId+","D":"+CurrentDate+"}, дата в формате yyyy-MM-dd HH:mm:ss
    
//    public static final String C_INFO_INSTALL_DATE = "INF_INS_DT"; //формат отправки: локальная дата установки в формате yyyy-MM-dd HH:mm:ss
    public static final String C_INFO_LOG = "INF_LOG"; //формат отправки: строка из строк с разделителем ||
    public static final String C_INFO_SHOW_NOTIFICATION = "INF_SH_NTF"; //формат отправки: {"N":"+NotificationId+","D":"+CurrentDate+"}, дата в формате yyyy-MM-dd HH:mm:ss
    public static final String C_INFO_SELECT_WIDGET = "INF_SEL_WD"; //формат отправки: {"N":"+NotificationId+","D":"+CurrentDate+"}, дата в формате yyyy-MM-dd HH:mm:ss
    public static final String C_INFO_DELETE_WIDGET = "INF_DEL_WD"; //формат отправки: {"N":"+NotificationId+","D":"+CurrentDate+"}, дата в формате yyyy-MM-dd HH:mm:ss
    public static final String C_INFO_DEVICE = "DEV_INFO"; //формат отправки: JSON
    public static final String C_INFO_VERSION = "VERSION"; //формат отправки: A_0.91 
    
    
    // Net:
//	public static final String C_ROOT_URL = "http://homeplus.kz/ftp_homeplus/hp_emulator/";
	public static final String C_DEFAULT_SYNC_PROC_URL1 = "http://www.homeplus.kz/cs/sync.php";
	public static final String C_DEFAULT_SYNC_PROC_URL2 = "http://178.89.186.131/~crystals/cs/sync.php";
	public static final String C_DEFAULT_SYNC_PROC_URL3 = "http://82.200.139.162/cs/sync.php"; //cs old 109.233.240.226
	
	public static final String C_PROVIDER_NAME = "kz.pointplus.db_provider";
	public static final String C_PROVIDER_ROOT_URL = "content://" + C_PROVIDER_NAME +"/";	
	public static final String C_WIDGET_CONFIG_URL = C_PROVIDER_ROOT_URL + "w_config.html";	
	
	public static final String C_CABINET_PREFIX = "cabinet";
	public static final String C_ENCRYPTED_EXTENSION = ".crypted";
	public static final String C_DECRYPTED_EXTENSION = ".decrypted";
	
	public static final String C_CABINET_PAGE = C_CABINET_PREFIX + ".html";
	public static final String C_START_PAGE = "informer.html"; //!!! 
//	public static final String C_START_PAGE = "informer_test.html"; 
	public static final String C_ERROR_PAGE = "error.html";
	public static final String C_ZIP_ASSET_FILES = "a.z";
	
	// Notifications:
	public static String C_NOTIFY_ACTION_NAME = "kz.crystalspring.android_client.nid.";
	public static String C_NOTIFY_EXTRA_NAME = "NotifyID";
	
	// Service:
	public static final int C_SERVICE_FIRST_RUN = 300000; // 5 min after boot start
	public static final int C_SERVICE_INTERVAL = 1800000; // 30 min 1800000
	
	//Log:
	public static final int C_LOG_LEVEL = 1; // 0 - minimal (err), 3 - maximal (debug)

	public static final String C_ALERT_TITLE = "CSInfo.kz";
	
	public static final String C_ONSHOW_JS_PROCNAME = "try{OnAppShowProc();}catch(e){}";
	public static final String C_ONHIDE_JS_PROCNAME = "try{OnAppHideProc();}catch(e){}";
	public static final String C_ONBACK_JS_PROCNAME = "try{OnPressKeyBackProc();}catch(e){}";
	public static final String C_ONARCLICK_JS_PROCNAME = "try{OnARClickProc(':1');}catch(e){}";
	public static final String C_ONSCANBARCODE_JS_PROCNAME = "try{OnScanBarcodeProc(':1',':2');}catch(e){}";
	
	
}
