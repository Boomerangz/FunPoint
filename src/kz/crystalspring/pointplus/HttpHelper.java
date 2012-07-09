package kz.crystalspring.pointplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FSQConnector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class HttpHelper
{

	private static final String GLOBAL_PROXY_URL = "http://www.homeplus.kz/parser/4sq_gzip_curl.php";
	private static final String LOCAL_PROXY = "http://192.168.1.50/jam/4sq_gzip_curl.php";
	private static final String CURRENT_PROXY = LOCAL_PROXY;
	private static final boolean USE_PROXY = true;
	static HttpClient client = new DefaultHttpClient();
	private static HttpResponse loadResponse(HttpUriRequest request)
	{
		
		try
		{
			return client.execute(request);
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static InputStream loadStream(HttpPost post)
	{
		try
		{
			InputStream is = loadResponse(post).getEntity().getContent();
			return is;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static InputStream loadStream(HttpGet get)
	{
		try
		{
			InputStream is;
			if (USE_PROXY)
			{
				HttpPost post = new HttpPost(CURRENT_PROXY);
				post.setHeader("Accept-Language", "ru");
				ArrayList<BasicNameValuePair> params = new ArrayList();
				String url = get.getURI().toString();
				params.add(new BasicNameValuePair("url", url));
				params.add(new BasicNameValuePair("key", FSQConnector.CLIENT_SECRET));
				post.setEntity(new UrlEncodedFormEntity(params));
				is= loadResponse(post).getEntity()
						.getContent();
				//byte[] bytes =ProjectUtils.getBytes(is);
				//byte[] unzipped_bytes=C_FileHelper.decompress(bytes);
				try
				{ 
					GZIPInputStream gzip = new GZIPInputStream(is);
					is=gzip;
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			} else
				is = loadResponse(get).getEntity().getContent();
			return is;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized String loadByUrl(String sUrl)
	{
		HttpGet request = new HttpGet();
		request.setHeader("Accept-Language", "ru");
		try
		{
			request.setURI(new URI(sUrl));
			InputStream is = loadStream(request);
			return streamToString(is);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized String loadPostByUrl(String sUrl,
			List<BasicNameValuePair> parameters)
	{

		String usedUrl = sUrl;
		try
		{
			if (USE_PROXY)
			{
				parameters.add(new BasicNameValuePair("url", sUrl));
				parameters.add(new BasicNameValuePair("key",
						FSQConnector.CLIENT_SECRET));
				usedUrl = CURRENT_PROXY;
			}
			HttpPost post = new HttpPost(usedUrl);
			post.setHeader("Accept-Language", "ru");
			post.setEntity(new UrlEncodedFormEntity(parameters));
			InputStream is = loadStream(post);
			try
			{
				GZIPInputStream gzip = new GZIPInputStream(is);
				is=gzip;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return streamToString(is);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized Drawable  loadPictureByUrl(String sUrl)
	{

		try
		{
			HttpGet request = new HttpGet();
			request.setURI(new URI(sUrl));
			request.setHeader("Accept-Language", "ru");
			HttpResponse response = loadResponse(request);
			return streamToDrawable(response.getEntity().getContent());
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String streamToString(InputStream is) throws IOException
	{
		String zippedSt = null;
		if (is != null)
		{
			StringBuilder sb = new StringBuilder();
			String line;
			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				while ((line = reader.readLine()) != null)
				{
					sb.append(line);
				}

				reader.close();
			} finally
			{
				is.close();
			}
			zippedSt = sb.toString();
		}
		return zippedSt;
	}

	private static Drawable streamToDrawable(InputStream is) throws IOException
	{
		Bitmap b = BitmapFactory.decodeStream(is);
		b.setDensity(Bitmap.DENSITY_NONE);
		Drawable d = new BitmapDrawable(b);
		return d;
	}
}
