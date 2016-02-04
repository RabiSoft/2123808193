package RabiSoft.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MicManager {

	public static final String m_actionPause = "RabiSoft.intent.action.PAUSE_MIC";
	public static final String m_actionResume = "RabiSoft.intent.action.RESUME_MIC";
	
	public static void pause(Context context, BroadcastReceiver receiverOnPause) {
		Intent broadcast = new Intent();
		broadcast.setAction(m_actionPause);
		context.sendOrderedBroadcast(broadcast, null, receiverOnPause, null, 0, null, null);
	}

	public static void resume(Context context, BroadcastReceiver receiverOnResume) {
		Intent broadcast = new Intent();
		broadcast.setAction(m_actionResume);
		context.sendOrderedBroadcast(broadcast, null, receiverOnResume, null, 0, null, null);
	}
		
}
