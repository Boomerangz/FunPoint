package kz.crystalspring.pointplus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kz.com.pack.jam.R;
import kz.crystalspring.android_client.C_JavascriptInterface;
import kz.crystalspring.android_client.C_NetHelper;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfo extends Activity implements OnClickListener {

    String pLang;
    Context context;
	
    Button bMainInfo;
	EditText tPseudo;
	EditText tPhone;
	EditText tEmail;
	Button bAddInfo;
	EditText tSurname;
	EditText tName;
	EditText tMidname;
	TextView lBdate;
	TextView lGender;
	DatePicker userBirthdate;
	RadioButton rMale;
	RadioButton rFemale;
	Button bSave;

	String pV_USER_NICK;
	String pV_USER_FAMILY;
	String pV_USER_NAME;
	String pV_USER_PAT;
	String pV_USER_BD;
	String pV_USER_EMAIL;
	String pV_USER_TEL;
	String pV_USER_SEX;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		
        context = getApplicationContext();
		
		bMainInfo = (Button) findViewById(R.id.btnMainInfo);
		tPseudo = (EditText) findViewById(R.id.txtPseudo);
		tPhone = (EditText) findViewById(R.id.txtPhone);
		tEmail = (EditText) findViewById(R.id.txtEmail);
		tEmail.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus&&!tEmail.getText().toString().equals(""))
				{
					String email=tEmail.getText().toString();
					Pattern p = Pattern.compile("[a-zA-Z]{1}[a-zA-Z\\d\\u002E\\u005F]+@([a-zA-Z]+\\u002E){1,2}((net)|(com)|(org)|(ru)|(kz))");
					Matcher m = p.matcher(email);
					boolean b = m.matches();
					if (!b)
						Toast.makeText(getApplicationContext(), ProjectUtils.getObjectTextById(context, pLang, "incorrect_txt")+" "+ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtemail"), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		bAddInfo = (Button) findViewById(R.id.btnAddInfo);
		tSurname = (EditText) findViewById(R.id.txtSurname);
		tName = (EditText) findViewById(R.id.txtName);
		tMidname = (EditText) findViewById(R.id.txtMidname);
		tPhone = (EditText) findViewById(R.id.txtPhone);
		tPhone.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					String phone=tPhone.getText().toString();
					Pattern p = Pattern.compile("(?:8|\\+7)?( |-)?\\(?(\\d{3})\\)?( |-)?(\\d{3})[ -]?(\\d{2})[ -]?(\\d{2})");
					Matcher m = p.matcher(phone);
					boolean b = m.matches();
					if (!b)
						Toast.makeText(getApplicationContext(), ProjectUtils.getObjectTextById(context, pLang, "incorrect_txt")+" "+ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtphone"), Toast.LENGTH_SHORT).show();
				}
			}
		});
        
		lBdate = (TextView) findViewById(R.id.lblBdate);
		lGender = (TextView) findViewById(R.id.lblGender);
		userBirthdate = (DatePicker) findViewById(R.id.userBirthdate);
		rMale = (RadioButton) findViewById(R.id.radMale);
		rFemale = (RadioButton) findViewById(R.id.radFemale);
		bSave = (Button) findViewById(R.id.btnSave);
		
		bSave.setOnClickListener(this);
        setObjectsTextVals();
	}
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	
        setObjectsTextVals();
        setUserDataVals();
        
		super.onResume();
	}

    
    public void setUserDataVals(){
    	//Set User Data from preferences
        pV_USER_NICK = Prefs.getV_USER_NICK(context);
        pV_USER_FAMILY = Prefs.getV_USER_FAMILY(context);
        pV_USER_NAME = Prefs.getV_USER_NAME(context);
        pV_USER_PAT = Prefs.getV_USER_PAT(context);
        pV_USER_BD = Prefs.getV_USER_BD(context);
        pV_USER_EMAIL = Prefs.getV_USER_EMAIL(context);
        pV_USER_TEL = Prefs.getV_USER_TEL(context);
        pV_USER_SEX = Prefs.getV_USER_SEX(context);

        //Log.i("get pV_USER_NICK", pV_USER_NICK);
        tPseudo.setText(pV_USER_NICK);
        tSurname.setText(pV_USER_FAMILY);
        tName.setText(pV_USER_NAME);
        tMidname.setText(pV_USER_PAT);
        tEmail.setText(pV_USER_EMAIL);
        
        if(pV_USER_BD.length()>5){
        	pV_USER_BD = pV_USER_BD.replace("."," - ");
            String[] birthdateArr = pV_USER_BD.split(" - ");

            int day = Integer.valueOf(birthdateArr[0]);
            int month = Integer.valueOf(birthdateArr[1]);
            int year = Integer.valueOf(birthdateArr[2]);
            userBirthdate.updateDate(year, month-1, day);
        }

        tPhone.setText(pV_USER_TEL);
        if(pV_USER_SEX.contentEquals("M")){
        	rMale.setChecked(true);
        	rFemale.setChecked(false);
        }else if(pV_USER_SEX.contentEquals("F")){
        	rMale.setChecked(false);
        	rFemale.setChecked(true);
        }
    }
    
    public void saveUserData(){
		//Save User Data
    	pV_USER_NICK = tPseudo.getText().toString();
    	pV_USER_FAMILY = tSurname.getText().toString();
    	pV_USER_NAME = tName.getText().toString();
    	pV_USER_PAT = tMidname.getText().toString();

    	String bDay = String.valueOf(userBirthdate.getDayOfMonth());
    	if((userBirthdate.getDayOfMonth())<10){
    		bDay = "0"+bDay;
    	}
		String bMonth = String.valueOf(userBirthdate.getMonth()+1);
    	if((userBirthdate.getMonth()+1)<10){
    		bMonth = "0"+bMonth;
    	}
    	String bYear = String.valueOf(userBirthdate.getYear());
    	
    	pV_USER_BD = bDay+"."+bMonth+"."+bYear;
    	pV_USER_EMAIL = tEmail.getText().toString();
    	pV_USER_TEL = tPhone.getText().toString();
		pV_USER_SEX = "";
    	if(rMale.isChecked()){
    		pV_USER_SEX = "M";
    	}else if(rFemale.isChecked()){
    		pV_USER_SEX = "F";
    	}

    	if(!(pV_USER_NICK.contentEquals(Prefs.getV_USER_NICK(context))
    		&& pV_USER_FAMILY.contentEquals(Prefs.getV_USER_FAMILY(context))
    		&& pV_USER_NAME.contentEquals(Prefs.getV_USER_NAME(context))
    		&& pV_USER_PAT.contentEquals(Prefs.getV_USER_PAT(context))
    		&& pV_USER_BD.contentEquals(Prefs.getV_USER_BD(context))
    		&& pV_USER_EMAIL.contentEquals(Prefs.getV_USER_EMAIL(context))
    		&& pV_USER_TEL.contentEquals(Prefs.getV_USER_TEL(context))
    		&& pV_USER_SEX.contentEquals(Prefs.getV_USER_SEX(context)))){

            Prefs.setV_USER_NICK(context,pV_USER_NICK);
            Prefs.setV_USER_FAMILY(context,pV_USER_FAMILY);
            Prefs.setV_USER_NAME(context,pV_USER_NAME);
            Prefs.setV_USER_PAT(context,pV_USER_PAT);
            Prefs.setV_USER_BD(context,pV_USER_BD);
            Prefs.setV_USER_EMAIL(context,pV_USER_EMAIL);
            Prefs.setV_USER_TEL(context,pV_USER_TEL);
            Prefs.setV_USER_SEX(context,pV_USER_SEX);

    	}
    }
    
    public void sendUserData(boolean toShow){
        String vJsonTxt = "{";
        boolean set = false;
        if(pV_USER_TEL.length() > 0){
        	String phone=tPhone.getText().toString();
			Pattern p = Pattern.compile("(?:8|\\+7)?( |-)?\\(?(\\d{3})\\)?( |-)?(\\d{3})[ -]?(\\d{2})[ -]?(\\d{2})");
			Matcher m = p.matcher(phone);
			boolean b = m.matches();
			if (!b){
				if(toShow){
					Toast.makeText(getApplicationContext(), ProjectUtils.getObjectTextById(context, pLang, "incorrect_txt")+" "+ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtphone"), Toast.LENGTH_SHORT).show();
				}
			}else{
	            //if (!Validate("persMTel", "Номер мобильного телефона", "t")) {return;}
	            vJsonTxt += "\"V_USER_TEL\":\""+pV_USER_TEL.replace("\"","'") + "\"";
	            set = true;
			}
        }
        if(pV_USER_EMAIL.length() > 0){
        	if (!tEmail.getText().toString().equals(""))
			{
				String email=tEmail.getText().toString();
				Pattern p = Pattern.compile("[a-zA-Z]{1}[a-zA-Z\\d\\u002E\\u005F]+@([a-zA-Z]+\\u002E){1,2}((net)|(com)|(org)|(ru)|(kz))");
				Matcher m = p.matcher(email);
				boolean b = m.matches();
				if (!b){
					if(toShow){
						Toast.makeText(getApplicationContext(), ProjectUtils.getObjectTextById(context, pLang, "incorrect_txt")+" "+ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtemail"), Toast.LENGTH_SHORT).show();
					}
				}else{
		            if(set){vJsonTxt += ",";}
		            vJsonTxt += "\"V_USER_EMAIL\":\""+pV_USER_EMAIL.replace("\"","'") + "\"";
		            set = true;
				}
			}
        }
        if(pV_USER_FAMILY.length() > 0){
            //if (!Validate("persMTel", "Номер мобильного телефона", "t")) {return;}
            if(set){vJsonTxt += ",";}
            vJsonTxt += "\"V_USER_FAMILY\":\""+pV_USER_FAMILY.replace("\"","'") + "\"";
            set = true;
        }
        if(pV_USER_NAME.length() > 0){
            //if (!Validate("persMTel", "Номер мобильного телефона", "t")) {return;}
            if(set){vJsonTxt += ",";}
            vJsonTxt += "\"V_USER_NAME\":\""+pV_USER_NAME.replace("\"","'") + "\"";
            set = true;
        }
        if(pV_USER_PAT.length() > 0){
            //if (!Validate("persMTel", "Номер мобильного телефона", "t")) {return;}
            if(set){vJsonTxt += ",";}
            vJsonTxt += "\"V_USER_PAT\":\""+pV_USER_PAT.replace("\"","'") + "\"";
            set = true;
        }
        if(pV_USER_BD.length() > 0){
            //if (!Validate("persMTel", "Номер мобильного телефона", "t")) {return;}
            if(set){vJsonTxt += ",";}
            vJsonTxt += "\"V_USER_BD\":\""+pV_USER_BD.replace("\"","'") + "\"";
            set = true;
        }
        if(pV_USER_NICK.length() > 0){
            //if (!Validate("persMTel", "Номер мобильного телефона", "t")) {return;}
            if(set){vJsonTxt += ",";}
            vJsonTxt += "\"V_USER_NICK\":\""+pV_USER_NICK.replace("\"","'") + "\"";
            set = true;
        }
        if(rMale.isChecked() || rFemale.isChecked()){
        	if(rMale.isChecked()){
        		pV_USER_SEX = "M";
        	}else if(rFemale.isChecked()){
        		pV_USER_SEX = "F";
        	}
            if(set){vJsonTxt += ',';}
            vJsonTxt += "\"V_USER_SEX\":\""+ pV_USER_SEX + "\"";
            set = true;
        }
        vJsonTxt += '}';

        
        
        String vQuery = vJsonTxt;
        String vQType = "V_PRIV_INFO";

        Long vId = C_JavascriptInterface.InsOutDataNew(context,vQType,vQuery);

        pLang = Prefs.getLangPref(context);
        String msgText = "";
        if(vId!=-1){
        	msgText = ProjectUtils.getObjectTextById(context, pLang, "confirm_profile_txt");
        	C_NetHelper.SyncData(context, false, false);
        }else{
        	msgText = ProjectUtils.getObjectTextById(context, pLang, "feedback_error");
        }
        
        if(toShow){
    		Toast.makeText(context, msgText, Toast.LENGTH_LONG).show();
        }
    }

	@Override
	protected void onPause() {
		
		saveUserData();
		sendUserData(false);
		
		super.onPause();
	}

    
    
    public void setObjectsTextVals(){
    	//Setting object titles according to selected languages
        pLang = Prefs.getLangPref(context);
        bMainInfo.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_maininf"));
        tPseudo.setHint(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtpseudo"));
        tPhone.setHint(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtphone"));
        tEmail.setHint(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtemail"));
        bAddInfo.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_addinf"));
        tSurname.setHint(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtsurname"));
        tName.setHint(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtname"));
        tMidname.setHint(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_txtmidname"));
        lBdate.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_lblbdate"));
        lGender.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_lblgender"));
        rMale.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_radmale"));
        rFemale.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_radfemale"));
        bSave.setText(ProjectUtils.getObjectTextById(context, pLang, "usr_inf_btnsave").toUpperCase());
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnSave:
			saveUserData();
			sendUserData(true);
			break;
		}
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
