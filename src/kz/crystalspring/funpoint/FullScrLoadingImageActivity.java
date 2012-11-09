package kz.crystalspring.funpoint;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.LoadingImageView;
import kz.crystalspring.views.TouchImageView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

public class FullScrLoadingImageActivity extends Activity {
	WebView iv;
	ProgressBar progressbar;
	UrlDrawable urlDr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_scr_loading_image);
		iv = (WebView) findViewById(R.id.photo);
		progressbar = (ProgressBar) findViewById(R.id.progress_bar);
		urlDr = MainApplication.selectedItemPhoto;
		if (urlDr != null) {
			// if (urlDr.getBigDrawable() == null)
			// // FSQConnector.loadImageAsync(iv, urlDr,
			// UrlDrawable.BIG_URL,true);
			//
			// {
			// Runnable task = new Runnable()
			// {
			// @Override
			// public void run()
			// {
			// Drawable dr = HttpHelper
			// .loadPictureByUrl(urlDr.bigUrl);
			// urlDr.setBigDrawable(dr);
			// }
			// };
			//
			// Runnable postTask = new Runnable()
			// {
			// @Override
			// public void run()
			// {
			// tuneImage();
			// }
			// };
			//
			// MainApplication.pwAggregator.addPriorityTask(task, postTask);
			// } else
			tuneImage();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void tuneImage() {
		progressbar.setVisibility(View.GONE);
		// Drawable dr=urlDr.getBigDrawable();
		// iv.setImageDrawable(dr);
		String imageUrl = urlDr.bigUrl;

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		long width = Math.round(metrics.widthPixels / MainApplication.mDensity
				* 0.9);

		final String mimeType = "text/html";
		final String encoding = "utf-8";
		final String html = "<center>"
				+ "<p><img src=\"file:///android_asset/icon_set_circle.gif\" name=\"myImage\"/></p></center>"
				+ "<script type=\"text/javascript\"> "
				+ "hiddenImg= new Image(); "
				+ "hiddenImg.src= \""+imageUrl+"\"; "
				+ "document.myImage.src= hiddenImg.src; " 
				+ "document.myImage.width= "+width+"; "
				+ "	</script>";

		iv.getSettings().setBuiltInZoomControls(true);
		iv.getSettings().setJavaScriptEnabled(true);
		iv.loadDataWithBaseURL("fake://not/needed", html, mimeType, encoding,"");
		iv.setVisibility(View.VISIBLE);
	}
}
