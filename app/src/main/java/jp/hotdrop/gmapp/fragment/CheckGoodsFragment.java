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

import org.parceler.Parcels;

import java.util.Iterator;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.activity.ActivityNavigator;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentCheckGoodsListBinding;
import jp.hotdrop.gmapp.databinding.ItemCheckGoodsBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;

public class CheckGoodsFragment extends BaseFragment {

    @Inject
    protected GoodsDao dao;
    @Inject
    protected ActivityNavigator activityNavigator;

    private GoodsCategory goodsCategory;
    private CheckGoodsAdapter adapter;
    private FragmentCheckGoodsListBinding binding;
    private int checkCount = 0;

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
        setHasOptionsMenu(false);

        adapter = new CheckGoodsAdapter(getContext());
        adapter.addAll(dao.selectByCategory(goodsCategory.getId()));

        checkCount = goodsCategory.getCheckedGoodsCount();

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        dao.beginTran();
        Iterator<Goods> ite = adapter.iterator();
        while(ite.hasNext()) {
            Goods goods = ite.next();
            dao.updateChecked(goods.getId(), goods.getChecked());
        }
        dao.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        int refreshMode = data.getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE);
        Goods goods = Parcels.unwrap(data.getParcelableExtra(Goods.class.getSimpleName()));

        if(requestCode == REQ_CODE_CHECK_GOODS_UPDATE && goods != null) {
            switch (refreshMode) {
                case REFRESH_ONE:
                    adapter.refresh(goods);
                    break;
                default:
                    break;
            }
        }
    }

    private void setResult() {
        Intent intent = new Intent();
        goodsCategory.setCheckedGoodsCount(checkCount);
        intent.putExtra(GoodsCategory.class.getSimpleName(), Parcels.wrap(goodsCategory));
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    /**
     * アダプター
     */
    private class CheckGoodsAdapter extends ArrayRecyclerAdapter<Goods, BindingHolder<ItemCheckGoodsBinding>> {

        public CheckGoodsAdapter(@NonNull Context context) {
            super(context);
        }

        public BindingHolder<ItemCheckGoodsBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_check_goods);
        }

        private void refresh(Goods goods) {
            for(int i = 0; i < adapter.getItemCount(); i++) {
                Goods g = getItem(i);
                if(g.equals(goods)) {
                    g.change(goods);
                    adapter.notifyItemChanged(i);
                }
            }
        }

        @Override
        public void onBindViewHolder(BindingHolder<ItemCheckGoodsBinding> holder, int position) {
            Goods goods = getItem(position);
            ItemCheckGoodsBinding binding = holder.binding;
            binding.setGoods(goods);

            if(goods.getChecked() == Goods.CHECKED) {
                binding.iconCheck.flipSilently(true);
            }

            binding.iconCheck.setOnFlippingListener((v, checked) -> {
                if(checked) {
                    goods.setChecked(Goods.CHECKED);
                    checkCount++;
                } else {
                    goods.setChecked(Goods.UN_CHECKED);
                    checkCount--;
                }
                setResult();
            });

            binding.cardView.setOnClickListener(v ->
                    activityNavigator.showCheckGoodsUpdate(CheckGoodsFragment.this, goods, REQ_CODE_CHECK_GOODS_UPDATE));
        }
    }
}
