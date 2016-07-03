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

    private static GoodsCategoryDao dao;

    private GoodsCategoryDao(Context context) {
        super(context);
    }

    public static synchronized GoodsCategoryDao getInstance(Context context) {
        if(dao == null) {
            dao = new GoodsCategoryDao(context);
        }
        return dao;
    }

    public static GoodsCategoryDao getInstance() {
        if(dao == null) {
            throw new IllegalStateException("プログラムエラー。最上位のActivityで引数付きgetInstanceを実行してください。");
        }
        return dao;
    }


    public List<GoodsCategory> selectAll() {

        String sql = SQL_SELECT_FROM + " ORDER BY view_order";
        List<GoodsCategory> categoryList = new ArrayList<>();
        Cursor cursor = execSelect(sql, null);
        while (cursor.moveToNext()) {
            GoodsCategory category = createCategory(cursor);
            categoryList.add(category);
        }

        return categoryList;
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
