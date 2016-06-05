package jp.hotdrop.gmapp.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

import jp.hotdrop.gmapp.R;

public class AppUtil {

    private static final String TAG = AppUtil.class.getSimpleName();
    private static final String STRING_RES_TYPE = "string";

    public static void setTaskDescription(Activity activity, String label, int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setTaskDescription(new ActivityManager.TaskDescription(label, null ,color));
        }
    }

    public static int getThemeColorPrimary(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

}
