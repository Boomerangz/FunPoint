package kz.crystalspring.funpoint.venues;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import kz.crystalspring.funpoint.FullScrLoadingImageActivity;
import kz.crystalspring.funpoint.MainApplication;
import kz.crystalspring.funpoint.WriteCommentActivity;
import kz.crystalspring.pointplus.HttpHelper;
import kz.crystalspring.pointplus.ImageCache;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.views.LoadingImageView;
import kz.sbeyer.atmpoint1.types.ItemCinema;
import kz.sbeyer.atmpoint1.types.ItemFood;
import kz.sbeyer.atmpoint1.types.ItemHotel;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

	private static final String CLIENT_STR = "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

	private static final String CHECK_IN_URL = API_URL + "/checkins/add";
	private static final String TIP_ADD_URL = API_URL + "/tips/add";
	private static final String TODO_ADD_URL = API_URL + "/lists/self/todos/additem";
	private static final String TODOS_GET_URL = API_URL + "/users/self/todos";
	private static final String BADGES_GET_URL = API_URL + "/users/self/badges";
	private static final String CHECKINS_GET_URL = API_URL + "/users/self/checkins";
	private static final String FRIEND_CHECKINS_GET_URL = API_URL + "/checkins/recent";
	private static final String EXPLORE_URL = API_URL + "/venues/explore";
	private static final String CATEGORIES_URL = API_URL + "/venues/categories";
	private static final String PHOTO_URL = API_URL + "/venues/";
	public static final String SELF_URL = API_URL + "/users/self";

	private static final String TAG = "FoursquareApi";
	private static final String API_VERSION = "&v=20120522";
	private static final String SEARCH_URL = API_URL + "/venues/search?";
	public static final int AREA_RADIUS = 1500;

	private static List<FSQTodo> todosList = new ArrayList<FSQTodo>(0);

	private static Set<String> todayCheckinsList = new HashSet<String>(0);
	private static Set<String> everCheckinsList = new HashSet<String>(0);

	private static List<FSQBadge> badgesList = new ArrayList<FSQBadge>(0);
	private static List<FSQFriendCheckin> friendFeedList = new ArrayList<FSQFriendCheckin>(0);
	private static HashMap<String, String> FSQCategories = null;
	private static List<FSQItem> exploreList = new ArrayList<FSQItem>(0);

	private static boolean isTodosLoaded = false;
	private static boolean isCheckinsLoaded = false;
	private static boolean isBadgesLoaded = false;
	private static boolean isFriendFeedLoaded = false;

	public static List<MapItem> loadItems(GeoPoint point, String category, int radius)
	{
		try
		{
			if (category.equals(MapItem.FSQ_UNDEFINED))
				category = null;
			List<MapItem> list;
			if (point != null)
				list = getNearby(point.getLatitudeE6() / 1e6, point.getLongitudeE6() / 1e6, category, radius);
			else
				list = getByCity(MainApplication.cityManager.getSelectedCityIfnull().toString(), category, radius);
			// list.addAll(getNearby(point.getLatitudeE6() / 1e6,
			// point.getLongitudeE6() / 1e6, category, 0));
			return list;
		} catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<MapItem>();
		}
	}

	public static ArrayList<MapItem> getNearby(double latitude, double longitude, String category, int radius) throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();

		try
		{
			String ll = String.valueOf(latitude) + "," + String.valueOf(longitude);
			String sUrl = SEARCH_URL + "ll=" + ll;

			if (category != null)
				sUrl += "&categoryId=" + category;

			if (radius > 0)
				sUrl += "&radius=" + Integer.toString(radius);
			if (category != null && category.equals(MapItem.FSQ_UNDEFINED))
				sUrl += "&intent=checkin";
			sUrl += CLIENT_STR + API_VERSION;

			System.out.println("на сервер отдан запрос на точки");
			String response = HttpHelper.getInstance().loadByUrl(sUrl);
			System.out.println("получена строка с точками с сервера");
			JSONObject jsonObj = new JSONObject(response);// (JSONObject) new
															// JSONTokener(response).nextValue();

			JSONArray items = (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");

			int length = items.length();

			if (length > 0)
			{
				for (int i = 0; i < length; i++)
				{
					JSONObject item = (JSONObject) items.get(i);

					FSQItem venue;
					category = (String) ProjectUtils.ifnull(category, MapItem.FSQ_UNDEFINED);
					if (category.equals(MapItem.FSQ_TYPE_FOOD))
						venue = new ItemFood();
					else if (category.equals(MapItem.FSQ_TYPE_HOTEL))
						venue = new ItemHotel();
					else if (category.equals(MapItem.FSQ_TYPE_CINEMA))
						venue = new ItemCinema();
					else
						venue = new FSQItem();
					venue.setCategory(category);
					if (venue.loadFromJSON(item) != null)
					{
						venueList.add(venue);
					}
				}
			}
		} catch (Exception ex)
		{
			throw ex;
		}
		System.out.println("Составлен список объектов на основе ответа от сервера");
		return venueList;
	}

	public static ArrayList<MapItem> getByCity(String city, String category, int radius) throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();

		try
		{
			String sUrl = SEARCH_URL + "near=" + city;

			if (category != null)
				sUrl += "&categoryId=" + category;

			if (radius > 0)
				sUrl += "&radius=" + Integer.toString(radius);

			sUrl += CLIENT_STR + API_VERSION;
			System.out.println("на сервер отдан запрос на точки");
			String response = HttpHelper.getInstance().loadByUrl(sUrl);
			System.out.println("получена строка с точками с сервера");
			JSONObject jsonObj = new JSONObject(response);// (JSONObject) new
															// JSONTokener(response).nextValue();

			JSONArray items = (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");

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
					venue.setCategory(category);
					if (venue.loadFromJSON(item) != null)
					{
						venueList.add(venue);
					}
				}
			}
		} catch (Exception ex)
		{
			throw ex;
		}
		System.out.println("Составлен список объектов на основе ответа от сервера");
		return venueList;
	}

	public static ArrayList<MapItem> getByName(double latitude, double longitude, String category, String name) throws Exception
	{
		ArrayList<MapItem> venueList = new ArrayList<MapItem>();
		try
		{
			String ll = String.valueOf(latitude) + "," + String.valueOf(longitude);

			String sUrl = API_URL + "/venues/search?ll=" + ll;

			if (!(category == null || category.equals(MapItem.FSQ_UNDEFINED)))
				sUrl += "&categoryId=" + category;

			if (name != null)
				sUrl += "&query=" + URLEncoder.encode(name);

			sUrl += "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + API_VERSION;
			System.out.println("на сервер отдан запрос на точки");
			String response = HttpHelper.getInstance().loadByUrl(sUrl);
			System.out.println("получена строка с точками с сервера");
			JSONObject jsonObj = new JSONObject(response);// (JSONObject) new
															// JSONTokener(response).nextValue();

			JSONArray items = (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");

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

	public static JSONObject loadVenueInformation(String id)
	{
		String sUrl = API_URL + "/venues/" + id;

		sUrl += "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + API_VERSION;

		String response = HttpHelper.getInstance().loadByUrl(sUrl);
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

	public static void uploadPhoto(String venueId, String checkinId, String tipId, byte[] image)
	{
		if (MainApplication.FsqApp.hasAccessToken())
		{
			String oAuthToken = MainApplication.FsqApp.getAccesToken();
			String ll = Float.toString(MainApplication.mapItemContainer.getSelectedMapItem().getLatitude()) + ","
					+ Float.toString(MainApplication.mapItemContainer.getSelectedMapItem().getLongitude());
			FoursquareApi foursquareApi = new FoursquareApi(CLIENT_ID, CLIENT_SECRET, CALLBACK_URL);
			foursquareApi.setoAuthToken(oAuthToken);
			try
			{
				Result<Photo> photo = foursquareApi.photosAdd(checkinId, tipId, venueId, "", ll, 0.0, 0.0, 0.0, image);
			} catch (FoursquareApiException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static boolean isCheckiningNow = false;

	public static boolean isCheckiningNow()
	{
		return isCheckiningNow;
	}

	public static void setCheckiningNow(boolean _isCheckiningNow)
	{
		isCheckiningNow = _isCheckiningNow;
	}

	public static void checkIn(final String venueID, final String comment, final byte[] image)
	{
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				setCheckiningNow(true);
				String st = "";
				try
				{
					String sUrl = CHECK_IN_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
					URL url = new URL(sUrl);
					Log.d(TAG, "Opening URL " + url.toString());

					List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
					pairs.add(new BasicNameValuePair("venueId", venueID));
					if (comment != null && !comment.trim().equalsIgnoreCase(""))
						pairs.add(new BasicNameValuePair("shout", comment));
					// HttpResponse response = client.execute(post);
					// st = HttpHelper.streamToString(response.getEntity()
					// .getContent());
					st = "{\"meta\":{\"code\":200},\"notifications\":[{\"type\":\"notificationTray\",\"item\":{\"unreadCount\":0}},{\"type\":\"message\",\"item\":{\"message\":\"ОК! Мы нашли вас в Street Bar & Grill. Вы были здесь 11 раз(а).\"}},{\"type\":\"tip\",\"item\":{\"tip\":{\"id\":\"5031a2cae4b0bde72784cbe8\",\"createdAt\":1345430218,\"text\":\"#7. Для мальчишника\",\"likes\":{\"count\":0,\"groups\":[]},\"like\":false,\"todo\":{\"count\":0},\"done\":{\"count\":1},\"user\":{\"id\":\"5305686\",\"firstName\":\"Anry\",\"lastName\":\"A.\",\"photo\":\"https://is0.4sqi.net/userpix_thumbs/KVXX4NB21ROKP1QP.jpg\",\"tips\":{\"count\":18},\"lists\":{\"groups\":[{\"type\":\"created\",\"count\":2,\"items\":[]}]},\"gender\":\"male\",\"homeCity\":\"Almaty, Kazakhstan\",\"bio\":\"\",\"contact\":{\"facebook\":\"100000690443377\"}}},\"name\":\"Популярная подсказка\"}},{\"type\":\"leaderboard\",\"item\":{\"leaderboard\":[{\"user\":{\"id\":\"27342785\",\"firstName\":\"igor\",\"lastName\":\"zygin\",\"relationship\":\"self\",\"photo\":\"https://foursquare.com/img/blank_boy.png\",\"tips\":{\"count\":17},\"lists\":{\"groups\":[{\"type\":\"created\",\"count\":1,\"items\":[]}]},\"gender\":\"male\",\"homeCity\":\"\",\"bio\":\"\",\"contact\":{\"email\":\"izygin@gmail.com\"}},\"scores\":{\"recent\":76,\"max\":79,\"checkinsCount\":23},\"rank\":1},{\"user\":{\"id\":\"9831657\",\"firstName\":\"Alexey\",\"lastName\":\"Tuchin\",\"relationship\":\"friend\",\"photo\":\"https://is1.4sqi.net/userpix_thumbs/OH501PJM34D1DMOK.png\",\"tips\":{\"count\":1},\"lists\":{\"groups\":[{\"type\":\"created\",\"count\":3,\"items\":[]}]},\"gender\":\"male\",\"homeCity\":\"Almaty, Kazakhstan\",\"bio\":\"Прилетел спасать Землю\",\"contact\":{\"email\":\"atuchin@gmail.com\",\"facebook\":\"100000753092441\"}},\"scores\":{\"recent\":70,\"max\":114,\"checkinsCount\":17},\"rank\":2},{\"user\":{\"id\":\"32728674\",\"firstName\":\"Sergey\",\"lastName\":\"Chekmarev\",\"relationship\":\"friend\",\"photo\":\"https://is1.4sqi.net/userpix_thumbs/KFI131TPBJHAPBQ4.jpg\",\"tips\":{\"count\":0},\"lists\":{\"groups\":[{\"type\":\"created\",\"count\":3,\"items\":[]}]},\"gender\":\"male\",\"homeCity\":\"Almaty\",\"bio\":\"\",\"contact\":{\"phone\":\"87053388442\",\"email\":\"nine.priest@gmail.com\",\"facebook\":\"100001895567569\"}},\"scores\":{\"recent\":14,\"max\":75,\"checkinsCount\":4},\"rank\":3}],\"message\":\"Вы №1! Вы возглавляете список лидеров.\",\"scores\":[{\"points\":1,\"icon\":\"https://foursquare.com/img/points/defaultpointsicon2.png\",\"message\":\"Nice! You've checked in 2 times today!\"}],\"total\":1}},{\"type\":\"score\",\"item\":{\"scores\":[{\"points\":1,\"icon\":\"https://foursquare.com/img/points/defaultpointsicon2.png\",\"message\":\"Nice! You've checked in 2 times today!\"}],\"total\":1}}],\"response\":{\"checkin\":{\"id\":\"50612fb7e4b06bd054586b53\",\"createdAt\":1348546487,\"type\":\"checkin\",\"timeZone\":\"Asia/Almaty\",\"timeZoneOffset\":360,\"venue\":{\"id\":\"4f278959e4b0ca643f33e9ac\",\"name\":\"Street Bar & Grill\",\"contact\":{},\"location\":{\"lat\":43.23659306978218,\"lng\":76.90875141782021,\"country\":\"Kazakhstan\",\"cc\":\"KZ\"},\"categories\":[{\"id\":\"4bf58dd8d48988d14e941735\",\"name\":\"Ресторан американской кухни\",\"pluralName\":\"Рестораны американской кухни\",\"shortName\":\"Американский\",\"icon\":{\"prefix\":\"https://foursquare.com/img/categories/food/default_\",\"sizes\":[32,44,64,88,256],\"name\":\".png\"},\"primary\":true}],\"verified\":false,\"stats\":{\"checkinsCount\":126,\"usersCount\":43,\"tipCount\":3},\"likes\":{\"count\":0,\"groups\":[]},\"beenHere\":{\"count\":0}},\"likes\":{\"count\":0,\"groups\":[]},\"photos\":{\"count\":0,\"items\":[]},\"comments\":{\"count\":0,\"items\":[]},\"source\":{\"name\":\"walker\",\"url\":\"http://homeplus.kz\"}}}}";
					st = HttpHelper.getInstance().loadPostByUrl(sUrl, pairs);
					String ID = new JSONObject(st).getJSONObject("response").getJSONObject("checkin").getString("id");
					if (ID != null && image != null)
						uploadPhoto(null, ID, null, image);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				WriteCommentActivity.setResponse(st);
				System.out.println(st);
				setCheckiningNow(false);
			}
		};
		Runnable postTask = new Runnable()
		{
			@Override
			public void run()
			{
				MainApplication.refreshMapItems();
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(task, postTask);
		todayCheckinsList.add(venueID);
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
					String sUrl = TIP_ADD_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
					List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
					pairs.add(new BasicNameValuePair("venueId", venueID));
					pairs.add(new BasicNameValuePair("text", comment));
					// HttpResponse response = client.execute(post);
					st = HttpHelper.getInstance().loadPostByUrl(sUrl, pairs);
					JSONObject jRequest = new JSONObject(st);
					tipId = jRequest.getJSONObject("response").getJSONObject("tip").getString("id");
					if (tipId != null && image != null)
						uploadPhoto(null, null, tipId, image);

				} catch (Exception e)
				{
					e.printStackTrace();
				}
				System.out.println(st);
				FSQItem item = (FSQItem) MainApplication.mapItemContainer.getItemById(venueID);
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
					String sUrl = TODO_ADD_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
					URL url;
					FSQTodo newTodo = new FSQTodo();
					try
					{

						List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
						pairs.add(new BasicNameValuePair("venueId", venueID));
						String st = HttpHelper.getInstance().loadPostByUrl(sUrl, pairs);

						newTodo = newTodo.loadFromJSON_NewToDo(new JSONObject((String) st).getJSONObject("response").getJSONObject("item"));
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
				Set<String> checkins = new HashSet<String>();
				Set<String> everCheckins = new HashSet<String>();
				if (isFSQConnected())
				{
					String st = "";
					try
					{
						String sUrl = CHECKINS_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
						st = HttpHelper.getInstance().loadByUrl(sUrl);

						JSONArray response = new JSONObject(st).getJSONObject("response").getJSONObject("checkins").getJSONArray("items");
						for (int i = 0; i < response.length(); i++)
						{
							int unixTime = response.getJSONObject(i).getInt("createdAt");
							String item = response.getJSONObject(i).getJSONObject("venue").getString("id");
							java.util.Date time = new java.util.Date((long) unixTime * 1000);
							Date currTime = new Date();

							if (currTime.getDate() == time.getDate())
								checkins.add(item);
							everCheckins.add(item);
						}
					} catch (Exception e)
					{
						checkins = new HashSet<String>();
						e.printStackTrace();
					}
					System.out.println(st);
				}
				synchronized (todayCheckinsList)
				{
					todayCheckinsList = checkins;

				}
				synchronized (todayCheckinsList)
				{
					everCheckinsList = everCheckins;
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
						String sUrl = TODOS_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent"
								+ API_VERSION;
						st = HttpHelper.getInstance().loadByUrl(sUrl);

						JSONArray response = new JSONObject(st).getJSONObject("response").getJSONObject("todos").getJSONArray("items");
						for (int i = 0; i < response.length(); i++)
						{
							FSQTodo item = new FSQTodo().loadFromJSON(response.getJSONObject(i));
							if (item != null)
								todos.add(item);
						}
						System.out.println(st);
					} catch (Exception e)
					{
						todos = new ArrayList<FSQTodo>();
						e.printStackTrace();
					}
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
			for (String venueID : todayCheckinsList)
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

	public static boolean isInEverCheckList(String id)
	{
		if (getCheckinsLoaded())
		{
			return everCheckinsList.contains(id);
		} else
			return false;
	}

	public static void loadFriendCheckins()
	{

	}

	public static void loadBadgesAsync()
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				List<FSQBadge> badges = new ArrayList<FSQBadge>();
				if (isFSQConnected())
				{
					String st = "";
					try
					{
						String sUrl = BADGES_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent"
								+ API_VERSION;
						st = HttpHelper.getInstance().loadByUrl(sUrl);

						JSONObject response = new JSONObject(st).getJSONObject("response").getJSONObject("badges");

						JSONArray names = response.names();

						for (int i = 0; i < response.length(); i++)
						{
							if (response.getJSONObject(names.getString(i)).getJSONArray("unlocks").length() > 0)// если
																												// значок
																												// получен
							{
								FSQBadge item = FSQBadge.loadFromJSON(response.getJSONObject(names.getString(i)));
								if (item != null)
									badges.add(item);
							}
						}
					} catch (Exception e)
					{
						badges = new ArrayList<FSQBadge>(0);
						e.printStackTrace();
					}
					System.out.println(st);
				}
				synchronized (badgesList)
				{
					badgesList = badges;
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

	public static boolean getFriendFeedLoaded()
	{
		return isFriendFeedLoaded;
	}

	public static void setBadgesLoaded(boolean loaded)
	{
		isBadgesLoaded = loaded;
	}

	public static void loadImageAsync(final LoadingImageView iv, final UrlDrawable urlDr, final int big_or_small, boolean prioity,
			final OnClickListener listner)
	{
		if (urlDr != null)
		{
			final Integer unicHash = ProjectUtils.ifnull(urlDr, new Object()).hashCode();
			if (iv != null)
				iv.setDrawable(null);
			Runnable preTask = new Runnable()
			{
				@Override
				public void run()
				{
					if (iv != null)
						iv.setTag(unicHash);
					if (big_or_small == UrlDrawable.BIG_URL && urlDr.getBigDrawable() == null)
					{
						String sUrl = (String) ProjectUtils.ifnull(urlDr.bigUrl, urlDr.smallUrl);
						if (sUrl != null)
						{
							Drawable dr = new BitmapDrawable(HttpHelper.getInstance().loadPictureByUrl(sUrl));
							urlDr.setBigDrawable(dr);
						}
					} else if (big_or_small == UrlDrawable.SMALL_URL && urlDr.getSmallDrawable() == null)
					{
						String sUrl = (String) ProjectUtils.ifnull(urlDr.smallUrl, urlDr.bigUrl);
						if (sUrl != null)
						{
							Drawable dr = new BitmapDrawable(HttpHelper.getInstance().loadPictureByUrl(sUrl));
							urlDr.setSmallDrawable(dr);
						}
					}
				}
			};

			Runnable postTask = new Runnable()
			{
				@Override
				public void run()
				{
					if (iv != null && iv.getTag() != null && iv.getTag().equals(unicHash))
					{
						Drawable pict;
						if (big_or_small == UrlDrawable.BIG_URL)
							pict = urlDr.getBigDrawable();
						else
							pict = urlDr.getSmallDrawable();
						iv.setDrawable(pict);
						OnClickListener localListner;
						if (listner == null)
							localListner = new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									Toast.makeText(iv.getContext(), "On Click", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(iv.getContext(), FullScrLoadingImageActivity.class);
									MainApplication.selectedItemPhoto = urlDr;
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									iv.getContext().startActivity(intent);
								}
							};
						else
							localListner = listner;
						iv.setOnClickListener(localListner);
					}
				}
			};

			if (!prioity)
				MainApplication.pwAggregator.addTaskToQueue(preTask, postTask);
			else
				MainApplication.pwAggregator.addPriorityTask(preTask, postTask);
		}
	}

	public static void loadFriendFeed()
	{
		Runnable preTask = new Runnable()
		{
			@Override
			public void run()
			{
				List<FSQFriendCheckin> checkins = new ArrayList<FSQFriendCheckin>();
				if (isFSQConnected())
				{
					String st = "";
					friendFeedList = new ArrayList(0);
					try
					{
						String sUrl = FRIEND_CHECKINS_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent"
								+ API_VERSION;
						st = HttpHelper.getInstance().loadByUrl(sUrl);

						JSONObject response = new JSONObject(st);
						if (response.getJSONObject("meta").getInt("code") == 200)
						{
							JSONArray feed = response.getJSONObject("response").getJSONArray("recent");
							for (int i = 0; i < feed.length(); i++)
							{
								JSONObject checkin = feed.getJSONObject(i);
								try
								{
									FSQFriendCheckin fcheck = new FSQFriendCheckin(checkin);
									addFriendFeed(fcheck);
								} catch (Exception e)
								{

								}
							}
						}
					} catch (Exception e)
					{
						checkins = new ArrayList<FSQFriendCheckin>(0);
						e.printStackTrace();
					}
					System.out.println(st);
				}
			}

			private void addFriendFeed(FSQFriendCheckin fcheck)
			{
				friendFeedList.add(fcheck);
			}
		};

		Runnable postTask = new Runnable()
		{
			@Override
			public void run()
			{
				isFriendFeedLoaded = true;
				MainApplication.refreshMapItems();
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(preTask, postTask);
	}

	public static boolean isFriendFeedLoaded()
	{
		return isFriendFeedLoaded;
	}

	public static synchronized void loadCategories()
	{
		Runnable preTask = new Runnable()
		{
			@Override
			public void run()
			{
				Log.w("Categories", "Stared to load");
				String st = "";
				HashMap<String, String> map = new HashMap<String, String>();
				try
				{
					String sUrl = CATEGORIES_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
					st = HttpHelper.getInstance().loadByUrl(sUrl);
					Log.w("Categories", "Point 1");
					JSONObject response = new JSONObject(st);
					Log.w("Categories", "Point 2");
					if (response.getJSONObject("meta").getInt("code") == 200)
					{
						response = response.getJSONObject("response");
						JSONArray globalCategories = response.getJSONArray("categories");
						for (int i = 0; i < globalCategories.length(); i++)
						{
							JSONObject category = globalCategories.getJSONObject(i);
							List<String> list = goThrough(category);
							for (String ctgr : list)
							{
								map.put(ctgr, category.getString("id"));
							}
						}
					}
					Log.w("Categories", "Point 3");
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				for (String str : MapItem.TYPES_ARRAY)
				{
					map.remove(str);
					map.put(str, str);
				}
				Log.w("Categories", "Ended to load");
				FSQCategories = map;
			}

			private List<String> goThrough(JSONObject response)
			{
				List<String> list = new LinkedList<String>();
				try
				{
					list.add(response.getString("id"));
				} catch (JSONException e1)
				{
					e1.printStackTrace();
				}
				if (response.has("categories"))
				{
					JSONArray array;
					try
					{
						array = response.getJSONArray("categories");
						for (int i = 0; i < array.length(); i++)
						{
							list.addAll(goThrough(array.getJSONObject(i)));
						}
					} catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return list;
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(preTask, null);
	}

	public static String getGlobalCategory(String localCategory)
	{
		String globalCat = null;
		while (FSQCategories == null)
		{
			for (int i = 0; i < 32000; i++)
			{
			}
		}
		synchronized (FSQCategories)
		{
			globalCat = FSQCategories.get(localCategory);
			return globalCat;
		}
	}

	public static void loadBadgesPictureAsync(FSQBadge fsqBadge)
	{
		// TODO Auto-generated method stub

	}

	public static List<FSQFriendCheckin> getFriendFeed()
	{
		return friendFeedList;
	}

	protected static boolean isExploringLoaded = false;

	public static void loadExploring(final GeoPoint point)
	{
		isExploringLoaded = false;
		exploreList = new ArrayList(0);
		Runnable preTask = new Runnable()
		{
			@Override
			public void run()
			{
				List<FSQItem> exploreItems = new ArrayList<FSQItem>();

				if (isFSQConnected())
				{
					String st = "";
					try
					{
						String lat = String.valueOf((double) (point.getLatitudeE6() / 1e6));
						String lon = String.valueOf((double) (point.getLongitudeE6() / 1e6));
						String ll = lat + "," + lon;

						String sUrl = EXPLORE_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&ll=" + ll + API_VERSION;
						st = HttpHelper.getInstance().loadByUrl(sUrl);

						JSONObject response = new JSONObject(st);
						if (response.getJSONObject("meta").getInt("code") == 200)
						{
							JSONArray array = response.getJSONObject("response").getJSONArray("groups").getJSONObject(0)
									.getJSONArray("items");
							for (int i = 0; i < array.length(); i++)
							{
								JSONObject place = array.getJSONObject(i).getJSONObject("venue");
								FSQItem item = (FSQItem) MainApplication.mapItemContainer.addItem(place);
								exploreItems.add(item);
							}
						}
					} catch (Exception e)
					{
						exploreItems = new ArrayList<FSQItem>(0);
						e.printStackTrace();
					}
					System.out.println(st);
					synchronized (exploreList)
					{
						exploreList = exploreItems;
					}
				}
			}
		};

		Runnable postTask = new Runnable()
		{
			@Override
			public void run()
			{
				isExploringLoaded = true;
				MainApplication.refreshMapItems();
			}
		};
		MainApplication.pwAggregator.addTaskToQueue(preTask, postTask);
	}

	public static boolean getExploringLoaded()
	{
		return isExploringLoaded;
	}

	public static List<FSQItem> getExplorer()
	{
		return exploreList;
	}

	public static Bitmap loadTitlePhotoForVenue(String id)
	{
		Bitmap image = null;
		ImageCache cache = ImageCache.getInstance();
		String cacheId = cache.getTitlePhotoUrlIfHave(id);
		if (cacheId == null || !cache.hasImage(cacheId))
		{
			String imageUrl = "";
			String sUrl = PHOTO_URL + id + "/photos?limit=1&group=venue" + CLIENT_STR + API_VERSION;
			String response = HttpHelper.getInstance().loadByUrl(sUrl);
			try
			{
				JSONObject jObject = new JSONObject(response);
				if (jObject.getJSONObject("meta").getInt("code") == 200)
				{
					JSONObject jResponse = jObject.getJSONObject("response");
					JSONObject jGroup = jResponse.getJSONObject("photos");
					jGroup = jGroup.getJSONArray("items").getJSONObject(0).getJSONObject("sizes");
					int count = jGroup.getInt("count") - 2;
					if (count >= 0)
					{
						JSONObject jSize = jGroup.getJSONArray("items").getJSONObject(count);
						imageUrl = jSize.getString("url");
					}
				} else
					imageUrl = "";
				int i;
			} catch (JSONException e)
			{
				imageUrl = "";
				e.printStackTrace();
			}
			cache.addPhotoUrl(id, imageUrl);
			if (imageUrl != null && !imageUrl.equals(""))
				image = HttpHelper.getInstance().loadPictureByUrl(imageUrl);
			cache.addToCache(cacheId, image);
		} else
		{
			image = cache.getImage(cacheId);
		}
		return image;
	}

	public static boolean isExploringLoaded()
	{
		return isExploringLoaded;
	}

	public static void dropUserActivity()
	{
		isBadgesLoaded = false;
		isCheckinsLoaded = false;
		isExploringLoaded = false;
		isFriendFeedLoaded = false;
		isTodosLoaded = false;
	}

	public static void loadStats()
	{
		// String sUrl = ;
		// String response = HttpHelper.getInstance().loadByUrl(sUrl);
	}

	public static void loadUserInfo(FSQUser fsqUser)
	{
		if (MainApplication.FsqApp.hasAccessToken())
		{
			Runnable preTask = new Runnable()
			{
				@Override
				public void run()
				{
					String sUrl = SELF_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
					String response = HttpHelper.getInstance().loadByUrl(sUrl);
					try
					{
						JSONObject jObject = new JSONObject(response);
						if (jObject.getJSONObject("meta").getInt("code") == 200)
						{
							jObject = jObject.getJSONObject("response");
							FSQUser.getInstance().modify(jObject);
						}
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			};

			Runnable postTask = new Runnable()
			{
				@Override
				public void run()
				{
					MainApplication.refreshMapItems();
				}
			};
			MainApplication.pwAggregator.addTaskToQueue(preTask, postTask);
		}
	}

	public static List<BasicNameValuePair> getUrlForProxy(GeoPoint point)
	{
		List<String> urlList = new ArrayList();

		// 76.0,43.0
		String ll = null;
		if (MainApplication.FsqApp.hasAccessToken())
		{
			String getTodos = TODOS_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent" + API_VERSION;
			String getBadges = BADGES_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent" + API_VERSION;
			String getCheckins = CHECKINS_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent" + API_VERSION;
			String getFriendCheckins = FRIEND_CHECKINS_GET_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&sort=recent"
					+ API_VERSION;
			String getSelf = SELF_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
			String getCategories = CATEGORIES_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + API_VERSION;
			if (point != null)
			{
				String lat = String.valueOf((double) (point.getLatitudeE6() / 1e6));
				String lon = String.valueOf((double) (point.getLongitudeE6() / 1e6));
				ll = lat + "," + lon;
				String getExplore = EXPLORE_URL + "?oauth_token=" + MainApplication.FsqApp.getAccesToken() + "&ll=" + ll + API_VERSION;
				urlList.add(getExplore);
			}
			urlList.add(getCategories);
			urlList.add(getTodos);
			urlList.add(getBadges);
			urlList.add(getCheckins);
			urlList.add(getFriendCheckins);
			urlList.add(getSelf);
		}

		if (point != null && ll != null)
		{
			ArrayList<String> filterArray = new ArrayList();
			filterArray.addAll(Arrays.asList(MapItem.TYPES_ARRAY));
			int radius;
			for (String category : filterArray)
			{
				String sUrl = SEARCH_URL + "ll=" + ll;
				if (!category.equals(MapItem.FSQ_TYPE_FOOD))
					radius = 0;
				else
					radius = FSQConnector.AREA_RADIUS;
				if (category.equals(MapItem.FSQ_UNDEFINED))
					category = null;
				if (category != null)
				{
					sUrl += "&categoryId=" + category;
					if (category.equals(MapItem.FSQ_UNDEFINED))
						sUrl += "&intent=checkin";
				}
				if (radius > 0)
					sUrl += "&radius=" + Integer.toString(radius);
				sUrl += "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + API_VERSION;
				urlList.add(sUrl);
			}
		}

		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		int i = 0;
		for (String s : urlList)
		{
			i++;
			String paramName = "url_" + Integer.toString(i);
			params.add(new BasicNameValuePair(paramName, s));
		}
		return params;
	}
}
