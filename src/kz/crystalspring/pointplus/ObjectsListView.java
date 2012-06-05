package kz.crystalspring.pointplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainMenu;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.sbeyer.animation.ProjectAnimation;
import kz.crystalspring.funpoint.R;
import kz.sbeyer.atmpoint1.types.ItemVisible;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ObjectsListView extends Activity implements OnItemClickListener,
		OnClickListener
{

	AdapterObjectsProducts adapter;
	String[] selBnkArrays;
	public static Context context;

	private ArrayList<String> selBnkArrays1;

	private ArrayList<Object> bankList;
	private ArrayList<Object> filtList;

	private ArrayList<Object> atmsList;
	private ArrayList<Object> hotelsList;
	private ArrayList<Object> cinemasList;
	private ArrayList<Object> foodList;
	private ArrayList<Object> objectsVisibleList;


	private ItemVisible visObj;

	LinearLayout objMainDetails;
	ScrollView objMainScroll;
	Button btnMainBack;
	Button btnHotel;
	Button btnFoods;
	Button btnCinemas;

	int bgClrBlack = Color.rgb(34, 38, 42);
	int bgClrWhite = Color.rgb(255, 255, 255);

	HorizontalScrollView sv;
	ListView lv;

	AssetManager assetManager;
	float coef_dp;

	public static float currPntLat;
	public static float currPntLon;

	float scale;
	int padSize;
	int margTopSize;
	int margLeftSize;
	int imgSize1;
	LinearLayout.LayoutParams lp;

	String pLang;
	String pLangActivity;
	String pCity;
	String pCityActivity;
	String pCurrObjTypeId;
	int pad;

	Animation fadeInAnimation;
	Animation fadeInAnimation3;

	Animation fadeInAnimationRight;
	Animation fadeInAnimation3Right;
	boolean animActive = false;

	Drawable drawableDown;
	Drawable drawableUp;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainobjects);

		drawableDown = getResources().getDrawable(R.drawable.arrow_down);
		drawableUp = getResources().getDrawable(R.drawable.arrow_up);

		currPntLat = (float) 43.2355248;
		currPntLon = (float) 76.8993344;

		coef_dp = getResources().getDisplayMetrics().density;
		pad = (int) (20 * coef_dp);

		context = getApplicationContext();
		pLangActivity = Prefs.getLangPref(context);
		pCityActivity = Prefs.getCityPref(context);

		objMainScroll = (ScrollView) findViewById(R.id.objMainScroll);
		objMainDetails = (LinearLayout) findViewById(R.id.objMainDetails);

		btnMainBack = (Button) findViewById(R.id.btnMainBack);
		btnHotel = (Button) findViewById(R.id.btnHotels);
		btnFoods = (Button) findViewById(R.id.btnFoods);
		btnCinemas = (Button) findViewById(R.id.btnCinemas);

		btnMainBack.setOnClickListener(this);
		btnHotel.setOnClickListener(this);
		btnFoods.setOnClickListener(this);
		btnCinemas.setOnClickListener(this);

		bankList = new ArrayList<Object>();
		filtList = new ArrayList<Object>();
		atmsList = new ArrayList<Object>();
		hotelsList = new ArrayList<Object>();
		cinemasList = new ArrayList<Object>();
		foodList = new ArrayList<Object>();
		objectsVisibleList = new ArrayList<Object>();

		assetManager = getAssets();

		// ������������� ����������� ������� ������
		setObjectsTextVals();

		sv = (HorizontalScrollView) findViewById(R.id.scrollView1);
		lv = (ListView) findViewById(R.id.listView1);
		/* String to split. */
		setActiveMenuItem();
		/* -------------- */

		// ����������� �������� � ��������
		scale = this.getResources().getDisplayMetrics().density;
		padSize = Math.round(0 * scale);
		margTopSize = Math.round(2 * scale);
		margLeftSize = Math.round(1 * scale);
		imgSize1 = Math.round(70 * scale);
		lp = new LinearLayout.LayoutParams(imgSize1, imgSize1);
		lp.setMargins(margLeftSize, margTopSize, margLeftSize, margTopSize);

		// -- ��������� ������� ��� ���������� � ������������ ���������
		selBnkArrays = new String[0];

		int objTypeId = Integer.valueOf(Prefs.getSelObjType(context));
		prepareVisibleObjectsList(objTypeId);

		adapter = new AdapterObjectsProducts(this, objectsVisibleList);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(this);

		fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_in1);
		fadeInAnimation3 = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_in_fromleft);

		fadeInAnimationRight = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_in1right);
		fadeInAnimation3Right = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.fade_in_fromright);

		fadeInAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation animation)
			{
				objMainDetails.startAnimation(fadeInAnimation3);
				objMainDetails.setVisibility(View.VISIBLE);
				// objDetAllInfo.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationStart(Animation animation)
			{
			}
		});

		fadeInAnimationRight
				.setAnimationListener(new Animation.AnimationListener()
				{
					@Override
					public void onAnimationEnd(Animation animation)
					{
						objMainScroll.startAnimation(fadeInAnimation3Right);
						objMainScroll.setVisibility(View.VISIBLE);
						objMainDetails.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}

					@Override
					public void onAnimationStart(Animation animation)
					{
					}
				});

		fadeInAnimation3Right
				.setAnimationListener(new Animation.AnimationListener()
				{
					@Override
					public void onAnimationEnd(Animation animation)
					{
						// makeDefault();
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}

					@Override
					public void onAnimationStart(Animation animation)
					{
					}
				});
	}

	private void setActiveMenuItem()
	{
		String pObjTypeId = Prefs.getSelObjType(context);
		// Toast.makeText(context, pObjTypeId, Toast.LENGTH_SHORT).show();
		Button btn=null;
		if (pObjTypeId.contentEquals(Integer.toString(MapItem.TYPE_HOTEL)))
		{
			btn=btnHotel;
		} else if (pObjTypeId.contentEquals(Integer.toString(MapItem.TYPE_FOOD)))
		{
			btn=btnFoods;
		} else if (pObjTypeId.contentEquals(Integer.toString(MapItem.TYPE_CINEMA)))
		{
			btn=btnCinemas;	
		}
		if (btn!=null)
		{
			btn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.menubganimsel));
			btn.setPadding(pad, 0, pad, 0);
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		// ������������� ����������� ������� ������

		Prefs.setInitTab(context, "0");
		Prefs.setFilterType(context, "0");

		pLang = Prefs.getLangPref(context);
		pCity = Prefs.getCityPref(context);

		/*
		 * setMenuBtnsDefColor(); btnAtm.setPadding(pad, 0, pad, 0);
		 * btnBranch.setPadding(pad, 0, pad, 0); btnExchs.setPadding(pad, 0,
		 * pad, 0); btnNotaries.setPadding(pad, 0, pad, 0); setActiveMenuItem();
		 */

		if ((!(pLangActivity.equalsIgnoreCase(pLang)))
				|| (!(pCityActivity.equalsIgnoreCase(pCity))))
		{
			setObjectsTextVals();

			prepareVisibleObjectsList(Integer.valueOf(Prefs
					.getSelObjType(context)));

			pLangActivity = Prefs.getLangPref(context);
			pCityActivity = Prefs.getCityPref(context);
		}

		super.onResume();
	}

	public void setObjectsTextVals()
	{
		// Setting object titles according to selected languages
		pLangActivity = Prefs.getLangPref(context);
		pLang = Prefs.getLangPref(context);
		btnHotel.setText(ProjectUtils.getObjectTextById(context, pLang,
				"obj_list_hotel_title"));
		btnFoods.setText(ProjectUtils.getObjectTextById(context, pLang,
				"obj_list_food_title"));
		btnCinemas.setText(ProjectUtils.getObjectTextById(context, pLang,
				"obj_list_cinema_title"));

		String pObjTypeId = Prefs.getSelObjType(context);
		String pBtnBackTitle = "";
		if (pObjTypeId.contentEquals(Integer.toString(MapItem.TYPE_HOTEL)))
		{
			pBtnBackTitle = btnHotel.getText().toString();
		} else if (pObjTypeId.contentEquals(Integer.toString(MapItem.TYPE_FOOD)))
		{
			pBtnBackTitle = btnFoods.getText().toString();
		} else if (pObjTypeId.contentEquals(Integer.toString(MapItem.TYPE_CINEMA)))
		{
			pBtnBackTitle = btnCinemas.getText().toString();
		}
		btnMainBack.setText(pBtnBackTitle);
	}


	// ����������/������� � ������ id ��������� ������ (����� ������� ����
	// �����)
	
	// ������ ������ ������� ������/�� ������

	

	// ���� �� ������ ����� �������� � ������
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id)
	{
		// TODO Auto-generated method stub
		// ItemBean bean = (ItemBean) adapter.getItem(position);

		visObj = (ItemVisible) objectsVisibleList.get(position);

		Prefs.setSelObjType(context, String.valueOf(visObj.getObjTypeId()));
		Prefs.setSelObjId(context, String.valueOf(visObj.getId()));

		MainMenu.tabHost.setCurrentTab(3);
	}

	// ���� �� ����� ����� �������� � ������
	public void func(View v)
	{

		visObj = (ItemVisible) objectsVisibleList.get((Integer) v.getTag());
		Float lat = visObj.getLatitude();
		Float lon = visObj.getLongitude();
		String titleShort = "ID " + String.valueOf(visObj.getId());
		String titleLong = visObj.getAddress();

		Prefs.setMapObjLat(context, String.valueOf(lon));
		Prefs.setMapObjLon(context, String.valueOf(lat));
		Prefs.setMapObjTitShort(context, titleShort);
		Prefs.setMapObjTitLong(context, titleLong);

		MainMenu.tabHost.setCurrentTab(2);
	}

	public void setBtnsVisible()
	{
		btnHotel.setVisibility(View.VISIBLE);
		btnFoods.setVisibility(View.VISIBLE);
		btnCinemas.setVisibility(View.VISIBLE);
	}

	// ����������� ������ � �������� ���������
	public void setInitial(View v)
	{
		sv.setVisibility(View.GONE);
		lv.setVisibility(View.GONE);
		switch (v.getId())
		{
		case R.id.btnHotels:
			btnHotel.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawableDown, null);
			ProjectAnimation.showInitialTop(btnHotel, 1);
			ProjectAnimation.showInitialBottom(btnFoods);
			ProjectAnimation.showInitialBottom(btnCinemas);
			setBtnsVisible();
			break;
		case R.id.btnFoods:
			btnFoods.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawableDown, null);
			ProjectAnimation.showInitialTop(btnHotel, 1);
			ProjectAnimation.showInitialTop(btnFoods, 2);
			ProjectAnimation.showInitialBottom(btnCinemas);
			setBtnsVisible();
			break;
		case R.id.btnCinemas:
			btnCinemas.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawableDown, null);
			ProjectAnimation.showInitialTop(btnHotel, 2);
			ProjectAnimation.showInitialTop(btnFoods, 1);
			ProjectAnimation.showInitialTop(btnCinemas, 3);
			setBtnsVisible();
			break;
		}
	}

	// ����������� ������ � �������� ���������
	public void setVisible()
	{
		sv.setVisibility(View.VISIBLE);
		lv.setVisibility(View.VISIBLE);
	}

	boolean opened = false;
	Thread t;

	@Override
	public void onClick(View v)
	{
		if (!animActive)
		{
			t = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						sleep(1500);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally
					{
						// Toast.makeText(getApplicationContext(), "CLICK",
						// Toast.LENGTH_SHORT).show();
						animActive = false;
					}
				}
			};
			t.start();
			animActive = true;
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.btnMainBack:
				opened = false;
				ProjectAnimation.hideLeft(objMainDetails);
				objMainScroll.startAnimation(fadeInAnimationRight);
				break;
			case R.id.btnHotels:
				opened = true;
				pCurrObjTypeId = Integer.toString(MapItem.TYPE_HOTEL);

				setMenuBtnsDefColor();

				ProjectAnimation.hideRight(objMainScroll);
				objMainDetails.startAnimation(fadeInAnimation);
				sv.setVisibility(View.VISIBLE);
				lv.setVisibility(View.VISIBLE);

				btnMainBack.setText(btnHotel.getText());
				// btnBranch.setCompoundDrawablesWithIntrinsicBounds(null, null,
				// drawableUp, null);
				btnHotel.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.menubganimsel));
				break;
			case R.id.btnFoods:
				opened = true;
				pCurrObjTypeId = Integer.toString(MapItem.TYPE_FOOD);

				setMenuBtnsDefColor();

				ProjectAnimation.hideRight(objMainScroll);
				objMainDetails.startAnimation(fadeInAnimation);
				sv.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);

				btnMainBack.setText(btnFoods.getText());
				// btnExchs.setCompoundDrawablesWithIntrinsicBounds(null, null,
				// drawableUp, null);
				btnFoods.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.menubganimsel));
				break;
			case R.id.btnCinemas:
				opened = true;
				pCurrObjTypeId = Integer.toString(MapItem.TYPE_CINEMA);

				setMenuBtnsDefColor();

				ProjectAnimation.hideRight(objMainScroll);
				objMainDetails.startAnimation(fadeInAnimation);
				sv.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);

				btnMainBack.setText(btnCinemas.getText());
				// btnNotaries.setCompoundDrawablesWithIntrinsicBounds(null,
				// null, drawableUp, null);
				btnCinemas.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.menubganimsel));
			}
			btnHotel.setPadding(pad, 0, pad, 0);
			btnFoods.setPadding(pad, 0, pad, 0);
			btnCinemas.setPadding(pad, 0, pad, 0);

			if (v.getId() == R.id.btnHotels
					|| v.getId() == R.id.btnFoods
					|| v.getId() == R.id.btnCinemas)
			{

				Prefs.setSelObjType(context, pCurrObjTypeId);
				prepareVisibleObjectsList(Integer.valueOf(Prefs
						.getSelObjType(context)));

			}
		}
	}

	public void setMenuBtnsDefColor()
	{
		btnHotel.setBackgroundResource(R.drawable.blackbganim);
		btnFoods.setBackgroundResource(R.drawable.blackbganim);
		btnCinemas.setBackgroundResource(R.drawable.blackbganim);
	}

	/*
	 * Method used to prepare the ArrayList, Same way, you can also do looping
	 * and adding object into the ArrayList.
	 */

	// �������� ������ �������� ���������� Cash-in USD EUR. False - ��
	// ������������� ��������. True - ������ ������������� ��������
	public boolean checkOtherFilters(String fieldVal, int chashIn)
	{
		for (int i = 0; i < selBnkArrays.length; i++)
		{
			if (selBnkArrays[i].contentEquals("777772"))
			{
				if (fieldVal.indexOf("2") < 0)
				{
					return false;
				}
			}
			if (selBnkArrays[i].contentEquals("777773"))
			{
				if (fieldVal.indexOf("3") < 0)
				{
					return false;
				}
			}
			if (selBnkArrays[i].contentEquals("777771"))
			{
				if (String.valueOf(chashIn).indexOf("1") < 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	// ������������� ������ � ��������� ������� �������� ��� ������� ��������
	// ��� ����������� � ������ ��������
	public void reWriteVisibleArray()
	{
		// replaceOthrBnk(1);

		currPntLat = MainMenu.getCurrentLatitude();
		currPntLon = MainMenu.getCurrentLongitude();
		objectsVisibleList.clear();

		int objTypeId = Integer.valueOf(Prefs.getSelObjType(context));
		


		Collections.sort(objectsVisibleList, new Comparator<Object>()
		{
			@Override
			public int compare(Object o1, Object o2)
			{
				ItemVisible p1 = (ItemVisible) o1;
				ItemVisible p2 = (ItemVisible) o2;
				return p1.getDist() == p2.getDist() ? 0 : (p1.getDist() < p2
						.getDist() ? -1 : 1);
			}
		});
		lv.invalidateViews();
		adapter = new AdapterObjectsProducts(this, objectsVisibleList);
		lv.setAdapter(adapter);

		// replaceOthrBnk(0);
	}

	public void prepareVisibleObjectsList(int objTypeId)
	{

		atmsList.clear();
		hotelsList.clear();
		cinemasList.clear();
		foodList.clear();

		objectsVisibleList = new ArrayList<Object>();
		objectsVisibleList.clear();

		switch (objTypeId)
		{
		case 1:
			break;
		case 2:
			prepareListOfBranches();
			break;
		case 3:
			prepareListOfFood();
			break;
		case 4:
			prepareListOfCinema();
			break;

		}
		reWriteVisibleArray();
	}

	// ������������ ������ ����������
	
	public void prepareListOfBranches()
	{
		pLang = Prefs.getLangPref(context);
		pCity = Prefs.getCityPref(context);

		// To load text file
		String fileName = "";
		InputStream input;
		try
		{
			fileName = "json_hotels_" + pCity + "_" + pLang+"_zip";

			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			vIconBytes = C_FileHelper.decompress(vIconBytes);
			String text = new String(vIconBytes, "UTF-8");
			/*
			 * input = assetManager.open(fileName);
			 * 
			 * int size = input.available(); byte[] buffer = new byte[size];
			 * input.read(buffer); input.close();
			 * 
			 * // byte buffer into a string String text = new String(buffer);
			 */
			try
			{
				JSONArray entries = new JSONArray(text);

				int vLen = entries.length();
				for (int i = 0; i < vLen; i++)
				{

					try
					{
						// Log.i("TAAG", i+";");
						JSONObject post = entries.getJSONObject(i);
						AddObjectToHotelsList(post);

					} catch (Exception e)
					{
						// Toast.makeText(getApplicationContext(),"Error in "+i,
						// Toast.LENGTH_SHORT).show();
						continue;
					}

				}
				// Toast.makeText(getApplicationContext(),x,
				// Toast.LENGTH_SHORT).show();
			} catch (Exception je)
			{
				// Toast.makeText(getApplicationContext(),"ERROR parsing "+fileName+"'",
				// Toast.LENGTH_SHORT).show();
				// je.printStackTrace();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// Toast.makeText(getApplicationContext(),"ERROR in accessing file '"+fileName+"'",
			// Toast.LENGTH_SHORT).show();
			// e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ������������ ������ ���������
	public void prepareListOfFood()
	{
		pLang = Prefs.getLangPref(context);
		pCity = Prefs.getCityPref(context);

		// To load text file
		String fileName = "";
		InputStream input;
		try
		{
			fileName = "json_rest_" + pCity + "_" + pLang;

			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			/*
			 * input = assetManager.open(fileName);
			 * 
			 * int size = input.available(); byte[] buffer = new byte[size];
			 * input.read(buffer); input.close();
			 * 
			 * // byte buffer into a string String text = new String(buffer);
			 */
			try
			{
				JSONArray entries = new JSONArray(text);

				int vLen = entries.length();
				for (int i = 0; i < vLen; i++)
				{
					try
					{
						JSONObject post = entries.getJSONObject(i);

						addObjectToFoodList(post);
					} catch (Exception e)
					{
						// Toast.makeText(getApplicationContext(),"Error in "+i,
						// Toast.LENGTH_SHORT).show();
						continue;
					}
				}
				// Toast.makeText(getApplicationContext(),x,
				// Toast.LENGTH_SHORT).show();
			} catch (Exception je)
			{
				// Toast.makeText(getApplicationContext(),"ERROR parsing "+fileName+"'",
				// Toast.LENGTH_SHORT).show();
				// je.printStackTrace();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// Toast.makeText(getApplicationContext(),"ERROR in accessing file '"+fileName+"'",
			// Toast.LENGTH_SHORT).show();
			// e.printStackTrace();
		}
	}

	// ������������ ������ ���������
	public void prepareListOfCinema()
	{
		pLang = Prefs.getLangPref(context);
		pCity = Prefs.getCityPref(context);

		// To load text file
		String fileName = "";
		InputStream input;
		try
		{
			fileName = "json_cinema_" + pCity + "_" + pLang;

			byte[] vIconBytes = C_FileHelper.ReadFile(new File(context
					.getFilesDir() + "/" + fileName));
			String text = new String(vIconBytes, "UTF-8");
			/*
			 * input = assetManager.open(fileName);
			 * 
			 * int size = input.available(); byte[] buffer = new byte[size];
			 * input.read(buffer); input.close();
			 * 
			 * // byte buffer into a string String text = new String(buffer);
			 */
			try
			{
				JSONArray entries = new JSONArray(text);

				int vLen = entries.length();
				for (int i = 0; i < vLen; i++)
				{
					try
					{
						JSONObject post = entries.getJSONObject(i);
						addObjectToCinemaList(post);
					} catch (Exception e)
					{
						// Toast.makeText(getApplicationContext(),"Error in "+i,
						// Toast.LENGTH_SHORT).show();
						continue;
					}
				}
				// Toast.makeText(getApplicationContext(),x,
				// Toast.LENGTH_SHORT).show();
			} catch (Exception je)
			{
				// Toast.makeText(getApplicationContext(),"ERROR parsing "+fileName+"'",
				// Toast.LENGTH_SHORT).show();
				// je.printStackTrace();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// Toast.makeText(getApplicationContext(),"ERROR in accessing file '"+fileName+"'",
			// Toast.LENGTH_SHORT).show();
			// e.printStackTrace();
		}
	}

	// ������� ���������� � ������������ ���������
	

	public void AddObjectToHotelsList(JSONObject jObject)
	{
		
	}

	// ���������� �������� � ������ ����������
	public void addObjectToCinemaList(JSONObject jObject)
	{
		
	}

	// ���������� �������� � ������ ����������
	public void addObjectToFoodList(JSONObject jObject)
	{  
		
	}

	// ���������� �������� � ������ ������ ����������

	// ���������� �������� � ������ ������� ��������
	public void AddObjectToVisibleList(int id, String address, float longitude,
			float latitude, int bankId, String alias, int rating,
			String atmType, int isCashIn, int isValid, String wrktime,
			String products, String exchName, String phones, int dist,
			int objTypeId)
	{
		visObj = new ItemVisible();
		visObj.setId(id);
		visObj.setAddress(address);
		visObj.setLongitude(longitude);
		visObj.setLatitude(latitude);
		visObj.setBankId(bankId);
		visObj.setAlias(alias);
		visObj.setRating(rating);
		visObj.setAtmType(atmType);
		visObj.setIsCashIn(isCashIn);
		visObj.setIsValid(isValid);
		visObj.setWrktime(wrktime);
		visObj.setProducts(products);
		visObj.setObjName(exchName);
		visObj.setPhones(phones);
		visObj.setDist(dist);
		visObj.setObjTypeId(objTypeId);
		objectsVisibleList.add(visObj);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (opened)
			{
				opened = false;
				ProjectAnimation.hideLeft(objMainDetails);
				objMainScroll.startAnimation(fadeInAnimationRight);
				return true;
			} else
			{
				MainMenu.tabHost.setCurrentTab(Integer.valueOf(0));
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}