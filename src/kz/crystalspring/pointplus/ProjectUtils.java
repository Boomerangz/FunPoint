package kz.crystalspring.pointplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

import kz.crystalspring.android_client.C_FileHelper;
import kz.crystalspring.funpoint.MainApplication;
import kz.sbeyer.atmpoint1.types.ItemLangValues;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

public class ProjectUtils
{

	public static String getObjectTextById(Context c, String pLangId,
			String pObjId)
	{
		AssetManager assetManager = c.getAssets();
		InputStream input;
		String fileName = "";
		try
		{
			fileName = "json_objects";

			// byte[] vIconBytes = C_FileHelper.ReadFile(new
			// File(MainMenu.context.getFilesDir() + "/" + fileName));
			// String text = new String(vIconBytes, "UTF-8");

			input = assetManager.open(fileName);

			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			// byte buffer into a string
			String text = new String(buffer);
			try
			{
				JSONArray entries = new JSONArray(text);

				int vLen = entries.length();
				for (int i = 0; i < vLen; i++)
				{
					try
					{
						JSONObject post = entries.getJSONObject(i);
						if (post.getString("idweb").equalsIgnoreCase(pObjId))
						{
							return post.getString(pLangId);
						}
					} catch (Exception e)
					{
						// Log.i("ProjectUtils_getObjectTextById1","Error in "+i);
						continue;
					}
				}
			} catch (Exception je)
			{
				// Log.i("ProjectUtils_getObjectTextById2","ERROR parsing "+fileName+"'");
				// je.printStackTrace();
			}
		} catch (IOException e)
		{
			// Log.i("ProjectUtils_getObjectTextById3","ERROR in accessing file '"+fileName+"'");
			// e.printStackTrace();
		}
		return "";
	}

	public static ArrayList<Object> getTranslList(Context c, String pLangId)
	{
		ArrayList<Object> finArrList = new ArrayList<Object>();
		ItemLangValues itemLangObj;

		AssetManager assetManager = c.getAssets();
		InputStream input;
		String fileName = "";
		try
		{
			fileName = "json_objects";

			// byte[] vIconBytes = C_FileHelper.ReadFile(new
			// File(MainMenu.context.getFilesDir() + "/" + fileName));
			// String text = new String(vIconBytes, "UTF-8");

			input = assetManager.open(fileName);

			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			// byte buffer into a string
			String text = new String(buffer);
			try
			{
				JSONArray entries = new JSONArray(text);

				int vLen = entries.length();
				for (int i = 0; i < vLen; i++)
				{
					try
					{
						JSONObject post = entries.getJSONObject(i);
						itemLangObj = new ItemLangValues();
						itemLangObj.setIdWeb(post.getString("idweb"));
						itemLangObj.setValue(post.getString(pLangId));
						finArrList.add(itemLangObj);
					} catch (Exception e)
					{
						// Log.i("ProjectUtils_getObjectTextById1","Error in "+i);
						continue;
					}
				}
			} catch (Exception je)
			{
				// Log.i("ProjectUtils_getObjectTextById2","ERROR parsing "+fileName+"'");
				// je.printStackTrace();
			}
		} catch (IOException e)
		{
			// Log.i("ProjectUtils_getObjectTextById3","ERROR in accessing file '"+fileName+"'");
			// e.printStackTrace();
		}
		return finArrList;
	}

	// ������� ��� ����������� ���������� ����� ����� �������
	public static float distance(float lat1, float lon1, float lat2, float lon2)
	{
		float R = 6371; // km (change this constant to get miles)
		float dLat = (float) ((lat2 - lat1) * Math.PI / 180);
		float dLon = (float) ((lon2 - lon1) * Math.PI / 180);
		float a = (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math
				.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180)
				* Math.sin(dLon / 2) * Math.sin(dLon / 2));
		float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
		float d = R * c;
		return (d * 1000);
	}

	// ������������ String �� Float. ������������� ����������� �������
	// ���������� ������ � ���������
	public static float getFloatFromString(String str)
	{
		float floatNum = 0;
		String strModified = str.replace(".", ",");
		int indexOfPoint = strModified.indexOf(",");
		int stringLen = strModified.length();
		int coef = stringLen - indexOfPoint - 1;
		String newLatStr = strModified.replace(",", "");
		floatNum = (Long.parseLong(newLatStr) / (float) (Math.pow(10, coef)));
		return floatNum;
	}

	public static float getSumOfArray(List<Float> lst)
	{
		float sum = 0;
		for (Float f : lst)
		{
			sum += f;
		}
		return sum;
	}

	public static float getSumOfArray(float[] arr)
	{
		float sum = 0;
		for (float f : arr)
		{
			sum += f;
		}
		return sum;
	}

	public static List<String> separateStrings(String income, String separator)
	{
		List<String> list = new ArrayList();
		String str = new String(income);
		while (str.contains(separator))
		{
			list.add(str.substring(0, str.indexOf(separator)));
			str = str.substring(str.indexOf(separator) + 1, str.length());
		}
		list.add(str.substring(0, str.length()));
		return list;
	}

	final static int PERIOD_YEAR = 0;
	final static int PERIOD_MONTH = 1;
	final static int PERIOD_WEEK = 2;
	final static int PERIOD_DAY = 3;
	final static int PERIOD_HOUR = 4;
	final static int PERIOD_MINUTE = 5;

	public static String dateToRelativeString(Date date)
	{
		final long MINUTE_IN_MILLIS = 1000 * 60;
		final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
		final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
		final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;
		final long MONTH_IN_MILLIS = DAY_IN_MILLIS * 30;
		final long YEAR_IN_MILLIS = DAY_IN_MILLIS * 365;

		Date nowDate = new Date();
		String postWord = nowDate.after(date) ? "назад" : "вперед";

		long difference = Math.abs(nowDate.getTime() - date.getTime());

		int period = -1;
		int count = -1;
		if ((int) (difference / YEAR_IN_MILLIS) > 0)
		{
			count = (int) (difference / YEAR_IN_MILLIS);
			period = PERIOD_YEAR;
		} else if ((int) (difference / MONTH_IN_MILLIS) > 0)
		{
			count = (int) (difference / MONTH_IN_MILLIS);
			period = PERIOD_MONTH;
		} else if ((int) (difference / WEEK_IN_MILLIS) > 0)
		{
			count = (int) (difference / WEEK_IN_MILLIS);
			period = PERIOD_WEEK;
		} else if ((int) (difference / DAY_IN_MILLIS) > 0)
		{
			count = (int) (difference / DAY_IN_MILLIS);
			period = PERIOD_DAY;
		} else if ((int) (difference / HOUR_IN_MILLIS) > 0)
		{
			count = (int) (difference / HOUR_IN_MILLIS);
			period = PERIOD_HOUR;
		} else if ((int) (difference / MINUTE_IN_MILLIS) > 0)
		{
			count = Math.round((difference / MINUTE_IN_MILLIS));
			period = PERIOD_MINUTE;
		}
		if (count == 0 && period == PERIOD_MINUTE)
			return "только что";
		else
			return IntegerToWords(count, period) + " "
					+ periodToWords(count, period) + " " + postWord;
	}

	public static String periodToWords(int n, int period)
	{
		String str = "";

		if (((int) (n / 10)) % 10 == 1)
			n = 10;

		if (period == PERIOD_YEAR)
		{
			switch (n % 10)
			{
			case 1:
				str = "год";
				break;
			case 2:
				str = "года";
				break;
			case 3:
				str = "года";
				break;
			case 4:
				str = "года";
				break;
			default:
				str = "лет";
				break;
			}
		} else if (period == PERIOD_MONTH)
		{
			switch (n % 10)
			{
			case 1:
				str = "месяц";
				break;
			case 2:
				str = "месяца";
				break;
			case 3:
				str = "месяца";
				break;
			case 4:
				str = "месяца";
				break;
			default:
				str = "месяцев";
				break;
			}
		} else if (period == PERIOD_WEEK)
		{
			switch (n % 10)
			{
			case 1:
				str = "неделю";
				break;
			case 2:
				str = "недели";
				break;
			case 3:
				str = "недели";
				break;
			case 4:
				str = "недели";
				break;
			default:
				str = "недель";
				break;
			}
		} else if (period == PERIOD_DAY)
		{
			switch (n % 10)
			{
			case 1:
				str = "день";
				break;
			default:
				str = "дней";
				break;
			}
		} else if (period == PERIOD_HOUR)
		{
			switch (n % 10)
			{
			case 1:
				str = "час";
				break;
			case 2:
				str = "часа";
				break;
			case 3:
				str = "часа";
				break;
			case 4:
				str = "часа";
				break;
			default:
				str = "часов";
				break;
			}
		} else if (period == PERIOD_MINUTE)
		{
			switch (n % 10)
			{
			case 1:
				str = "минуту";
				break;
			case 2:
				str = "минуты";
				break;
			case 3:
				str = "минуты";
				break;
			case 4:
				str = "минуты";
				break;
			default:
				str = "минут";
				break;
			}
		}
		;
		return str;
	}

	public static String IntegerToWords(int n, int mode)
	{
		String str = "";
		final int COUNT_DEX = 3;// количество разрядов
		final String[] words_one_male = { "", "один", "два", "три", "четыре",
				"пять", "шесть", "семь", "восемь", "девять" };
		final String[] words_one_female = { "", "одну", "две", "три", "четыре",
				"пять", "шесть", "семь", "восемь", "девять" };
		final String[] words_ten = { "", "десять", "двадцать", "тридцать",
				"сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят",
				"девяносто" };
		final String[] words_hundred = { "", "сто", "двести", "триста",
				"четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот",
				"девятьсот" };
		final String[][] words;
		if (mode == PERIOD_MINUTE || mode == PERIOD_WEEK)
			words = new String[][] { words_one_female, words_ten, words_hundred };
		else
			words = new String[][] { words_one_male, words_ten, words_hundred };
		ArrayList<Integer> numbers = new ArrayList();
		int x = 1;
		while ((int) (n / x) > 0)
		{
			numbers.add((int) (n / x) % 10);
			x *= 10;
		}

		int start = 0;

		if (numbers.size() > 1 && numbers.get(1) == 1)
		{
			start = 2;
			switch (numbers.get(0))
			{
			case 0:
				str = "десять";
				break;
			case 1:
				str = "одиннадцать";
				break;
			case 2:
				str = "двенадцать";
				break;
			case 3:
				str = "тринадцать";
				break;
			case 4:
				str = "четырнадцать";
				break;
			case 5:
				str = "пятнадцать";
				break;
			case 6:
				str = "шестнадцать";
				break;
			case 7:
				str = "семнадцать";
				break;
			case 8:
				str = "восемнадцать";
				break;
			case 9:
				str = "девятнадцать";
				break;
			}
		}

		int limit = numbers.size() > COUNT_DEX ? COUNT_DEX : numbers.size();// чтобы
																			// не
																			// идти
																			// дальше
																			// прописанных
																			// разрядов.
																			// не
																			// больше
																			// сотен
		for (int i = start; i < limit; i++)
		{
			int num = numbers.get(i).intValue();
			str = words[i][num] + " " + str;
		}

		return str;
	}

	public static ArrayList addToBeginOfArrayList(ArrayList list, Object obj)
	{
		ArrayList newList = new ArrayList();
		newList.add(obj);
		newList.addAll(list);
		return newList;
	}

	public static String loadByUrl(String sUrl)
	{
		InputStream is;
		// if (MainApplication.internetConnection==MainApplication.EDGE)
		is = loadGZIPStreamByUrl(sUrl);
		// else
		// is = loadStreamByUrl(sUrl);
		if (is != null)
		{
			try
			{
				return streamToString(is);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private static final String PROXY_URL = "http://www.homeplus.kz/parser/4sq_gzip_curl.php";

	public static InputStream loadGZIPStreamByUrl(String sUrl)
	{

		try
		{
			HttpClient client = new DefaultHttpClient();
			// HttpGet request = new HttpGet();
			// request.setURI(new URI(sUrl));
			// request.setHeader("Accept-Language", "ru");
			HttpPost post = new HttpPost(PROXY_URL);
			List pairs = new ArrayList();
			pairs.add(new BasicNameValuePair("url", sUrl));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post);

			// HttpResponse response = client.execute(request);
			System.out.println("получен поток с сервера");
			return new GZIPInputStream(response.getEntity().getContent());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream loadStreamByUrl(String sUrl)
	{

		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(sUrl));
			request.setHeader("Accept-Language", "ru");
			// HttpPost post = new HttpPost(PROXY_URL);
			// List pairs = new ArrayList();
			// pairs.add(new BasicNameValuePair("url", sUrl));
			// post.setEntity(new UrlEncodedFormEntity(pairs));
			// HttpResponse response = client.execute(post);

			HttpResponse response = client.execute(request);
			System.out.println("получен поток с сервера");

			return response.getEntity().getContent();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
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
			//
			// try
			// {
			// byte[] unzippedBytes =C_FileHelper.decompress(zippedSt
			// .getBytes());
			// unzippedSt = new String(unzippedBytes);
			// } catch (Exception e)
			// {
			// e.printStackTrace();
			// }
		}
		return zippedSt;
	}

	public static JSONObject XML2JSON(String xml)
	{
		org.json.JSONObject xmlJSONObj = null;
		try
		{
			if (xml != null)
			{
				xmlJSONObj = org.json.XML.toJSONObject(xml);
				String jsonPrettyPrintString = xmlJSONObj.toString();
				JSONObject jObject = new JSONObject(jsonPrettyPrintString);
				return jObject;
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
