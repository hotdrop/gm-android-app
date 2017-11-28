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
                    "    count(g.category_id) AS count," +
                    "    SUM(g.checked) AS checked_cnt" +
                    " FROM " +
                    "    m_goods_category gc " +
                    " LEFT JOIN " +
                    "    t_goods g ON gc.id = g.category_id";
    private static final String SQL_GROUP_BY = " GROUP BY gc.id, g.category_id ";
    private static final String SQL_ORDER_BY = " ORDER BY view_order";

    /** カテゴリーをSpinnerでリスト表示する際、いちいちselectするとコストがかかるのでフィールドに保持する */
    private static ArrayList<GoodsCategory> list;

    /**
     * カテゴリー名とカテゴリーidのマップ情報。
     * カテゴリー名からidを取得するために使用。Spinnerが名称しか持てないのでlistとは別にMap保持で内部保持している。
     **/
    private static HashMap<String, Integer> mapUsedSpinner;

    @Inject
    public GoodsCategoryDao(Context context) {
        super(context);
    }

    /**
     * 登録されている全てのカテゴリーをリスト形式で取得する。
     *
     * カテゴリーは色々な画面で使用するため、フィールドのlistでキャッシュ保持している。
     * このメソッドは「カテゴリー一覧画面」（CategoryFragment）で呼ばれるが、子画面で
     * 更新された内容も反映したかったため無条件でキャッシュ保持しているカテゴリー情報を最新化することにした。
     * @return 登録されている全カテゴリーのリスト
     */
    public List<GoodsCategory> selectAll() {
        saveCategoryList();
        return list;
    }

    /**
     * 商品が1つ以上登録されているカテゴリーを取得する。
     * @return 商品が1つ以上登録されているカテゴリーのリスト
     */
    public List<GoodsCategory> selectExceptUnRegisteredGoods() {

        final String sql =
                "SELECT * FROM ( " +
                        SQL_SELECT + SQL_GROUP_BY +
                "    ) gcl " +
                " WHERE count > 0 " + SQL_ORDER_BY;

        List<GoodsCategory> list = new ArrayList<>();
        Cursor cursor = execSelect(sql, null);
        while (cursor.moveToNext()) {
            list.add(createCategory(cursor));
        }
        return list;
    }

    /**
     * Spinnerに設定するカテゴリーのリスト取得用メソッド
     * カテゴリーのSpinnerは色々な画面で使用しているため、フィールドで持っているlistから取得する。
     * カテゴリーの更新はカテゴリー一覧画面を経由してしかできない使用のため、古い情報を持ってくることはないはず。
     * 作りはとても良くない。。
     *
     * @return 登録されている全カテゴリーのリスト
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

        return goodsCategory;
    }

    public void update(GoodsCategory goodsCategory) {

        String sql = "UPDATE m_goods_category SET name = ? WHERE id = ? ";
        String[] bind = {goodsCategory.getName(),
                String.valueOf(goodsCategory.getId())};

        execUpdate(sql, bind);
        destroyField();
    }

    public void insert(GoodsCategory goodsCategory) {

        String sql = "INSERT INTO m_goods_category" +
                "  (name, view_order) " +
                " VALUES " +
                "  (?, (SELECT MAX(view_order)+1 FROM m_goods_category))";

        String[] bind = {goodsCategory.getName()};

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
     * カテゴリー名に対応したカテゴリーIDを取得する。
     * 主にSpinnerで選択したカテゴリーをIDに変換するために使用。
     * @param name カテゴリー名
     * @return カテゴリーID
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
     * しかし、都度フィールドを更新するのはコストが無駄かつDRYに反している。（保持している時点で反してはいるが・）
     * そもそもlistとmapはカテゴリーが更新できない商品画面でのみ使用する。
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
        category.setGoodsCount(getCursorInt(cursor, "count"));
        category.setCheckedGoodsCount(getCursorInt(cursor, "checked_cnt"));
        return category;
    }
}
