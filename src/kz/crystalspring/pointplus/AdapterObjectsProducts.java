package kz.crystalspring.pointplus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemVisible;

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

public class AdapterObjectsProducts extends BaseAdapter{
	
	ArrayList<Object> objectsVisibleList;
 
    public Activity context;
    public LayoutInflater inflater;
    
    private Context mainContext;
    float coef_dp;
 
    public AdapterObjectsProducts(Activity context,ArrayList<Object> objectsVisibleList) {
        super();
 
        this.context = context;
        this.objectsVisibleList = objectsVisibleList;
        
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return objectsVisibleList.size();
    }
 
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return objectsVisibleList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
   
    public static class ViewHolder
    {
        ImageView imgViewLogo;
        TextView txtViewTitleMain;
        TextView txtViewTitle;
        TextView txtViewDescription;
        TextView txtViewDistance;
        RelativeLayout leftSide;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
 
        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.items, null);

            holder.leftSide = (RelativeLayout) convertView.findViewById(R.id.leftSide);
            holder.imgViewLogo = (ImageView) convertView.findViewById(R.id.imgViewLogo);
            holder.txtViewTitleMain = (TextView) convertView.findViewById(R.id.txtViewTitleMain);
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.txtViewDescription);
            holder.txtViewDistance = (TextView) convertView.findViewById(R.id.txtViewDistance);
 
            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();
 
        ItemVisible bean = (ItemVisible) objectsVisibleList.get(position);
        
        InputStream ims = null; 
        Bitmap image = null;

        String imgSrcStr = bean.getBankId()+"_b";
        if(bean.getObjTypeId() == 3){
        	imgSrcStr = "exchange";
        }else if(bean.getObjTypeId() == 4){
        	imgSrcStr = "notarius";
        }else{
            imgSrcStr = bean.getBankId()+"_b";
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
        holder.imgViewLogo.setImageBitmap(image);
        holder.txtViewTitle.setText(bean.getAddress());
        String descrStr = "ID "+bean.getId();
        if(bean.getObjTypeId() == 1 ){
            if(bean.getAlias().length() > 0 && bean.getAlias()!="NULL"){
            	descrStr = descrStr+", "+bean.getAlias();
            }
        }else if(bean.getObjTypeId() == 2){
            if(bean.getObjName().length() > 0 && bean.getObjName()!="NULL"){
            	descrStr = descrStr+", "+bean.getObjName();
            }
        }else if(bean.getObjTypeId() == 3){
            if(bean.getObjName().length() > 0 && bean.getObjName()!="NULL"){
            	descrStr = descrStr+", "+bean.getObjName();
            }
        }else if(bean.getObjTypeId() == 4){
            if(bean.getObjName().length() > 0 && bean.getObjName()!="NULL"){
            	descrStr = descrStr+", "+bean.getObjName();
            }
        }
        holder.txtViewTitleMain.setText(descrStr);
        //ERROR TEXT
        //holder.txtViewDescription.setText(descrStr);
        
        holder.leftSide.setTag(position);
        holder.txtViewDistance.setText((String.format("%,d",bean.getDist())).replace(',', ' ')+" m");
 
        return convertView;
    }
 
}
