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
	private static final int DATABASE_VERSION = 4;
	
	private static final String CREATE_CINEMA_EVENT_TABLE="create table "+CINEMA_EVENT_TABLE_NAME+" ("+KEY_ID+" integer primary key autoincrement, "
			+" event_id "+TYPE_INT+ ", type "+TYPE_TEXT+", title "+TYPE_TEXT+", country "+TYPE_TEXT+", year "+TYPE_TEXT+" , genre "+TYPE_TEXT+", director "+TYPE_TEXT+", cast "+TYPE_TEXT+
			", information "+TYPE_TEXT+", description "+TYPE_TEXT+", image "+TYPE_TEXT+", video "+TYPE_TEXT+");";
	
	private static final String CREATE_CINEMA_PLACES_TABLE="create table "+CINEMA_PLACES_TABLE_NAME+" ("+KEY_ID+" integer primary key autoincrement  , "
			+" place_id "+TYPE_INT+ ",type "+TYPE_TEXT+", title "+TYPE_TEXT+", city "+TYPE_TEXT+",address "+TYPE_TEXT+", phone "+TYPE_TEXT+", review "+TYPE_TEXT+", fsq_id "+TYPE_TEXT+");";
	
	private static final String CREATE_CINEMA_SECTIONPARENT_TABLE="create table "+CINEMA_SECTIONPARENT_TABLE_NAME+" ("+KEY_ID+" integer primary key autoincrement, "
			+" section_id "+TYPE_INT+ ", date "+TYPE_TEXT+",day "+TYPE_TEXT+", event_id "+TYPE_INT+", place_id "+TYPE_INT+", city  "+TYPE_INT+", ticket_flag "+TYPE_INT+");";
	
	private static final String CREATE_CINEMA_SECTIONCHILD_TABLE="create table "+CINEMA_SECTIONCHILD_TABLE_NAME+" ("+KEY_ID+" integer primary key autoincrement, "
			+" section_id "+TYPE_INT+ ", hash "+TYPE_TEXT+", time "+TYPE_TEXT+");";
	
	private static final String[] CREATE_ARRAY={CREATE_CINEMA_EVENT_TABLE,CREATE_CINEMA_PLACES_TABLE, CREATE_CINEMA_SECTIONPARENT_TABLE,CREATE_CINEMA_SECTIONCHILD_TABLE};
	private static final String[] TABLE_ARRAY={CINEMA_EVENT_TABLE_NAME, CINEMA_PLACES_TABLE_NAME, CINEMA_SECTIONPARENT_TABLE_NAME, CINEMA_SECTIONCHILD_TABLE_NAME};
	
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
	
	public void savePlacesToDB(JSONArray jArray)
	{
		int succes=0;
		int added=0;
		for (int i=0;i<jArray.length();i++)
		{
			try
			{
				JSONObject jPlace=jArray.getJSONObject(i);
				String type=jPlace.getString("type").replace("\"", "\\\"");
				String title=jPlace.getString("title").replace("\"", "\\\"");
				String address=jPlace.has("address")?jPlace.getString("address").replace("\"", "\\\""):"";
				String city=jPlace.getString("city").replace("\"", "\\\"");
				String phone=jPlace.has("phone")?jPlace.getString("phone").replace("\"", "\\\""):"";
				String review=jPlace.has("review")&&JSONObject.class.isInstance(jPlace.get("review"))&&jPlace.getJSONObject("review").has("short")?jPlace.getJSONObject("review").getString("short").replace("\"", "\\\""):"";
				String fsq_id="4cd5798e94848cfa7883e6b1";
				
				int place_id=jPlace.getInt("id");
				
				String insertQuery="insert into "+CINEMA_PLACES_TABLE_NAME+" (place_id,type,title,address,city,phone, review, fsq_id) values ("
						+Integer.toString(place_id)+",\""+type+"\",\""+title+"\",\""+address+"\",\""+city+"\",\""+phone+"\",\""+review+"\", \""+fsq_id+"\");";
				Cursor existense=db.rawQuery("select place_id from "+CINEMA_PLACES_TABLE_NAME+" where place_id="+Integer.toString(place_id), null);
				
				if (existense.getCount()==0)
				{
					db.execSQL(insertQuery);
					added++;
				}
				succes++;
				System.out.println(Integer.toString(i));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Всего :"+Integer.toString(jArray.length())+" Успешно:"+Integer.toString(succes)+" Добавлено:"+Integer.toString(added));
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
			for (String query:CREATE_ARRAY)
				db.execSQL(query);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			for (String query:TABLE_ARRAY)
				db.execSQL(query);
			onCreate(db);
		}

	}

}
