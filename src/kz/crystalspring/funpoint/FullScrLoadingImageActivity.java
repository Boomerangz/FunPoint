package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.OptionalInfo.UrlDrawable;
import kz.crystalspring.visualities.LoadingImageView;
import kz.crystalspring.visualities.TouchImageView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

public class FullScrLoadingImageActivity extends Activity
{
	TouchImageView iv;
	ProgressBar progressbar;
	UrlDrawable urlDr;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_scr_loading_image);
		iv = (TouchImageView) findViewById(R.id.photo);
		progressbar = (ProgressBar) findViewById(R.id.progress_bar);
		urlDr = MainApplication.selectedItemPhoto;
		if (urlDr != null)
		{
			if (urlDr.getBigDrawable() == null)
			// FSQConnector.loadImageAsync(iv, urlDr, UrlDrawable.BIG_URL,true);

			{
				Runnable task = new Runnable()
				{
					@Override
					public void run()
					{
						Drawable dr = FSQConnector
								.loadPictureByUrl(urlDr.bigUrl);
						urlDr.setBigDrawable(dr);
					}
				};

				Runnable postTask = new Runnable()
				{
					@Override
					public void run()
					{
						tuneImage();
					}
				};

				MainApplication.pwAggregator.addPriorityTask(task, postTask);
			} else
				tuneImage();
		}
	}

	private void tuneImage()
	{
		progressbar.setVisibility(View.GONE);
		Drawable dr=urlDr.getBigDrawable();
		iv.setImageDrawable(dr);
		iv.setVisibility(View.VISIBLE);
	}
}
