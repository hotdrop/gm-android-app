package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import jp.hotdrop.gmapp.model.GoodsCategory;

public class GoodsCategoryDao extends AbstractDao {

    private static final String SQL_SELECT_FROM =
            " SELECT " +
            "        id, " +
            "        name, " +
            "        view_order " +
            " FROM m_goods_category ";

    private boolean isListUpdate = false;
    private static List<GoodsCategory> goodsCategoryList;

    public GoodsCategoryDao(Context context) {
        super(context);
        if(goodsCategoryList == null) {
            goodsCategoryList = selectAll();
        }
    }

    public List<GoodsCategory> selectAll() {

        readableDatabase();

        String sql = SQL_SELECT_FROM + " ORDER BY view_order";

        List<GoodsCategory> categoryList = new ArrayList<>();
        Cursor cursor = execSelect(sql, null);
        while (cursor.moveToNext()) {
            GoodsCategory category = createCategory(cursor);
            categoryList.add(category);
        }

        return categoryList;
    }

    public String[] getStrList() {
        if(isListUpdate || goodsCategoryList == null) {
            goodsCategoryList = selectAll();
        }
        // TODO これダメ
        String[] strList = new String[goodsCategoryList.size()];
        goodsCategoryList.toArray(strList);
        return strList;
    }

    /**
     * カーソルから各値を取得し商品情報を生成する。
     * @param cursor
     * @return
     */
    private GoodsCategory createCategory(Cursor cursor) {
        GoodsCategory category = new GoodsCategory();
        category.setId(getCursorInt(cursor, "id"));
        category.setName(getCursorString(cursor, "name"));
        category.setViewOrder(getCursorInt(cursor, "view_order"));
        return category;
    }
}
