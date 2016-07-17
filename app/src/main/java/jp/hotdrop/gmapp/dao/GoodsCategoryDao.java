package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import jp.hotdrop.gmapp.model.GoodsCategory;

@Singleton
public class GoodsCategoryDao extends AbstractDao {

    private static final String SQL_SELECT =
                    "SELECT " +
                    "    gc.id AS id, " +
                    "    gc.name AS name, " +
                    "    gc.view_order AS view_order," +
                    "    gc.register_date AS register_date, " +
                    "    gc.update_date AS update_date, " +
                    "    count(*) AS count" +
                    " FROM " +
                    "    m_goods_category gc " +
                    " LEFT JOIN " +
                    "    t_goods g ON gc.id = g.category_id" +
                    " GROUP BY g.category_id " +
                    " ORDER BY view_order";

    /** カテゴリー情報は基本更新しないためstaticに保持する。 */
    private static ArrayList<GoodsCategory> list;
    /** カテゴリー名とidのマップ情報。カテゴリーのスピナーが名称しか持てないのでMAPでIDを取得する */
    private static HashMap<String, Integer> map;

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

    public List<GoodsCategory> selectRefresh() {
        saveCategoryList();
        map = null;
        return list;
    }

    public void update(GoodsCategory goodsCategory) {

        String sql = "UPDATE m_goods_category SET" +
                " name = ?, update_date = ? " +
                " WHERE id = ? ";
        String[] bind = {goodsCategory.getName(),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(goodsCategory.getId())};

        execUpdate(sql, bind);
    }

    public void insert(GoodsCategory goodsCategory) {

        String sql = "INSERT INTO m_goods_category" +
                "  (name, view_order, register_date) " +
                " VALUES " +
                "  (?, (SELECT MAX(view_order)+1 FROM m_goods_category), ?)";

        String[] bind = {goodsCategory.getName(),
                String.valueOf(System.currentTimeMillis())};

        execInsert(sql, bind);
    }

    public void delete(int id) {
        String sql = "DELETE FROM m_goods_category WHERE id = ? ";
        String[] bind = {String.valueOf(id)};
        execDelete(sql, bind);
    }

    public int getCategoryId(String name) {

        if(map == null) {
            map = new HashMap<>();
            for(GoodsCategory goodsCategory : list) {
                map.put(goodsCategory.getName(), goodsCategory.getId());
            }
        }

        return map.get(name);
    }

    public boolean existCategoryName(String name) {

        String sql = "SELECT name FROM m_goods_category WHERE name = ?";
        String[] bind = {name};

        Cursor cursor = execSelect(sql, bind);
        if (cursor.moveToNext()) {
            return true;
        }

        return false;
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
        category.setRegisterDate(getCursorDate(cursor, "register_date"));
        category.setUpdateDate(getCursorDate(cursor, "update_date"));
        category.setGoodsCount(getCursorInt(cursor, "count"));
        return category;
    }

}
