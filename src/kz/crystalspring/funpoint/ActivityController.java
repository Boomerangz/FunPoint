package kz.crystalspring.funpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.animation.ProjectAnimation;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ActivityController
{
	Activity context;
	int condition=NOT_CREATED;
	static final int NOT_CREATED=0;
	static final int CREATED=1;
	static final int FINISHED=2;

	ActivityController(Activity _context)
	{
		context = _context;
	}

	protected abstract void onCreate();
	
	protected abstract void onResume();

	protected abstract void onPause();

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		condition=FINISHED;
		context.finish();
		return true;
	}

	
}