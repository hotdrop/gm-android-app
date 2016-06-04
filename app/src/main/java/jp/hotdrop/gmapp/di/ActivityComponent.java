package jp.hotdrop.gmapp.di;

import dagger.Subcomponent;
import jp.hotdrop.gmapp.activity.MainActivity;
import jp.hotdrop.gmapp.di.scope.ActivityScope;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);
}
