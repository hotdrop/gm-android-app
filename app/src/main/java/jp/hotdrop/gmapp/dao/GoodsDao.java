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
            "        gs.id AS id, " +
            "        gs.name AS name, " +
            "        category_id, " +
            "        gc.name AS category_name, " +
            "        amount," +
            "        stock_num, " +
            "        last_update_amount_date, " +
            "        last_stock_date, " +
            "        update_date" +
            " FROM t_goods gs " +
            "    LEFT JOIN m_goods_category gc ON gs.category_id = gc.id";

    @Inject
    public GoodsDao(Context context) {
        super(context);
    }

    public Observable<List<Goods>> selectAll() {

        String sql = SQL_SELECT_FROM + " ORDER BY gc.view_order, gs.id";

        List<Goods> goodsList = new ArrayList<>();
        Cursor cursor = execSelect(sql, null);
        while (cursor.moveToNext()) {
            Goods goods = createGoods(cursor);
            goodsList.add(goods);
        }

        return Observable.just(goodsList);
    }

    public void insert(Goods goods) {

        String sql = "INSERT INTO t_goods" +
                "  (name, category_id, stock_num, last_update_amount_date, last_stock_date, update_date) " +
                " VALUES " +
                "  (?, ?, ?, ?, ?, ?)";

        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                goods.getStockNum(),
                String.valueOf(DateUtil.dateToLong(goods.getLastUpdateAmountDate())),
                String.valueOf(DateUtil.dateToLong(goods.getLastStockDate())),
                String.valueOf(System.currentTimeMillis())};

        execInsert(sql, bind);
    }

    public void update(Goods goods) {

        String sql = "UPDATE t_goods SET" +
                " name = ?, category_id = ?, stock_num = ?, last_update_amount_date = ?, " +
                " last_stock_date = ?, update_date = ? " +
                " WHERE id = ? ";
        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                goods.getStockNum(),
                String.valueOf(DateUtil.dateToLong(goods.getLastUpdateAmountDate())),
                String.valueOf(DateUtil.dateToLong(goods.getLastStockDate())),
                String.valueOf(System.currentTimeMillis()),
                goods.getId()};

        execUpdate(sql, bind);
    }

    public void delete(String id) {
        String sql = "DELETE FROM t_goods WHERE id = ? ";
        String[] bind = {id};
        execDelete(sql, bind);
    }

    public boolean existGoodsName(String name) {

        String sql = "SELECT name FROM t_goods WHERE name = ?";
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

    public void updateAmount(String id, int amount) {
        String sql = "UPDATE t_goods SET amount = ?, update_date = ? WHERE id = ? ";
        String[] bind = {String.valueOf(amount), String.valueOf(System.currentTimeMillis()), id};
        execUpdate(sql, bind);
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
        goods.setStockNum(getCursorString(cursor, "stock_num"));
        goods.setLastUpdateAmountDate(getCursorDate(cursor, "last_update_amount_date"));
        goods.setLastStockDate(getCursorDate(cursor, "last_stock_date"));
        goods.setUpdateDate(getCursorDate(cursor, "update_date"));
        return goods;
    }
}
