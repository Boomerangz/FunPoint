package kz.crystalspring.funpoint;

import java.io.PrintWriter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.http.HttpRequest;
import android.content.Intent;
import kz.crystalspring.funpoint.venues.MapItem;

public class SocialConnector
{
	private final String TWITTER_KEY="ZsXuhJ0ZtfzhTONOxX1lg";
	private final String TWITTER_SECRET="GIPq7FTTup99w2YpRcWcDxJETcXzp1mDliCa8t4oI";

	private final String TWITTER_LOGIN="gexxogen";
	private final String TWITTER_PASS="1ad63bcc";
	
	Twitter twitter;
	
	SocialConnector()
	{
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

	     configurationBuilder.setOAuthConsumerKey(TWITTER_KEY);
	     configurationBuilder.setOAuthConsumerSecret(TWITTER_SECRET);
	     configurationBuilder.setPassword(TWITTER_PASS);
	     configurationBuilder.setUser(TWITTER_LOGIN);
	     Configuration configuration = configurationBuilder.build();

	     Authorization auth=AuthorizationFactory.getInstance(configuration);
	     Twitter twitter = new TwitterFactory(configuration).getInstance(auth); 

	     AccessToken token;
		try
		{
			token = twitter.getOAuthAccessToken();
		     System.out.println("Access Token " +token );

		     String name = token.getScreenName();
		     System.out.println("Screen Name" +name);

		     System.out.println(token);
		} catch (TwitterException e)
		{
			e.printStackTrace();
		}

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
