package com.sinaapp.bashell.sayhi;

import android.util.Log;

public class LogHelper {
	private static boolean isDebug = true;
	private static final String TAG = "SayHi";

	public static int d(String msg,String subTAG) {
		if (isDebug) {
			return Log.d(TAG + "." + subTAG, msg);
		} else {
			return 0;
		}
	}
}
