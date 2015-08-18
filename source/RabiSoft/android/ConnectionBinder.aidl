package RabiSoft.android;

import RabiSoft.android.ConnectionListener;
import RabiSoft.android.RadioStateListener;
import RabiSoft.android.DisConnectionListener;

interface ConnectionBinder {

	void requestConnect(ConnectionListener listener);
	void requestEnable(RadioStateListener listener);
	void requestApEnable(RadioStateListener listener);
	void putDisConnectListener(DisConnectionListener listener);

}
