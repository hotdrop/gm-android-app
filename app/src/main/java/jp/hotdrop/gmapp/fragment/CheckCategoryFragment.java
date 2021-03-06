package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.parceler.Parcels;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.activity.ActivityNavigator;
import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentCheckCategoryListBinding;
import jp.hotdrop.gmapp.databinding.ItemCheckCategoryBinding;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.widget.ArrayRecyclerAdapter;
import jp.hotdrop.gmapp.widget.BindingHolder;

public class CheckCategoryFragment extends BaseFragment {

    @Inject
    protected GoodsCategoryDao dao;
    @Inject
    protected GoodsDao goodsDao;
    @Inject
    ActivityNavigator activityNavigator;

    private CheckCategoryAdapter adapter;
    private FragmentCheckCategoryListBinding binding;
    private AlertDialog.Builder confirmDialog;

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

        setConfirmDialog();

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.fabAddButton.setOnClickListener(v -> confirmDialog.show());

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

    private void setConfirmDialog() {
        confirmDialog = new AlertDialog.Builder(getContext());
        confirmDialog.setTitle("在庫の確定確認");
        confirmDialog.setMessage("商品の在庫チェックを確定します。よろしいですか？");
        confirmDialog.setPositiveButton("OK", (dialog, which) -> checkConfirm());
        confirmDialog.setNegativeButton("キャンセル", (dialog, which) -> {/* キャンセル時は何もしない */});
    }

    /**
     * 在庫チェックを実行する
     */
    private void checkConfirm() {
        goodsDao.beginTran();
        goodsDao.confirmChecked();
        goodsDao.commit();

        Toast.makeText(this.getActivity(), "在庫チェックを確定しました。", Toast.LENGTH_SHORT).show();
        exit();
    }

    private void exit() {
        if(isResumed()) {
            getActivity().onBackPressed();
        }
    }

    /**
     * アダプター
     */
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
