package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.activity.ActivityNavigator;
import jp.hotdrop.gmapp.databinding.FragmentGoodsTabBinding;
import jp.hotdrop.gmapp.databinding.ItemGoodsBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;

public class GoodsTabFragment extends BaseFragment {

    private static final String ARG_GOODS = "goods";

    @Inject
    protected ActivityNavigator activityNavigator;

    private GoodsAdapter adapter;
    private FragmentGoodsTabBinding binding;
    private List<Goods> goodsList;

    private GoodsFragment.OnChangeGoodsListener onChangeGoodsListener = goodsList -> {/* no operation */};

    public static GoodsTabFragment newInstance(List<Goods> goodsList) {
        GoodsTabFragment fragment = new GoodsTabFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GOODS, Parcels.wrap(goodsList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsList = Parcels.unwrap(getArguments().getParcelable(ARG_GOODS));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
        if (context instanceof GoodsFragment.OnChangeGoodsListener) {
            onChangeGoodsListener = (GoodsFragment.OnChangeGoodsListener)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoodsTabBinding.inflate(inflater, container, false);
        adapter = new GoodsAdapter(getContext());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.addAll(goodsList);
        return binding.getRoot();
    }

    /**
     * startActivityForResultで起動させたFragmentが
     * finish()により破棄された時に呼ばれる
     * @param requestCode startActivityForResultの第二引数で指定した値
     * @param resultCode 起動先ActivityのsetResultの第一引数
     * @param data intentの値
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        int refreshMode = data.getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE);
        Goods goods = Parcels.unwrap(data.getParcelableExtra(Goods.class.getSimpleName()));

        if(requestCode == REQ_CODE_UPDATE && goods != null) {
            switch(refreshMode) {
                case REFRESH_ONE:
                    adapter.refresh(goods);
                    onChangeGoodsListener.onChangeGoods(Collections.singletonList(goods));
                    break;
                case REFRESH_ALL:
                    // 全リフレッシュはTabFragmentではできないため上位のフラグメントに任せる。
                    getActivity().setIntent(data);
                    break;
                case REFRESH_NONE:
                default:
                    break;
            }
        }
    }

    public void scrollUpToTop() {
        binding.recyclerView.smoothScrollToPosition(0);
    }

    /**
     * アダプター
     */
    protected class GoodsAdapter extends ArrayRecyclerAdapter<Goods, BindingHolder<ItemGoodsBinding>> {

        public GoodsAdapter(@NonNull Context context) {
            super(context);
        }

        private void refresh(Goods goods) {
            for(int i = 0; i < adapter.getItemCount(); i++) {
                Goods g = getItem(i);
                if(goods.equals(g)) {
                    g.change(goods);
                    adapter.notifyItemChanged(i);
                }
            }
        }

        @Override
        public BindingHolder<ItemGoodsBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_goods);
        }

        @Override
        public void onBindViewHolder(BindingHolder<ItemGoodsBinding> holder, int position) {

            Goods goods = getItem(position);
            ItemGoodsBinding binding = holder.binding;
            binding.setGoods(goods);

            // TODO 量のクリックイベントを実装する
            binding.imgAmount.setOnClickListener(v ->
                    Toast.makeText(getActivity(), "画像が押下されました。", Toast.LENGTH_LONG).show()
            );

            binding.cardView.setOnClickListener(v ->
                    activityNavigator.showGoodsUpdate(GoodsTabFragment.this, goods, REQ_CODE_UPDATE));
        }
    }
}
