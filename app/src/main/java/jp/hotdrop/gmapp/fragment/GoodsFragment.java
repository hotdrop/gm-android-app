package jp.hotdrop.gmapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsListBinding;
import jp.hotdrop.gmapp.model.Goods;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GoodsFragment extends BaseFragment {

    private static final String ARG_IS_REFRESH = "isRefresh";

    GoodsDao dao;
    @Inject
    CompositeSubscription compositeSubscription;

    /** アダプター */
    private GoodsPagerAdapter adapter;
    /** 商品リスト画面のバインドオブジェクト */
    private FragmentGoodsListBinding binding;
    /** 他画面からの引数。リフレッシュするかどうか */
    private boolean isRefresh;
    /** 一覧変更リスナー */
    private OnChangeGoodsListener onChangeGoodsListener = session -> {};

    /**
     * コンストラクタ
     */
    public static GoodsFragment newInstance() {
        return newInstance(false);
    }

    /**
     * 引数付きコンストラクタ
     * @param isRefresh
     * @return
     */
    public static GoodsFragment newInstance(boolean isRefresh) {
        GoodsFragment fragment = new GoodsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_REFRESH, isRefresh);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * インスタンス生成で呼ばれるアタッチイベント
     * Activityに関連付けされる際に１度だけ呼ばれるようだ。なのでリスナー設定だけする
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
        if (context instanceof OnChangeGoodsListener) {
            onChangeGoodsListener = (OnChangeGoodsListener) context;
        }
    }

    /**
     * フラグメントの初期化処理を行う。
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.isRefresh = getArguments().getBoolean(ARG_IS_REFRESH);
        }

        dao = new GoodsDao(this.getActivity());
    }

    /**
     * Fragmentに関連付けるViewを作成し、returnする。
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGoodsListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        initEmptyView();

        compositeSubscription.add(loadData());
        return binding.getRoot();
    }

    /**
     * ビューの初期化 TODO
     */
    private void initEmptyView() {
    }

    protected Subscription loadData() {
        showLoadingView();
        Observable<List<Goods>> cachedGoodsList = dao.selectAll();
        return cachedGoodsList.flatMap(goodsList -> {
            return Observable.just(goodsList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onLoadDataSuccess,
                        this::onLoadDataFailure
                );
    }

    private void onLoadDataSuccess(List<Goods> goodsList) {
        if(isRefresh) {
            goodsList = dao.selectAll().toBlocking().single();
        }
        // TODO
        groupByCategoryGoods(goodsList);
    }

    private void onLoadDataFailure(Throwable throwable) {
        Snackbar.make(binding.containerMain, "ロードに失敗しました。", Snackbar.LENGTH_LONG).show();
    }

    protected void showEmptyView() {
        //binding.emptyView.setVisibility(View.VISIBLE);
    }

    protected void hideEmptyView() {
        // TODO
    }

    protected void showLoadingView() {
        binding.progressBarContainer.setVisibility(View.VISIBLE);
    }

    protected void hideLoadingView() {
        binding.progressBarContainer.setVisibility(View.GONE);
    }

    public interface OnChangeGoodsListener {
        void onChangeGoods(List<Goods> goodsList);
    }

    protected void groupByCategoryGoods(List<Goods> goodsList) {
        Map<String, List<Goods>> goodsByCategory = new TreeMap<>();
        for(Goods goods : goodsList) {
            String key = goods.getCategoryName();
            if(goodsByCategory.containsKey(key)) {
                goodsByCategory.get(key).add(goods);
            } else {
                List<Goods> list = new ArrayList<>();
                list.add(goods);
                goodsByCategory.put(key, list);
            }
        }

        adapter = new GoodsPagerAdapter(getFragmentManager());

        for(Map.Entry<String, List<Goods>> e : goodsByCategory.entrySet()) {
            addFragment(e.getKey(), e.getValue());
        }

        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.tabLayout.setOnTabSelectedListener(new CustomViewPagerOnTabSelectedListener(binding.viewPager));

        hideLoadingView();
        if(goodsList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    protected GoodsTabFragment createTabFragment(List<Goods> goodsList) {
        return GoodsTabFragment.newInstance(goodsList);
    }

    private void addFragment(String title, List<Goods> goodsList) {
        GoodsTabFragment fragment = createTabFragment(goodsList);
        adapter.add(title, fragment);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_goods_list, menu);
    }

    private class GoodsPagerAdapter extends FragmentStatePagerAdapter {

        private List<GoodsTabFragment> fragments = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        public GoodsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        @Nullable
        public Fragment getItem(int position) {
            if(position >= 0 && position < fragments.size()) {
                return fragments.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public void add(String title, GoodsTabFragment fragment) {
            fragments.add(fragment);
            titles.add(title);
            notifyDataSetChanged();
        }
    }

    private class CustomViewPagerOnTabSelectedListener extends TabLayout.ViewPagerOnTabSelectedListener {

        public CustomViewPagerOnTabSelectedListener(ViewPager viewPager) {
            super(viewPager);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            super.onTabReselected(tab);
            GoodsTabFragment fragment = (GoodsTabFragment) adapter.getItem(tab.getPosition());
            if (fragment != null) {
                fragment.scrollUpToTop();
            }
        }
    }

}
