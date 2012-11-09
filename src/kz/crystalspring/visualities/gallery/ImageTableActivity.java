package kz.crystalspring.visualities.gallery;

import kz.com.pack.jam.R;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.views.LoadingImageView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class ImageTableActivity extends Activity {
	LinearLayout mainLayout;
	private static final int PHOTOS_IN_ROW = 3;
	private Integer filledItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagetable_page);
		mainLayout = (LinearLayout) findViewById(R.id.main_layout);
	}

	@Override
	public void onResume() {
		super.onResume();
		ImageContainer item = MainApplication.getSelectedImageContainer();
		if (filledItem == null || !filledItem.equals(item.hashCode()))
		{
			fillLayout(item);
		}
	}

	private void fillLayout(ImageContainer item) 
	{
		mainLayout.removeAllViews();
		LinearLayout horizLayout = null;
		for (int i = 0; i < item.getPhotosCount(); i++) {
			if (i % PHOTOS_IN_ROW == 0)
				horizLayout = getNewHorizontalLayout();
			LoadingImageView iv = new LoadingImageView(this);
			fillLoadingImageView(iv, item.getUrlAndPhoto(i),i);
			horizLayout.addView(iv);
			filledItem = item.hashCode();
		}
	}

	private void fillLoadingImageView(LoadingImageView iv,
			final UrlDrawable urlAndPhoto, final int position) {
		int w = Math.round(80 * MainApplication.mDensity);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, w);
		lp.weight = 1;
		iv.setLayoutParams(lp);
		iv.setBackgroundColor(getResources().getColor(R.color.white));
		OnClickListener listner=new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainApplication.selectedItemPhoto = urlAndPhoto;
				Intent intent = new Intent(getBaseContext(),
						GalleryActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getBaseContext().startActivity(intent);
				GalleryActivity.selectedImage=position;
			}
		};
		if (urlAndPhoto.getSmallDrawable() == null) {
			FSQConnector.loadImageAsync(iv, urlAndPhoto, urlAndPhoto.SMALL_URL,
					false,listner);
		} else {
			iv.setDrawable(urlAndPhoto.getSmallDrawable());
			iv.setOnClickListener(listner);
		}
	}

	LinearLayout.LayoutParams lp = null;

	private LinearLayout.LayoutParams getStandartLayoutParams() 
	{
		if (lp == null) {
			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			int margin = Math.round(4 * MainApplication.mDensity);
			lp.bottomMargin = margin;
			lp.topMargin = margin;
		}
		return lp;
	}

	private LinearLayout getNewHorizontalLayout() {
		LinearLayout horizLayout = new LinearLayout(this);
		horizLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lp = getStandartLayoutParams();
		horizLayout.setLayoutParams(lp);
		mainLayout.addView(horizLayout);
		return horizLayout;
	}
}

