package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsRegisterBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.model.GoodsCategory;
import jp.hotdrop.gmapp.util.ArrayUtil;
import jp.hotdrop.gmapp.util.DateUtil;

public class GoodsRegisterFragment extends BaseFragment {

    @Inject
    protected GoodsDao goodsDao;
    @Inject
    protected GoodsCategoryDao categoryDao;

    private Goods goods;
    private FragmentGoodsRegisterBinding binding;
    private HashMap<String, Integer> categoryMap = new HashMap<>();

    /**
     * フラグメント生成
     * @return
     */
    public static GoodsRegisterFragment create() {
        GoodsRegisterFragment fragment = new GoodsRegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void onClickRegister(View v) {

        if(goods.getName().trim().equals("")) {
            Toast.makeText(this.getActivity(), "商品名を入力してください。", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO 同名は禁止

        String selectedCategoryName = (String)binding.spinnerCategory.getSelectedItem();

        goods.setCategoryId(categoryMap.get(selectedCategoryName));
        goods.setCategoryName(selectedCategoryName);
        goods.setLastUpdateAmountDate(DateUtil.longToDate(System.currentTimeMillis()));
        goods.setLastStockDate(DateUtil.longToDate(System.currentTimeMillis()));

        goodsDao.beginTran();
        goodsDao.insert(goods);
        goodsDao.commit();
        setResult();
        exit();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, REFRESH_ALL);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    private void exit() {
        if(isResumed()) {
            getActivity().onBackPressed();
        }
    }
}
