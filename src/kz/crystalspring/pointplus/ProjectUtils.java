package kz.crystalspring.pointplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.crystalspring.android_client.C_FileHelper;
import kz.sbeyer.atmpoint1.types.ItemLangValues;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

public class ProjectUtils {
    
    public static String getObjectTextById(Context c,String pLangId, String pObjId){
    	AssetManager assetManager = c.getAssets();
        InputStream input;  
        String fileName="";
        try {  
        	fileName = "json_objects";
        	
        	//byte[] vIconBytes = C_FileHelper.ReadFile(new File(MainMenu.context.getFilesDir() + "/" + fileName));
		//	String text = new String(vIconBytes, "UTF-8");
        
            input = assetManager.open(fileName);  

            int size = input.available();  
            byte[] buffer = new byte[size];  
            input.read(buffer);  
            input.close();  
            
            // byte buffer into a string  
            String text = new String(buffer);  
            try
            {
                JSONArray entries = new JSONArray(text);
                
                int vLen = entries.length();
                for (int i=0;i<vLen;i++)
                {
                	try{
                    	JSONObject post = entries.getJSONObject(i);
                    	if(post.getString("idweb").equalsIgnoreCase(pObjId)){
                    		return post.getString(pLangId);
                    	}
                	}catch(Exception e){
                	//	Log.i("ProjectUtils_getObjectTextById1","Error in "+i);
                		continue;
                	}
                }
            }
            catch (Exception je)
            {
        		//Log.i("ProjectUtils_getObjectTextById2","ERROR parsing "+fileName+"'");
                //je.printStackTrace();
            }
        } catch (IOException e) {  
    		//Log.i("ProjectUtils_getObjectTextById3","ERROR in accessing file '"+fileName+"'");
            //e.printStackTrace();  
        }
    	return "";
    }
    
    public static ArrayList<Object> getTranslList(Context c,String pLangId){
    	ArrayList<Object> finArrList = new ArrayList<Object>();
    	ItemLangValues itemLangObj;
    	
    	AssetManager assetManager = c.getAssets();
        InputStream input;  
        String fileName="";
        try {  
        	fileName = "json_objects";
        	
        //	byte[] vIconBytes = C_FileHelper.ReadFile(new File(MainMenu.context.getFilesDir() + "/" + fileName));
		//	String text = new String(vIconBytes, "UTF-8");
        
            input = assetManager.open(fileName);  

            int size = input.available();  
            byte[] buffer = new byte[size];  
            input.read(buffer);  
            input.close();  
            
            // byte buffer into a string  
            String text = new String(buffer); 
            try
            {
                JSONArray entries = new JSONArray(text);
                
                int vLen = entries.length();
                for (int i=0;i<vLen;i++)
                {
                	try{
                    	JSONObject post = entries.getJSONObject(i);
                		itemLangObj = new ItemLangValues();
                		itemLangObj.setIdWeb(post.getString("idweb"));
                		itemLangObj.setValue(post.getString(pLangId));
                		finArrList.add(itemLangObj);
                	}catch(Exception e){
                	//	Log.i("ProjectUtils_getObjectTextById1","Error in "+i);
                		continue;
                	}
                }
            }
            catch (Exception je)
            {
        		//Log.i("ProjectUtils_getObjectTextById2","ERROR parsing "+fileName+"'");
                //je.printStackTrace();
            }
        } catch (IOException e) {  
    		//Log.i("ProjectUtils_getObjectTextById3","ERROR in accessing file '"+fileName+"'");
            //e.printStackTrace();  
        }
    	return finArrList;
    }

    //������� ��� ����������� ���������� ����� ����� �������
    public static float distance(float lat1,float lon1, float lat2, float lon2) {
        float R = 6371; // km (change this constant to get miles)
        float dLat = (float) ((lat2-lat1) * Math.PI / 180);
        float dLon = (float) ((lon2-lon1) * Math.PI / 180);
        float a = (float) (Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180 ) * Math.cos(lat2 * Math.PI / 180 ) *
                Math.sin(dLon/2) * Math.sin(dLon/2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
        float d = R * c;
        return (d*1000);
    }


    //������������ String �� Float. ������������� ����������� ������� ���������� ������ � ���������
	public static float getFloatFromString(String str){
		float floatNum = 0;
		String strModified = str.replace(".",",");
		int indexOfPoint = strModified.indexOf(",");
    	int stringLen = strModified.length();
    	int coef = stringLen - indexOfPoint-1;
    	String newLatStr = strModified.replace(",","");
    	floatNum = (Long.parseLong(newLatStr)/(float)(Math.pow(10, coef)));
    	return floatNum;
	}
	
	
	public static float getSumOfArray(List<Float> lst)
	{
		float sum=0;
		for (Float f:lst)
		{
			sum+=f;
		}
		return sum;
	}

	public static float getSumOfArray(float[] arr)
	{
		float sum=0;
		for (float f:arr)
		{
			sum+=f;
		}
		return sum;
	}
	

	public static List<String> separateStrings(String income, String separator)
	{
		List<String> list=new ArrayList();
		String str=new String(income);
		while (str.contains(separator))
		{
			list.add(str.substring(0,str.indexOf(separator)));
			str=str.substring(str.indexOf(separator)+1, str.length());
		}
		list.add(str.substring(0,str.length()));
		return list;
	}
	
	public static String dateToRelativeString(Date date)
	{
		return "два дня назад";
	}

	
	
}
