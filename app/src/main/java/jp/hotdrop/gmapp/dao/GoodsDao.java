package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.util.DateUtil;
import rx.Observable;

@Singleton
public class GoodsDao extends AbstractDao {

    private static final String SQL_SELECT_FROM = "SELECT " +
            "        g.id AS id, " +
            "        g.name AS name, " +
            "        g.category_id, " +
            "        gc.name AS category_name, " +
            "        g.amount," +
            "        g.stock_num, " +
            "        g.replenishment_date, " +
            "        g.register_date, " +
            "        g.update_date AS update_date" +
            " FROM t_goods g " +
            "    LEFT JOIN m_goods_category gc ON g.category_id = gc.id";

    @Inject
    public GoodsDao(Context context) {
        super(context);
    }

    public Goods select(String id) {

        final String sql = SQL_SELECT_FROM + " WHERE g.id = ?";

        String[] bind = {id};
        Cursor cursor = execSelect(sql, bind);
        if(cursor.moveToNext()) {
            return createGoods(cursor);
        }

        return null;
    }

    public Observable<List<Goods>> selectAll() {

        final String sql = SQL_SELECT_FROM + " ORDER BY gc.view_order, g.id";

        List<Goods> goodsList = new ArrayList<>();
        Cursor cursor = execSelect(sql, null);
        while (cursor.moveToNext()) {
            Goods goods = createGoods(cursor);
            goodsList.add(goods);
        }

        return Observable.just(goodsList);
    }

    public void insert(Goods goods) {

        final String sql = "INSERT INTO t_goods" +
                "  (name, category_id, stock_num, replenishment_date, register_date, update_date) " +
                " VALUES " +
                "  (?, ?, ?, ?, ?, ?)";

        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                String.valueOf(goods.getStockNum()),
                String.valueOf(DateUtil.dateToLong(goods.getReplenishmentDate())),
                String.valueOf(DateUtil.dateToLong(goods.getRegisterDate())),
                String.valueOf(System.currentTimeMillis())};

        execInsert(sql, bind);
    }

    public void update(Goods goods) {

        final String sql = "UPDATE t_goods SET" +
                " name = ?, category_id = ?, amount = ?, stock_num = ?, " +
                " update_date = ? " +
                " WHERE id = ? ";

        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                String.valueOf(goods.getAmount()),
                String.valueOf(goods.getStockNum()),
                String.valueOf(System.currentTimeMillis()),
                goods.getId()};

        execUpdate(sql, bind);
    }

    public void replenishmentAmount(Goods goods) {

        final String sql = "UPDATE t_goods SET" +
                " amount = ?, stock_num = ?, replenishment_date = ?, update_date = ? " +
                " WHERE id = ? ";

        String[] bind = {String.valueOf(goods.getAmount()),
                String.valueOf(goods.getStockNum()),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(System.currentTimeMillis()),
                goods.getId()};

        execUpdate(sql, bind);
    }

    public void delete(String id) {
        final String sql = "DELETE FROM t_goods WHERE id = ? ";
        String[] bind = {id};
        execDelete(sql, bind);
    }

    public boolean existGoodsName(String name) {

        final String sql = "SELECT name FROM t_goods WHERE name = ?";
        String[] bind = {name};

        Cursor cursor = execSelect(sql, bind);
        if (cursor.moveToNext()) {
            return true;
        }

        return false;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(db, "t_goods");
    }

    /**
     * カーソルから各値を取得し商品情報を生成する。
     * @param cursor
     * @return
     */
    private Goods createGoods(Cursor cursor) {
        Goods goods = new Goods();
        goods.setId(getCursorString(cursor, "id"));
        goods.setName(getCursorString(cursor, "name"));
        goods.setCategoryId(getCursorInt(cursor, "category_id"));
        goods.setCategoryName(getCursorString(cursor, "category_name"));
        goods.setAmount(getCursorInt(cursor, "amount"));
        goods.setStockNum(getCursorInt(cursor, "stock_num"));
        goods.setReplenishmentDate(getCursorDate(cursor, "replenishment_date"));
        goods.setRegisterDate(getCursorDate(cursor, "register_date"));
        goods.setUpdateDate(getCursorDate(cursor, "update_date"));
        return goods;
    }
}
