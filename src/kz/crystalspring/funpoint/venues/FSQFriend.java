package kz.crystalspring.funpoint.venues;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FSQFriend extends Friend
{
	UrlDrawable photo;
	String id;
	String firstName;
	String lastName;
	
	public FSQFriend(JSONObject jObject){
		super("");
		try
		{
			id=jObject.getJSONObject("user").getString("id");
			firstName=jObject.getJSONObject("user").getString("firstName");
			lastName= jObject.getJSONObject("user").getString("lastName");
			photo=new UrlDrawable();
			photo.bigUrl=jObject.getJSONObject("user").getString("photo");
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName()
	{
		return firstName+" "+lastName;
	}
	
	public String getId()
	{
		return id;
	}

	@Override
	public View getView(Context context)
	{
		TextView tv=new TextView(context);
		tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		tv.setText(getName());
		return tv;
	}

}
