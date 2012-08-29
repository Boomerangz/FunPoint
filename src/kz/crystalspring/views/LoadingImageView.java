package kz.crystalspring.views;

import kz.crystalspring.funpoint.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoadingImageView extends LinearLayout
{
	private ImageView image;
	private ProgressBar progressBar;
	
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
		image=(ImageView) findViewById(R.id.image);
		progressBar=(ProgressBar) findViewById(R.id.progress_bar);
	}
	
	public void setDrawable(Drawable drawable)
	{
		image.setImageDrawable(drawable);
		image.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		invalidate();
	}
	
	public void onClick()
	{
		
	}

}
