package jp.hotdrop.gmapp;

import android.app.Application;

import jp.hotdrop.gmapp.di.AppComponent;
import jp.hotdrop.gmapp.di.AppModule;
import jp.hotdrop.gmapp.di.DaggerAppComponent;

public class MainApplication extends Application {

    AppComponent appComponent;

    public AppComponent getComponent() {
        return appComponent;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }
}
