package RabiSoft.android;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class Boot {

	public static final String m_actionBootCompeted = "RabiSoft.BootCompletedManager.intent.action.BOOT_COMPLETED";
	
	public static final String m_keyColumn_Package = "package";
	
	public static final String m_pathContent = "content://RabiSoft.BootCompletedManager.SettingsProvider";
	
	public static final String m_pathPackages = "/Packages";

	public static void registerBootPackage(Context context) {
		ContentResolver resolver = context.getContentResolver();
		String path = m_pathContent + m_pathPackages;
		Uri uri = Uri.parse(path);
		ContentValues values = new ContentValues();
		String namePackage = context.getPackageName();
		values.put(m_keyColumn_Package, namePackage);
		try {
			resolver.insert(uri, values);
		} catch (IllegalArgumentException e) {
			// do nothing.
		}
	}

	public static void unregisterBootPackage(Context context) {
		ContentResolver resolver = context.getContentResolver();
		String path = m_pathContent + m_pathPackages;
		Uri uri = Uri.parse(path);
		String where = m_keyColumn_Package + "=?";
		String namePackage = context.getPackageName();
		String[] args = { namePackage };
		try {
			resolver.delete(uri, where, args);
		} catch (IllegalArgumentException e) {
			// do nothing.
		}
	}

}
