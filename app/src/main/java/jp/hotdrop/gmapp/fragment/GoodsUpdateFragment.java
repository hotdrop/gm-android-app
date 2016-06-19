package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.parceler.Parcels;

import jp.hotdrop.gmapp.dao.GoodsCategoryDao;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentGoodsUpdateBinding;
import jp.hotdrop.gmapp.model.Goods;

public class GoodsUpdateFragment extends BaseFragment {

    private GoodsDao dao;
    private Goods goods;
    private FragmentGoodsUpdateBinding binding;

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

        // TODO Spinner等のリスナーイベントはここで設定する
        GoodsCategoryDao dao = new GoodsCategoryDao(this.getActivity());
        String[] categoryList = dao.getStrList();
        System.out.println(categoryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_dropdown_item_1line, categoryList);
        MaterialBetterSpinner categorySpinner = binding.spinnerCategory;
        categorySpinner.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }


    private void setResult() {
        // 更新ボタンや数量変更時に呼ぶ予定。DBアクセスに行く
        Intent intent = new Intent();
        intent.putExtra(Goods.class.getSimpleName(), Parcels.wrap(goods));
        getActivity().setResult(Activity.RESULT_OK, intent);
    }
}
