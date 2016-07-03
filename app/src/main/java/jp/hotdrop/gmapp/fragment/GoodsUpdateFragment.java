package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;

import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsUpdateBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.model.GoodsCategory;

public class GoodsUpdateFragment extends BaseFragment {

    private Goods goods;
    private FragmentGoodsUpdateBinding binding;
    private HashMap<String, Integer> categoryMap = new HashMap<>();

    public static GoodsUpdateFragment create(Goods goods) {
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
        binding.updateButton.setOnClickListener((View v) -> onClickUpdate(v));

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    private void setCategorySpinner() {
        GoodsCategoryDao categoryDao = new GoodsCategoryDao(this.getActivity());
        List<GoodsCategory> categoryList = categoryDao.selectAll();
        // TODO MAPをいちいちここで作成するのなんとか・・。Utilityとかでstaticに持ちたい
        for(GoodsCategory goodsCategory : categoryList) {
            categoryMap.put(goodsCategory.getName(), goodsCategory.getId());
        }
        String[] strList = toArrayStr(categoryList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setSelection(adapter.getPosition(goods.getCategoryName()));
    }

    private String[] toArrayStr(List<GoodsCategory> categoryList) {
        String[] strList = new String[categoryList.size()];

        int idx = 0;
        for(GoodsCategory category : categoryList) {
            strList[idx] = category.getName();
            idx++;
        }
        return strList;
    }

    private void onClickUpdate(View v) {

        String selectedCategoryName = (String)binding.spinnerCategory.getSelectedItem();
        int refreshMode = REFRESH_ONE;

        if(!selectedCategoryName.equals(goods.getCategoryName())) {
            // カテゴリーを変更した場合は全リフレッシュモードにする
            goods.setCategoryId(categoryMap.get(selectedCategoryName));
            goods.setCategoryName(selectedCategoryName);
            refreshMode = REFRESH_ALL;
        }

        GoodsDao dao = new GoodsDao(this.getActivity());
        dao.beginTran();
        dao.update(goods);
        dao.commit();
        setResult(refreshMode);

        if(isResumed()) {
            getActivity().onBackPressed();
        }
    }

    private void setResult(int refreshMode) {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, refreshMode);
        intent.putExtra(Goods.class.getSimpleName(), Parcels.wrap(goods));
        getActivity().setResult(Activity.RESULT_OK, intent);
    }
}
