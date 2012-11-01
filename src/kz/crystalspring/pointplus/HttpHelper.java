package kz.crystalspring.pointplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
import org.apache.http.params.HttpParams;
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
	private final String GLOBAL_PROXY = "http://www.homeplus.kz/jam/4sq_gzip_curl.php";
	// private final String LOCAL_PROXY =
	// "http://192.168.1.50/jam/4sq_gzip_curl.php";
	private final String CURRENT_PROXY;
	private final String GAE_PROXY;
	private boolean USE_PROXY = false;
	private final int NEED_TO_COMPLETE = 3;

	private final boolean NEED_TO_TEST = true;
	private final float CHANCE = (float) 0;

	HttpClient client;
	private static HttpHelper singletone;

	private HttpHelper()
	{
		client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		CURRENT_PROXY = getOurProxyURL();
		GAE_PROXY = getGAEProxyURL();
	}

	public static HttpHelper getInstance() 
	{
		if (singletone == null)
		{
			singletone = new HttpHelper();
		}
		return singletone;
	}

	private HttpResponse loadResponse(HttpUriRequest request)
	{
		int complete = 0;
		while (complete < NEED_TO_COMPLETE)
		{
			try
			{
				Date begin_d = new Date();
				Log.w("HTTPRequest", request.getURI().toString());
				if (NEED_TO_TEST && Math.random() < CHANCE)
				{
					Log.w("HTTPRequest", "Test Exception on " + request.getURI().toString());
					throw new IOException();
				}
				HttpResponse response = client.execute(request);
				Date end_d = new Date();
				Log.w("HTTPRequest", Long.toString(end_d.getTime() - begin_d.getTime()));
				complete = NEED_TO_COMPLETE;
				return response;
			} catch (ClientProtocolException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
				if (java.net.UnknownHostException.class.isInstance(e))
				{
					USE_PROXY = true;
					complete = NEED_TO_COMPLETE;
				}
			}
			complete++;
		}
		return null;
	}

	private InputStream loadStream(HttpPost post)
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

	private InputStream loadStream(HttpGet get)
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
				params.add(new BasicNameValuePair("key_zip", FSQConnector.CLIENT_SECRET));
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

	public String loadByUrl(String sUrl)
	{
		while (jUrls == null)
		{
			for (int i = 0; i < 65000; i++)
				;
		}

		if (jUrls != null && jUrls.has(sUrl))
		{
			// jUrls.names()
			String st;
			try
			{
				st = jUrls.getString(sUrl);
				Log.w("HTTPResponse", "Loaded from proxy");
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

	private synchronized String privateLoadByUrl(String sUrl)
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

	public synchronized String loadPostByUrl(String sUrl, List<BasicNameValuePair> parameters)
	{

		String usedUrl = sUrl;
		Log.w("HTTPRequest", sUrl);
		try
		{
			if (USE_PROXY)
			{
				parameters.add(new BasicNameValuePair("url", sUrl));
				parameters.add(new BasicNameValuePair("key_zip", FSQConnector.CLIENT_SECRET));
				usedUrl = CURRENT_PROXY;
			}
			HttpPost post = new HttpPost(usedUrl);
			post.setHeader("Accept-Language", "ru");
			AbstractHttpEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
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

	public Bitmap loadPictureByUrl(String sUrl)
	{
		if (imageCache == null)
		{
			imageCache = ImageCache.getInstance();
		}
		try
		{
			Bitmap bitmap = null;
			if (imageCache.hasImage(sUrl))
			{
				bitmap = imageCache.getImage(sUrl);
			}
			if (bitmap == null)
			{
				URL url = new URL(sUrl);
				URLConnection connection = url.openConnection();
				connection.setUseCaches(true);
				InputStream response = (InputStream) connection.getContent();
				boolean succes = false;
				while (!succes)
				{
					try
					{
						succes = true;
						bitmap = BitmapFactory.decodeStream(response);
					} catch (OutOfMemoryError e)
					{
						e.printStackTrace();
						succes = false;
						System.gc();
					}
				}
				imageCache.addToCache(sUrl, bitmap);
				System.gc();
			}
			return bitmap;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String streamToString(InputStream is) throws IOException
	{
		String zippedSt = null;
		if (is != null)
		{
			StringBuilder sb = new StringBuilder();
			String line;
			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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

	private Drawable streamToDrawable(InputStream is) throws IOException
	{
		Bitmap b = BitmapFactory.decodeStream(is);
		b.setDensity(Bitmap.DENSITY_NONE);
		Drawable d = new BitmapDrawable(b);
		return d;
	}

	ImageCache imageCache = null;

	public Drawable loadPictureByUrl(String sUrl, int i)
	{
		if (imageCache == null)
		{
			imageCache = ImageCache.getInstance();
		}
		try
		{
			if (!imageCache.hasImage(sUrl))
			{
				URL url = new URL(sUrl);
				URLConnection connection = url.openConnection();
				connection.setUseCaches(true);
				InputStream response = (InputStream) connection.getContent();
				boolean succes = false;
				while (!succes)
				{
					try
					{
						succes = true;
						Bitmap true_bitmap = BitmapFactory.decodeStream(response);
						int h_coof = i;
						int w_coof = Math.round(true_bitmap.getWidth() / ((float) true_bitmap.getHeight() / i));
						Bitmap small_bitmap = Bitmap.createScaledBitmap(true_bitmap, h_coof, w_coof, false);
						// true_bitmap.recycle();
						System.gc();
						return new BitmapDrawable(small_bitmap);
					} catch (OutOfMemoryError e)
					{
						succes = false;
						System.gc();
						e.printStackTrace();
					}
				}
				return null;
			} else
				return null;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private JSONObject jUrls = null;

	public synchronized void loadFromProxy(GeoPoint geoPoint)
	{
		try
		{
			final String PROXY_APP = "android_1";
			List<BasicNameValuePair> params = FSQConnector.getUrlForProxy(geoPoint);
			params.add(new BasicNameValuePair("count", Integer.toString(params.size())));
			params.add(new BasicNameValuePair("key", FSQConnector.CLIENT_SECRET));
			params.add(new BasicNameValuePair("version_id", PROXY_APP));
			String sResponse = loadZipPostByUrl(GAE_PROXY, params);
			jUrls = new JSONObject(sResponse);
			if (jUrls.has("Time"))
			{
				Log.w("HTTPResponse", "Proxy loaded in " + jUrls.getString("Time"));
			}
		} catch (Exception e)
		{
			jUrls = new JSONObject();
			e.printStackTrace();
		}
	}

	private String getGAEProxyURL()
	{
		try
		{
			JSONObject jConfig = getConfigJSON();
			String GaeProxyUrl = jConfig.getString("GAEProxy");
			return GaeProxyUrl;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return "http://jam-kz.appspot.com/myproject";
	}

	private String getOurProxyURL()
	{
		try
		{
			JSONObject jConfig = getConfigJSON();
			String GaeProxyUrl = jConfig.getString("OurProxy");
			return GaeProxyUrl;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return GLOBAL_PROXY;
	}

	private JSONObject configJSON;

	private JSONObject getConfigJSON()
	{
		if (configJSON != null)
		{
			return configJSON;
		} else
		{
			try
			{
				String configs;
				File configFile = new File(MainApplication.context.getFilesDir() + "/" + "config.json");
				if (configFile.exists() && configFile.canRead())
				{
					byte[] bytes = C_FileHelper.ReadFile(configFile);
					configs = new String(bytes);
				} else
				{
					configs = streamToString(MainApplication.context.getAssets().open("config.json"));
				}
				configJSON = new JSONObject(configs);
				return configJSON;
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	private boolean isConnected()
	{
		try
		{
			// Network is available but check if we can get access from the
			// network.
			URL url = new URL("https://ru.foursquare.com/");
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setRequestProperty("Connection", "close");
			urlc.setConnectTimeout(500); // Timeout 2 seconds.
			urlc.connect();

			if (urlc.getResponseCode() == 200) // Successful response.
			{
				return true;
			} else
			{
				Log.d("NO INTERNET", "NO INTERNET");
				return false;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private String loadZipPostByUrl(String sUrl, List<BasicNameValuePair> parameters)
	{
		String usedUrl = sUrl;
		Log.w("HTTPRequest", sUrl);
		try
		{
			HttpPost post = new HttpPost(usedUrl);
			post.setHeader("Accept-Language", "ru");
			AbstractHttpEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
			ent.setContentEncoding("UTF-8");
			post.setEntity(ent);
			InputStream is = loadStream(post);
			GZIPInputStream gzipInputStream = new GZIPInputStream(is);
			String s = streamToString(gzipInputStream);// new String(bytes);
			return s;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
