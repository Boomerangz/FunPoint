package kz.crystalspring.funpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.R;
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

	ActivityController(Activity _context)
	{
		context = _context;
	}

	protected abstract void onCreate();
	
	protected abstract void onResume();

	protected abstract void onPause();

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// if (buttonsShowed)
			// {
			// buttonsShowed = false;
			// ProjectAnimation.hideLeft(btnFeedButtons);
			// objDetAllInfo.startAnimation(fadeInAnimationRight);
			// return true;
			// } else
			// {
			MainMenu.tabHost.setCurrentTab(Integer.valueOf(Prefs
					.getInitTab(context.getApplicationContext())));
			return true;
			// }
		}
		return context.onKeyDown(keyCode, event);
	}

	
}