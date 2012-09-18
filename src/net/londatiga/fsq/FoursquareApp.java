package net.londatiga.fsq;

import net.londatiga.fsq.FoursquareDialog.FsqDialogListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.pointplus.HttpHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import android.app.ProgressDialog;

import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class FoursquareApp {
	private FoursquareSession mSession;
	private FsqAuthListener mListener;
	private ProgressDialog mProgress;
	private String mTokenUrl;
	private String mAccessToken;
	
	/**
	 * Callback url, as set in 'Manage OAuth Costumers' page (https://developer.foursquare.com/)
	 */
	public static final String CALLBACK_URL = "myapp://connect";
	private static final String AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
	private static final String TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";	
	private static final String API_URL = "https://api.foursquare.com/v2";
	
	private static final String TAG = "FoursquareApi";
	
	String url;
	
	public FoursquareApp(Context context, String clientId, String clientSecret) {
		mSession		= new FoursquareSession(context);
		
		mAccessToken	= mSession.getAccessToken();
		
		mTokenUrl		= TOKEN_URL + "&client_id=" + clientId + "&client_secret=" + clientSecret
						+ "&redirect_uri=" + CALLBACK_URL;
		
		url		= AUTH_URL + "&client_id=" + clientId + "&redirect_uri=" + CALLBACK_URL;
		
		
		

	}
	
	private void loadAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();
		
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				
				int what = 0;
				
				try {
					String url = mTokenUrl + "&code=" + code;
					
					Log.i(TAG, "Opening URL " + url.toString());
					
					JSONObject jsonObj  = (JSONObject) new JSONTokener(HttpHelper.loadByUrl(url)).nextValue();
		        	mAccessToken 		= jsonObj.getString("access_token");
		        	FSQConnector.dropUserActivity();
		        	MainApplication.loadAdditionalContent();
		        	Log.i(TAG, "Got access token: " + mAccessToken);
				} catch (Exception ex) {
					what = 1;
					ex.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}
	
	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");
		
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user name");
				int what = 0;
		
				try {
					String url = API_URL + "/users/self?oauth_token=" + mAccessToken;
					
					Log.d(TAG, "Opening URL " + url.toString());
					
					String response		= HttpHelper.loadByUrl(url);
					JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		       
					JSONObject resp		= (JSONObject) jsonObj.get("response");
					JSONObject user		= (JSONObject) resp.get("user");
					
					String firstName 	= user.getString("firstName");
		        	String lastName		= user.getString("lastName");
		        
		        	Log.i(TAG, "Got user name: " + firstName + " " + lastName);
		        	
		        	mSession.storeAccessToken(mAccessToken, firstName + " " + lastName);
				} catch (Exception ex) {
					what = 1;
					
					ex.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				if (msg.what == 0) {
					fetchUserName();
				} else {
					mProgress.dismiss();
					
					mListener.onFail("Failed to get access token");
				}
			} else {
				mProgress.dismiss();
				
				mListener.onSuccess();
			}
		}
	};
	
	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}
	
	public void setListener(FsqAuthListener listener) {
		mListener = listener;
	}
	
	public String getUserName() {
		return mSession.getUsername();
	}
	
	public void authorize(Context context) 
	{
		mProgress		= new ProgressDialog(context);
		
		mProgress.setCancelable(false);
		FsqDialogListener listener = new FsqDialogListener() {
			@Override
			public void onComplete(String code) {
				loadAccessToken(code);
			}
			
			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};
		FoursquareDialog mDialog			= new FoursquareDialog(context, url, listener);
		mDialog.show();
	}
	
	public interface FsqAuthListener {
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}

	public String getAccesToken()
	{
		if (hasAccessToken())
			return mAccessToken;
		else return null;
	}
}