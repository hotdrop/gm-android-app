package jp.hotdrop.gmapp.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.model.Goods;

public class DataBindingAttributeUtil {

    @BindingAdapter({"amountImageUrl", "amountImageSize"})
    public static void setAmountImage(ImageView iv, int amount, float sizeDimen) {

        //int resid = R.drawable.amount_full;

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

       // final int size = Math.round(sizeDimen);
       // iv.setBackground(ContextCompat.getDrawable(iv.getContext(), R.drawable.circle_border));
       // Picasso.with(iv.getContext()).load(resid).resize(size,size).centerInside().into(iv);
    }
}
