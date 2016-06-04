package jp.hotdrop.gmapp.activity;

import android.app.Activity;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

@Singleton
public class ActivityNavigator {

    public void showMain(@NonNull Activity activity, boolean isRefresh) {
        MainActivity.start(activity, isRefresh);
    }
}
