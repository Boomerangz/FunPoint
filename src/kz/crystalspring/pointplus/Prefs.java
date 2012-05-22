package kz.crystalspring.pointplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kz.crystalspring.android_client.C_FileHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class Prefs extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    
	private Context context;
	CharSequence[] entries;
	CharSequence[] entryValues;
	private ArrayList<String> lstCityNames;
	private ArrayList<Integer> lstCityVals;
	CharSequence[] entriesCity;
	CharSequence[] entryValuesCity;
	ListPreference lp;
	ListPreference lpCity;
	String pLang;
	AssetManager assetManager;
	InputStream input;  
    String fileName;
	
    @Override
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        assetManager = context.getAssets();
        pLang = getLangPref(context);
        
        lstCityNames = new ArrayList<String>();
        lstCityVals = new ArrayList<Integer>();
        lp = new ListPreference(this);
        lpCity = new ListPreference(this);
        
        setObjectsTextVals();

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        root.addPreference(lp);
        root.addPreference(lpCity);
        setPreferenceScreen(root);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }
    
    public void setObjectsTextVals(){
    	fillLanguages();
    	fillCities();
    	
        lp.setTitle(ProjectUtils.getObjectTextById(context, pLang, "prefs_lang_title"));
        lp.setSummary(ProjectUtils.getObjectTextById(context, pLang, "prefs_lang_summary"));
        lp.setDialogTitle(ProjectUtils.getObjectTextById(context, pLang, "prefs_lang_dlg_title"));
        lp.setNegativeButtonText(ProjectUtils.getObjectTextById(context, pLang, "prefs_no_btn"));

        lpCity.setTitle(ProjectUtils.getObjectTextById(context, pLang, "prefs_city_title"));
        lpCity.setSummary(ProjectUtils.getObjectTextById(context, pLang, "prefs_city_summary"));
        lpCity.setDialogTitle(ProjectUtils.getObjectTextById(context, pLang, "prefs_no_btn"));
        lpCity.setNegativeButtonText(ProjectUtils.getObjectTextById(context, pLang, "prefs_no_btn"));
    }
    
    public void fillLanguages(){
        lp.setKey("langPrefs");
        lp.setDefaultValue("ru");
        pLang = getLangPref(context);
        
        entries = new CharSequence[3];
        entries[0] = ProjectUtils.getObjectTextById(context, pLang, "prefs_lang_entr_1"); 
        entries[1] = ProjectUtils.getObjectTextById(context, pLang, "prefs_lang_entr_2");
        entries[2] = ProjectUtils.getObjectTextById(context, pLang, "prefs_lang_entr_3");
        lp.setEntries(entries);
        
        entryValues = new CharSequence[3];
        entryValues[0] = "ru"; 
        entryValues[1] = "en";
        entryValues[2] = "kz";
        lp.setEntryValues(entryValues);
    }
    
    public void fillCities(){
        lstCityNames.clear();
        lstCityVals.clear();
        
        lpCity.setKey("cityPrefs");
        pLang = getLangPref(context);
        
        try {  
        	fileName = "json_cities_dir_"+getLangPref(context);
        	
        	byte[] vIconBytes = C_FileHelper.ReadFile(new File(context.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
        	
			/*
            input = assetManager.open(fileName);  

            int size = input.available();  
            byte[] buffer = new byte[size];  
            input.read(buffer);  
            input.close();
            
            // byte buffer into a string  
            String text = new String(buffer);  */
            try
            {
                JSONArray entries = new JSONArray(text);
                
                int vLen = entries.length();
                for (int i=0;i<vLen;i++)
                {
                	try{
                    	JSONObject post = entries.getJSONObject(i);
                    	if(post.getInt("isset") == 1){
                        	lstCityNames.add(post.getString("title"));
                        	lstCityVals.add(post.getInt("id"));
                    	}
                	}catch(Exception e){
                	//	Log.i("ProjectUtils_getObjectTextById1","Error in "+i);
                		continue;
                	}
                }
            }
            catch (Exception je)
            {
        	//	Log.i("ProjectUtils_getObjectTextById2","ERROR parsing "+fileName+"'");
            //    je.printStackTrace();
            }
        } catch (IOException e) {  
    		//Log.i("ProjectUtils_getObjectTextById3","ERROR in accessing file '"+fileName+"'");
            //e.printStackTrace();  
        }
        
        int lstSize = lstCityVals.size();
        entriesCity = new CharSequence[lstSize];
        entryValuesCity = new CharSequence[lstSize];
        for(int j=0;j<lstSize;j++){
        	
        	entriesCity[j] = lstCityNames.get(j);
        	entryValuesCity[j] = String.valueOf(lstCityVals.get(j));
    		//Log.i("ProjectUtils_getObjectTextById2"," parsing "+entriesCity[j]+"'");
        }
        lpCity.setEntries(entriesCity);
        lpCity.setEntryValues(entryValuesCity);
    }
    
    // Inherited abstract method so it must be implemented
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {	
        //Log.i("Preferences", "Preferences changed, key="+key);
        if(key.compareTo("editTextPref")==0){}
        setObjectsTextVals();
    }
    
    // Static method to return the preference for the GPS precision setting
    public static void setLangPref(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("langPrefs", pString);
        editor.commit();
    }
    public static String getLangPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("langPrefs", "ru");
    }
    // Static method to return the preference for the name (only used for demonstration)
    public static String getCityPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("cityPrefs", "1");
    }

    //Object Type Id
    public static void setObjTypeId(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("objTypeId", pString);
        editor.commit();
    }
    public static String getObjTypeId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("objTypeId", "1");
    }

    //ATM Filters
    public static void setSelAtmFilters(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("atmFiltStr", pString);
        editor.commit();
    }
    public static String getSelAtmFilters(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("atmFiltStr", "22;37;26;1");
    }

    //Branch Filters
    public static void setSelBranchFilters(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("branchFiltStr", pString);
        editor.commit();
    }
    public static String getSelBranchFilters(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("branchFiltStr", "22;37;26;1");
    }

    //Object Type Id
    public static void setSelObjType(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("selObjType", pString);
        editor.commit();
    }
    public static String getSelObjType(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("selObjType", "1");
    }

    //������������� ��� ������� ��� ����������� � �������
    public static void setSelProdType(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("selProdType", pString);
        editor.commit();
    }
    //�������� ��� ������� ��� ����������� � �������
    public static String getSelProdType(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("selProdType", "6");
    }

    //������������� ID ������� ��� ����������� � �������
    public static void setSelObjId(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("selObjId", pString);
        editor.commit();
    }
    //�������� ID ������� ��� ����������� � �������
    public static String getSelObjId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("selObjId", "1");
    }


    //������������� ID ������� ��� ����������� � �������
    public static void setLastTabId(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("lastTabId", pString);
        editor.commit();
    }
    //�������� ID ������� ��� ����������� � �������
    public static String getLastTabId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("lastTabId", "1");
    }
    
    //Settings for Private Information
    //Nickname
    public static void setV_USER_NICK(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_NICK", pString);
        editor.commit();
    }
    public static String getV_USER_NICK(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_NICK", "");
    }
    //Surnamee (family)
    public static void setV_USER_FAMILY(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_FAMILY", pString);
        editor.commit();
    }
    public static String getV_USER_FAMILY(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_FAMILY", "");
    }
    //Name
    public static void setV_USER_NAME(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_NAME", pString);
        editor.commit();
    }
    public static String getV_USER_NAME(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_NAME", "");
    }
    //Middlename
    public static void setV_USER_PAT(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_PAT", pString);
        editor.commit();
    }
    public static String getV_USER_PAT(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_PAT", "");
    }
    //Birthdate
    public static void setV_USER_BD(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_BD", pString);
        editor.commit();
    }
    public static String getV_USER_BD(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_BD", "");
    }
    //E-mail
    public static void setV_USER_EMAIL(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_EMAIL", pString);
        editor.commit();
    }
    public static String getV_USER_EMAIL(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_EMAIL", "");
    }
    //Phone
    public static void setV_USER_TEL(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_TEL", pString);
        editor.commit();
    }
    public static String getV_USER_TEL(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_TEL", "");
    }
    //Sex
    public static void setV_USER_SEX(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("V_USER_SEX", pString);
        editor.commit();
    }
    public static String getV_USER_SEX(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("V_USER_SEX", "");
    }
    

    //Initial tab. For Key_Back Button
    public static void setInitTab(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("init_tab", pString);
        editor.commit();
    }
    public static String getInitTab(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("init_tab", "0");
    }
    //Filter Value 0 - Objects / 1- Products
    public static void setFilterType(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("filterType", pString);
        editor.commit();
    }
    public static String getFilterType(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("filterType", "0");
    }
    //Product Filters TRANSFERS
    public static void setSelTranfsFilters(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("selTranfsFilters", pString);
        editor.commit();
    }
    public static String getSelTranfsFilters(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("selTranfsFilters", "7");
    }
    //Product Filters DEPOSITS
    public static void setSelDeposFilters(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("selDeposFilters", pString);
        editor.commit();
    }
    public static String getSelDeposFilters(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("selDeposFilters", "4;22;26");
    }
    //Product Filters CARDS
    public static void setSelCardsFilters(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("selCardsFilters", pString);
        editor.commit();
    }
    public static String getSelCardsFilters(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("selCardsFilters", "1;4;22");
    }
    //Product Filters CARDS
    public static void setFirstRun(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("FirstRun", pString);
        editor.commit();
    }
    public static String getFirstRun(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("FirstRun", "");
    }
    

    public static void setMapObjLat(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("MapObjLat", pString);
        editor.commit();
    }
    public static String getMapObjLat(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("MapObjLat", "");
    }
    public static void setMapObjLon(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("MapObjLon", pString);
        editor.commit();
    }
    public static String getMapObjLon(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("MapObjLon", "");
    }
    public static void setMapObjTitShort(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("MapObjTitShort", pString);
        editor.commit();
    }
    public static String getMapObjTitShort(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("MapObjTitShort", "");
    }
    public static void setMapObjTitLong(Context context, String pString){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString("MapObjTitLong", pString);
        editor.commit();
    }
    public static String getMapObjTitLong(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("MapObjTitLong", "");
    }
}