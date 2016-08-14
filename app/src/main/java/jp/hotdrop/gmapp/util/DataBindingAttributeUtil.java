package jp.hotdrop.gmapp.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

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

    @BindingAdapter("checkIconUrl")
    public static void changeIconImage(ImageView iv, int checked) {
        switch(checked) {
            case Goods.CHECKED:
                //iv.setImageResource(R.drawable.ic_check_circle);
                //iv.setColorFilter(R.color.img_circle_border);
                break;
            case Goods.UN_CHECKED:
                //iv.setImageResource(R.drawable.ic_cancel);
                break;
        }
    }
}
