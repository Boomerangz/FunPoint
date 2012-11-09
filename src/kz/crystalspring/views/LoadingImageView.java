package kz.crystalspring.views;

import kz.com.pack.jam.R;
import kz.crystalspring.pointplus.ImageCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoadingImageView extends LinearLayout
{
	private ImageView image;
	private View progressBar;

	public LoadingImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public LoadingImageView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		li.inflate(R.layout.loading_image, this, true);
		image = (ImageView) findViewById(R.id.image);
		progressBar = (View) findViewById(R.id.progress_bar);
	}

	public void setDrawable(Drawable drawable)
	{
		cleanJunk();
		removeFromJunk(drawable);
		if (drawable != null)
		{
			image.setImageDrawable(drawable);
			image.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		} else
		{
			image.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}
		invalidate();
	}

	private void removeFromJunk(Drawable drawable)
	{
		if (BitmapDrawable.class.isInstance(drawable))
		{
			BitmapDrawable btmDrw = (BitmapDrawable) drawable;
			Bitmap btm = btmDrw.getBitmap();
			ImageCache cache = ImageCache.getInstance();
			cache.useBitmap(btm);
		}
	}

	public void cleanJunk()
	{
		if (image.getDrawable() != null)
			if (BitmapDrawable.class.isInstance(image.getDrawable()))
			{
				BitmapDrawable btmDrw = (BitmapDrawable) image.getDrawable();
				Bitmap btm = btmDrw.getBitmap();
				ImageCache cache = ImageCache.getInstance();
				cache.unUse(btm);
			}
	}

	public void onClick()
	{

	}

	public boolean hasDrawable()
	{
		return (image.getVisibility() == View.VISIBLE);
	}

	@Override
	protected void finalize() throws Throwable
	{
		// cleanJunk();
		super.finalize();
	}

}
