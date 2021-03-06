package jp.hotdrop.gmapp.util;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.model.Goods;

public class DataBindingAttributeUtil {

    @BindingAdapter({"amountImage", "amountImageSize"})
    public static void setAmountImage(ImageView imageView, int amount, float sizeInDimen) {

        switch(amount) {
            case Goods.AMOUNT_FULL:
                setImageWithCircle(imageView, R.drawable.amount_full, sizeInDimen);
                break;
            case Goods.AMOUNT_MANY:
                setImageWithCircle(imageView, R.drawable.amount_many, sizeInDimen);
                break;
            case Goods.AMOUNT_HALF:
                setImageWithCircle(imageView, R.drawable.amount_half, sizeInDimen);
                break;
            case Goods.AMOUNT_A_LITTLE:
                setImageWithCircle(imageView, R.drawable.amount_little, sizeInDimen);
                break;
            default:
                setImageWithCircle(imageView, R.drawable.amount_empty, sizeInDimen);
                break;
        }
    }

    private static void setImageWithCircle(ImageView imageView, int amountResId, float sizeInDimen) {
        final int size = Math.round(sizeInDimen);
        Picasso.with(imageView.getContext()).load(amountResId)
                .resize(size, size)
                .centerInside()
                .transform(new CircleTransform())
                .into(imageView);
    }

    @BindingAdapter("amountImageCanChange")
    public static void changeAmountImage(ImageView imageView, int amount) {

        switch(amount) {
            case Goods.AMOUNT_FULL:
                imageView.setImageResource(R.drawable.amount_full);
                break;
            case Goods.AMOUNT_MANY:
                imageView.setImageResource(R.drawable.amount_many);
                break;
            case Goods.AMOUNT_HALF:
                imageView.setImageResource(R.drawable.amount_half);
                break;
            case Goods.AMOUNT_A_LITTLE:
                imageView.setImageResource(R.drawable.amount_little);
                break;
            default:
                imageView.setImageResource(R.drawable.amount_empty);
                break;
        }
    }

    @BindingAdapter("stateImageByCategory")
    public static void setStateImageByCategory(ImageView imageView, boolean checked) {
        if(checked) {
            imageView.setImageResource(R.drawable.ic_check_circle);
            imageView.setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN);
        } else {
            imageView.setImageResource(R.drawable.ic_cancel);
            imageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
    }

    @BindingAdapter({"checkedCount", "goodsCount"})
    public static void setCheckedCountText(TextView textView, int checkedCnt, int totalCnt) {
        textView.setText(String.valueOf(checkedCnt) + "/" + String.valueOf(totalCnt));
        if(checkedCnt != totalCnt) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.BLACK);
        }
    }
}
