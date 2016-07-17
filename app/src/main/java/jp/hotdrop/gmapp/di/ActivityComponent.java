package jp.hotdrop.gmapp.di;

import dagger.Subcomponent;
import jp.hotdrop.gmapp.activity.CategoryUpdateActivity;
import jp.hotdrop.gmapp.activity.GoodsRegisterActivity;
import jp.hotdrop.gmapp.activity.GoodsUpdateActivity;
import jp.hotdrop.gmapp.activity.MainActivity;
import jp.hotdrop.gmapp.di.scope.ActivityScope;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(GoodsUpdateActivity activity);

    void inject(GoodsRegisterActivity activity);

    void inject(CategoryUpdateActivity activity);

    FragmentComponent plus(FragmentModule module);
}
