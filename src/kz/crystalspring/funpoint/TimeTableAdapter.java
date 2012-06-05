package kz.crystalspring.funpoint;

import java.util.List;

import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemCinema.CinemaHall;
import kz.sbeyer.atmpoint1.types.ItemCinema.CinemaTimeLine;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


class TimeTableAdapter extends BaseAdapter 
{
    private LayoutInflater mInflater;
    private List<CinemaTimeLine> data;

    public TimeTableAdapter(Context context, List<CinemaTimeLine> _data) 
    {
    	data=_data;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() 
    {
        return data.size();
    }
    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) 
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	ViewHolder holder;
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.list_item_timetable, null);
            holder = new ViewHolder();
            holder.filmName = (TextView) convertView.findViewById(R.id.film_name);
            holder.filmTime = (TextView) convertView.findViewById(R.id.film_time);
         
           // convertView.setMinimumHeight(60);
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.filmName.setText(data.get(position).getFilm());
        holder.filmTime.setText(data.get(position).getTime());
        return convertView;
    }

    static class ViewHolder {
        TextView filmName;
        TextView filmTime;
        
    }
}
