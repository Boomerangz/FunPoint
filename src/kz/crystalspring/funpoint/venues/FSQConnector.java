package kz.crystalspring.funpoint.venues;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.crystalspring.funpoint.FullScrLoadingImageActivity;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.LoadingImageView;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;
import kz.sbeyer.atmpoint1.types.ItemHotel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Photo;

public class FSQConnector
{
	public static final String CLIENT_ID = "ATCDKP1BI3F1YDPOHVOWI2UCEXIUFWGPR0GF3DOVSLJFRFBM";
	public static final String CLIENT_SECRET = "YADGMVO5M5QJTZXXIDEIIDOYTRS5KLI5QHUQKB5DZ22ADROO";
	public static final String CALLBACK_URL = "myapp://connect";
	private static final String API_URL = "https://api.foursquare.com/v2";
	private static final String CHECK_IN_URL = "https://api.foursquare.com/v2/checkins/add";
	private static final String TIP_ADD_URL = "https://api.foursquare.com/v2/tips/add";
	private static final String TODO_ADD_URL = "https://api.foursquare.com/v2/lists/self/todos/additem";
	private static final String TODOS_GET_URL = "https://api.foursquare.com/v2/users/self/todos";
	private static final String BADGES_GET_URL = "https://api.foursquare.com/v2/users/self/badges";
	private static final String CHECKINS_GET_URL = "https://api.foursquare.com/v2/users/self/checkins";
	private static final String TAG = "FoursquareApi";
	private static final String API_VERSION = "&v=20120522";
	public static final int AREA_RADIUS = 1500;

	private static List<FSQTodo> todosList = new ArrayList<FSQTodo>(0);
	private static List<String> checkinsList = new ArrayList<String>(0);
	private static List<FSQBadge> badgesList = new ArrayList<FSQBadge>(0);

	private static boolean isTodosLoaded = false;
	private static boolean isCheckinsLoaded = false;
	private static boolean isBadgesLoaded = false;

	public static List<MapItem> loadItems(GeoPoint point, String category,
			int radius)
	{
		if (point != null)
		{
			try
			{
				List<MapItem> list = getNearby(point.getLatitudeE6() / 1e6,
						point.getLongitudeE6() / 1e6, category, radius);
				// list.addAll(getNearby(point.getLatitudeE6() / 1e6,
				// point.getLongitudeE6() / 1e6, category, 0));
				return list;
			} catch (Exception e)
			{
				e.printStackTrace();
				return new ArrayList<MapItem>();
			}
		} else
			return new ArrayList<MapItem>();
	}

	public static ArrayList<MapItem> getNearby(double latitude,
			double longitude, String category, int radius) throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();

		try
		{
			String ll = String.valueOf(latitude) + ","
					+ String.valueOf(longitude);
			String sUrl = API_URL + "/venues/search?ll=" + ll;

			if (category != null)
				sUrl += "&categoryId=" + category;

			if (radius > 0)
				sUrl += "&radius=" + Integer.toString(radius);

			sUrl += "&client_id=" + CLIENT_ID + "&client_secret="
					+ CLIENT_SECRET + API_VERSION;
			System.out.println("на сервер отдан запрос на точки");
			String response = HttpHelper.loadByUrl(sUrl);
			System.out.println("получена строка с точками с сервера");
			JSONObject jsonObj = new JSONObject(response);// (JSONObject) new
															// JSONTokener(response).nextValue();

			JSONArray items = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONArray("venues");

			int length = items.length();

			if (length > 0)
			{
				for (int i = 0; i < length; i++)
				{
					JSONObject item = (JSONObject) items.get(i);

					FSQItem venue;
					if (category.equals(MapItem.FSQ_TYPE_FOOD))
						venue = new ItemFood();
					else if (category.equals(MapItem.FSQ_TYPE_HOTEL))
						venue = new ItemHotel();
					else if (category.equals(MapItem.FSQ_TYPE_CINEMA))
						venue = new ItemCinema();
					else
						venue = new FSQItem();
					venue.loadFromJSON(item);
					venue.setCategory(category);
					if (venue != null)
						venueList.add(venue);
				}
			}
		} catch (Exception ex)
		{
			throw ex;
		}
		System.out.println("Составлен список объектов на основе ответа от сервера");
		return venueList;
	}

	public static ArrayList<MapItem> getByName(double latitude,
			double longitude, String category, String name) throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();
		try
		{
			String ll = String.valueOf(latitude) + ","
					+ String.valueOf(longitude);

			String sUrl = API_URL + "/venues/search?ll=" + ll;

			if (category != null)
				sUrl += "&categoryId=" + category;

			if (name != null)
				sUrl += "&query=" + URLEncoder.encode(name);

			sUrl += "&client_id=" + CLIENT_ID + "&client_secret="
					+ CLIENT_SECRET + API_VERSION;
			System.out.println("на сервер отдан запрос на точки");
			String response = HttpHelper.loadByUrl(sUrl);
			System.out.println("получена строка с точками с сервера");
			JSONObject jsonObj = new JSONObject(response);// (JSONObject) new
															// JSONTokener(response).nextValue();

			JSONArray items = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONArray("venues");

			int length = items.length();

			if (length > 0)
			{
				for (int i = 0; i < length; i++)
				{
					JSONObject item = (JSONObject) items.get(i);

					FSQItem venue;
					if (category.equals(MapItem.FSQ_TYPE_FOOD))
						venue = new ItemFood();
					else if (category.equals(MapItem.FSQ_TYPE_HOTEL))
						venue = new ItemHotel();
					else if (category.equals(MapItem.FSQ_TYPE_CINEMA))
						venue = new ItemCinema();
					else
						venue = new FSQItem();
					venue.loadFromJSON(item);
					venue.setCategory(category);
					if (venue != null)
						venueList.add(venue);
				}
			}
		} catch (Exception ex)
		{
			throw ex;
		}
		System.out
				.println("Составлен список объектов на основе ответа от сервера");
		return venueList;
	}

	public static JSONObject getVenueInformation(String id)
	{
		String sUrl = API_URL + "/venues/" + id;

		sUrl += "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
				+ API_VERSION;

		String response = HttpHelper.loadByUrl(sUrl);
		JSONObject jsonObj;
		try
		{
			System.out.println("информация о ресторане");
			System.out.println(response);
			jsonObj = (JSONObject) new JSONObject(response);
			return jsonObj.getJSONObject("response").getJSONObject("venue");
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public static void uploadPhoto(String venueId, String checkinId,
			String tipId, byte[] image)
	{
		if (MainApplication.FsqApp.hasAccessToken())
		{
			String oAuthToken = MainApplication.FsqApp.getAccesToken();
			String ll = Float.toString(MainApplication.mapItemContainer
					.getSelectedMapItem().getLatitude())
					+ ","
					+ Float.toString(MainApplication.mapItemContainer
							.getSelectedMapItem().getLongitude());
			FoursquareApi foursquareApi = new FoursquareApi(CLIENT_ID,
					CLIENT_SECRET, CALLBACK_URL);
			foursquareApi.setoAuthToken(oAuthToken);
			try
			{
				Result<Photo> photo = foursquareApi.photosAdd(checkinId, tipId,
						venueId, "", ll, 0.0, 0.0, 0.0, image);
			} catch (FoursquareApiException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void checkIn(final String venueID, final String comment, final byte[] image)
	{
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				String st = "";
				try
				{
					String sUrl = CHECK_IN_URL + "?oauth_token="
							+ MainApplication.FsqApp.getAccesToken()
							+ API_VERSION;
					URL url = new URL(sUrl);
					Log.d(TAG, "Opening URL " + url.toString());
					
					List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
					pairs.add(new BasicNameValuePair("venueId", venueID));
					if (comment!=null&&!comment.trim().equalsIgnoreCase(""))
						pairs.add(new BasicNameValuePair("shout", comment));
//					HttpResponse response = client.execute(post);
//					st = HttpHelper.streamToString(response.getEntity()
//							.getContent());
					st = HttpHelper.loadPostByUrl(sUrl, pairs);
					String ID = new JSONObject(st).getJSONObject("response").getJSONObject("checkin").getString("id");
					if (ID!=null&&image!=null)
						uploadPhoto(null, ID, null, image);
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				System.out.println(st);
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, null);
		checkinsList.add(venueID);
	}

	public static boolean isFSQConnected()
	{
		return MainApplication.FsqApp.hasAccessToken();
	}

	public static void addToTips(final String venueID, final String comment, final byte[] image)
	{
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				String st = "";
				String tipId = null;
				try
				{
					String sUrl = TIP_ADD_URL + "?oauth_token="
							+ MainApplication.FsqApp.getAccesToken()
							+ API_VERSION;
					List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
					pairs.add(new BasicNameValuePair("venueId", venueID));
					pairs.add(new BasicNameValuePair("text", comment));
					//HttpResponse response = client.execute(post);
					st = HttpHelper.loadPostByUrl(sUrl, pairs);
					JSONObject jRequest = new JSONObject(st);
					tipId = jRequest.getJSONObject("response")
							.getJSONObject("tip").getString("id");
					if (tipId!=null&&image!=null)
						uploadPhoto(null, null, tipId, image);

				} catch (Exception e)
				{
					e.printStackTrace();
				}
				System.out.println(st);
				FSQItem item = (FSQItem) MainApplication.mapItemContainer
						.getItemById(venueID);
				item.getOptionalInfo().addCommentFromResponse(st);
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, null);
	}

	public static void addToTodos(final String venueID)
	{
		if (!isInTodoList(venueID))
		{
			Runnable task = new Runnable()
			{
				@Override
				public void run()
				{
					String sUrl = TODO_ADD_URL + "?oauth_token="
							+ MainApplication.FsqApp.getAccesToken()
							+ API_VERSION;
					URL url;
					FSQTodo newTodo = new FSQTodo();
					try
					{

						List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
						pairs.add(new BasicNameValuePair("venueId", venueID));
						String st = HttpHelper.loadPostByUrl(sUrl, pairs);

						newTodo = newTodo.loadFromJSON_NewToDo(new JSONObject(
								(String) st).getJSONObject("response")
								.getJSONObject("item"));
						System.out.println(st);
					} catch (Exception e)
					{
						newTodo = null;
						e.printStackTrace();
					}
					if (newTodo != null)
						todosList.add(newTodo);
				}
			};
			MainApplication.pwAggregator.addTaskToQueue(task, null);
		}
	}

	public static List<FSQTodo> getTodos()
	{
		return todosList;
	}

	public static void loadCheckinsAsync()
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				List<String> checkins = new ArrayList<String>();
				if (isFSQConnected())
				{
					String st = "";
					try
					{
						String sUrl = CHECKINS_GET_URL + "?oauth_token="
								+ MainApplication.FsqApp.getAccesToken()
								+ "&sort=recent" + API_VERSION;
						st = HttpHelper.loadByUrl(sUrl);

						JSONArray response = new JSONObject(st)
								.getJSONObject("response")
								.getJSONObject("checkins")
								.getJSONArray("items");
						for (int i = 0; i < response.length(); i++)
						{
							int unixTime = response.getJSONObject(i).getInt(
									"createdAt");
							String item = response.getJSONObject(i)
									.getJSONObject("venue").getString("id");
							java.util.Date time = new java.util.Date(
									(long) unixTime * 1000);
							Date currTime = new Date();

							if (currTime.getDate() == time.getDate())
								checkins.add(item);
						}
					} catch (Exception e)
					{
						checkins = new ArrayList<String>();
						e.printStackTrace();
					}
					System.out.println(st);
				}
				synchronized (checkinsList)
				{
					checkinsList = checkins;
				}
				setCheckinsLoaded(true);
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, null);
	}

	protected static void setCheckinsLoaded(boolean b)
	{
		isCheckinsLoaded = b;
	}

	public static void loadTodosAsync()
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				List<FSQTodo> todos = new ArrayList<FSQTodo>();
				if (isFSQConnected())
				{
					String st = "";
					try
					{
						String sUrl = TODOS_GET_URL + "?oauth_token="
								+ MainApplication.FsqApp.getAccesToken()
								+ "&sort=recent" + API_VERSION;
						st = HttpHelper.loadByUrl(sUrl);

						JSONArray response = new JSONObject(st)
								.getJSONObject("response")
								.getJSONObject("todos").getJSONArray("items");
						for (int i = 0; i < response.length(); i++)
						{
							FSQTodo item = new FSQTodo().loadFromJSON(response
									.getJSONObject(i));
							if (item != null)
								todos.add(item);
						}
					} catch (Exception e)
					{
						todos = new ArrayList<FSQTodo>();
						e.printStackTrace();
					}
					System.out.println(st);
				}
				synchronized (todosList)
				{
					todosList = todos;
				}
				setTodosLoaded(true);
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, null);
	}

	public static boolean getTodosLoaded()
	{
		return isTodosLoaded;
	}

	public static boolean getCheckinsLoaded()
	{
		return isCheckinsLoaded;
	}

	public static void setTodosLoaded(boolean loaded)
	{
		isTodosLoaded = loaded;
	}

	public static boolean isInTodoList(String itemID)
	{
		if (getTodosLoaded())
		{
			boolean found = false;
			for (FSQTodo todo : todosList)
			{
				if (todo.getVenueId().equals(itemID))
				{
					found = true;
					break;
				}
			}
			return found;
		} else
			return false;
	}

	public static boolean isInCheckList(String id)
	{
		if (getCheckinsLoaded())
		{
			boolean found = false;
			for (String venueID : checkinsList)
			{
				if (venueID.equals(id))
				{
					found = true;
					break;
				}
			}
			return found;
		} else
			return false;
	}

	public static void loadBadgesAsync()
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				List<FSQBadge> todos = new ArrayList<FSQBadge>();
				if (isFSQConnected())
				{
					String st = "";
					try
					{
						String sUrl = BADGES_GET_URL + "?oauth_token="
								+ MainApplication.FsqApp.getAccesToken()
								+ "&sort=recent" + API_VERSION;
						st = HttpHelper.loadByUrl(sUrl);

						JSONObject response = new JSONObject(st).getJSONObject(
								"response").getJSONObject("badges");

						JSONArray names = response.names();

						for (int i = 0; i < response.length(); i++)
						{
							if (response.getJSONObject(names.getString(i))
									.getJSONArray("unlocks").length() > 0)// если
																			// значок
																			// получен
							{
								FSQBadge item = FSQBadge.loadFromJSON(response
										.getJSONObject(names.getString(i)));
								if (item != null)
									todos.add(item);
							}
						}
					} catch (Exception e)
					{
						todos = new ArrayList<FSQBadge>(0);
						e.printStackTrace();
					}
					System.out.println(st);
				}
				synchronized (badgesList)
				{
					badgesList = todos;
				}
				setBadgesLoaded(true);
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, null);
	}

	public static List<FSQBadge> getBadgesList()
	{
		return badgesList;
	}

	public static boolean getBadgessLoaded()
	{
		return isBadgesLoaded;
	}

	public static void setBadgesLoaded(boolean loaded)
	{
		isBadgesLoaded = loaded;
	}

	public static void loadImageAsync(final LoadingImageView iv,
			final UrlDrawable urlDr, final int big_or_small, boolean prioity)
	{
		Runnable preTask = new Runnable()
		{
			@Override
			public void run()
			{
				if (big_or_small == UrlDrawable.BIG_URL
						&& urlDr.getBigDrawable() == null)
				{
					Drawable dr = HttpHelper.loadPictureByUrl(urlDr.bigUrl);
					urlDr.setBigDrawable(dr);
				} else if (big_or_small == UrlDrawable.SMALL_URL
						&& urlDr.getSmallDrawable() == null)
				{
					urlDr.setSmallDrawable(HttpHelper
							.loadPictureByUrl(urlDr.smallUrl));
				}
			}
		};

		Runnable postTask = new Runnable()
		{
			@Override
			public void run()
			{
				Drawable pict;
				if (big_or_small == UrlDrawable.BIG_URL)
					pict = urlDr.getBigDrawable();
				else
					pict = urlDr.getSmallDrawable();
				iv.setDrawable(pict);
				iv.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Toast.makeText(iv.getContext(), "On Click",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(iv.getContext(),
								FullScrLoadingImageActivity.class);
						MainApplication.selectedItemPhoto = urlDr;
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						iv.getContext().startActivity(intent);
					}
				});
			}
		};

		if (!prioity)
			MainApplication.pwAggregator.addTaskToQueue(preTask, postTask);
		else
			MainApplication.pwAggregator.addPriorityTask(preTask, postTask);
	}

	public static void loadBadgesPictureAsync(FSQBadge fsqBadge)
	{
		// TODO Auto-generated method stub

	}
}
