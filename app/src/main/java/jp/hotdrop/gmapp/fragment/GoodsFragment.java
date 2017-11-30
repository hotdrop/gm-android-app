package jp.hotdrop.gmapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import jp.hotdrop.gmapp.activity.ActivityNavigator;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsListBinding;
import jp.hotdrop.gmapp.model.Goods;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GoodsFragment extends BaseFragment implements StackedPageListener  {

    @Inject
    protected CompositeSubscription compositeSubscription;
    @Inject
    protected GoodsDao dao;
    @Inject
    protected ActivityNavigator activityNavigator;

    private GoodsPagerAdapter adapter;
    private FragmentGoodsListBinding binding;
    private boolean isRefresh = false;
    private String tabName = "";

    public static GoodsFragment newInstance() {
        return new GoodsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoodsListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        compositeSubscription.add(loadData());
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    /**
     * Tabの切り替えとupdateFragmentでの更新時に無条件で呼ばれる。
     * そのため、商品のカテゴリーを修正した場合はここで検知してリフレッシュする。
     */
    @Override
    public void onResume() {
        super.onResume();
        int refreshMode = getActivity().getIntent().getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE);
        if(refreshMode == REFRESH_ALL) {
            isRefresh = true;
            compositeSubscription.add(loadData());
            getActivity().getIntent().removeExtra(ARG_REFRESH_MODE);
        }
    }

    /**
     * 今の所RegisterFragmentからの復帰時に呼ばれる。
     * （Updateとは別タイミング）
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) {
            return;
        }

        int refreshMode = data.getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE);
        if(refreshMode == REFRESH_ALL) {
            isRefresh = true;
            compositeSubscription.add(loadData());
            getActivity().getIntent().removeExtra(ARG_REFRESH_MODE);
        }
    }

    @Override
    public void onTop() {
        compositeSubscription.add(loadData());
    }

    /**
     * データベースから商品情報をすべて取得して画面表示のためのリストを生成する。
     * @return
     */
    protected Subscription loadData() {
        showLoadingView();
        Observable<List<Goods>> cachedGoodsList = dao.selectAll();
        return cachedGoodsList.flatMap(Observable::just)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::onLoadDataSuccess,
                    this::onLoadDataFailure
                );
    }

    private void onLoadDataSuccess(List<Goods> goodsList) {
        groupByCategoryGoods(goodsList);
    }

    private void onLoadDataFailure(Throwable throwable) {
        Snackbar.make(binding.containerMain, "ロードに失敗しました。", Snackbar.LENGTH_LONG).show();
    }

    protected void groupByCategoryGoods(List<Goods> goodsList) {
        // KEYの格納順にタブを表示したいのでLinkedHashMapを使う
        Map<String, List<Goods>> goodsByCategory = new LinkedHashMap<>();
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
        binding.fabAddButton.setOnClickListener(v -> activityNavigator.showGoodsRegister(GoodsFragment.this, tabName, REQ_CODE_REGISTER));

        if(isRefresh) {
            // もともと選択していたタブを選択状態にする
            binding.viewPager.setCurrentItem(adapter.getPagePosition(tabName));
            isRefresh = false;
        }

        hideLoadingView();
    }

    protected void showLoadingView() {
        binding.progressBarContainer.setVisibility(View.VISIBLE);
    }

    protected void hideLoadingView() {
        binding.progressBarContainer.setVisibility(View.GONE);
    }

    private void addFragment(String title, List<Goods> goodsList) {
        GoodsTabFragment fragment = createTabFragment(goodsList);
        adapter.add(title, fragment);
    }

    private GoodsTabFragment createTabFragment(List<Goods> goodsList) {
        return GoodsTabFragment.newInstance(goodsList);
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

            if(argTitle == null) {
                return 0;
            }

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
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            GoodsTabFragment fragment = (GoodsTabFragment) adapter.getItem(tab.getPosition());
            if(fragment != null) {
                tabName = adapter.getPageTitle(tab.getPosition()).toString();
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            super.onTabReselected(tab);
            GoodsTabFragment fragment = (GoodsTabFragment) adapter.getItem(tab.getPosition());
            if(fragment != null) {
                fragment.scrollUpToTop();
            }
        }
    }

}
