package jp.hotdrop.gmapp.activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import javax.inject.Singleton;

import jp.hotdrop.gmapp.model.Goods;

@Singleton
public class ActivityNavigator {

    public void showMain(@NonNull Activity activity) {
        MainActivity.start(activity);
    }

    public void showGoodsUpdate(Fragment fragment, Goods goods, int requestCode) {
        GoodsUpdateActivity.startForResult(fragment, goods, requestCode);
    }
}
