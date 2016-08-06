package jp.hotdrop.gmapp.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.model.Goods;

public class DataBindingAttributeUtil {

    @BindingAdapter("amountImageUrl")
    public static void setAmountImage(ImageView iv, int amount) {

        switch(amount) {
            case Goods.AMOUNT_FULL:
                iv.setImageResource(R.drawable.amount_full);
                break;
            case Goods.AMOUNT_MANY:
                iv.setImageResource(R.drawable.amount_many);
                break;
            case Goods.AMOUNT_HALF:
                iv.setImageResource(R.drawable.amount_half);
                break;
            case Goods.AMOUNT_A_LITTLE:
                iv.setImageResource(R.drawable.amount_little);
                break;
            default:
                iv.setImageResource(R.drawable.amount_empty);
                break;
        }
    }

    @BindingAdapter("checkIconUrl")
    public static void changeIconImage(ImageView iv, int checked) {
        switch(checked) {
            case Goods.CHECKED:
                iv.setImageResource(R.drawable.ic_check_circle);
                break;
            case Goods.UN_CHECKED:
                iv.setImageResource(R.drawable.ic_cancel);
                break;
        }
    }
}
