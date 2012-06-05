package com.boomerang.metromenu;

import java.util.ArrayList;
import java.util.List;

import kz.crystalspring.funpoint.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

public class TextImageSwitcher extends FrameLayout implements ViewFactory
{
	TextView text;
	ImageSwitcher switcher;
	List<SwitcherDesc> ImageSource;
	static final int durationTime = 900;
	int currImage = -1;
	Handler mHandler=new Handler();

	public TextImageSwitcher(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public TextImageSwitcher(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public TextImageSwitcher(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.text_image_switcher, this, true);

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

	public void updateImage()
	{
		switcher.setImageDrawable(getNextImage());
		mHandler.removeCallbacks(mUpdateTextTask);
		mHandler.postDelayed(mUpdateTextTask, (int)(durationTime*0.7));
	}

	private Drawable getNextImage()
	{
		currImage++;
		if (currImage == ImageSource.size())
			currImage = 0;
		return getContext().getResources().getDrawable(
				ImageSource.get(currImage).source);
	}
	
	private void updateText()
	{
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		for (Integer rule:ImageSource.get(currImage).rules)
			params.addRule(rule);
		params.leftMargin=5;
		params.rightMargin=5;
		text.setLayoutParams(params);
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
	
	private Runnable mUpdateTextTask = new Runnable()
	{
		public void run()
		{
			updateText();
		}
	};
}


class SwitcherDesc
{
	Integer source;
	ArrayList<Integer> rules;
	SwitcherDesc(int imSource,int... args)
	{
		source=new Integer(imSource);
		rules=new ArrayList<Integer>();
		for (int n:args)
			rules.add(n);
	}
	
}
