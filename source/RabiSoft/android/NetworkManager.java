package RabiSoft.android;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

public class NetworkManager {

	static class ConnectConnection implements ServiceConnection {

		ConnectionListener.Stub m_listenerConnect;
		DisConnectionListener.Stub m_listenerDisConnect;
		
		ConnectConnection(ConnectionListener.Stub listenerConnect, DisConnectionListener.Stub listenerDisConnect) {
			m_listenerConnect = listenerConnect;
			m_listenerDisConnect = listenerDisConnect;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			ConnectionBinder binder = ConnectionBinder.Stub.asInterface(service);
			
			try {
				binder.requestConnect(m_listenerConnect);
			} catch (RemoteException e) {
				// do nothing.
			}
			
			try {
				binder.putDisConnectListener(m_listenerDisConnect);
			} catch (RemoteException e) {
				// do nothing.
			}

		}

		public void onServiceDisconnected(ComponentName name) {
			// do nothing.
		}
		
	};

	static class EnableConnection implements ServiceConnection {

		RadioStateListener.Stub m_listener;
		
		EnableConnection(RadioStateListener.Stub listener) {
			m_listener = listener;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			ConnectionBinder binder = ConnectionBinder.Stub.asInterface(service);
			
			try {
				binder.requestEnable(m_listener);
			} catch (RemoteException e) {
				// do nothing.
			}

		}

		public void onServiceDisconnected(ComponentName name) {
			// do nothing.
		}
		
	};

	public enum TypeNetwork {

		Default(m_actionDefaultService),
		Mobile(m_actionMobileService),
		Wifi(m_actionWifiService),
		Bluetooth(m_actionBluetoothService),
		Nfc(m_actionNfcService),
		Tethering(m_actionTetheringService),
		None(null);

		public String m_action;

		TypeNetwork(String action) {
			m_action = action;
		}

	}

	public enum TypeConnection {
		Manual, Auto
	}

	public static final String m_actionDefaultService = "RabiSoft.NetworkManager.intent.action.DEFAULT_SERVICE";
	public static final String m_actionMobileService = "RabiSoft.NetworkManager.intent.action.MOBILE_SERVICE";
	public static final String m_actionBluetoothService = "RabiSoft.NetworkManager.intent.action.BLUETOOTH_SERVICE";
	public static final String m_actionWifiService = "RabiSoft.NetworkManager.intent.action.WIFI_SERVICE";
	public static final String m_actionNfcService = "RabiSoft.NetworkManager.intent.action.NFC_SERVICE";
	public static final String m_actionTetheringService = "RabiSoft.NetworkManager.intent.action.TETHERING_SERVICE";

	public static final String m_keyColumn_Connection = "connection";

	public static final String m_pathContent = "content://RabiSoft.NetworkManager.SettingsProvider";

	public static final String m_pathConnectionDefault = "/ConnectionDefault";
	public static final String m_pathConnectionMobile = "/ConnectionMobile";
	public static final String m_pathConnectionWifi = "/ConnectionWifi";
	public static final String m_pathConnectionBluetooth = "/ConnectionBluetooth";
	public static final String m_pathConnectionNfc = "/ConnectionNfc";

	HashMap<TypeNetwork, ConnectConnection> m_connects = new HashMap<TypeNetwork, ConnectConnection>();
	HashMap<TypeNetwork, EnableConnection> m_enables = new HashMap<TypeNetwork, EnableConnection>();

	static public Intent getIntent(Context context, TypeNetwork type) {

		ResolveInfo info;

		{
			List<ResolveInfo> list;

			{
				PackageManager manager = context.getPackageManager();
				Intent query = new Intent();
				query.setAction(type.m_action);
				list = manager.queryIntentServices(query, 0);
			}

			if (list.isEmpty()) {
				return null;
			}

			info = list.get(0);
		}

		Intent service = new Intent();
		service.setClassName(info.serviceInfo.packageName, info.serviceInfo.name);
		return service;

	}

	public void connect(Context context, TypeNetwork type) {
		connect(context, type, null, null);
	}

	public void connect(Context context, TypeNetwork type, ConnectionListener.Stub listenerConnect) {
		connect(context, type, listenerConnect, null);
	}

	public void connect(Context context, TypeNetwork type, ConnectionListener.Stub listenerConnect, DisConnectionListener.Stub listenerDisConnect) {
		
   		Intent service = getIntent(context, type);
		if( service == null ) {
			return;
		}

		ConnectConnection connection = new ConnectConnection(listenerConnect, listenerDisConnect);

   		boolean b = context.bindService(service, connection, Context.BIND_AUTO_CREATE);
   		if( ! b ) {
   			return;
   		}
   		
		m_connects.put(type, connection);

	}
	
	public void disconnect(Context context, TypeNetwork type) {

		ConnectConnection connection = m_connects.remove(type);
		if( connection != null ) {
			context.unbindService(connection);
		}
		
	}

	public void disconnectAll(Context context) {

		Collection<ConnectConnection> collection = m_connects.values();
		
		for( ConnectConnection connection : collection ) {
			if( connection != null ) {
				context.unbindService(connection);
			}
		}
		
		m_connects.clear();
		
	}

	public void enable(Context context, TypeNetwork type) {
		enable(context, type, null);
	}

	public void enable(Context context, TypeNetwork type, RadioStateListener.Stub listener) {

		Intent service = getIntent(context, type);
		if( service == null ) {
			return;
		}

   		EnableConnection connection = new EnableConnection(listener);

   		boolean b = context.bindService(service, connection, Context.BIND_AUTO_CREATE);
   		if( ! b ) {
   			return;
   		}
		
   		m_enables.put(type, connection);

	}
	
	public void disable(Context context, TypeNetwork type) {

		EnableConnection connection = m_enables.remove(type);
		if( connection != null ) {
			context.unbindService(connection);
		}
		
	}

	public void disableAll(Context context) {

		Collection<EnableConnection> collection = m_enables.values();
		
		for( EnableConnection connection : collection ) {
			if( connection != null ) {
				context.unbindService(connection);
			}
		}
		
		m_enables.clear();
		
	}

	public static TypeConnection getConnection(Context context, TypeNetwork type) {
		switch (type) {
			case Mobile:
				return getMobileConnection(context);
			case Wifi:
				return getWifiConnection(context);
			case Bluetooth:
				return getBluetoothConnection(context);
			case Nfc:
				return getNfcConnection(context);
			case Tethering:
				return getTetheringConnection(context);
			default:
				throw new IllegalArgumentException();
		}
	}

	public static TypeConnection getDefaultConnection(Context context) {

		String path = m_pathContent + m_pathConnectionDefault;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getMobileConnection(Context context) {

		String path = m_pathContent + m_pathConnectionMobile;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getBluetoothConnection(Context context) {

		String path = m_pathContent + m_pathConnectionBluetooth;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getWifiConnection(Context context) {

		String path = m_pathContent + m_pathConnectionWifi;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getNfcConnection(Context context) {

		String path = m_pathContent + m_pathConnectionNfc;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getTetheringConnection(Context context) {
		return TypeConnection.Auto;
	}

	protected static TypeConnection getConnection(Context context, String strUri) {

		TypeConnection connection = TypeConnection.Manual;

		{
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse(strUri);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_Connection);
				String value = cursor.getString(index);
				connection = TypeConnection.valueOf(value);
				cursor.close();
			}
		}

		return connection;

	}

}
