package jp.hotdrop.gmapp.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.databinding.ActivityMainBinding;
import jp.hotdrop.gmapp.fragment.GoodsFragment;
import jp.hotdrop.gmapp.fragment.StackedPageListener;
import jp.hotdrop.gmapp.model.MainContentStateBrokerProvider;
import jp.hotdrop.gmapp.model.Page;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private static final String EXTRA_MENU = "menu";
    private static final long DRAWER_CLOSE_DELAY_MILLS = 300L;

    @Inject
    MainContentStateBrokerProvider brokerProvider;
    @Inject
    CompositeSubscription subscription;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        DataBindingUtil.bind(binding.navView.getHeaderView(0));

        getComponent().inject(this);

        /*
         * 補足
         * このsubscriptionではナビゲーションメニューの各ページを取得。
         * 1. CompositeSubscription: 複数のsubscriptionをまとめてunSubscribeするためのクラス
         * 2. observe(): ここで返している値は
         *    private final Subject<Page, Page> sj = new SerializedSubject<>(PublishSubject.create());
         *    このsjがobserver()の戻り値となっている。
         *    subject: subscriberとObservableの機能を併せ持ったもの。
         *             subscriberのようにonNext onError onCompleteを始めObserverのようにsubscribeを持つ
         *             onNextで最後の1つしか返ってこないような場合はこれを使うと便利らしい。
         */
        subscription.add( //フィールドのCompositeSubscriptionクラスにaddする。unSubscribeするためフィールドにもつ
                brokerProvider.get() // フィールドに定義したMainContentStateBrokerProviderクラス（自作）が持つstaticな自作クラスを返す
                    .observe() // staticな自作クラスでObservable<Page>を返す。これはPage専用のObserve
                    .subscribe(page -> { // subscribeを登録する。登録するのはページで
                            toggleToolbarElevation(page.shouldToggleToolbar());
                            changePage(page.getTitleResId(), page.createFragment());
                            binding.navView.setCheckedItem(page.getMenuId());
                    })
        );

        initView();

        if(savedInstanceState == null) {
            replaceFragment(GoodsFragment.newInstance());
        } else if(savedInstanceState.getInt(EXTRA_MENU) != 0) {
            Page page = Page.forMenuId(savedInstanceState.getInt(EXTRA_MENU));
            binding.toolbar.setTitle(page.getTitleResId());
            toggleToolbarElevation(page.shouldToggleToolbar());
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    /**
     * 現在の表示画面IDを保持する。
     * 長時間放置などでシステムに殺された場合にこれで復旧する。
     * 復旧しているのはonCreateの中
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.content_view);
        if (current != null) {
            outState.putInt(EXTRA_MENU, Page.forName(current).getMenuId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        subscription.unsubscribe();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open, R.string.close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.setItemIconTintList(null);
        binding.navView.setCheckedItem(R.id.nav_goods_list);
    }

    /**
     * フラグメントの置換
     * 生成ではなくリプレイスでフラグメントを実現している。
     * リプレイスしたらcommitする前にバックスタックに追加する。
     * ここでは無条件に追加しているため、メイン画面を生成した分だけ[戻る]ボタンを押さないとアプリが終了できない・・
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.activity_fade_enter, R.anim.activity_fade_exit);
        ft.replace(R.id.content_view, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 戻る操作をした時の挙動
     * バックスタックに積んだフラグメントがあれば取り出す
     */
    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        binding.drawer.closeDrawer(GravityCompat.START);

        Page page = Page.forMenuId(item);
        toggleToolbarElevation(page.shouldToggleToolbar());
        changePage(page.getTitleResId(), page.createFragment());

        return true;
    }

    private void toggleToolbarElevation(boolean enable) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float elevation = enable ? getResources().getDimension(R.dimen.elevation) : 0;
            binding.toolbar.setElevation(elevation);
        }
    }

    private void changePage(@StringRes int titleRes, @NonNull Fragment fragment) {
        new Handler().postDelayed(() -> {
            binding.toolbar.setTitle(titleRes);
            replaceFragment(fragment);
        }, DRAWER_CLOSE_DELAY_MILLS);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_fade_enter, R.anim.activity_fade_exit);
    }

    /**
     * onBackPressedやreplaceFragmentはフラグメントの操作しか行わないため
     * バックスタックに変更があった場合にハンドリングし、ナビゲーションビューの選択状態、
     * ツールバーのタイトルや画面表示時にスクロールバーを一番上に戻す
     * といった操作を行う。
     */
    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.content_view);
        if (current == null) {
            finish();
            return;
        }
        Page page = Page.forName(current);
        binding.navView.setCheckedItem(page.getMenuId());
        binding.toolbar.setTitle(page.getTitleResId());
        toggleToolbarElevation(page.shouldToggleToolbar());
        if (current instanceof StackedPageListener) {
            StackedPageListener l = (StackedPageListener) current;
            l.onTop();
        }
    }
}
