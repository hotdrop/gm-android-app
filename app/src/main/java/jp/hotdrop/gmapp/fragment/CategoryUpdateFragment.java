package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.parceler.Parcels;

import javax.inject.Inject;

import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.databinding.FragmentCategoryUpdateBinding;
import jp.hotdrop.gmapp.model.GoodsCategory;

public class CategoryUpdateFragment extends BaseFragment {

    @Inject
    protected GoodsCategoryDao dao;

    private GoodsCategory goodsCategory;
    private String originCategoryName;
    private FragmentCategoryUpdateBinding binding;

    private AlertDialog.Builder deleteConfirmDlg;

    public static CategoryUpdateFragment create(@NonNull GoodsCategory goodsCategory){
        CategoryUpdateFragment fragment = new CategoryUpdateFragment();
        Bundle args = new Bundle();
        args.putParcelable(GoodsCategory.class.getSimpleName(), Parcels.wrap(goodsCategory));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsCategory = Parcels.unwrap(getArguments().getParcelable(GoodsCategory.class.getName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryUpdateBinding.inflate(inflater, container, false);
        setHasOptionsMenu(false);
        binding.setCategory(goodsCategory);

        setDeleteConfirmDlg();

        binding.updateButton.setOnClickListener(v -> onClickUpdate(v));
        binding.deleteButton.setOnClickListener(v -> deleteConfirmDlg.show());
        originCategoryName = goodsCategory.getName();

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    /**
     * 削除ボタン押下時の確認ダイアログを生成する
     */
    private void setDeleteConfirmDlg() {
        deleteConfirmDlg = new AlertDialog.Builder(getContext());
        deleteConfirmDlg.setTitle("削除の確認");
        deleteConfirmDlg.setMessage("このカテゴリーを削除しますが本当によろしいですか？");
        deleteConfirmDlg.setPositiveButton("OK", (dialog, which) -> { doDelete(); });
        deleteConfirmDlg.setNegativeButton("キャンセル", (dialog, which) -> {/* キャンセル時は何もしない */});
    }

    private void onClickUpdate(View v) {

        if(!canUpdate()) {
            return;
        }

        dao.beginTran();
        dao.update(goodsCategory);
        dao.commit();
        setResult(REFRESH_UPDATE);
        exit();
    }

    private boolean canUpdate() {

        if(goodsCategory.getName().trim().equals("")) {
            Toast.makeText(this.getActivity(), "カテゴリー名を入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!goodsCategory.getName().equals(originCategoryName) && dao.existCategoryName(goodsCategory.getName())) {
            Toast.makeText(this.getActivity(), "同じカテゴリー名が既に登録されています。", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void doDelete() {
        dao.beginTran();
        dao.delete(goodsCategory.getId());
        dao.commit();
        setResult(REFRESH_DELETE);
        exit();
    }

    private void setResult(int refreshMode) {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, refreshMode);
        intent.putExtra(GoodsCategory.class.getSimpleName(), Parcels.wrap(goodsCategory));
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    /**
     * フラグメントを抜ける
     */
    private void exit() {
        if(isResumed()) {
            getActivity().onBackPressed();
        }
    }

}
