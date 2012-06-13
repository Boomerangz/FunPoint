package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import kz.crystalspring.visualities.LoadingImageView;
import android.app.Activity;
import android.os.Bundle;

public class FullScrLoadingImageActivity extends Activity
{
	LoadingImageView iv;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_scr_loading_image);
		iv=(LoadingImageView) findViewById(R.id.loading_image);
		UrlDrawable urlDr=MainApplication.selectedItemPhoto;
		if (urlDr!=null)
		{
			FSQConnector.loadImageAsync(iv, urlDr, UrlDrawable.BIG_URL,true);
		}
	}
}
