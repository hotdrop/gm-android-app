package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import javax.inject.Inject;

import jp.hotdrop.gmapp.activity.GoodsRegisterActivity;
import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsRegisterBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.util.ArrayUtil;
import jp.hotdrop.gmapp.util.DateUtil;

public class GoodsRegisterFragment extends BaseFragment {

    @Inject
    protected GoodsDao goodsDao;
    @Inject
    protected GoodsCategoryDao categoryDao;

    private Goods goods;
    private String selectedTabName;
    private FragmentGoodsRegisterBinding binding;

    /**
     * フラグメント生成
     * @return
     */
    public static GoodsRegisterFragment create(@NonNull String tabName) {
        GoodsRegisterFragment fragment = new GoodsRegisterFragment();
        Bundle args = new Bundle();
        args.putString(GoodsRegisterActivity.ARG_TAB_NAME, tabName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTabName = getArguments().getString(GoodsRegisterActivity.ARG_TAB_NAME);
        goods = new Goods();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoodsRegisterBinding.inflate(inflater, container, false);
        setHasOptionsMenu(false);

        setCategorySpinner();
        binding.registerButton.setOnClickListener((View v) -> onClickRegister(v));
        binding.setGoods(goods);

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
        String[] strList = ArrayUtil.toArrayStr(categoryDao.selectAll());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setSelection(adapter.getPosition(selectedTabName));
    }

    /**
     * 登録ボタン押下
     * @param v
     */
    private void onClickRegister(View v) {

        if(!canRegister()) {
            return;
        }

        String selectedCategoryName = (String)binding.spinnerCategory.getSelectedItem();
        goods.setCategoryId(categoryDao.getCategoryId(selectedCategoryName));
        goods.setCategoryName(selectedCategoryName);
        goods.setLastUpdateAmountDate(DateUtil.longToDate(System.currentTimeMillis()));
        goods.setLastStockDate(DateUtil.longToDate(System.currentTimeMillis()));

        goodsDao.beginTran();
        goodsDao.insert(goods);
        goodsDao.commit();

        setResult();
        exit();
    }

    /**
     * 登録前の入力チェック
     * @return
     */
    private boolean canRegister() {

        if(goods.getName().trim().equals("")) {
            Toast.makeText(this.getActivity(), "商品名を入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(goodsDao.existGoodsName(goods.getName())) {
            Toast.makeText(this.getActivity(), "同じ商品名が登録されています。", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 登録は商品一覧の全タブ更新指示をアクティビティに通知する
     */
    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, REFRESH_ALL);
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
