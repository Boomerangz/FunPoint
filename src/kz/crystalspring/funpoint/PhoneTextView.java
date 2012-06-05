package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneTextView extends LinearLayout
{
	TextView text;
	public PhoneTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	public PhoneTextView(Context context)
	{
		super(context);
		init();
	}
	
	public void init()
	{
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.phone_textview, this, true);

		text = (TextView) findViewById(R.id.phone);
	}
	
	public void setPhone(String phone)
	{
		text.setText(phone);
	}
	
	public String getPhone()
	{
		return text.getText().toString();
	}
	

}
