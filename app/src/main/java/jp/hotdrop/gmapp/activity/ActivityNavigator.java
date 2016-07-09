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

    public void showGoodsUpdate(@NonNull Fragment fragment, @NonNull Goods goods, int requestCode) {
        GoodsUpdateActivity.startForResult(fragment, goods, requestCode);
    }

    public void showGoodsRegister(@NonNull Fragment fragment, @NonNull String tabName, int requestCode) {
        GoodsRegisterActivity.startForResult(fragment, tabName, requestCode);
    }
}
