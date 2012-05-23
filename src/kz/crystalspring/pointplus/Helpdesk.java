package kz.crystalspring.pointplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.android_client.C_JavascriptInterface;
import kz.crystalspring.android_client.C_NetHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.atmpoint1.types.ItemMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class Helpdesk extends Activity{

    Button bHlpdskTitle;
    EditText tHlpdskInp;
    Button bHlpdskSend;
    ExpandableListView lvMessages;
    AdapterMessages adapter;
    ItemMessage message;
    
    public static Context context;
    AssetManager assetManager; 
    
    String pLang;
    String pLangActivity;
    String pCity;
    String pCityActivity;
    
	SharedPreferences mPrefs;
    
	String fileName = "";
	static final String MSG_READ="message_is_read_";
    InputStream input;  
    
    private ArrayList<Object> messagesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpdesk);
		
        context = getApplicationContext();
        assetManager = getAssets();

        pLang = Prefs.getLangPref(context);
        pCity = Prefs.getCityPref(context);
        pLangActivity = Prefs.getLangPref(context);
        pCityActivity = Prefs.getCityPref(context);
        
		mPrefs=MainApplication.mPrefs;

        bHlpdskTitle = (Button) findViewById(R.id.btnHlpdskTitle);
        tHlpdskInp = (EditText) findViewById(R.id.txtHlpdskInp);
		bHlpdskSend = (Button) findViewById(R.id.btnHlpdskSend);

		bHlpdskSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				String msgText="";
				String vText = tHlpdskInp.getText().toString();
				if(vText.length()>0){
					String vQuery = "P_FDB_TXT:"+vText;
	                String vQType = "V_FEEDBACK_TXT";
	                Long vId = C_JavascriptInterface.InsOutDataNew(context,vQType,vQuery);

			        pLang = Prefs.getLangPref(context);
	                if(vId!=-1){

	                	tHlpdskInp.setText("");
	                	msgText = ProjectUtils.getObjectTextById(context, pLang, "feedback_after_text");
	                	C_NetHelper.SyncData(context, false, false);
	                }else{
	                	msgText = ProjectUtils.getObjectTextById(context, pLang, "feedback_error");
	                }
				}else{
					msgText = ProjectUtils.getObjectTextById(context, pLang, "feedback_error_empty");
				}
				Toast.makeText(context, msgText, Toast.LENGTH_LONG).show();
			}
		});
		
		messagesList = new ArrayList<Object>();
        adapter = new AdapterMessages(this, messagesList);

        lvMessages = (ExpandableListView) findViewById(R.id.lvFeedback);
        lvMessages.setAdapter(adapter);
		lvMessages.setOnGroupExpandListener(new OnGroupExpandListener()
		{
			@Override
			public void onGroupExpand(int arg0)
			{
				setMessageRead(adapter.getGroup(arg0));
			}
		});
        setObjectsTextVals();
        prepareMessagesList();
	}

	
	void setMessageRead(Object object)
	{
		ItemMessage msg=(ItemMessage) object;
		final String PREF_QUERY=MSG_READ+Integer.toString(msg.getId());

		String pref = getMessageRead(mPrefs,msg.getId());
		if (pref==null||pref.equals("NO"))
		{
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putString(PREF_QUERY,"YES");
			editor.commit();
			//Toast.makeText(Helpdesk.this,
			//		Integer.toString(msg.getId())+" не было прочитано", Toast.LENGTH_SHORT).show();
		}
		else
		{
			//Toast.makeText(Helpdesk.this,
			//	Integer.toString(msg.getId())+" было прочитано", Toast.LENGTH_SHORT).show();
		}
	}
	
	static String getMessageRead(SharedPreferences prefs,int id)
	{
		final String PREF_QUERY=MSG_READ+Integer.toString(id);
		return prefs.getString(PREF_QUERY, "NO");
	}
	
	 
	
	public static int getMessagesCount(Context context)
	{
		InputStream input;
		AssetManager assetManager=context.getAssets();
        String pLang = Prefs.getLangPref(context);
		int numberRead=0;
		String fileName = "";
        try {  
        	fileName = "json_messages_"+pLang;
        	
			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			
			SharedPreferences prefs = MainApplication.mPrefs;
			
			/*input = assetManager.open(fileName);

			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			
			// byte buffer into a string
			String text = new String(buffer);*/
			try
			{
				JSONArray entries = new JSONArray(text);
				int len = entries.length();
				for (int i = 0; i < len; i++)
				{
					try
					{
						JSONObject post = entries.getJSONObject(i);
						if (getMessageRead(prefs,post.getInt("id")).equals("NO"))
						{
							numberRead++;
						}
					} catch (Exception e)
					{
						//Toast.makeText(context,
						//		"Error in " + i, Toast.LENGTH_SHORT).show();
						//e.printStackTrace();
						continue;

					}
				}
			} catch (Exception je)
			{
				//Toast.makeText(context,
				//		"ERROR parsing " + fileName + "'", Toast.LENGTH_SHORT)
				//		.show();
				//je.printStackTrace();
				numberRead=0;
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			//Toast.makeText(context,
			//		"ERROR in accessing file '" + fileName + "'",
			//		Toast.LENGTH_SHORT).show();
			//e.printStackTrace();
			numberRead=0;
		}
		return numberRead;
	}

	
	public void prepareMessagesList(){
		String objType;
		String objTypeTitle = null;
        pLang = Prefs.getLangPref(context);
        try {  
        	fileName = "json_messages_"+pLang;
        	
        	messagesList.clear();
			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			
			/*
            input = assetManager.open(fileName);  
  
            int size = input.available();  
            byte[] buffer = new byte[size];  
            input.read(buffer);  
            input.close();  
  
            // byte buffer into a string  
            String text = new String(buffer); */ 
            try
            {
                JSONArray entries = new JSONArray(text);
                int len = entries.length();
                for (int i=0;i<len;i++)
                {
                	try{
	                	JSONObject post = entries.getJSONObject(i);

	            		AddObjectToMessagesList(post.getInt("id"),post.getString("date"),
	                			post.getString("msgimg"),post.getString("msgtxt"));

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

        adapter = new AdapterMessages(this, messagesList);
        lvMessages = (ExpandableListView) findViewById(R.id.lvFeedback);
        lvMessages.setAdapter(adapter);
	}
	
    //���������� �������� � ������ ������������
	
    public void AddObjectToMessagesList(int id,String date,String msgimg,String msgtxt)
    {
    	message = new ItemMessage();
    	message.setId(id);
    	message.setDate(date);
    	message.setMsgimg(msgimg);
    	message.setMsgtxt(msgtxt);
    	messagesList.add(message);
    }
	
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
        //������������� ����������� ������� ������

        pLang = Prefs.getLangPref(context);
        pCity = Prefs.getCityPref(context);
        
        prepareMessagesList();
        
    	if((!(pLangActivity.equalsIgnoreCase(pLang))) || (!(pCityActivity.equalsIgnoreCase(pCity)))){

    		messagesList = new ArrayList<Object>();
    		
            setObjectsTextVals();
            //prepareMessagesList();
            
            pLangActivity = Prefs.getLangPref(context);
            pCityActivity = Prefs.getCityPref(context);
    	}
        
		super.onResume();
	}
    
    public void setObjectsTextVals(){
    	//������� ����������
        pLang = Prefs.getLangPref(context);

		bHlpdskTitle.setText(ProjectUtils.getObjectTextById(context, pLang, "helpdesk_title"));
		tHlpdskInp.setHint(ProjectUtils.getObjectTextById(context, pLang, "helpdesk_txtinput"));
        bHlpdskSend.setText(ProjectUtils.getObjectTextById(context, pLang, "helpdesk_btnsend").toUpperCase());
    }

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if (keyCode == KeyEvent.KEYCODE_BACK) {
//    		MainMenu.tabHost.setCurrentTab(Integer.valueOf(Prefs.getInitTab(context)));
//    		return true;
//	    }
	    return super.onKeyDown(keyCode, event);
	}


}

