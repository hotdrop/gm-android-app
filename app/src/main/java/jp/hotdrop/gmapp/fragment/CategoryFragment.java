package jp.hotdrop.gmapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.databinding.FragmentCategoryListBinding;
import jp.hotdrop.gmapp.databinding.ItemCategoryBinding;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;

public class CategoryFragment extends BaseFragment {

    private CategoryAdapter adapter;
    private FragmentCategoryListBinding binding;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false);
        // adapter
        return binding.getRoot();
    }

    /**
     * アダプタークラス
     */
    protected class CategoryAdapter extends ArrayRecyclerAdapter<GoodsCategory, BindingHolder<ItemCategoryBinding>> {

        public CategoryAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public BindingHolder<ItemCategoryBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_category);
        }

        @Override
        public void onBindViewHolder(BindingHolder<ItemCategoryBinding> holder, int position) {

            GoodsCategory category = getItem(position);
            ItemCategoryBinding itemBinding = holder.binding;
            itemBinding.setCategory(category);
            
            itemBinding.getRoot().setOnClickListener(v -> {/* 編集別画面へ */});
        }
    }

}
