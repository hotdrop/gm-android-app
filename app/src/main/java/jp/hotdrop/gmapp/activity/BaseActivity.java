package jp.hotdrop.gmapp.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import jp.hotdrop.gmapp.MainApplication;
import jp.hotdrop.gmapp.di.ActivityComponent;
import jp.hotdrop.gmapp.di.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity {

    private ActivityComponent activityComponent;

    @NonNull
    public ActivityComponent getComponent() {
        if(activityComponent == null) {
            MainApplication mainApplication = (MainApplication)getApplication();
            activityComponent = mainApplication.getComponent().plus(new ActivityModule(this));
        }

        return activityComponent;
    }
}
