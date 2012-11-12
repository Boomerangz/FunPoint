package kz.crystalspring.visualities.gallery;

import com.google.analytics.tracking.android.EasyTracker;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.FullScrLoadingImageActivity;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.views.LoadingImageView;
import kz.crystalspring.views.WebViewGallery;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;

public class GalleryActivity extends Activity
{
	LinearLayout mainLayout;
	private static final int PHOTOS_IN_ROW = 3;
	private String filledItem;
	private WebViewGallery galleryView;
	private ImageAdapter adapter;

	public static int selectedImage = -1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_page);
		galleryView = (WebViewGallery) findViewById(R.id.gallery);
		EasyTracker.getInstance().activityStart(this);
		// mainLayout = (LinearLayout) findViewById(R.id.main_layout);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		ImageContainer item = MainApplication.getSelectedImageContainer();
		if (filledItem == null || !(filledItem.hashCode() == item.hashCode()))
		{
			fillLayout(item);
		}
		if (selectedImage > -1)
		{
			if (selectedImage > (galleryView.getAdapter().getCount() - 1))
				selectedImage = 0;
			galleryView.setSelection(selectedImage);
		}
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	private void fillLayout(ImageContainer item)
	{
		galleryView.setAdapter(new ImageAdapter(item, this));
	}

	private void fillLayout1(FSQItem item)
	{
		mainLayout.removeAllViews();
		LinearLayout horizLayout = null;
		for (int i = 0; i < item.getPhotosCount(); i++)
		{
			if (i % PHOTOS_IN_ROW == 0)
				horizLayout = getNewHorizontalLayout();
			LoadingImageView iv = new LoadingImageView(this);
			fillLoadingImageView(iv, item.getUrlAndPhoto(i));
			horizLayout.addView(iv);
			filledItem = item.getId();
		}
	}

	private void fillLoadingImageView(LoadingImageView iv,
			final UrlDrawable urlAndPhoto)
	{
		int w = Math.round(80 * MainApplication.mDensity);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, w);
		lp.weight = 1;
		iv.setLayoutParams(lp);
		OnClickListener listner = new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				MainApplication.selectedItemPhoto = urlAndPhoto;
				Intent intent = new Intent(getBaseContext(),
						FullScrLoadingImageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getBaseContext().startActivity(intent);
			}
		};
		if (urlAndPhoto.getSmallDrawable() == null)
		{
			FSQConnector.loadImageAsync(iv, urlAndPhoto, urlAndPhoto.SMALL_URL,
					false, listner);
		} else
		{
			iv.setDrawable(urlAndPhoto.getSmallDrawable());
			iv.setOnClickListener(listner);
		}
	}

	LinearLayout.LayoutParams lp = null;

	private LinearLayout.LayoutParams getStandartLayoutParams()
	{
		if (lp == null)
		{
			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			int margin = Math.round(4 * MainApplication.mDensity);
			lp.bottomMargin = margin;
			lp.topMargin = margin;
		}
		return lp;
	}

	private LinearLayout getNewHorizontalLayout()
	{
		LinearLayout horizLayout = new LinearLayout(this);
		horizLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lp = getStandartLayoutParams();
		horizLayout.setLayoutParams(lp);
		mainLayout.addView(horizLayout);
		return horizLayout;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		selectedImage = -1;
	}
}

@SuppressLint("SetJavaScriptEnabled")
class ImageAdapter extends BaseAdapter
{
	ImageContainer item;
	Activity context;
	Gallery.LayoutParams lp;

	ImageAdapter(ImageContainer item, Activity context)
	{
		this.item = item;
		this.context = context;

		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		long width = Math.round(metrics.widthPixels / MainApplication.mDensity);
		long height = Math.round(metrics.heightPixels
				/ MainApplication.mDensity);
		lp = new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
				Gallery.LayoutParams.FILL_PARENT);
	}

	@Override
	public int getCount()
	{
		return item.getPhotosCount();
	}

	@Override
	public Object getItem(int arg0)
	{
		return item.getUrlAndPhoto(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2)
	{
		if (view == null)
		{
			WebView wv = new WebView(context);
			wv.setLayoutParams(lp);
			UrlDrawable urlDr = (UrlDrawable) getItem(position);
			String imageUrl = urlDr.bigUrl;

			DisplayMetrics metrics = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			long width = Math.round(metrics.widthPixels
					/ MainApplication.mDensity * 0.6);

			final String mimeType = "text/html";
			final String encoding = "utf-8";
			final String html = ""
					+ "<body style=\"width:100%; height:100%;\" >"
					+ "<center >"
					+ "<p><img style=\"margin:10%\" src=\"file:///android_asset/icon_set_circle.gif\" name=\"myImage\"/></p><"
					+ "/center></body>" + "<script type=\"text/javascript\"> "
					+ "hiddenImg= new Image(); " + "hiddenImg.src= \""
					+ imageUrl + "\"; "
					+ "document.myImage.src= hiddenImg.src; "
					+ "document.myImage.width= " + width + "; " + "	</script>";

			wv.getSettings().setBuiltInZoomControls(true);
			wv.getSettings().setJavaScriptEnabled(true);
			wv.loadDataWithBaseURL("fake://not/needed", html, mimeType,
					encoding, "");
			return wv;
		} else
			return view;
	}
}
