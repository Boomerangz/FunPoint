package kz.crystalspring.funpoint;

import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FoursquareApp.FsqAuthListener;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQTodo;
import kz.crystalspring.funpoint.venues.FSQUser;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.GalleryWrapper;
import kz.crystalspring.views.LoadingImageView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends Activity implements RefreshableMapList
{
	FSQUser user;
	
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
		
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		user=FSQUser.getInstance();
		user.fillIfNot();
		MainApplication.refreshable=this;
		refreshMapItems();
	}

	@Override
	public void refreshMapItems()
	{
		View pg=(View) findViewById(R.id.progress_bar);
		View infLayout= (View) findViewById(R.id.main_info_layout);
		if (user.isFilled())
		{
			TextView usernameTV=(TextView) findViewById(R.id.user_name);
			TextView usermailTV=(TextView) findViewById(R.id.user_email);
			TextView recentScoreTV=(TextView) findViewById(R.id.recent_score);
			TextView maxScoreTV=(TextView) findViewById(R.id.max_score);
			LoadingImageView loadingImage= (LoadingImageView) findViewById(R.id.loading_imageview);
			LinearLayout badgeGallery= (LinearLayout) findViewById(R.id.badge_gallery);
			
			usernameTV.setText(user.getName());
			usermailTV.setText(user.getEmail());
			FSQConnector.loadImageAsync(loadingImage, user.getPhoto(), UrlDrawable.BIG_URL, false, null);
			loadBadgesGallery(badgeGallery);
			recentScoreTV.setText(user.getRecentScore().toString());
			maxScoreTV.setText(user.getMaxScore().toString());
			
			pg.setVisibility(View.GONE);
			infLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			pg.setVisibility(View.VISIBLE);
			infLayout.setVisibility(View.GONE);
		}
	}

	private void loadBadgesGallery(LinearLayout badgeGallery)
	{
		GalleryWrapper galleryWrapper=new GalleryWrapper(this,GalleryWrapper.MODE_BADGES);
		galleryWrapper.addDrawableList(user.getBadgesList());
		badgeGallery.removeAllViews();
		badgeGallery.addView(galleryWrapper.getView());
	}
	
}
