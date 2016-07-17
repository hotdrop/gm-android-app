package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.parceler.Parcels;

import javax.inject.Inject;

import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.databinding.FragmentCategoryRegisterBinding;
import jp.hotdrop.gmapp.model.GoodsCategory;

public class CategoryRegisterFragment extends BaseFragment {

    @Inject
    protected GoodsCategoryDao dao;

    private GoodsCategory goodsCategory;
    private FragmentCategoryRegisterBinding binding;

    public static CategoryRegisterFragment create() {
        CategoryRegisterFragment fragment = new CategoryRegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsCategory = new GoodsCategory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryRegisterBinding.inflate(inflater, container, false);
        setHasOptionsMenu(false);

        binding.registerButton.setOnClickListener(v -> onClickRegister(v));
        binding.setCategory(goodsCategory);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    private void onClickRegister(View v) {

        if(!canRegister()) {
            return;
        }

        dao.beginTran();
        dao.insert(goodsCategory);
        dao.commit();

        // insert時に生成されるidを再取得する
        goodsCategory = dao.select(goodsCategory.getName());

        setResult();
        exit();
    }

    private boolean canRegister() {

        if(goodsCategory.getName().trim().equals("")) {
            Toast.makeText(this.getActivity(), "カテゴリー名を入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(dao.existCategoryName(goodsCategory.getName())) {
            Toast.makeText(this.getActivity(), "同じカテゴリー名が既に登録されています。", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, REFRESH_INSERT);
        intent.putExtra(GoodsCategory.class.getSimpleName(), Parcels.wrap(goodsCategory));
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    private void exit() {
        if(isResumed()) {
            getActivity().onBackPressed();
        }
    }
}
