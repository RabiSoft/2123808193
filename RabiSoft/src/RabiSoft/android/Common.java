package RabiSoft.android;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.PowerManager;

public class Common {

	public static final String m_actionPickReceiveIntent = "RabiSoft.intent.action.PICK_RECEIVE_INTENT";
	public static final String m_actionPickSendIntent = "RabiSoft.intent.action.PICK_SEND_INTENT";
	
	public static final String m_categoryManaged = "RabiSoft.intent.category.MANAGED";
	
	public static final String m_typeBroadcastIntent = "intent/broadcast";
	public static final String m_typeActivityIntent = "intent/activity";
	public static final String m_typeServiceIntent = "intent/service";

	public static final String m_keyExtra_ExtraName = "extraName";
	public static final String m_keyExtra_ExtraValue = "extraValue";

	public static final String m_actionPickPackage = "RabiSoft.intent.action.PICK_PACKAGE";

	public static final String m_keyExtra_Package = "package";

	public static final String m_actionPickLocation = "RabiSoft.intent.action.PICK_LOCATION";

	public static final String m_keyExtra_Latitude = "latitude";
	public static final String m_keyExtra_Longitude = "longitude";
	public static final String m_keyExtra_Altitude = "altitude";

    public static final String m_actionChangeMode = "RabiSoft.intent.action.MODE_CHANGE";

    public static final String m_keyExtra_Mode = "mode";

    public static final String m_actionPickMode = "RabiSoft.intent.action.PICK_MODE";
    public static final String m_actionPickSlot = "RabiSoft.intent.action.PICK_SLOT";

    public static final String m_keyExtra_Name = "name";

	public static final String m_actionPickPassword = "RabiSoft.intent.action.PICK_PASSWORD";

	public static final String m_keyExtra_Password = "password";

	public static final String m_actionEditText = "RabiSoft.intent.action.EDIT_TEXT";

	public static enum SensorSleep {
		sleep, wake
	}

	public static enum SensorWakeLock {

		none(0),
		partial(PowerManager.PARTIAL_WAKE_LOCK),
		@SuppressWarnings("deprecation")
		screen_dim(PowerManager.SCREEN_DIM_WAKE_LOCK);

		public int m_flag;

		SensorWakeLock(int flag) {
			m_flag = flag;
		}

	}
	
	public static final String m_keyColumn_Provider = "provider";
	public static final String m_keyColumn_SensorSleep = "sensor_sleep";
	public static final String m_keyColumn_SensorWakeLock = "sensor_wakelock";
	
	public static final String m_pathContent = "content://RabiSoft.CommonSettings.SettingsProvider";
	
	public static final String m_pathLocationProvider = "/LocationProvider";
	public static final String m_pathSensorSleep = "/SensorSleep";
	public static final String m_pathSensorWakeLock = "/SensorWakeLock";

	public static String getLocationProvider(Context context) {

		String provider = getCurrentLocationProvider(context);

		if (provider == null) {
			LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				provider = LocationManager.GPS_PROVIDER;
			} else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
			} else {
				// do nothing.
			}
		}

		return provider;

	}

	public static String getCurrentLocationProvider(Context context) {

		String provider = null;

		{
			ContentResolver resolver = context.getContentResolver();
			String path = m_pathContent + m_pathLocationProvider;
			Uri uri = Uri.parse(path);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_Provider);
				provider = cursor.getString(index);
				cursor.close();
			}
		}

		return provider;
		
	}
	
	public static void registerObserverLocationProvider(Context context, ContentObserver observer) {

		ContentResolver resolver = context.getContentResolver();
		String path = m_pathContent + m_pathLocationProvider;
		Uri uri = Uri.parse(path);
		resolver.registerContentObserver(uri, false, observer);

	}
	
	public static void unregisterObserverLocationProvider(Context context, ContentObserver observer) {

		ContentResolver resolver = context.getContentResolver();
		resolver.unregisterContentObserver(observer);

	}
	
	public static SensorSleep getSensorSleep(Context context) {

		SensorSleep sleep = SensorSleep.wake;

		{
			ContentResolver resolver = context.getContentResolver();
			String path = m_pathContent + m_pathSensorSleep;
			Uri uri = Uri.parse(path);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_SensorSleep);
				String value = cursor.getString(index);
				sleep = SensorSleep.valueOf(value);
				cursor.close();
			}
		}

		return sleep;

	}

	public static SensorWakeLock getSensorWakeLock(Context context) {

		SensorWakeLock lock = SensorWakeLock.screen_dim;

		{
			ContentResolver resolver = context.getContentResolver();
			String path = m_pathContent + m_pathSensorWakeLock;
			Uri uri = Uri.parse(path);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_SensorWakeLock);
				String value = cursor.getString(index);
				lock = SensorWakeLock.valueOf(value);
				cursor.close();
			}
		}

		return lock;

	}
	
	public static void registerObserverSensorWakeLock(Context context, ContentObserver observer) {

		ContentResolver resolver = context.getContentResolver();
		String path = m_pathContent + m_pathSensorWakeLock;
		Uri uri = Uri.parse(path);
		resolver.registerContentObserver(uri, false, observer);

	}
	
	public static void unregisterObserverSensorWakeLock(Context context, ContentObserver observer) {

		ContentResolver resolver = context.getContentResolver();
		resolver.unregisterContentObserver(observer);

	}

}
