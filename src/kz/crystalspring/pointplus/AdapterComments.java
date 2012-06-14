package kz.crystalspring.pointplus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemComment;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdapterComments extends BaseAdapter{
	
	ArrayList<Object> commentsList;
 
    public Activity context;
    public LayoutInflater inflater;
    
    private Context mainContext;
    float coef_dp;
 
    public AdapterComments(Activity context,ArrayList<Object> objectsVisibleList) {
        super();
 
        this.context = context;
        this.commentsList = objectsVisibleList;
        
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return commentsList.size();
    }
 
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return commentsList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
   
    public static class ViewHolder
    {
    	ImageView cmntImg;
    	TextView cmntUsrname;
    	TextView cmntDatetime;
    	TextView cmntText;
        RelativeLayout leftSide;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
 
        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
//            convertView = inflater.inflate(R.layout.comment_item, null);
//
//            holder.leftSide = (RelativeLayout) convertView.findViewById(R.id.leftSideCmnt);
//            holder.cmntImg = (ImageView) convertView.findViewById(R.id.cmntImg);
//            holder.cmntUsrname = (TextView) convertView.findViewById(R.id.cmntUsrname);
//            holder.cmntDatetime = (TextView) convertView.findViewById(R.id.cmntDatetime);
//            holder.cmntText = (TextView) convertView.findViewById(R.id.cmntText);
 
            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();
 
        ItemComment bean = (ItemComment) commentsList.get(position);

        InputStream ims = null; 
        Bitmap image = null;

        String imgSrcStr = bean.getBankid()+"_b";
        if(String.valueOf(bean.getObjType()).contentEquals("3")){
        	imgSrcStr = "exchange";
        }else if(String.valueOf(bean.getObjType()).contentEquals("4")){
        	imgSrcStr = "notarius";
        }else{
            imgSrcStr = bean.getBankid()+"_b";
        }

        mainContext = ObjectsListView.context;
        
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
        holder.cmntImg.setImageBitmap(image);
        
        
        String cmntText = "";
        
        cmntText = bean.getObjTypeTitle() + " ( ID "+bean.getObjId() + "). \n";
        cmntText = cmntText.toLowerCase();
        
        if(bean.getCmntType().equalsIgnoreCase("2")){
        	cmntText += bean.getCmntText().toUpperCase();
        }else if(bean.getCmntType().equalsIgnoreCase("1")){
        	cmntText += bean.getErrBtnId().toUpperCase();
        }else{
        	cmntText += bean.getCmntText().toUpperCase();
        }
        holder.cmntUsrname.setText(bean.getCmntUsrname());
        holder.cmntDatetime.setText(bean.getCmntDatetime());
        holder.leftSide.setTag(position);
        holder.cmntText.setText(cmntText);
 
        return convertView;
    }
 
}
