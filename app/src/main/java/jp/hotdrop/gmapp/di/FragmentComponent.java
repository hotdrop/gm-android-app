package jp.hotdrop.gmapp.di;

import dagger.Subcomponent;
import jp.hotdrop.gmapp.di.scope.FragmentScope;
import jp.hotdrop.gmapp.fragment.GoodsFragment;
import jp.hotdrop.gmapp.fragment.GoodsRegisterFragment;
import jp.hotdrop.gmapp.fragment.GoodsTabFragment;
import jp.hotdrop.gmapp.fragment.GoodsUpdateFragment;

@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(GoodsTabFragment fragment);

    void inject(GoodsFragment fragment);

    void inject(GoodsUpdateFragment fragment);

    void inject(GoodsRegisterFragment fragment);
}
