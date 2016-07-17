package jp.hotdrop.gmapp.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.fragment.GoodsRegisterFragment;

public class GoodsRegisterActivity extends BaseActivity {

    public static final String ARG_TAB_NAME = "ARG_TAB";

    public static void startForResult(@NonNull Fragment fragment, @NonNull String tabName, int requestCode) {
        Intent intent = createIntent(fragment.getContext(), tabName);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String tabName) {
        Intent intent = new Intent(context, GoodsRegisterActivity.class);
        intent.putExtra(ARG_TAB_NAME, tabName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_goods_register);
        getComponent().inject(this);

        String tabName = getIntent().getStringExtra(ARG_TAB_NAME);
        replaceFragment(GoodsRegisterFragment.create(tabName));
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
