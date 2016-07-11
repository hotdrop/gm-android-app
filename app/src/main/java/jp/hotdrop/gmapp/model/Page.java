package jp.hotdrop.gmapp.model;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import jp.hotdrop.gmapp.R;
import jp.hotdrop.gmapp.fragment.CategoryFragment;
import jp.hotdrop.gmapp.fragment.GoodsFragment;

/**
 * ナビゲーションメニューの各ページを表す。
 */
public enum Page {

    GOODS_LIST(R.id.nav_goods_list, R.string.nav_goods_list, false, GoodsFragment.class.getSimpleName()) {
        @Override
        public Fragment createFragment() {
            return GoodsFragment.newInstance();
        }
    },
    CATEGORY_LIST(R.id.nav_category_list, R.string.nav_category_list, false, CategoryFragment.class.getSimpleName()) {
        @Override
        public Fragment createFragment() {
            return CategoryFragment.newInstance();
        }
    };

    private final int menuId;
    private final int titleResId;
    private final boolean toggleToolbar;
    private final String pageName;

    Page(int menuId, int titleResId, boolean toggleToolbar, String pageName) {
        this.menuId = menuId;
        this.titleResId = titleResId;
        this.toggleToolbar = toggleToolbar;
        this.pageName = pageName;
    }

    public static Page forMenuId(MenuItem item) {
        int id = item.getItemId();
        return forMenuId(id);
    }

    public static Page forMenuId(int id) {
        for (Page page : values()) {
            if (page.menuId == id) {
                return page;
            }
        }
        throw new AssertionError("no menu enum found for the id. you forgot to implement?");
    }

    public static Page forName(Fragment fragment) {
        String name = fragment.getClass().getSimpleName();
        for (Page page : values()) {
            if (page.pageName.equals(name)) {
                return page;
            }
        }
        throw new AssertionError("no menu enum found for the id. you forgot to implement?");
    }

    public int getMenuId() {
        return menuId;
    }

    public boolean shouldToggleToolbar() {
        return toggleToolbar;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public String getPageName() {
        return pageName;
    }

    public abstract Fragment createFragment();
}
