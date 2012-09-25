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
import kz.crystalspring.funpoint.venues.UrlDrawable;
import kz.crystalspring.visualities.RatingActivity;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WriteCommentActivity extends Activity implements RefreshableMapList
{
	ImageView mImageView;
	protected String _path;
	protected boolean _taken;
	Bitmap imageBitmap = null;
	protected static final String PHOTO_TAKEN = "photo_taken";
	public static final int COMMENT_MODE = 1;
	public static final int CHECKIN_MODE = 2;
	Button clearButton;
	private static String response=null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_comment_write);
		TextView header = (TextView) findViewById(R.id.header);
		Button okButton = (Button) findViewById(R.id.ok_button);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		clearButton = (Button) findViewById(R.id.clear_button);

		mImageView = (ImageView) findViewById(R.id.imageView1);
		_path = Environment.getExternalStorageDirectory()
				+ "/images/make_machine_example.jpg";

		switch (getIntent().getExtras().getInt("requestCode"))
		{
		case COMMENT_MODE:
			header.setText("Оставить комментарий");
			break;
		case CHECKIN_MODE:
			header.setText("Комментарий к чекину");
			break;
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

		final OnClickListener takePhotoListner = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				takePhoto();
			}
		};
		mImageView.setOnClickListener(takePhotoListner);

		clearButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				imageBitmap = null;
				mImageView.setImageResource(R.drawable.camera);
				clearButton.setVisibility(View.GONE);
				mImageView.setOnClickListener(takePhotoListner);
			}
		});

	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		MainApplication.refreshable=this;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			setResult(RESULT_CANCELED, null);
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
		if (getIntent().getExtras().getInt("requestCode") == COMMENT_MODE)
			sendTip(comment, venueId);
		else
			sendCheckin(comment, venueId);
	//	finish();
	}

	private void sendTip(String comment, String venueId)
	{
		FSQConnector.addToTips(venueId, comment, bitmapToBytes(imageBitmap));
		finish();
	}

	
	Dialog pd=null;
	private void sendCheckin(String comment, String venueId)
	{
		pd=ProgressDialog.show(this, "Checkin", "Checkining now");
		FSQConnector.checkIn(venueId, comment, bitmapToBytes(imageBitmap));
		//loadRatingPage();
	}
	
	@Override
	public void refreshMapItems()
	{
		if (pd!=null&&response!=null)
		{
			pd.dismiss();
			pd=null;
			loadRatingPage(response);
			response=null;
		}
	}
	
	private void loadRatingPage(String checkinResponse)
	{
		Intent intent=new Intent(this,RatingActivity.class);
		intent.putExtra("checkinResponse", checkinResponse);
		startActivity(intent);
	}

	public void cancelComment()
	{
		finish();
	}

	final int TAKE_PHOTO_CODE = 100;
	final int TAKE_GALLERY_IMAGE_CODE = 200;

	public void takePhoto()
	{
		LayoutInflater mInflater = LayoutInflater.from(getBaseContext());
		View dialogView = mInflater.inflate(R.layout.image_pick_dialog, null);
		Button takePhoto = (Button) dialogView.findViewById(R.id.take_photo);
		Button pickFromGallery = (Button) dialogView
				.findViewById(R.id.pick_from_gallery);
		final Dialog dialog = new Dialog(this);
		takePhoto.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				takePhotoFromCamera();
				dialog.dismiss();
			}
		});

		pickFromGallery.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				takePhotoFromGallery();
				dialog.dismiss();
			}
		});
		dialog.setContentView(dialogView);
		dialog.setTitle("Выберите способ");
		dialog.show();
	}

	private void takePhotoFromCamera()
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

	private void takePhotoFromGallery()
	{
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto, TAKE_GALLERY_IMAGE_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TAKE_PHOTO_CODE)
		{
			switch (resultCode)
			{
			case 0:
				Log.i("MakeMachine", "User cancelled");
				break;
			case -1:
				File file = new File(_path);
				if (file.exists())
					handlePhotoResult(data);
				break;
			}
		} else if (requestCode == TAKE_GALLERY_IMAGE_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				Uri selectedImage = data.getData();
				try
				{
					Bitmap btm = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), selectedImage);
					if (btm.getHeight() > 1000 || (btm.getWidth() > 1000))
						btm = Bitmap.createScaledBitmap(btm,
								btm.getWidth() / 2, btm.getHeight() / 2, true);
					mImageView.setImageBitmap(btm);
					imageBitmap = btm;
					mImageView.setOnClickListener(openPhotoListner);
					clearButton.setVisibility(View.VISIBLE);
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		mImageView.setImageBitmap(mImageBitmap);
		imageBitmap = mImageBitmap;
		mImageView.setOnClickListener(openPhotoListner);
		clearButton.setVisibility(View.VISIBLE);
	}

	OnClickListener openPhotoListner = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			openPhotoViewActivity();
		}
	};

	private void openPhotoViewActivity()
	{
		Intent intent = new Intent(this, FullScrLoadingImageActivity.class);
		UrlDrawable drwbl = new UrlDrawable();
		drwbl.setBigDrawable(new BitmapDrawable(imageBitmap));

		MainApplication.selectedItemPhoto = drwbl;

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public byte[] bitmapToBytes(Bitmap btm)
	{
		if (btm != null)
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			btm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] bytes = stream.toByteArray();
			return bytes;
		}
		return null;
	}

	public static synchronized void setResponse(String st)
	{
		response=st;
	}

}
