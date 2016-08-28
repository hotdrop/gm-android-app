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

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.activity.ActivityNavigator;
import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.databinding.FragmentCheckCategoryListBinding;
import jp.hotdrop.gmapp.databinding.ItemCheckCategoryBinding;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;

public class CheckCategoryFragment extends BaseFragment {

    @Inject
    protected GoodsCategoryDao dao;
    @Inject
    ActivityNavigator activityNavigator;

    private CheckCategoryAdapter adapter;
    private FragmentCheckCategoryListBinding binding;

    public static CheckCategoryFragment newInstance() {
        return new CheckCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckCategoryListBinding.inflate(inflater, container, false);

        adapter = new CheckCategoryAdapter(getContext());
        adapter.addAll(dao.selectExceptUnRegisteredGoods());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // TODO 確定ボタンとその動作追加

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_CHECK_GOODS) {
            return;
        }

        GoodsCategory goodsCategory = Parcels.unwrap(data.getParcelableExtra(GoodsCategory.class.getSimpleName()));
        adapter.refresh(goodsCategory);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    private class CheckCategoryAdapter extends ArrayRecyclerAdapter<GoodsCategory, BindingHolder<ItemCheckCategoryBinding>> {

        public CheckCategoryAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public BindingHolder<ItemCheckCategoryBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_check_category);
        }

        private void refresh(GoodsCategory goodsCategory) {
            for(int i = 0; i < adapter.getItemCount(); i++) {
                GoodsCategory g = getItem(i);
                if(goodsCategory.equals(g)) {
                    g.change(goodsCategory);
                    adapter.notifyItemChanged(i);
                }
            }
        }

        public void onBindViewHolder(BindingHolder<ItemCheckCategoryBinding> holder, int position) {
            GoodsCategory category = getItem(position);
            ItemCheckCategoryBinding binding = holder.binding;
            binding.setCategory(category);
            binding.cardView.setOnClickListener(v ->
                    activityNavigator.showCheckGoods(CheckCategoryFragment.this, category, REQ_CODE_CHECK_GOODS));
        }
    }
}
