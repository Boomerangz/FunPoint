package kz.crystalspring.funpoint;

import kz.crystalspring.android_client.C_NetHelper;
import kz.crystalspring.funpoint.venues.MapItem;
import kz.crystalspring.pointplus.MyLocation.LocationResult;
import kz.crystalspring.pointplus.ObjectsListView;
import kz.crystalspring.pointplus.R.drawable;
import kz.crystalspring.pointplus.R.id;
import kz.crystalspring.pointplus.R.layout;
import kz.crystalspring.pointplus.Helpdesk;
import kz.crystalspring.pointplus.MyLocation;
import kz.crystalspring.pointplus.Prefs;
import kz.crystalspring.pointplus.ProjectUtils;
import kz.crystalspring.pointplus.R;
import kz.crystalspring.pointplus.UserInfo;
import kz.crystalspring.pointplus.UtilTabHost;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainMenu extends TabActivity
{

	AssetManager assetManager;

	FrameLayout tabcontent;

	RelativeLayout.LayoutParams pUnderHead;
	RelativeLayout.LayoutParams pUnderFilt;

	Button foodTabButton;
	Button hotelTabButton;
	Button cinemaTabButton;

	public static SharedPreferences mPrefs;

	public static Context context;
	/** message frame for update notification */
	private ScrollView msgFrame_;
	/** message body update notification */
	private TextView msgTxtView_;
	/** message body update notification */
	private TextView msgTxtViewTitle;
	/** message body update notification */
	float scale;
	public static TabHost tabHost;
	private TextView logoTv;

	String pLang;
	String pLangActivity;

	int currentTab = 0;
	Animation fadeOutAnimationRight;
	Animation fadeInAnimation3Right;

	int prevTab = 0;
	int currTab = 0;

	Drawable drawableUp;

	String pFilterType;
	String pProdTypeId;
	String pObjTypeId;
	String logoTvStr;
	private static double currLat = 0.0;
	private static double currLon = 0.0;

	public static final int OBJECT_LIST_TAB=4;
	public static final int OBJECT_MAP_TAB=0;
	public static int currentListTab=0;
	
	
	public static float getCurrentLatitude()
	{
		String city = "";
		String latitude = "";
		if (currLat != 0.0)
		{
			latitude = String.valueOf(currLat);
		} else
		{
			city = Prefs.getCityPref(context);
			if (city.contentEquals("1"))
			{
				latitude = "43.23717";
			} else if (city.contentEquals("2"))
			{
				latitude = "51.1558";
			} else
			{
				latitude = "43.23717";
			}
		}
		return ProjectUtils.getFloatFromString(latitude);
	}

	public static float getCurrentLongitude()
	{
		String city = "";
		String longitude = "";
		if (currLon != 0.0)
		{
			longitude = String.valueOf(currLon);
		} else
		{
			city = Prefs.getCityPref(context);
			if (city.contentEquals("1"))
			{
				longitude = "76.9155";
			} else if (city.contentEquals("2"))
			{
				longitude = "71.43195";
			} else
			{
				longitude = "76.9155";
			}
		}
		return ProjectUtils.getFloatFromString(longitude);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.menu);
		context = getApplicationContext();
		assetManager = getAssets();

		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

		LocationResult locationResult = new LocationResult()
		{
			@Override
			public void gotLocation(Location location)
			{
				// Got the location!
				currLat = location.getLatitude();
				currLon = location.getLongitude();
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(context, locationResult);

		Prefs.setFilterType(this, "0");
		Prefs.setMapObjLat(context, String.valueOf(""));
		Prefs.setMapObjLon(context, String.valueOf(""));
		Prefs.setMapObjTitShort(context, "");
		Prefs.setMapObjTitLong(context, "");

		pLangActivity = Prefs.getLangPref(context);

		drawableUp = getResources().getDrawable(R.drawable.arrow_up);

		logoTv = (TextView) findViewById(R.id.logoTv);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		// menuFiltSwitch = (LinearLayout) findViewById(R.id.menuFiltSwitch);
		tabcontent = (FrameLayout) findViewById(android.R.id.tabcontent);

		cinemaTabButton = (Button) findViewById(R.id.cinema_btn);
		foodTabButton = (Button) findViewById(R.id.food_btn);
		hotelTabButton = (Button) findViewById(R.id.hotel_btn);

		tabHost.setBackgroundColor(Color.BLACK);

		
		
		UtilTabHost.addTab(tabHost, R.drawable.food_btn, 1, new Intent(this,
				funMap.class));
		UtilTabHost.addTab(tabHost, R.drawable.tab_main_notif_selector, 1,
				new Intent(this, Helpdesk.class));
		UtilTabHost.addTab(tabHost, R.drawable.tab_main_pers_selector, 2,
				new Intent(this, UserInfo.class));
		UtilTabHost.addTab(tabHost, R.drawable.tab_main_pers_selector, 3,
				new Intent(this, funObjectDetail.class));
		UtilTabHost.addTab(tabHost, R.drawable.tab_main_pers_selector, 4,
				new Intent(this, funObjectList.class));

		String filtType = Prefs.getFilterType(context);
		// if(filtType.contentEquals("0")){
		// btnSwitchObj.setBackgroundDrawable(getResources().getDrawable(R.drawable.menubganimseltop));
		// btnSwitchProd.setBackgroundResource(R.drawable.blackbganimtop);
		//
		// tabHost.setCurrentTab(0);
		// tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
		// tabHost.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
		// }else if(filtType.contentEquals("1")){
		// btnSwitchProd.setBackgroundDrawable(getResources().getDrawable(R.drawable.menubganimseltop));
		// btnSwitchObj.setBackgroundResource(R.drawable.blackbganimtop);
		//
		// tabHost.setCurrentTab(1);
		// tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);
		// tabHost.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
		// }
		//
		// btnSwitchObj.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// String filtTypeTmp = Prefs.getFilterType(context);
		// if(filtTypeTmp.contentEquals("0")){
		// }
		// // }else if(filtTypeTmp.contentEquals("1")){
		// //
		// btnSwitchObj.setBackgroundDrawable(getResources().getDrawable(R.drawable.menubganimseltop));
		// // btnSwitchProd.setBackgroundResource(R.drawable.blackbganimtop);
		// // Prefs.setFilterType(context, "0");
		// // tabHost.setCurrentTab(0);
		// // tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
		// // tabHost.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
		// // }
		// }
		// });
		// btnSwitchProd.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// String filtTypeTmp = Prefs.getFilterType(context);
		// if(filtTypeTmp.contentEquals("0")){
		// btnSwitchProd.setBackgroundDrawable(getResources().getDrawable(R.drawable.menubganimseltop));
		// btnSwitchObj.setBackgroundResource(R.drawable.blackbganimtop);
		// Prefs.setFilterType(context, "1");
		// tabHost.setCurrentTab(1);
		// tabHost.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
		// tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);
		// }else if(filtTypeTmp.contentEquals("1")){
		// }
		// }
		// });

		scale = this.getResources().getDisplayMetrics().density;

		tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);
		tabHost.getTabWidget().getChildAt(3).setVisibility(View.GONE);
		tabHost.getTabWidget().getChildAt(4).setVisibility(View.GONE);

		foodTabButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				MainApplication.mapItemContainer.addVisibleFilter(MapItem.FSQ_TYPE_FOOD);
				goToCurrentTab();
				currentTab = 0;
				MainApplication.refreshMap();
			}
		});

		hotelTabButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				MainApplication.mapItemContainer.addVisibleFilter(MapItem.FSQ_TYPE_HOTEL);
				goToCurrentTab();
				Prefs.setSelObjType(MainMenu.this,
						Integer.toString(MapItem.TYPE_HOTEL));

				currentTab = 1;
				MainApplication.refreshMap();
			}
		});

		cinemaTabButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				MainApplication.mapItemContainer.addVisibleFilter(MapItem.FSQ_TYPE_CINEMA);
				goToCurrentTab();
				Prefs.setSelObjType(MainMenu.this,
						Integer.toString(MapItem.TYPE_CINEMA));

				currentTab = 2;
				MainApplication.refreshMap();
			}
		});

		tabHost.getTabWidget().getChildAt(1)
				.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						prevTab = tabHost.getCurrentTab();

						tabHost.setCurrentTab(1);
					}
				});
		tabHost.getTabWidget().getChildAt(2)
				.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

						// tabHost.getCurrentView().setAnimation(fadeOutAnimationRight);
						tabHost.setCurrentTab(2);
						currentTab = 4;
					}
				});
		tabHost.getTabWidget().getChildAt(3)
				.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// tabHost.getCurrentView().setAnimation(fadeOutAnimationRight);
						tabHost.setCurrentTab(3);
						currentTab = 5;
					}
				});
		// tabHost.getTabWidget().getChildAt(6)
		// .setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// // tabHost.getCurrentView().setAnimation(fadeOutAnimationRight);
		// tabHost.setCurrentTab(6);
		// currentTab = 6;
		// }
		// });

		pUnderHead = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		pUnderHead.addRule(RelativeLayout.BELOW, R.id.logoTv);
		pUnderHead.addRule(RelativeLayout.ABOVE, android.R.id.tabs);

		pUnderFilt = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		pUnderFilt.addRule(RelativeLayout.BELOW, R.id.menuFiltSwitch);
		pUnderFilt.addRule(RelativeLayout.ABOVE, android.R.id.tabs);

		tabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			@Override
			public void onTabChanged(String tabId)
			{
				if (Helpdesk.getMessagesCount(context) != 0)
				{
					setMessageOpen();
				} else
				{
					setMessageClose();
				}

				currTab = tabHost.getCurrentTab();

				pFilterType = Prefs.getFilterType(context);
				pProdTypeId = Prefs.getSelProdType(context);
				pObjTypeId = Prefs.getSelObjType(context);
				logoTvStr = "Point+";
				pLang = Prefs.getLangPref(context);

				renameObjects();
			}
		});

		// ImageButton ibInfo = (ImageButton) findViewById(R.id.ibInfo);
		// ibInfo.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// //dialog.dismiss();
		// showDialog(R.id.AboutDlg);
		// }
		// });
		//
		tabHost.setCurrentTab(0);

		if (Prefs.getFirstRun(context).length() < 2)
		{
			final CharSequence[] items = new CharSequence[3];
			items[0] = ProjectUtils.getObjectTextById(context, pLang,
					"prefs_lang_entr_1");
			items[1] = ProjectUtils.getObjectTextById(context, pLang,
					"prefs_lang_entr_2");
			items[2] = ProjectUtils.getObjectTextById(context, pLang,
					"prefs_lang_entr_3");

			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setItems(items, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int item)
				{
					switch (item)
					{
					case 0:
						Prefs.setLangPref(context, "ru");
						break;
					case 1:
						Prefs.setLangPref(context, "en");
						break;
					case 2:
						Prefs.setLangPref(context, "kz");
						break;
					}
					pLang = Prefs.getLangPref(context);
					renameObjects();
				}
			});
			builder1.create().show();
			Prefs.setFirstRun(context, "123456");
		}

		if (Helpdesk.getMessagesCount(context) != 0)
		{
			setMessageOpen();
		} else
		{
			setMessageClose();
		}
	}

	public static void setMessageOpen()
	{
		ViewGroup identifyView = (ViewGroup) tabHost.getTabWidget().getChildAt(
				1);
		ImageView iv = (ImageView) identifyView.getChildAt(0);
		iv.setImageDrawable(context.getResources().getDrawable(
				R.drawable.tab_main_notif_new_selector));
	}

	public static void setMessageClose()
	{
		ViewGroup identifyView = (ViewGroup) tabHost.getTabWidget().getChildAt(
				1);
		ImageView iv = (ImageView) identifyView.getChildAt(0);
		iv.setImageDrawable(context.getResources().getDrawable(
				R.drawable.tab_main_notif_selector));
	}

	Thread t;

	public Animation inFromRightAnimation()
	{
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(300);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	public Animation outToRightAnimation()
	{
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(240);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	public void setTitleText()
	{

		pLang = Prefs.getLangPref(context);
		logoTv.setText(ProjectUtils.getObjectTextById(context, pLang,
				"obj_list_atm_title"));

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (currTab != 0)
			{
				MainMenu.tabHost.setCurrentTab(prevTab);
				return true;
			}
			// moveTaskToBack(true);
			// return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void renameObjects()
	{
		pFilterType = Prefs.getFilterType(context);
		pProdTypeId = Prefs.getSelProdType(context);
		pObjTypeId = Prefs.getSelObjType(context);
		logoTvStr = "Point+";

		if (currTab == 0 || currTab == 1)
		{
			logoTv.setText(logoTvStr);
			logoTv.setTextSize(20);
			// menuFiltSwitch.setVisibility(View.VISIBLE);
			// tabcontent.setLayoutParams(pUnderFilt);
		} else
		{
			// menuFiltSwitch.setVisibility(View.GONE);
			// tabcontent.setLayoutParams(pUnderHead);
			if (pFilterType.contentEquals("0"))
			{
				if (pObjTypeId.contentEquals("1"))
				{
					logoTvStr = ProjectUtils.getObjectTextById(context, pLang,
							"obj_list_atm_title");
				} else if (pObjTypeId.contentEquals("2"))
				{
					logoTvStr = ProjectUtils.getObjectTextById(context, pLang,
							"obj_list_branch_title");
				} else if (pObjTypeId.contentEquals("3"))
				{
					String exchTitle = ProjectUtils.getObjectTextById(context,
							pLang, "obj_list_exch_title");
					String exchTitleFin = exchTitle.substring(0,
							exchTitle.indexOf("kurs.kz")).trim();
					logoTvStr = exchTitleFin;
				} else if (pObjTypeId.contentEquals("4"))
				{
					logoTvStr = ProjectUtils.getObjectTextById(context, pLang,
							"obj_list_notar_title");
				}
			} else if (pFilterType.contentEquals("1"))
			{
				if (pProdTypeId.contentEquals("6"))
				{
					logoTvStr = ProjectUtils.getObjectTextById(context, pLang,
							"prod_list_transf_title");
				} else if (pProdTypeId.contentEquals("20"))
				{
					logoTvStr = ProjectUtils.getObjectTextById(context, pLang,
							"obj_list_depos_title");
				} else if (pProdTypeId.contentEquals("40"))
				{
					logoTvStr = ProjectUtils.getObjectTextById(context, pLang,
							"obj_list_cards_title");
				}
			}
			logoTv.setText(logoTvStr);
			logoTv.setTextSize(16);
		}
		// btnSwitchProd.setText(ProjectUtils.getObjectTextById(context, pLang,
		// "obj_list_product_title").toUpperCase());
		// btnSwitchObj.setText(ProjectUtils.getObjectTextById(context, pLang,
		// "btnSwitchObj").toUpperCase());

	}

	@Override
	protected void onResume()
	{
		goToCurrentTab();
		pLang = Prefs.getLangPref(context);
		C_NetHelper.SyncData(this, false, false);
		if ((!(pLangActivity.equalsIgnoreCase(pLang))))
		{
			renameObjects();

			pLangActivity = Prefs.getLangPref(context);
		}
		super.onResume();
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		/*switch (id)
		{
		case R.id.AboutDlg:
		{
			// ��������� � ����������
			int padSize = Math.round(4 * scale);
			int margTopSize = Math.round(4 * scale);
			int margLeftSize = Math.round(4 * scale);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			lp.setMargins(margLeftSize, margTopSize, margLeftSize, margTopSize);

			LinearLayout linMain = new LinearLayout(this);
			linMain.setGravity(Gravity.CENTER_HORIZONTAL);
			linMain.setOrientation(LinearLayout.VERTICAL);
			linMain.setBackgroundColor(Color.BLACK);
			linMain.setPadding(margTopSize, margTopSize, margTopSize,
					margTopSize);

			LinearLayout linSocialButtons = new LinearLayout(this);
			linSocialButtons.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			linSocialButtons.setGravity(Gravity.CENTER_HORIZONTAL);
			linSocialButtons.setBackgroundColor(Color.BLACK);

			ImageButton ivFB = new ImageButton(this);
			ivFB.setImageDrawable(this.getResources().getDrawable(
					R.drawable.fb_c));
			ivFB.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					String url = "http://m.facebook.com/pages/ATMPoint/256617504387201";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
			ImageButton ivTW = new ImageButton(this);
			ivTW.setImageDrawable(this.getResources().getDrawable(
					R.drawable.tw_c));
			ivTW.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					String url = "https://twitter.com/ATMPoint";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
			ImageButton ivVK = new ImageButton(this);
			ivVK.setImageDrawable(this.getResources().getDrawable(
					R.drawable.vk_c));
			ivVK.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					String url = "http://vkontakte.ru/id153857705";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});

			LinearLayout linMessageAndButtons = new LinearLayout(this);
			linMessageAndButtons.setOrientation(LinearLayout.VERTICAL);

			ImageView ivLogo = new ImageView(this);
			ivLogo.setImageDrawable(this.getResources().getDrawable(
					R.drawable.logo_atm));
			ivLogo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			ivLogo.setBackgroundColor(Color.BLACK);

			ImageView ivLogoCs = new ImageView(this);
			ivLogoCs.setImageDrawable(this.getResources().getDrawable(
					R.drawable.logo_cs));
			ivLogoCs.setPadding(margTopSize, margTopSize, margTopSize,
					margTopSize);
			ivLogoCs.setBackgroundColor(Color.BLACK);

			String msgStr = ProjectUtils.getObjectTextById(context, pLang,
					"about_txt");

			msgTxtView_ = new TextView(this);
			msgTxtView_.setTextSize(14);
			msgTxtView_.setText(msgStr);
			msgTxtView_.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			msgTxtView_.setTextColor(Color.WHITE);
			msgTxtView_.setBackgroundColor(Color.BLACK);
			msgFrame_ = new ScrollView(this);
			linSocialButtons.addView(ivFB);
			linSocialButtons.addView(ivTW);
			linSocialButtons.addView(ivVK);

			linMessageAndButtons.addView(msgTxtView_);
			linMessageAndButtons.addView(linSocialButtons);
			linMessageAndButtons.addView(ivLogoCs);
			msgFrame_.addView(linMessageAndButtons);

			linMain.addView(ivLogo);
			linMain.addView(msgFrame_);

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(linMain);
			/*
			 * .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int id) {
			 * // do something if OK dialog.dismiss(); } });
			 /
			builder.create().show();
		}
		}*/
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		return true;
	}

	// Handle events from the popup menu above
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
		case (Menu.FIRST):
			this.finish();
			return true;
		case (Menu.FIRST + 1):
			C_NetHelper.SyncData(this, true, false);
			return true;
		case (Menu.FIRST + 2):
			// Actions for settings page
			Intent j = new Intent(this, Prefs.class);
			startActivity(j);

			return true;
		}
		return false;
	}

	public void close()
	{
		this.finish();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{

		menu.clear();
		int groupId = 0;
		int menuItemOrder = Menu.NONE;
		// Create menu ids for the event handler to reference
		int menuItemId1 = Menu.FIRST;
		int menuItemId2 = Menu.FIRST + 1;
		int menuItemId3 = Menu.FIRST + 2;
		// Create menu text
		String menuItemText1 = ProjectUtils.getObjectTextById(context, pLang,
				"vButExit");
		String menuItemText2 = ProjectUtils.getObjectTextById(context, pLang,
				"vButUpdate");
		String menuItemText3 = ProjectUtils.getObjectTextById(context, pLang,
				"vButSetting");
		// Add the items to the menu
		MenuItem menuItem1 = menu.add(groupId, menuItemId1, menuItemOrder,
				menuItemText1).setIcon(R.drawable.ic_menu_exit);
		MenuItem menuItem2 = menu.add(groupId, menuItemId2, menuItemOrder,
				menuItemText2).setIcon(R.drawable.ic_menu_refresh);
		MenuItem menuItem3 = menu.add(groupId, menuItemId3, menuItemOrder,
				menuItemText3).setIcon(android.R.drawable.ic_menu_preferences);

		return true;
	}

	public static void goToObjectList()
	{
		tabHost.setCurrentTab(OBJECT_LIST_TAB);
		currentListTab=OBJECT_LIST_TAB;
	}
	
	public static void goToObjectMap()
	{
		tabHost.setCurrentTab(OBJECT_MAP_TAB);
		currentListTab=OBJECT_MAP_TAB;
	}
	
	public static void goToCurrentTab()
	{
		tabHost.setCurrentTab(currentListTab);
	}

}