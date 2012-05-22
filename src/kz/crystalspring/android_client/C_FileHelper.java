package kz.crystalspring.android_client;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class C_FileHelper {
	static final String C_TAG = "CS_FileHelper";
	
	
	/**
	* Копирование потока pInput в поток pOutput
	* @param pInput - входящий поток
	* @param pOutput - исходящий поток
	* @return - количество скопированных байтов
	*/
    private static int copy(InputStream pInput, OutputStream pOutput) throws InterruptedException, IOException {
        C_Log.v(3, C_TAG, "copy - start");   	
	   	int DEFAULT_BUFFER_SIZE = 1024 * 4;
    	byte[] vBuffer = new byte[DEFAULT_BUFFER_SIZE];
    	int vCount = 0;
    	int n = 0;
    	while (-1 != (n = pInput.read(vBuffer))) {
    		pOutput.write(vBuffer, 0, n);
    		vCount += n;
    		if (Thread.interrupted()) {
    			throw new InterruptedException();
    		}
    	}
        C_Log.v(3, C_TAG, "copy, len=" + vCount + " - end");
    	return vCount;
    }
    	
	/**
	* Сжатие байтового массива методом gzip 
	* @param pData - дексриптор базы данных
	* @return - сжатый байтовый массив
	*/	
    public static byte[] compress(byte[] pData) {
    	C_Log.v(3, C_TAG, "compress - start");   	
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(pData);
            gzipOutputStream.close();
        } catch(IOException e){
            throw new RuntimeException(e);
        }
//        CS_Log.v(2, C_TAG, "Compression, len=" + content.length + " ratio " + (1.0f * content.length/byteArrayOutputStream.size()));
        C_Log.v(3, C_TAG, "compress - end");
        return byteArrayOutputStream.toByteArray();
    }
    
	/**
	* Восстановление байтового массива, сжатого методом gzip 
	* @param pData - дексриптор базы данных
	* @return - сжатый байтовый массив
	*/    
    public static byte[] decompress(byte[] pData) throws InterruptedException{
        C_Log.v(3, C_TAG, "decompress - start"); 
        ByteArrayOutputStream pOut = new ByteArrayOutputStream();
        try{
            copy(new GZIPInputStream(new ByteArrayInputStream(pData)), pOut);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        byte[] vRes = pOut.toByteArray(); 
//        CS_Log.v(2, C_TAG, "decompress, in_len=" + contentBytes.length + 
//        		" out_len=" + vRes.length + " ratio " + Math.round(vRes.length*100 / contentBytes.length) + " %");
        C_Log.v(3, C_TAG, "decompress - end"); 
        return vRes;
    }	    

	/**
	* Кописрование файлов из папки приложения Assets в папку Documents
	* @param pContext - контекст приложения
	*/
    public static void CopyAssetFiles(Context pContext){
        C_Log.v(3, C_TAG, "CopyAssetFiles - start");
    	AssetManager assetManager = pContext.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
            for(int i=0; i<files.length; i++) {
                try {
                	CopyAssetFile(pContext, files[i]);
                } catch (Exception e) {
                	C_Log.v(2, C_TAG, "e: CopyAssetFiles file:" +  files[i] + " e:" + e.getMessage());
                }
            }
        } catch (Exception e) {
        	C_Log.v(0, C_TAG, "e: CopyAssetFiles :" + e.getMessage());
        }
        C_Log.v(3, C_TAG, "CopyAssetFiles - end");
    }
    
	/**
	* Копирование указанного файла из папки приложения Assets в папку Documwnts
	* @param pContext - контекст приложения
	* @param pFileName - имя файла для копирования
	 * @throws IOException 
	 * @throws InterruptedException 
	*/    
    public static void CopyAssetFile(Context context, String pFileName) throws Exception{
        C_Log.v(3, C_TAG, "CopyAssetFile - start");
   		InputStream vInStream = context.getAssets().open(pFileName);
   		FileOutputStream vOutStream = context.openFileOutput(pFileName, Context.MODE_PRIVATE );
   		int vLen = C_FileHelper.copy(vInStream, vOutStream);
   		vInStream.close();
   		vOutStream.close();
   		C_Log.v(1, C_TAG, "CopyAssetFile pFileName=" + pFileName + " :" + vLen);
    }	

	/**
	* Сохранение данных из байтового массива в указанный файл 
	* @param pFile - файл
	* @param pData - данные
	* @param pDelefeIfExists - флаг предварительного удаления, если указан то перед 
	* записью проверяется наличие файла с указанным именеим и если имеется то удаляется
	*/
	public static void SaveFile(File pFile, byte[] pData, boolean pDelefeIfExists) throws IOException{
        C_Log.v(3, C_TAG, "SaveFile - start"); 
		if (pDelefeIfExists) {
			if(pFile.exists()) {
				if (pFile.delete())
					C_Log.v(2, C_TAG, "SaveFile: delete old file " + pFile.getName());
			}
		}
//		FileOutputStream fos = context.openFileOutput(pName, Context.MODE_PRIVATE );
//		fos.write(pData); 
	
		FileOutputStream fos = new FileOutputStream(pFile);
		fos.write(pData);	 
		fos.close(); 
        C_Log.v(2, C_TAG, "SaveFile, name=" + pFile.getName() + " len=" + pFile.length());
		fos.close();
        C_Log.v(3, C_TAG, "SaveFile - end"); 
	}	
		
	/**
	* Чтение данных указанного файла в байтовый массив 
	* @param pFile - файл
	* @return если все ок то данные файла, иначе генерируется ошибка
	*/
	public static byte[] ReadFile(File pFile) throws IOException {
        C_Log.v(3, C_TAG, "ReadFile - end");	    
	    InputStream is = new FileInputStream(pFile);
	    long length = pFile.length();
	    byte[] bytes = new byte[(int)length];
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file " + pFile.getName());
	    }
	    is.close();
        C_Log.v(3, C_TAG, "ReadFile - end");	    
	    return bytes;
	}
	
	/**
	* Удаление кэшированных расшифрованных файлов приложения (делается при завершении пользовательской сессии). 
	* Перед удалением делается перезапись данных файла массивом случайных значений.  
	* @param context - контекст приложения
	*/	
	public static void DeleteCachedDecryptedFiles(Context context) {
		C_Log.v(3, C_TAG, "DeleteCachedDecryptedFiles - end");
		try {
			FileFilter vFileFilter = new FileFilter() { 
				@Override
				public boolean accept(File vFile) {
					return vFile.getName().endsWith(C_Vars.C_DECRYPTED_EXTENSION); 
				} 
			};
			File[] files = context.getCacheDir().listFiles(vFileFilter);
			for ( File vF : files) {
				byte[] vB = new byte[(int) vF.length()]; //!128 = 16
				new SecureRandom().nextBytes(vB);
				SaveFile(vF, vB, false);
				vF.delete();
				C_Log.v(2, C_TAG, "DeleteCachedDecryptedFiles file=" + vF.getName());
			}
		} catch (Exception e) {
			C_Log.v(0, C_TAG, "DeleteCachedDecryptedFiles e:" + e.getMessage());
		}
		C_Log.v(3, C_TAG, "DeleteCachedDecryptedFiles - end");

	}

	/**
 	* Разархивирование файлов из файла в assets c именем pZipFile в папку Documents
 	* @param context
 	* @param pZipFile
 	* @throws IOException
 	*/
	public static void UnzipAssetFiles(Context pContext, String pZipFile) throws IOException {
		try {
			ZipInputStream zin = new ZipInputStream(pContext.getAssets().open(pZipFile));
			String vFilesDir = pContext.getFilesDir() + "/";
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = vFilesDir + ze.getName();

					if (ze.isDirectory()) {
						File unzipFile = new File(path);
						if(!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						FileOutputStream fout = new FileOutputStream(path, false);
						try {
							for (int c = zin.read(); c != -1; c = zin.read()) {
								fout.write(c);
							}
							zin.closeEntry();
						}
						finally {
							fout.close();
						}
					}
					C_Log.v(1, C_TAG, "UnzipAssetFiles - " + path);
				}
			}
			finally {
				zin.close();
			}
		}
		catch (Exception e) {
			C_Log.v(0, C_TAG, "UnzipAssetFiles e:" + e.getMessage());
		}
	}

	
	

}

