package kz.crystalspring.pointplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemComment;
import kz.sbeyer.atmpoint1.types.ItemLangValues;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Comments extends Activity implements OnItemClickListener {

    ListView lstComments;
    AdapterComments adapter;
    private ArrayList<Object> commentsList;
    private ArrayList<Object> translationsList;
    ItemComment comment;
    
    public static Context context;
    AssetManager assetManager; 
    
    String pLang;
    String pLangActivity;
    String pCity;
    String pCityActivity;

    String username = "";
	String errBtnString = "";
	String txtId = "";
	String fileName = "";
    InputStream input;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);
		
        context = getApplicationContext();
        assetManager = getAssets();

        pLang = Prefs.getLangPref(context);
        pCity = Prefs.getCityPref(context);
        
		commentsList = new ArrayList<Object>();
		translationsList = new ArrayList<Object>();
        pLangActivity = Prefs.getLangPref(context);
        pCityActivity = Prefs.getCityPref(context);
		
        //-- ��������� ������� ��� ���������� � ������������ ���������
	    translationsList = ProjectUtils.getTranslList(context,pLang);
	    
        
        adapter = new AdapterComments(this, commentsList);
        lstComments = (ListView) findViewById(R.id.lstComments);
        
        lstComments.setAdapter(adapter);
        //lstComments.setOnItemClickListener(this);
        prepareCommentsList();
        
	}
	
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
        //������������� ����������� ������� ������

        pLang = Prefs.getLangPref(context);
        pCity = Prefs.getCityPref(context);
		prepareCommentsList();

    	if((!(pLangActivity.equalsIgnoreCase(pLang))) || (!(pCityActivity.equalsIgnoreCase(pCity)))){
    		commentsList = new ArrayList<Object>();
    		translationsList = new ArrayList<Object>();
    	    translationsList = ProjectUtils.getTranslList(context,pLang);
    	    
            pLangActivity = Prefs.getLangPref(context);
            pCityActivity = Prefs.getCityPref(context);
    	}
        
		super.onResume();
	}
    
    public String getTranslationById(String id){
    	ItemLangValues it;
		//Log.i("L111115",String.valueOf(translationsList.size()));
    	for(int k=0;k<translationsList.size();k++){
    		it = (ItemLangValues) translationsList.get(k);
    		
    		if(it.getIdWeb().contentEquals(id)){
    			return it.getValue();
    		}
    	}
		return "";
    }
	
	public void prepareCommentsList(){
		String objType;
		String objTypeTitle = null;
        pCity = Prefs.getCityPref(context);
        pLang = Prefs.getLangPref(context);
        try {  
        	fileName = "json_comments";
			
        	commentsList.clear();
        	
        	byte[] vIconBytes = C_FileHelper.ReadFile(new File(context.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			/*
            input = assetManager.open(fileName);  
  
            int size = input.available();  
            byte[] buffer = new byte[size];  
            input.read(buffer);  
            input.close();  
  
            // byte buffer into a string  
            String text = new String(buffer);  
            */try
            {
                JSONArray entries = new JSONArray(text);
                int len = entries.length();
                for (int i=0;i<len;i++)
                {
            		//Log.i("L4","----");
                	if(i == 300){
                		break;
                	}
                	try{
	                	JSONObject post = entries.getJSONObject(i);
	                	if(pCity.contentEquals(post.getString("cityid"))){
	
		                	if(post.getString("username").length()<1){
		                		username = getTranslationById("cmnt_unknown_user");
		                	}else{
		                		username = post.getString("username");
		                	}
		                	
		                	if(post.getString("errbtn").length()>0){
		                		txtId = "feedback_button_"+post.getString("errbtn");
		                		errBtnString = getTranslationById(txtId);
		                	}
		                	objType = post.getString("objtype");
		                	
		                	if(objType.contentEquals("1")){
		                		objTypeTitle = getTranslationById("obj_type_list_atm_title");
		                	}else if(objType.contentEquals("2")){
		                		objTypeTitle = getTranslationById("obj_type_list_branch_title");
		                	}else if(objType.contentEquals("3")){
		                		objTypeTitle = getTranslationById("obj_type_list_exch_title");
		                	}else if(objType.contentEquals("4")){
		                		objTypeTitle = getTranslationById("obj_type_list_notar_title");
		                	}else if(objType.contentEquals("5")){
		                		objTypeTitle = getTranslationById("obj_type_list_transf_title");
		                	}else if(objType.contentEquals("6")){
		                		objTypeTitle = getTranslationById("obj_type_list_depos_title");
		                	}else if(objType.contentEquals("7")){
		                		objTypeTitle = getTranslationById("obj_type_list_credit_title");
		                	}else if(objType.contentEquals("8")){
		                		objTypeTitle = getTranslationById("obj_type_list_cards_title");
		                	}
		                	
		            		AddObjectToCommentsList(post.getInt("id"),username,post.getString("date"),
		                			post.getString("objtype"),post.getString("objname"),objTypeTitle,post.getString("objid"),
		                			post.getString("cmnttxt"),errBtnString,post.getString("cmnttype"),post.getString("bankid"));
	                		
	                	}
                	}catch(Exception e){
                		//Toast.makeText(getApplicationContext(),"Error in "+i, Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
                		continue;
                		
                	}
                }
            }
            catch (Exception je)
            {
                //Toast.makeText(getApplicationContext(),"ERROR parsing "+fileName+"'", Toast.LENGTH_SHORT).show();
                //je.printStackTrace();
            }
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            //Toast.makeText(getApplicationContext(),"ERROR in accessing file '"+fileName+"'", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();  
        }
		//Log.i("Comments4","--"+pLang+"--");
        adapter = new AdapterComments(this, commentsList);
        lstComments = (ListView) findViewById(R.id.lstComments);
		lstComments.setAdapter(adapter);
	}
	
    public void func(View v){
 /*
    	comment = (ItemComment) commentsList.get((Integer) v.getTag());
    	//Float lat = comment.getLatitude();
    	//Float lon = comment.getLongitude();
    	
    	MainMenu.tabHost.setCurrentTab(2);
    	
    	Toast.makeText(this, String.valueOf(comment.getObjId()), Toast.LENGTH_SHORT).show();*/
    }

    //���������� �������� � ������ ������������
	
    public void AddObjectToCommentsList(int id,String cmntUsrname,String cmntDatetime,
    		String objType,String objName,String objTypeTitle,String objId,String cmntText,String errBtnId,String cmntType,String bankid)
    {
    	comment = new ItemComment();
    	comment.setId(id);
    	comment.setCmntUsrname(cmntUsrname);
    	comment.setCmntDatetime(cmntDatetime);
    	comment.setObjType(objType);
    	comment.setObjName(objName);
    	comment.setObjTypeTitle(objTypeTitle);
    	comment.setObjId(objId);
    	comment.setCmntText(cmntText);
    	comment.setErrBtnId(errBtnId);
    	comment.setCmntType(cmntType);
    	comment.setBankid(bankid);
    	commentsList.add(comment);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	/*	ItemComment aa = (ItemComment) arg0.getItemAtPosition(arg2);
    	
		Prefs.setFilterType(context,"0");
	    Prefs.setSelObjType(context, String.valueOf(aa.getObjType()));
		Prefs.setSelObjId(context, String.valueOf(aa.getObjId()));

    	MainMenu.tabHost.setCurrentTab(6);*/
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
    		MainMenu.tabHost.setCurrentTab(Integer.valueOf(Prefs.getInitTab(context)));
    		return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
