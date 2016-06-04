package jp.hotdrop.gmapp.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jp.hotdrop.gmapp.activity.ActivityNavigator;
import rx.subscriptions.CompositeSubscription;

@Module
public class AppModule {

    private Context context;

    public AppModule(Application app) {
        context = app;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    public CompositeSubscription provideCompositeSubscription() {
        return new CompositeSubscription();
    }

    @Singleton
    @Provides
    public ActivityNavigator provideActivityNavigator() {
        return new ActivityNavigator();
    }
}
