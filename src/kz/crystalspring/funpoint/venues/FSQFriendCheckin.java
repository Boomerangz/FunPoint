package kz.crystalspring.funpoint.venues;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.funObjectDetail;
import kz.crystalspring.pointplus.ImageCache;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.LoadingImageView;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FSQFriendCheckin
{
	MapItem place;
	String friendName;
	Date time;
	UrlDrawable friendPhoto;

	public FSQFriendCheckin(JSONObject checkin)
	{
		int i = 1;
		while (i < 6)
		{
			try
			{
				String lastName = null;
				String firstName = null;
				switch (i)
				{
				case 1:
					lastName = checkin.getJSONObject("user").getString("lastName");
					i++;
				case 2:
					firstName = checkin.getJSONObject("user").getString("firstName");
					i++;
				case 3:
					String name = ProjectUtils.ifnull(firstName, "") + " " + ProjectUtils.ifnull(lastName, "");
					friendName = name;
					i++;
				case 4:
					String photoUrl = checkin.getJSONObject("user").getString("photo");
					friendPhoto = new UrlDrawable();
					friendPhoto.bigUrl = photoUrl;
					i++;
				case 5:
					Date time = new Date(checkin.getInt("createdAt"));
					this.time = time;
					i++;
				case 6:
					JSONObject place = checkin.getJSONObject("venue");
					this.place = MainApplication.mapItemContainer.addItem(place);
					i++;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				i++;
			}
		}
	}

	@Override
	public String toString()
	{
		return friendName + " зачекинился в " + ProjectUtils.ifnull(place, "").toString();
	}

	View view = null;

	public View getView(View convertView)
	{
		if (view != null)
			return view;
		Context context = MainApplication.context;
		LayoutInflater mInflater = LayoutInflater.from(context);
		view = mInflater.inflate(R.layout.feed_list_item, null);
		TextView userAction = (TextView) view.findViewById(R.id.user_action);
		userAction.setText(Html.fromHtml("<b>" + friendName + "</b>" + " зачекинился в"));
		TextView userPlace = (TextView) view.findViewById(R.id.user_place);
		userPlace.setText(ProjectUtils.ifnull(place, "").toString());

		LoadingImageView LVI = (LoadingImageView) view.findViewById(R.id.loading_imageview);
		ImageCache imageCache = ImageCache.getInstance();
		String sUrl = (String) ProjectUtils.ifnull(friendPhoto.smallUrl, friendPhoto.bigUrl);
		if (imageCache.hasImage(sUrl))
			LVI.setDrawable(new BitmapDrawable(imageCache.getImage(sUrl)));
		else
		{
			LVI.setTag(friendName);
			if (friendPhoto.getBigDrawable() == null)
				FSQConnector.loadImageAsync(LVI, friendPhoto, UrlDrawable.BIG_URL, false, null);
			else
				LVI.setDrawable(friendPhoto.getBigDrawable());
		}
		final Context locContext = context;
		View clickView = view.findViewById(R.id.list_block);
		clickView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				MainApplication.mapItemContainer.setSelectedItem(place);
				Intent intent = new Intent(locContext, funObjectDetail.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				locContext.startActivity(intent);
			}
		});
		return view;
	}

}
