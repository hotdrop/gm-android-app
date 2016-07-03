package jp.hotdrop.gmapp.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.fragment.GoodsRegisterFragment;

public class GoodsRegisterActivity extends BaseActivity {

    static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = createIntent(fragment.getContext());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, GoodsRegisterActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_goods_register);
        getComponent().inject(this);
        replaceFragment(GoodsRegisterFragment.create());
    }

    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_view, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
