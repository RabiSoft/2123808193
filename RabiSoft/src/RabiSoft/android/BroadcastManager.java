package RabiSoft.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.List;

public class BroadcastManager {

	static private final int m_levelManifestDisable = 26;

	static public void sendBroadcast(Context context, Intent broadcast) {

        context.sendBroadcast(broadcast);

		if( Build.VERSION.SDK_INT < m_levelManifestDisable) {
			return;
		}

		Intent unicast = (Intent)broadcast.clone();

		PackageManager manager = context.getPackageManager();

		List<ResolveInfo> list = manager.queryBroadcastReceivers(broadcast, 0);

		for (ResolveInfo infoResolve : list) {

			{
				ApplicationInfo infoApplication;

				try {
                    infoApplication = manager.getApplicationInfo(infoResolve.activityInfo.packageName, 0);
				} catch (PackageManager.NameNotFoundException e) {
					continue;
				}

				if (infoApplication.targetSdkVersion < m_levelManifestDisable) {
					continue;
				}
			}

			unicast.setClassName(infoResolve.activityInfo.packageName, infoResolve.activityInfo.name);
			context.sendBroadcast(unicast);
		}

	}

}
