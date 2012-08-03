package kz.crystalspring.funpoint;


import android.content.Intent;
import kz.crystalspring.funpoint.venues.MapItem;

public class SocialConnector
{
	SocialConnector()
	{

	}
	
	
	public void shareCheckinOnTwitter(MapItem item)
	{
	//	shareOnTwitter("I'm in "+item.toString()+" now");
	}
	
	
	public void shareOnTwitter(String text)
	{
		Intent share = new Intent(Intent.ACTION_SEND);
	    share.putExtra(Intent.EXTRA_TEXT, text);
	    MainApplication.context.startActivity(Intent.createChooser(share, "Share this via"));
	}
}
