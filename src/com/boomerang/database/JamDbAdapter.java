package com.boomerang.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class JamDbAdapter
{
	private static final String DATABASE_NAME = "jam_kz.db";
	private static final String KEY_ID = "_id";
	private static final String TYPE_TEXT = " text not null ";
	private static final String TYPE_INT = " int not null ";

	private static final String CINEMA_EVENT_TABLE_NAME = "cinemaEventsTable";
	private static final String CINEMA_PLACES_TABLE_NAME = "cinemaPlacesTable";
	private static final String CINEMA_SECTIONPARENT_TABLE_NAME = "cinemaSectionParentTable";
	private static final String CINEMA_SECTIONCHILD_TABLE_NAME = "cinemaSectionChildTable";
	private static final int DATABASE_VERSION =22;

	private static final String CREATE_CINEMA_EVENT_TABLE = "create table "
			+ CINEMA_EVENT_TABLE_NAME + " (" + KEY_ID
			+ " integer primary key autoincrement, " + " event_id " + TYPE_INT
			+ ", type " + TYPE_TEXT + ", title " + TYPE_TEXT + ", country "
			+ TYPE_TEXT + ", year " + TYPE_TEXT + " , genre " + TYPE_TEXT
			+ ", director " + TYPE_TEXT + ", cast " + TYPE_TEXT
			+ ", information " + TYPE_TEXT + ", description " + TYPE_TEXT
			+ ", image " + TYPE_TEXT + ", video " + TYPE_TEXT + ");";

	private static final String CREATE_CINEMA_PLACES_TABLE = "create table "
			+ CINEMA_PLACES_TABLE_NAME + " (" + KEY_ID
			+ " integer primary key autoincrement  , " + " place_id "
			+ TYPE_INT + ",type " + TYPE_TEXT + ", title " + TYPE_TEXT
			+ ", city " + TYPE_TEXT + ",address " + TYPE_TEXT + ", phone "
			+ TYPE_TEXT + ", review " + TYPE_TEXT + ", fsq_id " + TYPE_TEXT
			+ ");";

	private static final String CREATE_CINEMA_SECTIONPARENT_TABLE = "create table "
			+ CINEMA_SECTIONPARENT_TABLE_NAME
			+ " ("
			+ KEY_ID
			+ " integer primary key autoincrement, "
			+ " section_id "
			+ TYPE_TEXT
			+ ", date "
			+ TYPE_TEXT
			+ ",day "
			+ TYPE_TEXT
			+ ", event_id "
			+ TYPE_INT
			+ ", place_id "
			+ TYPE_INT
			+ ", city  "
			+ TYPE_TEXT + ", ticket_flag " + TYPE_INT + ");";

	private static final String CREATE_CINEMA_SECTIONCHILD_TABLE = "create table "
			+ CINEMA_SECTIONCHILD_TABLE_NAME
			+ " ("
			+ KEY_ID
			+ " integer primary key autoincrement, "
			+ " time_id "
			+ TYPE_INT
			+ ", section_id "
			+ TYPE_TEXT
			+ ", hash "
			+ TYPE_TEXT
			+ ", time "
			+ TYPE_TEXT + ");";
	
	private static final String CREATE_CINEMA_SECTIONCHILD_INDEX="create index if not exists child_index on "+CINEMA_SECTIONCHILD_TABLE_NAME+" (time_id)";

	private static final String[] CREATE_ARRAY = { CREATE_CINEMA_EVENT_TABLE,
			CREATE_CINEMA_PLACES_TABLE, CREATE_CINEMA_SECTIONPARENT_TABLE,
			CREATE_CINEMA_SECTIONCHILD_TABLE, CREATE_CINEMA_SECTIONCHILD_INDEX};
	private static final String[] TABLE_ARRAY = { CINEMA_EVENT_TABLE_NAME,
			CINEMA_PLACES_TABLE_NAME, CINEMA_SECTIONPARENT_TABLE_NAME,
			CINEMA_SECTIONCHILD_TABLE_NAME };

	private SQLiteDatabase db;
	private final Context context;
	private myDbHelper dbHelper;

	public JamDbAdapter(Context context)
	{
		this.context = context;
		dbHelper = new myDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public JamDbAdapter open() throws SQLException
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		db.close();
	}

	public Cursor selectSQL(String s)
	{
		return db.rawQuery(s, null);
	}

	public void execSQL(String s)
	{
		db.execSQL(s);
	}

	public synchronized void savePlacesToDB(JSONArray jArray)
	{
		int success = 0;
		System.out.println("Загрузка данных в БД начата");
		for (int i = 0; i < jArray.length(); i++)
		{
			try
			{
				JSONObject jPlace = jArray.getJSONObject(i);
				String type = jPlace.getString("type").replace("\"", "\\\"");
				String title = jPlace.getString("title").replace("\"", "\\\"");
				String address = jPlace.has("address") ? jPlace.getString(
						"address").replace("\"", "\\\"") : "";
				String city = jPlace.getString("city").replace("\"", "\\\"");
				String phone = jPlace.has("phone") ? jPlace.getString("phone")
						.replace("\"", "\\\"") : "";
				// Сложная ситуация, когда пустой review воспринимается как
				// строка. поэтому идет проверка JSONObject ли это
				String review = jPlace.has("review")
						&& JSONObject.class.isInstance(jPlace.get("review"))
						&& jPlace.getJSONObject("review").has("short") ? jPlace
						.getJSONObject("review").getString("short")
						.replace("\"", "\\\"") : "";
				
			

				int place_id = jPlace.getInt("id");
				String fsq_id;
				switch (place_id)
				{
				case 829:
					fsq_id = "4cd5798e94848cfa7883e6b1";
					break;
				case 949:
					fsq_id = "4ea8208277c8129d55c2501b";
					break;
				case 309:
					fsq_id = "4e885fca490102a7a3de53a4";
					break;
				default:
					fsq_id = "";
				}

				String insertQuery = "insert into "
						+ CINEMA_PLACES_TABLE_NAME
						+ " (place_id,type,title,address,city,phone, review, fsq_id) ";

				String selectQuery = "select a.* from " + "(select "
						+ Integer.toString(place_id) + " as place_id, "
						+ "          '" + type + "' as type," + " 		   '"
						+ title + "' as title," + " 		   '" + address
						+ "' as address," + " 		   '" + city + "' as city,"
						+ " 		   '" + phone + "' as phone," + " 		   '"
						+ review + "' as review," + " 		   '" + fsq_id
						+ "' as fsq_id" + " ) as a " + "left join "
						+ CINEMA_PLACES_TABLE_NAME + " as b "
						+ "on a.place_id=b.place_id "
						+ "where b.place_id is null ";

				db.execSQL(insertQuery + selectQuery);
				success++;
				System.out.println(Integer.toString(i));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Всего :" + Integer.toString(jArray.length())
				+ " Успешно:" + Integer.toString(success));
	}

	public synchronized void saveEventsToDB(JSONArray jArray)
	{
		int success = 0;
		System.out.println("Загрузка событий в БД начата");
		for (int i = 0; i < jArray.length(); i++)
		{
			try
			{
				JSONObject jEvent = jArray.getJSONObject(i);

				int event_id = jEvent.getInt("id");
				String type = jEvent.getString("type");
				String title = jEvent.getString("title");
				String country = jEvent.getString("country");
				String year = jEvent.has("year") ? jEvent.getString("year")
						: "";
				String genre = jEvent.getString("genre");
				String director = jEvent.has("director") ? jEvent
						.getString("director") : "";
				String cast = jEvent.has("cast") ? jEvent.getString("cast")
						: "";
				String information = jEvent.has("information") ? jEvent
						.getString("information") : "";
				String description = jEvent.has("description") ? jEvent
						.getString("description") : "";
				String image = jEvent.has("image") ? jEvent.getString("image")
						: "";
				String video = jEvent.has("video") ? jEvent.getString("video")
						: "";

				String insertQuery = "insert into "
						+ CINEMA_EVENT_TABLE_NAME
						+ " ( event_id,type,title,country,year,genre, director, cast, information, description, image, video) ";

				String selectQuery = "select a.* from " + "(select "
						+ Integer.toString(event_id) + " as event_id, "
						+ "          '" + type + "' as type," + " 		   '"
						+ title + "' as title," + " 		   '" + country
						+ "' as country," + " 		   '" + year + "' as year,"
						+ " 		   '" + genre + "' as genre," + " 		   '"
						+ director + "' as director," + " 		   '" + cast
						+ "' as cast," + " 		   '" + information
						+ "' as information," + " 		   '" + description
						+ "' as description," + " 		   '" + image
						+ "' as image," + " 		   '" + video + "' as video"
						+ " ) as a " + "left join " + CINEMA_EVENT_TABLE_NAME
						+ " as b " + "on a.event_id=b.event_id "
						+ "where b.event_id is null ";

				db.execSQL(insertQuery + selectQuery);
				success++;
				System.out.println(Integer.toString(i));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Всего :" + Integer.toString(jArray.length())
				+ " Успешно:" + Integer.toString(success));
	}

	public synchronized void saveSectionsToDB(JSONArray jArray)
	{
		for (int i = 0; i < jArray.length(); i++)
		{
			try
			{
				JSONObject jSection = jArray.getJSONObject(i);
				String section_id = jSection.getString("id");
				String date = jSection.getString("date");
				String day = jSection.getString("day");
				int event_id = jSection.getInt("event_id");
				int place_id = jSection.getInt("place_id");
				String city = jSection.getString("city");
				int ticket_flag = jSection.getInt("ticket_flag");

				String insertQuery = "insert into "
						+ CINEMA_SECTIONPARENT_TABLE_NAME
						+ " (  section_id,date,day, event_id,place_id, city, ticket_flag) ";

				String selectQuery = "select a.* from " + "(select   '"
						+ section_id + "' as section_id, " + "          '"
						+ date + "' as date," + " 		   '" + day + "' as day, "
						+Integer.toString(event_id)
						+ " as event_id," + " 		   "
						+ Integer.toString(place_id) + " as place_id,"
						+ " 		   '" + city + "' as city," + " 		   '"
						+ Integer.toString(ticket_flag) + "' as ticket_flag"
						+ " ) as a " + "left join "
						+ CINEMA_SECTIONPARENT_TABLE_NAME + " as b "
						+ "on a.section_id=b.section_id "
						+ "where b.event_id is null ";

				if (place_id==309)
				{
					System.out.println(829);
				}
				
				db.execSQL(insertQuery + selectQuery);
				System.out.println("Сохранен заголовок расписания "+Integer.toString(i));
				if (jSection.has("time"))
				{
					System.out.println("Начато сохранение расписания "+Integer.toString(i));
					Object time = jSection. get("time");
					JSONArray timeArray = null;
					if (JSONArray.class.isInstance(time))
						timeArray = (JSONArray) time;
					else if (JSONObject.class.isInstance(time))
					{
						timeArray = new JSONArray("["
								+ ((JSONObject) time).toString() + "]");
					}
					saveChildSectionsToDB(section_id, timeArray);
					System.out.println("Сохранено расписание "+Integer.toString(i));
				}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	//	getCinemaInfo("4cd5798e94848cfa7883e6b1");
	}

	private void saveChildSectionsToDB(String section_id, JSONArray jArray)
			throws JSONException
	{
		final String begin_insert="insert into "
				+ CINEMA_SECTIONCHILD_TABLE_NAME
				+ " (time_id,section_id,hash,time) "
				+"select a.* from (";
		final String end_insert=" ) as a " + "left join "
				+ CINEMA_SECTIONCHILD_TABLE_NAME + " as b "
				+ "on a.section_id=b.section_id and a.time=b.time and a.hash=b.hash "
				+ "where b.time_id is null ";
		String insertQuery="";
		
		if (jArray != null)
			for (int i = 0; i < jArray.length(); i++)
			{
				JSONObject jTime = jArray.getJSONObject(i);
				int time_id = jTime.getInt("id");
				String hash = jTime.getString("hash");
				String time = jTime.getString("content");

				insertQuery+=" select   	"
						+ Integer.toString(time_id) + " as time_id, " + "			'"
						+ section_id + "' as section_id, " + "          '"
						+ hash + "' as hash," + " 		   '" + time + "' as time";
				if (i<jArray.length()-1)
					insertQuery+=" union all ";
			}
		db.execSQL(begin_insert+insertQuery+end_insert);
	}
	
	public Cursor getCinemaInfo(String FsqId)
	{
		Cursor left=db.rawQuery("select * from "+CINEMA_SECTIONPARENT_TABLE_NAME+" where place_id=309 and ticket_flag=1", null);
		
		
		
		String st="select cinemas.fsq_id, cinemas.title, events.event_id, events.title, secpar.date, secpar.day, sechild.time, secpar.ticket_flag,sechild.hash " +
				  " from "+CINEMA_PLACES_TABLE_NAME+" as cinemas "+
	              "inner join "+CINEMA_SECTIONPARENT_TABLE_NAME+" as secpar on cinemas.place_id=secpar.place_id "+
				  "inner join "+CINEMA_SECTIONCHILD_TABLE_NAME+" as sechild on secpar.section_id=sechild.section_id "+
	              "inner join "+CINEMA_EVENT_TABLE_NAME+" as events on secpar.event_id=events.event_id "+
				  "where cinemas.fsq_id='"+FsqId+"' and date(secpar.date)>=date('now')" +
				  "order by secpar.date,events.title, sechild.time ";
		Cursor cinema_selectCursor=db.rawQuery(st, null);
		return cinema_selectCursor;
	}


	private static class myDbHelper extends SQLiteOpenHelper
	{

		public myDbHelper(Context context, String name, CursorFactory factory,
				int version)
		{
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			for (String query : CREATE_ARRAY)
				db.execSQL(query);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			for (String query : TABLE_ARRAY)
				db.execSQL("drop table " + query);
			onCreate(db);
		}

	}


	public Cursor getEventById(int id)
	{
		return db.rawQuery("select * from "+CINEMA_EVENT_TABLE_NAME+" where event_id="+Integer.toString(id), null);
	}
	

}
