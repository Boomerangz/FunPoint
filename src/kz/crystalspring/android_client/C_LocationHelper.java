package kz.crystalspring.android_client;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
 

//public class LocationHelper implements LocationIntarface {
public class C_LocationHelper {
	protected LocationListener fLocationListener;
	protected LocationManager fLocationManager;
	protected Criteria fCriteria;
	protected String fProviderName = null;
	protected Context fContext;
	public boolean fIsProviderEnabled = false;
	static final String C_TAG = "LocationHelper";
  

	public C_LocationHelper(Context context, String pProviderName, boolean pIsBackground) {
		fLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		fCriteria = new Criteria();
		if (pProviderName != null) {
			if (pProviderName.length() > 0) {
				fProviderName = pProviderName;
			}
		}
		if (pIsBackground) {
			fCriteria.setAccuracy(Criteria.POWER_LOW);
		} else {
			fCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		}
		fContext = context;
	}

	/**
	 * Проверка доступности провайдера получения местоположения
	 * Для network проверка производится по доступности сети, для отстальных - стандартный метод
	 * @param pProvider - строка с именем провайдера
	 * @return - результат
	 */
	private boolean isThisProviderEnabled (String pProvider) {
		if (fLocationManager == null) return false;
		if (pProvider.equals(LocationManager.NETWORK_PROVIDER)) {
			return C_Utils.isNetworkEnabled(fContext) & fLocationManager.isProviderEnabled(pProvider);
		}
		return fLocationManager.isProviderEnabled(pProvider);
	}
	
	/**
	* Получение местоположения для указанных параметров. 
	* Если посдледнее полученое ранее обновление не подходит по параметрам и установлен Listener, 
	* то запускается прослушивание для лучшего провайдера. 
	* @param pMinAccuracy - требуемая точность (м) 
	* @param pMaxTimeAgo - время, прошедшее с момента последнего обновления (сек)
	* @return Location
	*/
	public Location getLocation(int pMinAccuracy, int pMaxTimeAgo) {
		if (fLocationManager == null) return null;
		C_Log.v(3, C_TAG, "getLocation - pMinAccuracy=" + pMinAccuracy + ", pMaxTimeAgo=" + pMaxTimeAgo + " - start");
		Location vBestResult = null;
		long vMaxTime = new Date().getTime() - pMaxTimeAgo * 1000;
		String vSelectedProvider = null;
		String vOneProvider = null; 
		fIsProviderEnabled = false;
		
		// перебор всех провайдеров для поиска готового результата:
		List<String> vProviders = fLocationManager.getAllProviders();
		for (String vItemProvider: vProviders) {
			Location vLocation = fLocationManager.getLastKnownLocation(vItemProvider);
			if (vLocation != null) {
				float vAccuracy = vLocation.getAccuracy();
				long vTime = vLocation.getTime();
				if ((vTime > vMaxTime && vAccuracy <= pMinAccuracy)) {
					vBestResult = vLocation;
				} else {
					String vOneProv = vLocation.getProvider();
					if (isThisProviderEnabled(vOneProv)) {
						// запоминаем на всякий случай доступного провайдера
						vOneProvider = vOneProv;
					}
				}
			}
		}
		if (vBestResult == null && fLocationListener != null) { 
			// если не найден готовый результат и имеется listener отложенных ответов то:
			// 1. получаем провайдера - если указан при запуске, то выбвраем указанного
			if (fProviderName != null) {
				if (isThisProviderEnabled(fProviderName)) {
					vSelectedProvider = fProviderName;
					fIsProviderEnabled = true;
				}
			}
			// 2. иначе выбираем лучшего по энегропотреблению:
			if ( !fIsProviderEnabled) {
				String vBestProvider = fLocationManager.getBestProvider(fCriteria, true);
				if (vBestProvider != null) {
					if (isThisProviderEnabled(vBestProvider)) {
						vSelectedProvider = vBestProvider;
						fIsProviderEnabled = true;
					}
				}
			}
			// 3. если лучший недоступен то берем любой доступный:  
			if ( !fIsProviderEnabled && vOneProvider != null) {
				// если лучший провайдер недоступен то берем первый доступный провайдер:
				vSelectedProvider = vOneProvider;
				fIsProviderEnabled = true;
			}
			// если имеется доступрый провайдер - запускаем обновление:
			if (fIsProviderEnabled) {
				C_Log.v(3, C_TAG, "getLocation - requestLocationUpdates, vSelectedProvider=" + vSelectedProvider);
				fLocationManager.requestLocationUpdates(vSelectedProvider, 0, 0, fLocationListener, fContext.getMainLooper());
			}
		} else {
			C_Log.v(3, C_TAG, "getLocation (ok) - end");
		}
		return vBestResult;
	}
  
	/*
	protected LocationListener fUpdateListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (fLocationListener != null && location != null)
				fLocationListener.onLocationChanged(location);
			fLocationManager.removeUpdates(fUpdateListener);
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (fLocationListener != null) 
				fLocationListener.onStatusChanged(provider, status, extras);
		}
		public void onProviderEnabled(String provider) {
			if (fLocationListener != null) 
				fLocationListener.onProviderEnabled(provider);    	
		}
		public void onProviderDisabled(String provider) {
			if (fLocationListener != null) 
				fLocationListener.onProviderDisabled(provider);
		}
	};
	*/
  
	/**
	 * Установка {@link LocationListener} для получения однократного обновления
	 * @param pLocationListener LocationListener
	 */
	public void setChangedLocationListener(LocationListener pLocationListener) {
		C_Log.v(3, C_TAG, "setChangedLocationListener");		
		fLocationListener = pLocationListener;
	}
  
	/**
	 * Принудительное отключение ожидания обновления
	 */
	public void cancel() {
		if (fLocationManager == null) return;
		C_Log.v(3, C_TAG, "cancel");		
		fLocationManager.removeUpdates(fLocationListener);
		fLocationManager = null;
//		fLocationManager.sendExtraCommand(fProviderName, "stop", new Bundle )
	}



}