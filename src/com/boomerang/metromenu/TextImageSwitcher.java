package com.boomerang.metromenu;

import java.util.List;

import kz.crystalspring.pointplus.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class TextImageSwitcher extends FrameLayout implements ViewFactory {
	TextView text;
	ImageSwitcher switcher;
	List<Integer> ImageSource;
	int currImage = -1;

	public TextImageSwitcher(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextImageSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextImageSwitcher(Context context) {
		super(context);
		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.text_image_switcher, this, true);

		text = (TextView) findViewById(R.id.text);
		switcher = (ImageSwitcher) findViewById(R.id.Switcher);
		switcher.setFactory(this);
		switcher.setInAnimation(AnimationUtils.loadAnimation(getContext(),
				android.R.anim.slide_in_left));
		switcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
				android.R.anim.slide_out_right));
	}

	public void updateImage() {
		switcher.setImageDrawable(getNextImage());
	}

	private Drawable getNextImage() {
		currImage++;
		if (currImage == ImageSource.size())
			currImage = 0;
		return getContext().getResources().getDrawable(
				ImageSource.get(currImage));
	}

	@Override
	public View makeView() {
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
