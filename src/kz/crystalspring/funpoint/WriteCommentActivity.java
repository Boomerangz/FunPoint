package kz.crystalspring.funpoint;

import kz.crystalspring.funpoint.venues.FSQConnector;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
		EditText edit=(EditText) findViewById(R.id.comment_text);
		String comment=edit.getText().toString();
		String venueID=MainApplication.mapItemContainer.getSelectedItem().getId();
		FSQConnector.addToTips(venueID, comment);
	}
	
	public void cancelComment()
	{
		finish();
	}
}
