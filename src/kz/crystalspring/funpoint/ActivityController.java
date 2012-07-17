package kz.crystalspring.funpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ActivityController
{
	protected FragmentActivity context;

	protected ActivityController(FragmentActivity _context)
	{
		context = _context;
	}

	protected abstract void onCreate();
	
	protected abstract void onResume();

	protected abstract void onPause();

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		context.finish();
		return true;
	}

	
}