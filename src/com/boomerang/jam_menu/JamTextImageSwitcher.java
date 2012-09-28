package com.boomerang.jam_menu;

import java.util.ArrayList;
import java.util.List;

import com.nikkoaiello.mobile.android.PinchImageView;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class JamTextImageSwitcher extends FrameLayout implements ViewFactory
{
	TextView text;
	ImageSwitcher switcher;
	private List<SwitcherDescription> ImageSource;
	Drawable[] drawableArray;
	public static final int durationTime = 900;
	int currImage = -1;
	Handler mHandler=new Handler();
	
	public JamTextImageSwitcher(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public JamTextImageSwitcher(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public JamTextImageSwitcher(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.jam_menu_item, this, true);

		text = (TextView) findViewById(R.id.text);
		switcher = (ImageSwitcher) findViewById(R.id.Switcher);
		switcher.setFactory(this);

		Animation inAnimation = AnimationUtils.loadAnimation(getContext(),
				android.R.anim.fade_in);
		Animation outAnimation = AnimationUtils.loadAnimation(getContext(),
				android.R.anim.fade_out);
		inAnimation.setDuration(durationTime);
		outAnimation.setDuration(durationTime);
		switcher.setInAnimation(inAnimation);
		switcher.setOutAnimation(outAnimation);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		int width=(getWidth());
		getLayoutParams().height=width;
	}
	
	public void setImageSource(List<SwitcherDescription> imageSource)
	{
		ImageSource = imageSource;
		drawableArray=new Drawable[ImageSource.size()];
	}

//	public void setTextBackground(int color)
//	{
//		View textLayout=findViewById(R.id.text_background);
//		textLayout.setBackgroundColor(color);
//	}

	public void updateImage()
	{
		switcher.setImageDrawable(getNextImage());
		System.gc();
		//switcher.setImageResource(getNextImageId());
	}

	private Drawable getNextImage()
	{
		currImage++;
		if (currImage == ImageSource.size())
			currImage = 0;
//		if (drawableArray[currImage]==null)
//		{
//			Drawable drw;
////			Bitmap btm=BitmapFactory.decodeResource(getContext().getResources(), ImageSource.get(currImage).source);
////			if (btm!=null)
////			{
////			//	btm.setDensity(Math.round(MainApplication.mDensity/2));
////				drw=new BitmapDrawable(btm);
////			}
////			else
////			{
//				drw=getContext().getResources().getDrawable(
//						ImageSource.get(currImage).source);
////			}
//			drawableArray[currImage]=drw;
//		}
//		Drawable drw=drawableArray[currImage];
		return getContext().getResources().getDrawable(
				ImageSource.get(currImage).source);
	}
	
	private int getNextImageId()
	{
		currImage++;
		if (currImage == ImageSource.size())
			currImage = 0;
		int imageSourceInt=ImageSource.get(currImage).source;
		return imageSourceInt;
	}
	

	@Override
	public View makeView()
	{
		ImageView iView = new ImageView(getContext());
		iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iView.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		iView.setBackgroundColor(0xFF000000);
		return iView;
	}

	public void setText(String text)
	{
		this.text.setText(text);
	}
	
}


