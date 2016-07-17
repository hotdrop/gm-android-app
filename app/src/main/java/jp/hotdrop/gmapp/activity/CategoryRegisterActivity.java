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
import jp.hotdrop.gmapp.fragment.CategoryRegisterFragment;

public class CategoryRegisterActivity extends BaseActivity {

    public static void startForResult(@NonNull Fragment fragment, int requestCode) {
        Intent intent = createIntent(fragment.getContext());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static Intent createIntent(@NonNull Context context) {
        Intent intent = new Intent(context, CategoryRegisterActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_category_register);
        getComponent().inject(this);

        replaceFragment(CategoryRegisterFragment.create());
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
