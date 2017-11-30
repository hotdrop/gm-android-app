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

import org.parceler.Parcels;

import javax.inject.Inject;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.dao.GoodsDao;
import jp.hotdrop.gmapp.databinding.FragmentCheckGoodsUpdateBinding;
import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.util.DataBindingAttributeUtil;

public class CheckGoodsUpdateFragment extends BaseFragment {

    @Inject
    protected GoodsDao goodsDao;

    private Goods goods;
    private FragmentCheckGoodsUpdateBinding binding;

    public static CheckGoodsUpdateFragment create(@NonNull Goods goods) {
        CheckGoodsUpdateFragment fragment = new CheckGoodsUpdateFragment();
        Bundle args = new Bundle();
        args.putParcelable(Goods.class.getSimpleName(), Parcels.wrap(goods));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        goods = Parcels.unwrap(getArguments().getParcelable(Goods.class.getSimpleName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        binding = FragmentCheckGoodsUpdateBinding.inflate(inflater, container, false);
        setHasOptionsMenu(false);
        binding.setGoods(goods);

        setStockNumSpinner();

        binding.icAmountIncrease.setOnClickListener(v -> onClickAmountIncrease());
        binding.icAmountReduce.setOnClickListener(v -> onClickAmountDecrease());

        binding.updateButton.setOnClickListener(v -> onClickUpdate());

        if(goods.getAmount() == Goods.AMOUNT_EMPTY) {
            setViewAmountEmpty();
        }

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    private void setStockNumSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, Goods.STOCK_NUM_LIST);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerStock.setAdapter(adapter);
        binding.spinnerStock.setSelection(adapter.getPosition(String.valueOf(goods.getStockNum())));
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
        if(goods.getAmount() == Goods.AMOUNT_FULL) {
            return;
        }
        int amount = goods.getAmount() + Goods.AMOUNT_INCREASE_DECREASE_UNIT;
        goods.setAmount(amount);
        DataBindingAttributeUtil.changeAmountImage(binding.imgAmount, amount);
    }

    private void onClickAmountDecrease() {
        if(goods.getAmount() == Goods.AMOUNT_EMPTY) {
            return;
        }
        int amount = goods.getAmount() - Goods.AMOUNT_INCREASE_DECREASE_UNIT;
        goods.setAmount(amount);
        DataBindingAttributeUtil.changeAmountImage(binding.imgAmount, amount);
    }

    private void onClickUpdate() {

        goodsDao.beginTran();
        goodsDao.updateForStockCheck(goods);
        goodsDao.commit();

        setResult();
        exit();
    }

    private void onClickReplenishment() {

        // このボタンの表示条件は「stockNumが１以上」であるため−1しても問題ない
        goods.setStockNum(goods.getStockNum() - 1);
        goods.setAmount(Goods.AMOUNT_FULL);

        goodsDao.beginTran();
        goodsDao.replenishmentAmount(goods);
        goodsDao.commit();

        goods = goodsDao.select(goods.getId());

        setResult();
        exit();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(ARG_REFRESH_MODE, REFRESH_ONE);
        intent.putExtra(Goods.class.getSimpleName(), Parcels.wrap(goods));
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    private void exit() {
        if(isResumed()) {
            getActivity().onBackPressed();
        }
    }
}
