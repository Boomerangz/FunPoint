package kz.crystalspring.funpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.sbeyer.atmpoint1.types.ItemFood;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyRestOverlay extends Overlay
{
	private final int mRadius=5;
	private MapItem item;
	GeoPoint geoPoint;
	
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		Projection projection=mapView.getProjection();
//		if (!shadow)
		{
			if (item!=null)
			{
			geoPoint=new GeoPoint(item.getLatitudeE6(), item.getLongitudeE6());
				
			Point point=new Point();
			projection.toPixels(geoPoint, point);
			
			RectF oval=new RectF(point.x-mRadius,point.y-mRadius,
					             point.x+mRadius,point.y+mRadius);
		
			
			Paint paint = new Paint();
			paint.setARGB(250, 0, 0, 0);
			paint.setAntiAlias(true);
			
			Paint textPaint= new Paint();
			textPaint.setARGB(250, 255, 255, 255);
			textPaint.setAntiAlias(true); 
			textPaint.setTextSize(14*MainApplication.mDensity);
			textPaint.setTextAlign(Paint.Align.CENTER);
			textPaint.setTypeface(Typeface.MONOSPACE);
			textPaint.setTextScaleX((float) 0.9);
			
		
			
		//	canvas.drawOval(oval, paint);
		//    canvas.drawBitmap(item.getIconBM(),null,rect,null);
			String st;
			if (item.getObjTypeId().equals(MapItem.FSQ_TYPE_FOOD))
				st=((ItemFood)item).getLunchPrice();
			else
				st=item.toString();
			
			
			//textPaint.getFontMetrics().descent+
			
			float[] widths=new float[st.length()];
			textPaint.getTextWidths(st, widths);
			
			final float letterSize=(float) ProjectUtils.getSumOfArray(widths)/st.length();//(float) (textPaint.getFontMetrics().descent*1.2)+(textPaint.getTextWidths(text, widths);//+textPaint.getFontMetrics().;
			final int xSize=(int) Math.round(ProjectUtils.getSumOfArray(widths)*MainApplication.mDensity);
			final int xMoveLeft=-Math.round(xSize*MainApplication.mDensity);
			final int xMoveRight=Math.round(xSize*MainApplication.mDensity);
			final int yMoveUp=-Math.round((12 * mRadius)*MainApplication.mDensity);
			final int yMoveDown=Math.round(yMoveUp+(3*mRadius*MainApplication.mDensity));
			
			RectF rect= new RectF(point.x + xMoveLeft,
								  point.y + yMoveUp,
								  point.x + xMoveRight,
								  point.y + yMoveDown);
			
			
			if (!(st.equals("")||st.toUpperCase().equals("NO"))&&mapView.getZoomLevel()>15)
			{
				canvas.drawRoundRect(rect,5,5, paint);
				canvas.drawText(st, point.x, point.y+yMoveDown-1, textPaint);
			}	
			}
			super.draw(canvas, mapView, shadow);
		}
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapview)
	{
		return false;
	}
	


	public MapItem getGeoPoint()
	{
		return item;
	}

	public void setMapItem(MapItem mapItem)
	{
		this.item = mapItem;
	}

	
}
