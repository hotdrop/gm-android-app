package jp.hotdrop.gmapp.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import org.parceler.Parcels;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.fragment.CheckGoodsUpdateFragment;
import jp.hotdrop.gmapp.model.Goods;

public class CheckGoodsUpdateActivity extends BaseActivity {

    public static void startForResult(@NonNull Fragment fragment, @NonNull Goods goods, int requestCode) {
        Intent intent = createIntent(fragment.getContext(), goods);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Goods goods) {
        Intent intent = new Intent(context, CheckGoodsUpdateActivity.class);
        intent.putExtra(Goods.class.getSimpleName(), Parcels.wrap(goods));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_check_goods_update);
        getComponent().inject(this);

        Goods goods = Parcels.unwrap(getIntent().getParcelableExtra(Goods.class.getSimpleName()));
        replaceFragment(CheckGoodsUpdateFragment.create(goods));
    }

    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_view, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
