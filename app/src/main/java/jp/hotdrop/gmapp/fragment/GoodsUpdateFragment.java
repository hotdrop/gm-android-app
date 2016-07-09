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

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsUpdateBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.util.ArrayUtil;

public class GoodsUpdateFragment extends BaseFragment {

    @Inject
    protected GoodsDao goodsDao;
    @Inject
    protected GoodsCategoryDao categoryDao;

    private Goods goods;
    private String originGoodsName;
    private FragmentGoodsUpdateBinding binding;
    private HashMap<String, Integer> categoryMap = new HashMap<>();
    private AlertDialog.Builder deleteConfirmDlg;

    /**
     * フラグメント生成
     * @param goods
     * @return
     */
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
        setDeleteConfirmDlg();

        binding.updateButton.setOnClickListener((View v) -> onClickUpdate(v));
        binding.deleteButton.setOnClickListener((View v) -> deleteConfirmDlg.show());
        originGoodsName = goods.getName();

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

        List<GoodsCategory> categoryList = categoryDao.selectAll();
        // TODO MAPをいちいちここで作成するのなんとか・・。Utilityとかでstaticに持ちたい
        for(GoodsCategory goodsCategory : categoryList) {
            categoryMap.put(goodsCategory.getName(), goodsCategory.getId());
        }
        String[] strList = ArrayUtil.toArrayStr(categoryList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setSelection(adapter.getPosition(goods.getCategoryName()));
    }

    /**
     * 削除ボタン押下時の確認ダイアログを生成する
     */
    private void setDeleteConfirmDlg() {
        deleteConfirmDlg = new AlertDialog.Builder(getContext());
        deleteConfirmDlg.setTitle("削除の確認");
        deleteConfirmDlg.setMessage("この商品を削除しますが本当によろしいですか？");
        deleteConfirmDlg.setPositiveButton("OK", (dialog, which) -> { doDelete(); });
        deleteConfirmDlg.setNegativeButton("cancel", (dialog, which) -> {/* キャンセル時は何もしない */});
    }

    /**
     * 更新ボタン押下
     * @param v
     */
    private void onClickUpdate(View v) {

        if(!canUpdate()) {
            return;
        }

        int refreshMode = REFRESH_ONE;
        String selectedCategoryName = (String)binding.spinnerCategory.getSelectedItem();
        if(!selectedCategoryName.equals(goods.getCategoryName())) {
            // カテゴリーを変更した場合は全リフレッシュモードにする
            goods.setCategoryId(categoryMap.get(selectedCategoryName));
            goods.setCategoryName(selectedCategoryName);
            refreshMode = REFRESH_ALL;
        }

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

    /**
     * 商品情報を削除する。
     * 削除ボタンを押下し、確認ダイアログでOKを選択した場合に呼ばれる
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
        // いろいろ全リフレッシュ以外を試行錯誤したが、TabFragmentで持つリストとGoodsFragmentで持つリスト
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
