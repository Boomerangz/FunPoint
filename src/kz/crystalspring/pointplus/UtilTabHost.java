package kz.crystalspring.pointplus;


import kz.crystalspring.funpoint.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

/**
 * Acts as an interface to the TabSpec class for setting the content view. 
 * 
 * @date March 14, 2012
 * @author Sergey Beyer (sergey.beyer@gmail.com), crystalspring.
 */ 
public abstract class UtilTabHost {
    
    public static void addTab(TabHost host, int drawable, int index, int layout) {
        TabHost.TabSpec spec = host.newTabSpec("tab" + index);
        spec.setContent(layout);
        View view = prepareTabView(host.getContext(), drawable);
    	spec.setIndicator(view);
        host.addTab(spec);
    }
    
    public static TabHost.TabSpec addTab(TabHost host, int drawable, int index, Intent intent) {
        TabHost.TabSpec spec = host.newTabSpec("tab" + index);
        spec.setContent(intent);
        View view;
        //if(index == 3){
        //	view = prepareRoundTabView(host.getContext(), drawable);
        //}else{
        	view = prepareTabView(host.getContext(), drawable);
        //}
        spec.setIndicator(view);
        host.addTab(spec);
        return spec;
    }
    
    
    private static View prepareTabView(Context context, int drawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_main, null);
        ImageView iv = (ImageView) view.findViewById(R.id.ivTabIcon);
        iv.setImageResource(drawable);
        return view;
    }
    
    private static View prepareRoundTabView(Context context, int drawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_main_round, null);
        ImageView iv = (ImageView) view.findViewById(R.id.ivTabIcon);
        iv.setImageResource(drawable);
        return view;
    }
}