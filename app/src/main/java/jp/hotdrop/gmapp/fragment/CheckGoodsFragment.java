package jp.hotdrop.gmapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.parceler.Parcels;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentCheckGoodsListBinding;
import jp.hotdrop.gmapp.databinding.ItemCheckGoodsBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.util.DataBindingAttributeUtil;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;

public class CheckGoodsFragment extends BaseFragment {

    @Inject
    protected GoodsDao dao;

    private GoodsCategory goodsCategory;
    private CheckGoodsAdapter adapter;
    private FragmentCheckGoodsListBinding binding;

    Animation flipAnim = AnimationUtils.loadAnimation(getContext(), R.anim.flip_anim);

    public static CheckGoodsFragment create(@NonNull GoodsCategory goodsCategory) {
        CheckGoodsFragment fragment = new CheckGoodsFragment();
        Bundle args = new Bundle();
        args.putParcelable(GoodsCategory.class.getSimpleName(), Parcels.wrap(goodsCategory));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsCategory = Parcels.unwrap(getArguments().getParcelable(GoodsCategory.class.getSimpleName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckGoodsListBinding.inflate(inflater, container, false);
        // TODO ここfalseにするか後で検討
        setHasOptionsMenu(false);
        adapter = new CheckGoodsAdapter(getContext());

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.addAll(dao.selectByCategory(goodsCategory.getId()));

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDetach();
        // TODO チェック内容を保存する
    }

    private class CheckGoodsAdapter extends ArrayRecyclerAdapter<Goods, BindingHolder<ItemCheckGoodsBinding>> {

        public CheckGoodsAdapter(@NonNull Context context) {
            super(context);
        }

        public BindingHolder<ItemCheckGoodsBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_check_goods);
        }

        @Override
        public void onBindViewHolder(BindingHolder<ItemCheckGoodsBinding> holder, int position) {
            Goods goods = getItem(position);
            ItemCheckGoodsBinding binding = holder.binding;
            binding.setGoods(goods);

            binding.iconCheck.setOnClickListener(v -> {
                // チェックアイコンを反転させる
                if(goods.getChecked() == Goods.CHECKED) {
                    goods.setChecked(Goods.UN_CHECKED);
                    DataBindingAttributeUtil.changeIconImage(binding.iconCheck, Goods.UN_CHECKED);
                } else {
                    goods.setChecked(Goods.CHECKED);
                    DataBindingAttributeUtil.changeIconImage(binding.iconCheck, Goods.CHECKED);
                }
            });
        }
    }

}
