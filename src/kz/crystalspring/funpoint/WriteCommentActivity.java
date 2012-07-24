package kz.crystalspring.funpoint;

import java.io.IOException;

import kz.crystalspring.funpoint.venues.FSQConnector;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WriteCommentActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_comment_write);
		Button okButton=(Button) findViewById(R.id.ok_button);
		Button cancelButton=(Button) findViewById(R.id.cancel_button);
		okButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sendComment();
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cancelComment();
			}
		});
	}
	
	
	public void sendComment()
	{
//		EditText edit=(EditText) findViewById(R.id.comment_text);
//		String comment=edit.getText().toString();
//		String venueID=MainApplication.mapItemContainer.getSelectedMapItem().getId();
//		FSQConnector.addToTips(venueID, comment);
		Camera camera=Camera.open();
		try
		{
			camera.setPreviewDisplay(new SurfaceHolder()
			{
				
				@Override
				public void unlockCanvasAndPost(Canvas canvas)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setType(int type)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setSizeFromLayout()
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setKeepScreenOn(boolean screenOn)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setFormat(int format)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setFixedSize(int width, int height)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void removeCallback(Callback callback)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public Canvas lockCanvas(Rect dirty)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Canvas lockCanvas()
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean isCreating()
				{
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public Rect getSurfaceFrame()
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Surface getSurface()
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void addCallback(Callback callback)
				{
					// TODO Auto-generated method stub
					
				}
			});
			camera.startPreview();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void cancelComment()
	{
		finish();
	}
}
