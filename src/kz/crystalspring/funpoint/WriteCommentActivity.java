package kz.crystalspring.funpoint;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import kz.crystalspring.funpoint.venues.FSQConnector;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class WriteCommentActivity extends Activity
{
	ImageView mImageView;
	protected String _path;
	protected boolean _taken;
		
	protected static final String PHOTO_TAKEN = "photo_taken";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_comment_write);
		Button okButton=(Button) findViewById(R.id.ok_button);
		Button cancelButton=(Button) findViewById(R.id.cancel_button);
		Button photoButton=(Button) findViewById(R.id.photo_button);
		mImageView = (ImageView) findViewById(R.id.imageView1);
		_path = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";
		
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
		
		photoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				takePhoto();
			}
		});
	}
	
	
	public void sendComment()
	{
		EditText edit=(EditText) findViewById(R.id.comment_text);
		String comment=edit.getText().toString();
		String venueID=MainApplication.mapItemContainer.getSelectedMapItem().getId();
		FSQConnector.addToTips(venueID, comment);
	}
	
	public void cancelComment()
	{
		finish();
	}
	
	final int TAKE_PHOTO_CODE=100;
	
	public void takePhoto()
	{
		_path = Environment.getExternalStorageDirectory().getName() + File.separatorChar + "Android/data/" + WriteCommentActivity.this.getPackageName() + "/files/1111.jpg";
        File _photoFile = new File(_path);
        try {
            if(_photoFile.exists() == false) {
                _photoFile.getParentFile().mkdirs();
                _photoFile.createNewFile();
            }

        } catch (IOException e) {
           // Log.e(TAG, "Could not create file.", e);
        }
       // Log.i(TAG, path);

        Uri _fileUri = Uri.fromFile(_photoFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, _fileUri);
        startActivityForResult(intent, TAKE_PHOTO_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==TAKE_PHOTO_CODE)
		{
			switch( resultCode )
		    {
		    	case 0:
		    		Log.i( "MakeMachine", "User cancelled" );
		    		break;
		    	case -1:
		    		handlePhotoResult(data);
		    		break;
		    }
		}
			
	}


	private void handlePhotoResult(Intent intent) 
	{
//		Bundle extras = intent.getExtras();
//	    Bitmap mImageBitmap = (Bitmap) extras.get("data");
		_taken = true;
    	
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 4;
	    	
	    Bitmap mImageBitmap = BitmapFactory.decodeFile( _path, options );
	    mImageView.setImageBitmap(mImageBitmap);
	}
}

