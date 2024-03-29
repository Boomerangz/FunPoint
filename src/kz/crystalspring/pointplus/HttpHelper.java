package kz.crystalspring.pointplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.venues.FSQConnector;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class HttpHelper
{
	private static final String GLOBAL_PROXY = "http://www.homeplus.kz/jam/4sq_gzip_curl.php";
	private static final String LOCAL_PROXY = "http://192.168.1.50/jam/4sq_gzip_curl.php";
	private static final String CURRENT_PROXY = GLOBAL_PROXY;
	private static boolean USE_PROXY = false;
	static HttpClient client = new DefaultHttpClient();

	private static HttpResponse loadResponse(HttpUriRequest request)
	{
		try
		{
			Date begin_d = new Date();
			HttpResponse response = client.execute(request);
			Date end_d = new Date();
			Log.w("HTTPRequest",
					Long.toString(end_d.getTime() - begin_d.getTime()));
			return response;
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
			if (java.net.UnknownHostException.class.isInstance(e))
			{
				USE_PROXY=true;
			}
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
				params.add(new BasicNameValuePair("key_zip",
						FSQConnector.CLIENT_SECRET));
				post.setEntity(new UrlEncodedFormEntity(params));
				is = loadResponse(post).getEntity().getContent();
				try
				{
					GZIPInputStream gzip = new GZIPInputStream(is);
					is = gzip;
				} catch (IOException e)
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

	public static String loadByUrl(String sUrl)
	{
		if (jUrls!=null&&jUrls.has(sUrl))
		{
			String st;
			try
			{
				st = jUrls.getString(sUrl);
				return st;
			} catch (JSONException e)
			{
				e.printStackTrace();
				return privateLoadByUrl(sUrl);
			}
		} else
		{
			return privateLoadByUrl(sUrl);
		}
	}

	private static synchronized String privateLoadByUrl(String sUrl)
	{
		HttpGet request = new HttpGet();
		Log.w("HTTPRequest", sUrl);
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
		Log.w("HTTPRequest", sUrl);
		try
		{
			if (USE_PROXY)
			{
				parameters.add(new BasicNameValuePair("url", sUrl));
				parameters.add(new BasicNameValuePair("key_zip",
						FSQConnector.CLIENT_SECRET));
				usedUrl = CURRENT_PROXY;
			}
			HttpPost post = new HttpPost(usedUrl);
			post.setHeader("Accept-Language", "ru");
			AbstractHttpEntity ent = new UrlEncodedFormEntity(parameters,
					HTTP.UTF_8);
			ent.setContentEncoding("UTF-8");
			post.setEntity(ent);
			InputStream is = loadStream(post);
			if (USE_PROXY)
			{
				try
				{
					GZIPInputStream gzip = new GZIPInputStream(is);
					is = gzip;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return streamToString(is);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized Drawable loadPictureByUrl(String sUrl)
	{
		try
		{
			URL url = new URL(sUrl);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
			InputStream response = (InputStream) connection.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(response);
			return new BitmapDrawable(bitmap);
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
						new InputStreamReader(is, "UTF-8"));
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

	public static Drawable loadPictureByUrl(String sUrl, int i)
	{
		try
		{
			URL url = new URL(sUrl);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
			InputStream response = (InputStream) connection.getContent();
			Bitmap true_bitmap = BitmapFactory.decodeStream(response);
			int h_coof = i;
			int w_coof = Math.round(true_bitmap.getWidth()
					/ ((float) true_bitmap.getHeight() / i));
			Bitmap small_bitmap = Bitmap.createScaledBitmap(true_bitmap,
					h_coof, w_coof, false);
			true_bitmap.recycle();
			System.gc();
			return new BitmapDrawable(small_bitmap);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static JSONObject jUrls = null;

	public static synchronized void loadFromProxy(GeoPoint geoPoint)
	{
		try
		{
			String sResponse1=HttpHelper.loadByUrl(FSQConnector.SELF_URL);
			List<BasicNameValuePair> params = FSQConnector
					.getUrlForProxy(geoPoint);
			params.add(new BasicNameValuePair("count", Integer.toString(params
					.size())));
			String sResponse = loadZipPostByUrl(
					"http://jam-kz.appspot.com/myproject", params);
			jUrls = new JSONObject(sResponse);
			if (jUrls.has("Time"))
			{
				Log.w("HTTPResponse", "Proxy loaded in "+jUrls.getString("Time"));
			}
		} catch (Exception e)
		{
			jUrls = new JSONObject();
			e.printStackTrace();
		}
	}

	private static String loadZipPostByUrl(String sUrl,
			List<BasicNameValuePair> parameters)
	{
		String usedUrl = sUrl;
		Log.w("HTTPRequest", sUrl);
		try
		{
			HttpPost post = new HttpPost(usedUrl);
			post.setHeader("Accept-Language", "ru");
			AbstractHttpEntity ent = new UrlEncodedFormEntity(parameters,
					HTTP.UTF_8);
			ent.setContentEncoding("UTF-8");
			post.setEntity(ent);
			// InputStream is = ;
			InputStream is = loadStream(post);
			GZIPInputStream gzipInputStream = new GZIPInputStream(is);
			// is.
			// byte[] bytes=new byte[is.available()];
			// is.read(bytes);
			// bytes=C_FileHelper.decompress(bytes);
			String s = streamToString(gzipInputStream);// new String(bytes);
			return s;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
