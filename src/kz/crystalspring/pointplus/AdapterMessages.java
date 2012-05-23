package kz.crystalspring.pointplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.R;
import kz.sbeyer.atmpoint1.types.ItemMessage;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterMessages extends BaseExpandableListAdapter{
	/*
	ArrayList<Object> messagesList;
 
    public Activity context;
    public LayoutInflater inflater;
    private Context mainContext;
    float coef_dp;
 
    public AdapterMessages(Activity context,ArrayList<Object> objectsVisibleList) {
        super();
 
        this.context = context;
        this.messagesList = objectsVisibleList;
        
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messagesList.size();
    }
 
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return messagesList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
   
    public static class ViewHolder
    {
    	ImageView msgImg;
    	TextView msgDatetime;
    	TextView msgText;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
 
        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.message_item, null);
            
            holder.msgImg = (ImageView) convertView.findViewById(R.id.msgImg);
            holder.msgDatetime = (TextView) convertView.findViewById(R.id.msgDatetime);
            holder.msgText = (TextView) convertView.findViewById(R.id.msgText);
 
            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();
 
        ItemMessage bean = (ItemMessage) messagesList.get(position);
        

        InputStream ims = null; 
        Bitmap image = null;
        String imgSrcStr = bean.getMsgimg();
        
        mainContext = MainMenu.context;
        
        AssetManager assetManager = mainContext.getAssets();
        coef_dp = mainContext.getResources().getDisplayMetrics().density;
    	
		try {
			ims = assetManager.open(imgSrcStr+".png");
			image = BitmapFactory.decodeStream(ims);
			image.setDensity((int) (image.getDensity()/coef_dp));
			
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

        holder.msgImg.setImageBitmap(image);
        //holder.msgImg.setVisibility(View.GONE);
        holder.msgDatetime.setText(bean.getDate());
        holder.msgText.setText(Html.fromHtml(bean.getMsgtxt()));
        
        /*holder.msgText.setText(
                Html.fromHtml(
                    "<b>text3:</b>  Text with a " +
                    "<a href=\"http://www.google.com\">lin11111111k</a> " +
                    "created in the Java source code using HTML."));*/
       /* holder.msgText.setMovementMethod(LinkMovementMethod.getInstance());
 
        return convertView;
    }*/
	
	// Sample data set. children[i] contains the children (String[]) for
	// groups[i].
	private String[] groups =
	{ "People Names", "Dog Names", "Cat Names", "Fish Names" };
	private String[][] children =
	{
	{ "Arnold", "Barry", "Chuck", "David" },
	{ "Ace", "Bandit", "Cha-Cha", "Deuce" },
	{ "Fluffy", "Snuggles" },
	{ "Goldy", "Bubbles" } };
	ArrayList<Object> messagesList;
	public Activity context;
	public LayoutInflater inflater;
	private Context mainContext;
	float coef_dp;

	public AdapterMessages(Activity context,
			ArrayList<Object> objectsVisibleList)
	{
		super();

		this.context = context;
		this.messagesList = objectsVisibleList;

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return children[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return 1;
	}

	public View getGenericView(String text)
	{
		// Layout parameters for the ExpandableListView
		float density = context.getResources().getDisplayMetrics().density;
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(lp);
		TextView textView = new TextView(context);
		textView.setLayoutParams(llp);
		textView.setTextColor(context.getResources().getColor(R.color.white));
		textView.setTextSize(10*density);
		
		
		ll.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.blackbganim));
		ll.setPadding((int) (50 * density), (int) (10 * density),
				(int) (10 * density), (int) (10 * density));
		// Center the text vertically
		// Set the text starting position
		textView.setText(Html.fromHtml(text));
		ll.addView(textView);
		return ll;
	}

	public static class ViewHolder
	{
		ImageView msgImg;
		TextView msgDatetime;
		TextView msgText;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.message_item, null);

			holder.msgImg = (ImageView) convertView.findViewById(R.id.msgImg);
			holder.msgDatetime = (TextView) convertView
					.findViewById(R.id.msgDatetime);
			holder.msgText = (TextView) convertView.findViewById(R.id.msgText);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		ItemMessage bean = (ItemMessage) messagesList.get(groupPosition);

		InputStream ims = null;
		Bitmap image = null;
		String imgSrcStr = bean.getMsgimg();

		mainContext = kz.crystalspring.funpoint.MainApplication.context;

		AssetManager assetManager = mainContext.getAssets();
		coef_dp = mainContext.getResources().getDisplayMetrics().density;

		byte[] vIconBytes;
		try
		{
			vIconBytes = C_FileHelper.ReadFile(new File(MainApplication.context.getFilesDir() + "/" + imgSrcStr + ".png"));
			image = BitmapFactory.decodeByteArray(vIconBytes, 0, vIconBytes.length);
			//image = BitmapFactory.decodeStream(ims);
			image.setDensity((int) (image.getDensity() / coef_dp));

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.msgImg.setImageBitmap(image);
		// holder.msgImg.setVisibility(View.GONE);
		holder.msgDatetime.setText(bean.getDate());
		holder.msgText.setText(Html.fromHtml(bean.getMsgtxt()));

		// holder.msgText.setText(
		// Html.fromHtml(
		// "<b>text3:</b>  Text with a " +
		// "<a href=\"http://www.google.com\">lin11111111k</a> " +
		// "created in the Java source code using HTML."));
		holder.msgText.setMovementMethod(LinkMovementMethod.getInstance());

		return convertView;
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return messagesList.get(groupPosition);
	}

	@Override
	public int getGroupCount()
	{
		return messagesList.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		View view = getGenericView(((ItemMessage) messagesList
				.get(groupPosition)).getMsgtxt().substring(0, 18) + "...");
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

 
}
