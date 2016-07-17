package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.activity.ActivityNavigator;
import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.databinding.FragmentCategoryListBinding;
import jp.hotdrop.gmapp.databinding.ItemCategoryBinding;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;
import rx.subscriptions.CompositeSubscription;

public class CategoryFragment extends BaseFragment {

    @Inject
    protected CompositeSubscription compositeSubscription;
    @Inject
    protected GoodsCategoryDao dao;
    @Inject
    protected ActivityNavigator activityNavigator;

    private CategoryAdapter adapter;
    private ItemTouchHelper helper;
    private FragmentCategoryListBinding binding;

    private OnChangeCategoryListener onChangeCategoryListener = categoryGoods -> {/* no operation */};

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
        adapter = new CategoryAdapter(getContext());

        helper = new ItemTouchHelper(new CategoryItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(binding.recyclerView);

        binding.recyclerView.addItemDecoration(helper);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.addAll(dao.selectAll());

        // TODO 追加ボタンのリスナー

        return binding.getRoot();
    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        helper.startDrag(viewHolder);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        int refreshMode = data.getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE);
        GoodsCategory goodsCategory = Parcels.unwrap(data.getParcelableExtra(GoodsCategory.class.getSimpleName()));
        if(requestCode == REQ_CODE_UPDATE && goodsCategory != null) {
            switch (refreshMode) {
                case REFRESH_INSERT:
                    break;
                case REFRESH_UPDATE:
                    adapter.refresh(goodsCategory);
                    onChangeCategoryListener.onChangeCategory(Collections.singletonList(goodsCategory));
                    break;
                case REFRESH_DELETE:
                    adapter.remove(goodsCategory);
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onDetach() {
        // TODO フラグメントが終了する際にview_orderを更新する
        Toast.makeText(this.getActivity(), "call onDetach", Toast.LENGTH_SHORT).show();
    }

    public interface OnChangeCategoryListener {
        void onChangeCategory(List<GoodsCategory> GoodsCategoryList);
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
            ItemCategoryBinding binding = holder.binding;
            binding.setCategory(category);
            binding.iconReorderCategory.setOnTouchListener((view, event) -> {
                if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder);
                }
                return false;
            });

            binding.cardView.setOnClickListener(v ->
                    activityNavigator.showCategoryUpdate(CategoryFragment.this, category, REQ_CODE_CATEGORY_UPDATE));
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

        private void remove(GoodsCategory goodsCategory) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                GoodsCategory g = getItem(i);
                if (goodsCategory.equals(g)) {
                    adapter.removeItem(i);
                    adapter.notifyItemRemoved(i);
                }
            }
        }
    }

    /**
     * アイテム選択時のCallbackクラス
     */
    private class CategoryItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final CategoryAdapter adapter;

        public CategoryItemTouchHelperCallback(CategoryAdapter adapter) {
            this.adapter = adapter;
        }

        /**
         * dragとswipeの動作指定
         * dragの上下のみ許容する。
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * drag時の動作
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        /**
         * swipe時の動作
         * 何もしない
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            return;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }
    }

}
