package kz.crystalspring.visualities;

import com.boomerang.jam_menu.JamMenuActivity;
import com.boomerang.metromenu.MetromenuActivity;

import kz.crystalspring.android_client.C_DBHelper;
import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.android_client.C_Log;
import kz.crystalspring.android_client.C_NetHelper;
import kz.crystalspring.android_client.C_Utils;
import kz.crystalspring.android_client.C_Vars;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class Splash extends Activity
{
	public static Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		context = getApplicationContext();


		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					CheckForFirstStart();
					sleep(1500);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				} finally
				{
					Intent openMainPage = new Intent(Splash.this,
							HomeScreen.class);
					startActivity(openMainPage);
				}
			}
		};
		if (MainApplication.internetConnection!=MainApplication.NO_CONNECTION)
			t.start();
	}

	static final String C_TAG = "CS_MainActivity";

	/**
	 * �������� �� ������ ������ ���� ��� ������ ������, �� ������������
	 * ����������� ������ �� Assets � Documents � �������� ���������� ��
	 * ���������� �� ������
	 */
	private void CheckForFirstStart()
	{
		C_Log.v(3, C_TAG, "CheckForFirstStart - start");
		C_DBHelper dbHelper = new C_DBHelper(context);
		SQLiteDatabase vDb = dbHelper.getWritableDatabase();
		if (vDb == null)
		{
			C_Log.v(0, C_TAG,
					"CheckForFirstStart getWritableDatabase is null! - end");
			return;
		}
		try
		{
			if (!dbHelper.GetVar(vDb, C_Vars.C_VAR_VERSION, "-").equals(
					C_Vars.C_VERSION))
			{
				C_Log.v(1, C_TAG, "CheckForFirstStart: first start");
				C_FileHelper.CopyAssetFiles(context);
				// C_FileHelper.UnzipAssetFiles(fContext,
				// C_Vars.C_ZIP_ASSET_FILES);
				dbHelper.SetVar(vDb, C_Vars.C_VAR_VERSION, C_Vars.C_VERSION);
				dbHelper.SetVar(vDb, C_Vars.C_VAR_SERVICE_STATE, "ON");
				dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_DEVICE, C_Utils
						.GetDeviceInfo().getBytes());
				dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_VERSION,
						C_Vars.C_VERSION.getBytes());
			}
			// dbHelper.AddOutDataRec(vDb, C_Vars.C_INFO_DEVICE_TYPE,
			// "A".getBytes());
			vDb.close();
		} catch (Exception e)
		{
			C_Log.v(0, C_TAG, "CheckForFirstStart err:" + e.getMessage());
		} finally
		{
			dbHelper.close();
		}

		// if (vIsFirstStart) {
		// // ������ �������������:
		// ����� ������ ��������� �������������, ������ ���� ����� �� ��������
		// ������ ������
		// CS_NetHelper.SyncData(fContext, true, false);
		// Intent v = new Intent();
		// v.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -2);
		// setResult(RESULT_OK, v);
		// }

		C_Log.v(3, C_TAG, "CheckForFirstStart - end");
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}