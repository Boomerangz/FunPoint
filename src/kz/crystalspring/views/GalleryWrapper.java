package kz.crystalspring.views;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.FullScrLoadingImageActivity;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.visualities.gallery.GalleryActivity;
import kz.crystalspring.visualities.gallery.ImageTableActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class GalleryWrapper
{
	LinearLayout mainLayout;
	Context context;
	List<UrlDrawable> drawList;
	TextView moreButton;
	public static final int MODE_PLACE=999;
	public static final int MODE_BADGES=9999;

	private final static int PHOTOS_COUNT = 3;

	public GalleryWrapper(final Activity context, int mode)
	{
		this.context = context;
		LayoutInflater inflater = context.getLayoutInflater();
		mainLayout = (LinearLayout) inflater.inflate(R.layout.gallery_widget,
				null);
		mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
		drawList = new ArrayList<UrlDrawable>();
		moreButton = (TextView) mainLayout.findViewById(R.id.more_button);
//		if (mode==MODE_BADGES)
//			moreButton.setEnabled(false);
	}

	public void addDrawable(UrlDrawable drawabl)
	{
		drawList.add(drawabl);
		refreshLayout();
	}

	private void refreshLayout()
	{
		LinearLayout layout = (LinearLayout) mainLayout
				.findViewById(R.id.gallery_layout);
		layout.removeAllViews();
		for (int i = 0; i < drawList.size() && i < PHOTOS_COUNT; i++)
		{
			UrlDrawable drw = drawList.get(i);
			View imageView = createImageView(drw, i);
			layout.addView(imageView);
		}
		if (drawList.size() > 0)
		{
			moreButton.setText("Посмотреть все " + drawList.size()
					+ " фотографий");
			moreButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					Intent intent = new Intent(GalleryWrapper.this.context,
							ImageTableActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					GalleryWrapper.this.context.startActivity(intent);
				}
			});
		} else
		{
			moreButton.setText("Фотографий этого места нет");
			moreButton.setOnClickListener(null);
		}
		
	}

	private View createImageView(final UrlDrawable drw, final int position)
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				Math.round(80 * MainApplication.mDensity));
		lp.weight = 1;
		final LoadingImageView iv = new LoadingImageView(context);
		iv.setLayoutParams(lp);
		OnClickListener listner = new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				MainApplication.selectedItemPhoto = drw;
				Intent intent = new Intent(context, GalleryActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				GalleryActivity.selectedImage = position;
				context.startActivity(intent);
			}
		};
		if (drw.getSmallDrawable() != null)
		{
			iv.setDrawable(drw.getSmallDrawable());
		} else
		{
			FSQConnector.loadImageAsync(iv, drw, UrlDrawable.SMALL_URL, false,
					listner);
		}
		iv.setOnClickListener(listner);
		return iv;
	}

	public View getView()
	{
		refreshLayout();
		return mainLayout;
	}

	public void clear()
	{
		drawList.clear();
	}

	public void addDrawableList(List<UrlDrawable> badgesList)
	{
		drawList.addAll(badgesList);
		refreshLayout();
	}
}
