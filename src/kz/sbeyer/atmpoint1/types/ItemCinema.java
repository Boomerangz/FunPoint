package kz.sbeyer.atmpoint1.types;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarOutputStream;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.funpoint.venues.MapItem.ViewHolder;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemCinema.CinemaTimeLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ItemCinema extends FSQItem 
{


	List<CinemaHall> hallTable;
	boolean hallInfoFilled=false;
	
	public static final String CINEMA_IMG="m_2";
	@Override
	public String getObjTypeId()
	{
		return FSQ_TYPE_CINEMA;
	}
	
	public ItemCinema loadFromJSON(JSONObject jObject)
	{
		super.loadFromJSON(jObject);
		return this;
	}
	
	public ItemCinema loadHallTableFromJSON(JSONArray jArray)
	{
		hallTable=new ArrayList();
		for (int i=0;i<jArray.length();i++)
		{
			try
			{
				JSONObject jObject=jArray.getJSONObject(i);
				CinemaHall hall=new CinemaHall().loadFromJSON(jObject);
				hallTable.add(hall);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this;
	}
	
	public List<CinemaHall> getHallTable()
	{
		return hallTable;
	}
	
	
	
	public class CinemaHall
	{
		String hallName;
		List<CinemaTimeLine> timeTable;
		
		CinemaHall()
		{
			timeTable=new ArrayList();
		}
		
		public void addTimeLine(CinemaTimeLine tl)
		{
			timeTable.add(tl);
		}
		
		public List<CinemaTimeLine> getTimeTable()
		{
			Collections.sort(timeTable);
			return timeTable;
		}
		
		public CinemaHall loadFromJSON(JSONObject jObject) throws JSONException
		{
			timeTable=new ArrayList<CinemaTimeLine>();
			hallName=jObject.getString("hall");
			JSONArray jTimeTable=jObject.getJSONArray("timetable");
			for (int i=0; i<jTimeTable.length(); i++)
			{
				JSONObject jTimeLine=jTimeTable.getJSONObject(i);
				CinemaTimeLine timeLine=new CinemaTimeLine();
				timeLine.film=jTimeLine.getString("mov");
				timeLine.ogran=jTimeLine.getString("ogran");
				timeLine.form=jTimeLine.getString("form");
				timeLine.setDateTime(jTimeLine.getString("time"));
				timeLine.prices[0]=jTimeLine.getString("price1");
				timeLine.prices[1]=jTimeLine.getString("price2");
				timeLine.prices[2]=jTimeLine.getString("price3");
				timeLine.prices[3]=jTimeLine.getString("price4");
				timeTable.add(timeLine);
			}
			hallInfoFilled=true;
			return this;
		}
		
		public String getName()
		{
			return hallName;
		}
	}
	
	public class CinemaTimeLine implements Comparable<CinemaTimeLine>
	{
		int PRICES_NUMBER=4;
		String film;
		java.util.Date datetime;
		String ogran;
		String[] prices=new String[PRICES_NUMBER];
		String form;
		final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		public void setDateTime(String dt)
		{
			try
			{
				datetime=sdf.parse(dt);
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public int compareTo(CinemaTimeLine arg0)
		{
			return datetime.compareTo(arg0.datetime);
		}	
		
		public String getFilm()
		{
			return film;
		}
		
		public String getTime()
		{
			return datetime.toLocaleString();
		}
	}

	public void setHallInfoNotFilled()
	{
		hallInfoFilled=false;
	}
	
	public boolean isHallInfoFilled()
	{
		return hallInfoFilled;
	}
	
	
	public String getIconName()
	{
		return CINEMA_IMG;
	}
	
	@Override 
	public String toString()
	{
		return getName()+"______"+getId();
	}
	
	
	//@Override 
	public View getView1(View convertView, int position)
	{
		final ViewHolderCinema holder;
    	LayoutInflater mInflater = LayoutInflater.from(context);
    	if (convertView == null||convertView.getTag().getClass()!=ViewHolderCinema.class) 
        {
            convertView = mInflater.inflate(R.layout.object_list_item_cinema, null);
            holder = new ViewHolderCinema();
            holder.name1 = (TextView) convertView.findViewById(R.id.name1);
            holder.switcher = (ViewSwitcher) convertView.findViewById(R.id.switcher);
            holder.cancelButton = (Button) convertView.findViewById(R.id.cancelButton);
            holder.okButton = (Button) convertView.findViewById(R.id.okButton);
            convertView.setMinimumHeight(60);
            convertView.setTag(holder);
        } 
    	else 
    	{
    		holder = (ViewHolderCinema) convertView.getTag();
    	}
        
        String st=Integer.toString(-position)+". "+toString();
        if (MainApplication.getCurrentLocation()!=null)
        	st+="   "+Float.toString(distanceTo(MainApplication.getCurrentLocation()));
        holder.name1.setText(st);
        
        
        holder.name1.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				holder.switcher.showNext();
			}
		});
        
        holder.cancelButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				holder.switcher.showPrevious();
			}
		});
        
        return convertView;
    }
	
	 public static class ViewHolderCinema 
	 {
	        public TextView name1;
	        public ViewSwitcher switcher;
	        public Button okButton;
	        public Button cancelButton;
	 }
}


class MyScaler extends ScaleAnimation {

    private View mView;
    private View secondView;

    private LinearLayout.LayoutParams mLayoutParams;

    private int mMarginBottomFromY, mMarginBottomToY;

    private boolean mVanishAfter = false;

    public MyScaler(float fromX, float toX, float fromY, float toY, int duration, View view, View secondView,
            boolean vanishAfter) {
        super(fromX, toX, fromY, toY);
        setDuration(duration);
        mView = view;
        this.secondView = secondView;
        
        mVanishAfter = vanishAfter;
        mLayoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        int height = mView.getHeight();
        mMarginBottomFromY = (int) (height * fromY) + mLayoutParams.bottomMargin - height;
        mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin)) - height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            int newMarginBottom = mMarginBottomFromY
                    + (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
            mLayoutParams.setMargins(mLayoutParams.leftMargin, mLayoutParams.topMargin,
                mLayoutParams.rightMargin, newMarginBottom);
            mView.getParent().requestLayout();
        } else if (mVanishAfter) {
            mView.setVisibility(View.GONE);
            secondView.setVisibility(View.VISIBLE);
        }
    }

}
