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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.parceler.Parcels;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsUpdateBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.util.ArrayUtil;
import jp.hotdrop.gmapp.util.DataBindingAttributeUtil;

public class GoodsUpdateFragment extends BaseFragment {

    @Inject
    protected GoodsDao goodsDao;
    @Inject
    protected GoodsCategoryDao categoryDao;

    private Goods goods;
    private String originGoodsName;
    private FragmentGoodsUpdateBinding binding;
    private AlertDialog.Builder deleteConfirmDlg;

    public static GoodsUpdateFragment create(@NonNull Goods goods) {
        GoodsUpdateFragment fragment = new GoodsUpdateFragment();
        Bundle args = new Bundle();
        args.putParcelable(Goods.class.getSimpleName(), Parcels.wrap(goods));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goods = Parcels.unwrap(getArguments().getParcelable(Goods.class.getSimpleName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoodsUpdateBinding.inflate(inflater, container, false);
        setHasOptionsMenu(false);
        binding.setGoods(goods);

        setCategorySpinner();
        setStockNumSpinner();
        setDeleteConfirmDlg();

        binding.icAmountIncrease.setOnClickListener(v -> onClickAmountIncrease());
        binding.icAmountReduce.setOnClickListener(v -> onClickAmountDecrease());

        binding.updateButton.setOnClickListener(v -> onClickUpdate());
        binding.deleteButton.setOnClickListener(v -> deleteConfirmDlg.show());
        originGoodsName = goods.getName();

        if(goods.getAmount() == goods.AMOUNT_EMPTY) {
            setViewAmountEmpty();
        }

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    /**
     * カテゴリーのドロップダウンリストを作成する
     */
    private void setCategorySpinner() {
        String[] strList = ArrayUtil.toArrayStr(categoryDao.selectForSpinner());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setSelection(adapter.getPosition(goods.getCategoryName()));
    }

    /**
     * 在庫数のドロップダウンリストを作成する
     */
    private void setStockNumSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, goods.STOCK_NUM_LIST);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerStock.setAdapter(adapter);
        binding.spinnerStock.setSelection(adapter.getPosition(String.valueOf(goods.getStockNum())));
    }

    /**
     * 削除ボタン押下時の確認ダイアログを生成する
     */
    private void setDeleteConfirmDlg() {
        deleteConfirmDlg = new AlertDialog.Builder(getContext());
        deleteConfirmDlg.setTitle("削除の確認");
        deleteConfirmDlg.setMessage("この商品を削除しますが本当によろしいですか？");
        deleteConfirmDlg.setPositiveButton("OK", (dialog, which) -> { doDelete(); });
        deleteConfirmDlg.setNegativeButton("キャンセル", (dialog, which) -> {/* キャンセル時は何もしない */});
    }

    private void setViewAmountEmpty() {
        binding.icAmountIncrease.setVisibility(View.GONE);
        binding.icAmountReduce.setVisibility(View.GONE);
        binding.amountEmptyAttention.setVisibility(View.VISIBLE);
        if(goods.getStockNum() >= 1) {
            binding.amountEmptyAttention.setText(R.string.label_amount_empty_attention);
            binding.replenishmentButton.setVisibility(View.VISIBLE);
            binding.replenishmentButton.setOnClickListener(v -> onClickReplenishment());
        } else {
            binding.amountEmptyAttention.setText(R.string.label_amount_and_stock_empty_attention);
        }
    }

    private void onClickAmountIncrease() {
        if(goods.getAmount() == goods.AMOUNT_FULL) {
           return;
        }
        int amount = goods.getAmount() + goods.AMOUNT_INCREASE_DECREASE_UNIT;
        goods.setAmount(amount);
        DataBindingAttributeUtil.changeAmountImage(binding.imgAmount, amount);
    }

    private void onClickAmountDecrease() {
        if(goods.getAmount() == goods.AMOUNT_EMPTY) {
            return;
        }
        int amount = goods.getAmount() - goods.AMOUNT_INCREASE_DECREASE_UNIT;
        goods.setAmount(amount);
        DataBindingAttributeUtil.changeAmountImage(binding.imgAmount, amount);
    }

    private void onClickReplenishment() {

        // このボタンの表示条件は「stockNumが１以上」であるため−1しても問題ない
        goods.setStockNum(goods.getStockNum() - 1);
        goods.setAmount(goods.AMOUNT_FULL);

        goodsDao.beginTran();
        goodsDao.replenishmentAmount(goods);
        goodsDao.commit();

        goods = goodsDao.select(goods.getId());

        setResult(REFRESH_ONE);
        exit();
    }

    /**
     * 更新ボタン押下
     */
    private void onClickUpdate() {

        if(!canUpdate()) {
            return;
        }

        int refreshMode = REFRESH_ONE;
        if(changedCategory()) {
            // spinnerはバインドできないため手動で値を設定する
            setCategoryToGoods();
            refreshMode = REFRESH_ALL;
        }

        // spinnerの手動設定
        setStockNumToGoods();

        goodsDao.beginTran();
        goodsDao.update(goods);
        goodsDao.commit();

        setResult(refreshMode);
        exit();
    }

    /**
     * 更新前の入力チェック
     * @return
     */
    private boolean canUpdate() {

        if(goods.getName().trim().equals("")) {
            Toast.makeText(this.getActivity(), "商品名を入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!originGoodsName.equals(goods.getName()) && goodsDao.existGoodsName(goods.getName())) {
            Toast.makeText(this.getActivity(), "同じ商品名が登録されています。", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean changedCategory() {
        String selectedCategoryName = String.valueOf(binding.spinnerCategory.getSelectedItem());
        if(!selectedCategoryName.equals(goods.getCategoryName())) {
            return true;
        }
        return false;
    }

    private void setCategoryToGoods() {
        String selectedCategoryName = String.valueOf(binding.spinnerCategory.getSelectedItem());
        goods.setCategoryId(categoryDao.getCategoryId(selectedCategoryName));
        goods.setCategoryName(selectedCategoryName);
    }

    private void setStockNumToGoods() {
        goods.setStockNum(Integer.valueOf(binding.spinnerStock.getSelectedItem().toString()));
    }

    /**
     * 商品情報を削除する
     */
    private void doDelete() {

        if(goodsDao.getCount() == 1) {
            Toast.makeText(this.getActivity(), "商品が１つしか登録されていないため削除できません。", Toast.LENGTH_SHORT).show();
            return;
        }

        goodsDao.beginTran();
        goodsDao.delete(goods.getId());
        goodsDao.commit();

        // 削除の場合は全リフレッシュ
        // 全リフレッシュ以外を試行錯誤したが、TabFragmentで持つリストとGoodsFragmentで持つリスト
        // 等々の整合性を合わせるのが厳しかったので一旦リフレッシュとした。
        setResult(REFRESH_ALL);
        exit();
    }

    /**
     * 更新は修正した商品情報によって元のアクティビティのタブ更新指示を変更する
     */
    private void setResult(int refreshMode) {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, refreshMode);
        intent.putExtra(Goods.class.getSimpleName(), Parcels.wrap(goods));
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
