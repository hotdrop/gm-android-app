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
import jp.hotdrop.gmapp.fragment.CheckGoodsFragment;
import jp.hotdrop.gmapp.model.GoodsCategory;

public class CheckGoodsActivity extends BaseActivity {

    public static void startForResult(@NonNull Fragment fragment, @NonNull GoodsCategory goodsCategory, int requestCode) {
        Intent intent = createIntent(fragment.getContext(), goodsCategory);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull GoodsCategory goodsCategory) {
        Intent intent = new Intent(context, CheckGoodsActivity.class);
        intent.putExtra(GoodsCategory.class.getSimpleName(), Parcels.wrap(goodsCategory));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_check_goods);
        getComponent().inject(this);

        GoodsCategory goodsCategory = Parcels.unwrap(getIntent().getParcelableExtra(GoodsCategory.class.getSimpleName()));
        replaceFragment(CheckGoodsFragment.create(goodsCategory));
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
