package kz.crystalspring.funpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import kz.com.pack.jam.R;
import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.MapItem;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ActivityController
{
	protected MapItem mapItem;
	protected AsyncTask currentTask;
	protected FragmentActivity context;

	protected ActivityController(FragmentActivity _context)
	{
		context = _context;
	}

	protected abstract void onCreate();

	protected void onResume()
	{
		mapItem = (MapItem) MainApplication.getMapItemContainer().getSelectedItem();
	}

	protected abstract void onPause();

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			exit();
		}
		return true;
	}

	private void exit()
	{
		context.finish();
		onExit();
	}

	public void checkInHere()
	{
		Intent intent = new Intent(context, WriteCommentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("requestCode", WriteCommentActivity.CHECKIN_MODE);
		context.startActivity(intent);
		// MainApplication.socialConnector.shareCheckinOnTwitter(itemFood);
	}

	public void checkToDo()
	{
		FSQConnector.addToTodos(mapItem.getId());
		setStateTodo();
	}

	public void goToMap()
	{
		MainApplication.getMapItemContainer().setSelectedItem(mapItem);
		// MainMenu.goToObjectMap();
		Intent intent = new Intent(context, funMap.class);
		context.startActivity(intent);
		// exit();
	}

	public void openAddCommentActivity()
	{
		if (MainApplication.FsqApp.hasAccessToken())
		{
			Intent intent = new Intent(context, WriteCommentActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("requestCode", WriteCommentActivity.COMMENT_MODE);
			context.startActivity(intent);
		}
		else
		{
			showNeedLogin();
		}
	}

	public void onExit()
	{
		if (currentTask != null && !currentTask.isCancelled())
			currentTask.cancel(false);
	}

	public abstract void setStateTodo();

	public abstract void setStateChecked();

	protected void showNeedLogin()
	{
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.need_to_login);
		dialog.setTitle("Необходимо залогиниться");
		dialog.setCancelable(true);
		// set up button
		Button loginButton = (Button) dialog.findViewById(R.id.ok_button);
		loginButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, ProfilePage.class);
				context.startActivity(intent);
				dialog.cancel();
			}
		});

		Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.cancel();
			}
		});
		dialog.show();
	}

}