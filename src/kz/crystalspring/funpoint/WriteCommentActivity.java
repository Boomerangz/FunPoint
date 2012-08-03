package kz.crystalspring.funpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import kz.crystalspring.funpoint.venues.FSQConnector;
import kz.crystalspring.funpoint.venues.FSQItem;
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
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WriteCommentActivity extends Activity
{
	ImageView mImageView;
	protected String _path;
	protected boolean _taken;
	byte[] imageBytes = null;
	protected static final String PHOTO_TAKEN = "photo_taken";
	public static final int COMMENT_MODE=1;
	public static final int CHECKIN_MODE=2;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_comment_write);
		TextView header = (TextView) findViewById(R.id.header);
		Button okButton = (Button) findViewById(R.id.ok_button);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		Button photoButton = (Button) findViewById(R.id.photo_button);
		mImageView = (ImageView) findViewById(R.id.imageView1);
		_path = Environment.getExternalStorageDirectory()
				+ "/images/make_machine_example.jpg";

		switch (getIntent().getExtras().getInt("requestCode"))
		{
			case COMMENT_MODE:header.setText("Оставить комментарий"); break;
			case CHECKIN_MODE:header.setText("Комментарий к чекину"); break;
		}
		
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

	@Override
	public void onResume()
	{
		super.onResume();
		if (imageBytes != null)
		{
			Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageBytes, 0,
					imageBytes.length);
			mImageView.setImageBitmap(bitmapImage);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode==KeyEvent.KEYCODE_BACK)
		{
			setResult(RESULT_CANCELED,null);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void sendComment()
	{
		EditText edit = (EditText) findViewById(R.id.comment_text);
		String comment = edit.getText().toString();
		String venueId = MainApplication.mapItemContainer.getSelectedMapItem()
				.getId();
		if (getIntent().getExtras().getInt("requestCode")==COMMENT_MODE)
			sendTip(comment,venueId);
		else
			sendCheckin(comment, venueId);
		finisм нh();
	}
	
	private void sendTip(String comment, String venueId)
	{
		FSQConnector.addToTips(venueId, comment, imageBytes);
	}
	
	private void sendCheckin(String comment, String venueId)
	{
		FSQConnector.checkIn(venueId,comment, imageBytes);
	}
	

	public void cancelComment()
	{
		finish();
	}

	final int TAKE_PHOTO_CODE = 100;

	public void takePhoto()
	{
		_path = Environment.getExternalStorageDirectory().getName()
				+ File.separatorChar + "Android/data/"
				+ WriteCommentActivity.this.getPackageName()
				+ "/files/1111.jpg";
		File _photoFile = new File(_path);
		try
		{
			if (_photoFile.exists())
			{
				_photoFile.delete();
			}

			if (_photoFile.exists() == false)
			{
				_photoFile.getParentFile().mkdirs();
				_photoFile.createNewFile();
			}

		} catch (IOException e)
		{
			// Log.e(TAG, "Could not create file.", e);
		}

		Uri _fileUri = Uri.fromFile(_photoFile);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri);
		startActivityForResult(intent, TAKE_PHOTO_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TAKE_PHOTO_CODE)
		{
			switch (resultCode) {
			case 0:
				Log.i("MakeMachine", "User cancelled");
				break;
			case -1:
				File file = new File(_path);
				if (file.exists())
					handlePhotoResult(data);
				break;
			}
		}
	}

	private void handlePhotoResult(Intent intent)
	{
		// Bundle extras = intent.getExtras();
		// Bitmap mImageBitmap = (Bitmap) extras.get("data");
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		FileInputStream fis = null;
		byte[] image = new byte[0];
		try
		{
			fis = new FileInputStream(new File(_path));
			image = new byte[fis.available()];
			fis.read(image);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		Bitmap mLargeBitmap = BitmapFactory.decodeByteArray(image, 0,
				image.length);

		Bitmap mImageBitmap = Bitmap
				.createScaledBitmap(mLargeBitmap, mLargeBitmap.getWidth() / 2,
						mLargeBitmap.getHeight() / 2, true);
		// FileOutputStream out;
		// try {
		// out = new FileOutputStream(_path);
		// mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		mImageView.setImageBitmap(mImageBitmap);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		imageBytes = stream.toByteArray();
	}
}
