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
import android.view.MenuItem;
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

    @Inject
    CompositeSubscription compositeSubscription;

    private GoodsDao dao;
    private GoodsPagerAdapter adapter;
    private FragmentGoodsListBinding binding;

    private int refreshMode;
    private String activeTabName;

    private OnChangeGoodsListener onChangeGoodsListener = session -> {/* no operation */};

    /**
     * コンストラクタ
     */
    public static GoodsFragment newInstance() {
        GoodsFragment fragment = new GoodsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REFRESH_MODE, REFRESH_NONE);
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

    @Override
    public void onResume() {
        super.onResume();
        int refreshMode = getActivity().getIntent().getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE);

        if(refreshMode == REFRESH_ALL) {
            int idx = binding.viewPager.getCurrentItem();
            if(idx != 0) {
                activeTabName = adapter.getPageTitle(idx).toString();
            }
            compositeSubscription.add(loadData());
            getActivity().getIntent().removeExtra(ARG_REFRESH_MODE);
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
            this.refreshMode = getArguments().getInt(ARG_REFRESH_MODE);
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
        compositeSubscription.add(loadData());
        return binding.getRoot();
    }

    protected Subscription loadData() {
        showLoadingView();
        Observable<List<Goods>> cachedGoodsList = dao.selectAll();
        return cachedGoodsList.flatMap(goodsList -> Observable.just(goodsList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onLoadDataSuccess,
                        this::onLoadDataFailure
                );
    }

    private void onLoadDataSuccess(List<Goods> goodsList) {
        if(refreshMode != REFRESH_NONE) {
            goodsList = dao.selectAll().toBlocking().single();
        }
        groupByCategoryGoods(goodsList);
    }

    private void onLoadDataFailure(Throwable throwable) {
        Snackbar.make(binding.containerMain, "ロードに失敗しました。", Snackbar.LENGTH_LONG).show();
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
        
        if(activeTabName != null) {
            int idx = adapter.getPagePosition(activeTabName);
            binding.viewPager.setCurrentItem(idx);
        }

        hideLoadingView();
    }

    private void addFragment(String title, List<Goods> goodsList) {
        GoodsTabFragment fragment = createTabFragment(goodsList);
        adapter.add(title, fragment);
    }

    protected GoodsTabFragment createTabFragment(List<Goods> goodsList) {
        return GoodsTabFragment.newInstance(goodsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_goods_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO 検索アイコン選択時の動作を書く
        return super.onOptionsItemSelected(item);
    }

    /**
     * アダプタークラス
     */
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

        public int getPagePosition(String argTitle) {
            int idx = 0;
            for(String title : titles) {
                if(title.equals(argTitle)) {
                    break;
                }
                idx++;
            }
            return idx;
        }
    }

    /**
     * タブ選択時のカスタムリスナークラス
     */
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
