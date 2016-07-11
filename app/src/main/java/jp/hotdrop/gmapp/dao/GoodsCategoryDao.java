package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import jp.hotdrop.gmapp.model.GoodsCategory;

@Singleton
public class GoodsCategoryDao extends AbstractDao {

    private static final String SQL_SELECT =
            " SELECT id, name, view_order FROM m_goods_category ORDER BY view_order";

    /** カテゴリー情報はあまり更新しないためstaticに保持する。 */
    private static ArrayList<GoodsCategory> list;

    @Inject
    public GoodsCategoryDao(Context context) {
        super(context);
    }

    public List<GoodsCategory> selectAll() {
        if(list == null) {
            saveCategoryList();
        }
        return list;
    }

    private void saveCategoryList() {
        list = new ArrayList<>();
        Cursor cursor = execSelect(SQL_SELECT, null);
        while (cursor.moveToNext()) {
            list.add(createCategory(cursor));
        }
    }

    private GoodsCategory createCategory(Cursor cursor) {
        GoodsCategory category = new GoodsCategory();
        category.setId(getCursorInt(cursor, "id"));
        category.setName(getCursorString(cursor, "name"));
        category.setViewOrder(getCursorInt(cursor, "view_order"));
        return category;
    }

}
