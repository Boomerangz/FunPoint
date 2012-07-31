package kz.crystalspring.pointplus;

import java.io.File;
import java.io.FileNotFoundException;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.android_client.C_Log;
import kz.crystalspring.android_client.C_SecurityHelper;
import kz.crystalspring.android_client.C_Vars;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class C_ContentProvider extends ContentProvider {
	static final String C_TAG = "CS_ContentProvider";
	private UriMatcher uriMatcher;
    private static final int C_FILE_ID = 1;	
	

	@Override
	public boolean onCreate() {
		C_Log.v(3, C_TAG, "onCreate - start");
		this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		if(this.uriMatcher == null){ 
			return false; 
		}
		this.uriMatcher.addURI(C_Vars.C_PROVIDER_NAME, "*", C_FILE_ID);
		C_Log.v(3, C_TAG, "onCreate - end");
		return true;
	}

	/**
	* Метод вызывается при обращении браузера к адресам C_PROVIDER_NAME 
	*/	
	@Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {		
		String vFileName = uri.getPath();
		
		if (vFileName.equals("/.png")) { // hack of css - no file needed!
			throw new FileNotFoundException("!");
		}
		
		if ( vFileName.endsWith(".html") ) {
			C_Log.v(1, "f", vFileName); // сохранение переходов 
		} else {
			C_Log.v(2, C_TAG, "openFile vFileName=" + vFileName + " - start");
		}
		try {
			Context vContext = getContext();
			String vFilesDir = vContext.getFilesDir() + "/";
			String vCacheDir = vContext.getCacheDir() + "/";
			vFileName = vFileName.substring(1).replace("/",".."); // поддиректории запрещены, поэтому заменяем  "/" на ".."
			File vFile;
			// если файл зашифрован то расшировать в темповый файл
			if (vFileName.endsWith(C_Vars.C_ENCRYPTED_EXTENSION)) {
				byte[] vData = C_FileHelper.ReadFile(new File(vFilesDir + vFileName));
				String vDFileName = vFileName.substring(0, vFileName.lastIndexOf('.')) + C_Vars.C_DECRYPTED_EXTENSION;
				C_Log.v(2, C_TAG, "openFile decrypt file " + vFileName + " to " + vDFileName);
				vData = C_SecurityHelper.sym_device_decrypt(vData);
				vFile = new File(vCacheDir + vDFileName);
				C_FileHelper.SaveFile(vFile, vData, true);
			} else {
				vFile = new File(vFilesDir + vFileName);
				if(!vFile.exists()) { 
//					vFile = vContext.getAssets().openFd(vFileName);
//					if ( vFileName.endsWith(".png") && vFileName.startsWith("map..")) {
//						 файлы подложки карты
//						try {
//							// для карты ошибки игнорировать!
//							C_FileHelper.CopyAssetFile(vContext, vFileName);
//						} catch (Exception e) {					
//						}
//						if(!vFile.exists()) {
//						C_Log.v(2, C_TAG, "e:openFile vFileName=" + vFileName + " - img file is not exists!");
//						}
//					} else {
						C_Log.v(0, C_TAG, "e:openFile vFileName=" + vFileName + " - file is not exists!");
						if ( vFileName.endsWith(".html") ) {
							C_Log.v(1, C_TAG, "openFile - load static error page");
							vFile = new File(vFilesDir + C_Vars.C_ERROR_PAGE);
							if(!vFile.exists()) {
								C_Log.v(1, C_TAG, "openFile - error page not found!");
							}
						}
//					}
					
				}
			}
			ParcelFileDescriptor vParcel = ParcelFileDescriptor.open(vFile, ParcelFileDescriptor.MODE_READ_ONLY);
			C_Log.v(2, C_TAG, "openFile - end");
			return vParcel;
		} catch (Exception e) {
// при работе с картой возможно много ошибок!			
//!!!			C_Log.v(0, C_TAG, "e:openFile vFileName=" + vFileName + ": " + e.getLocalizedMessage());
			throw new FileNotFoundException("(T) File reading is fail: " + vFileName + ": " + e.getLocalizedMessage());
		}
	}

	@Override
	public String getType(Uri uri) {
		C_Log.v(1, C_TAG, "getType uri="+uri.getPath());
		switch (uriMatcher.match(uri)){
         case C_FILE_ID:
             return "application/octet-stream";
         default:
             throw new IllegalArgumentException("(T) Unsupported URI: " + uri);
        }
	}
 	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		C_Log.v(0, C_TAG, "e:update");
		throw new UnsupportedOperationException("update not supported");
	}	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,	String[] selectionArgs, String sortOrder) {
		C_Log.v(0, C_TAG, "e:query");
		throw new UnsupportedOperationException("query not supported");
	}

 	@Override
	public Uri insert(Uri uri, ContentValues values) {
 		C_Log.v(0, C_TAG, "e:insert");
 		throw new UnsupportedOperationException("insert not supported");
	}


	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		C_Log.v(0, C_TAG, "e:delete");
		throw new UnsupportedOperationException("delete not supported");
	}

}
