package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
                    "    count(g.category_id) AS count" +
                    " FROM " +
                    "    m_goods_category gc " +
                    " LEFT JOIN " +
                    "    t_goods g ON gc.id = g.category_id";
    private static final String SQL_GROUP_BY = " GROUP BY gc.id, g.category_id ";
    private static final String SQL_ORDER_BY = " ORDER BY view_order";

    /** カテゴリーリストをスピナーで表示する際、いちいちselectするとコストがかかるのでフィールドに保持する */
    private static ArrayList<GoodsCategory> list;
    /** カテゴリー名とidのマップ情報。カテゴリーのスピナーが名称しか持てないのでこのMAPを使う */
    private static HashMap<String, Integer> mapUsedSpinner;

    @Inject
    public GoodsCategoryDao(Context context) {
        super(context);
    }

    /**
     * 通常のカテゴリー取得メソッド
     * カテゴリー一覧など表示するためのデータはこのメソッドを使用する。
     * @return
     */
    public List<GoodsCategory> selectAll() {
        saveCategoryList();
        return list;
    }

    /**
     * スピナーに設定するリスト取得用のメソッド
     * @return
     */
    public List<GoodsCategory> selectForSpinner() {
        if(list == null) {
            saveCategoryList();
        }
        return list;
    }

    public GoodsCategory select(String name) {

        String sql = SQL_SELECT + " WHERE gc.name = ? " + SQL_GROUP_BY;
        String[] bind = {name};

        GoodsCategory goodsCategory = null;
        Cursor cursor = execSelect(sql, bind);
        if(cursor.moveToNext()) {
            goodsCategory = createCategory(cursor);
        }

        // TODO nullの場合は落としたほうが良いか・
        return goodsCategory;
    }

    public void update(GoodsCategory goodsCategory) {

        String sql = "UPDATE m_goods_category SET" +
                " name = ?, update_date = ? " +
                " WHERE id = ? ";
        String[] bind = {goodsCategory.getName(),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(goodsCategory.getId())};

        execUpdate(sql, bind);
        destroyField();
    }

    public void insert(GoodsCategory goodsCategory) {

        String sql = "INSERT INTO m_goods_category" +
                "  (name, view_order, register_date) " +
                " VALUES " +
                "  (?, (SELECT MAX(view_order)+1 FROM m_goods_category), ?)";

        String[] bind = {goodsCategory.getName(),
                String.valueOf(System.currentTimeMillis())};

        execInsert(sql, bind);
        destroyField();
    }

    public void delete(int id) {
        String sql = "DELETE FROM m_goods_category WHERE id = ? ";
        String[] bind = {String.valueOf(id)};
        execDelete(sql, bind);
        destroyField();
    }

    public void updateViewOrder(Iterator<GoodsCategory> iterator) {
        final String sql = "UPDATE m_goods_category SET view_order = ? WHERE id = ? ";
        int newViewOrder = 1;
        while(iterator.hasNext()) {
            GoodsCategory gc = iterator.next();
            String[] bind = {String.valueOf(newViewOrder),
                             String.valueOf(gc.getId())};
            execUpdate(sql, bind);
            newViewOrder++;
        }
        destroyField();
    }

    /**
     * 商品情報の登録または更新画面にて、スピナーで選択したカテゴリー名のIDを取得するために使用。
     * @param name
     * @return
     */
    public int getCategoryId(String name) {
        if(mapUsedSpinner == null) {
            saveCategoryMap();
        }
        return mapUsedSpinner.get(name);
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
        Cursor cursor = execSelect(SQL_SELECT + SQL_GROUP_BY + SQL_ORDER_BY, null);
        while (cursor.moveToNext()) {
            list.add(createCategory(cursor));
        }
    }

    private void saveCategoryMap() {
        mapUsedSpinner = new HashMap<>();
        for(GoodsCategory goodsCategory : list) {
            mapUsedSpinner.put(goodsCategory.getName(), goodsCategory.getId());
        }
    }

    /**
     * update/insert/deleteの各処理を行った後、フィールドのlistとmapを更新する必要がある。
     * しかし、都度フィールドを更新するコストが無駄なことと、そもそもlistとmapはカテゴリーが
     * 更新できない商品画面でのみ使用する。
     * 従って、それぞれのフィールドを使う時に再度オブジェクト化することとし、カテゴリーを更新した場合は
     * このメソッドでフィールドを初期化する。
     */
    private void destroyField() {
        list = null;
        mapUsedSpinner = null;
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
