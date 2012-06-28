package kz.crystalspring.funpoint;

import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FoursquareApp.FsqAuthListener;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQTodo;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_page);
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setText("Log In");
		final TextView text1 = (TextView) findViewById(R.id.textView1);
		final FoursquareApp mFsqApp = MainApplication.FsqApp;
		ProgressDialog mProgress = new ProgressDialog(this);

		mProgress.setMessage("Loading data ...");

		if (mFsqApp.hasAccessToken())
			text1.setText("Connected as " + mFsqApp.getUserName());

		FsqAuthListener listener = new FsqAuthListener()
		{
			@Override
			public void onSuccess()
			{
				Toast.makeText(ProfilePage.this,
						"Connected as " + mFsqApp.getUserName(),
						Toast.LENGTH_SHORT).show();
				text1.setText("Connected as " + mFsqApp.getUserName());
			}

			@Override
			public void onFail(String error)
			{
				Toast.makeText(ProfilePage.this, error, Toast.LENGTH_SHORT)
						.show();
			}
		};

		mFsqApp.setListener(listener);
		button1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mFsqApp.authorize(ProfilePage.this);
			}
		});

		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		if (FSQConnector.getBadgessLoaded())
			iv.setImageDrawable(HttpHelper.loadPictureByUrl(FSQConnector
					.getBadgesList().get(0).getPictureMiddleURL()));

	}
}
