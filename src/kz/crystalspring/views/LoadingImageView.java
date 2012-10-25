package kz.crystalspring.views;

import kz.crystalspring.funpoint.R;
import android.content.Context;
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
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.loading_image, this, true);
		image = (ImageView) findViewById(R.id.image);
		progressBar = (View) findViewById(R.id.progress_bar);
	}

	public void setDrawable(Drawable drawable)
	{
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

	public void onClick()
	{

	}

	public boolean hasDrawable()
	{
		return (image.getVisibility()==View.VISIBLE);
	}

}
