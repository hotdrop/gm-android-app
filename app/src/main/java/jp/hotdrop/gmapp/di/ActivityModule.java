package jp.hotdrop.gmapp.di;

import android.support.v7.app.AppCompatActivity;

import dagger.Module;

@Module
public class ActivityModule {

    final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }
}
