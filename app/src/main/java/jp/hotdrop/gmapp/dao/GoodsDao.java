package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.util.DateUtil;
import rx.Observable;

public class GoodsDao extends AbstractDao {

    private static final String SQL_SELECT_FROM = "SELECT " +
            "        gs.id AS id, " +
            "        gs.name AS name, " +
            "        category_id, " +
            "        gc.name AS category_name, " +
            "        amount," +
            "        stock_num, " +
            "        last_stock_date, " +
            "        last_stock_price, " +
            "        update_date" +
            " FROM t_goods gs " +
            "    LEFT JOIN m_goods_category gc ON gs.category_id = gc.id";

    private static GoodsDao dao;

    private GoodsDao(Context context) {
        super(context);
    }

    public static synchronized GoodsDao getInstance(Context context) {
        if(dao == null) {
            dao = new GoodsDao(context);
        }
        return dao;
    }

    public static GoodsDao getInstance() {
        if(dao == null) {
            throw new IllegalStateException("プログラムエラー。最上位のActivityで引数付きgetInstanceを実行してください。");
        }
        return dao;
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

    public Goods select(String id) {

        Goods goods = null;
        String sql = SQL_SELECT_FROM + " WHERE gs.id = ?";
        String[] bind = {id};

        Cursor cursor = execSelect(sql, bind);
        if (cursor.moveToNext()) {
            goods = createGoods(cursor);
        }

        return goods;
    }

    public void insert(Goods goods) {

        String sql = "INSERT INTO t_goods" +
                "  (name, category_id, stock_num, last_stock_date, last_stock_price, update_date) " +
                " VALUES " +
                "  (?, ?, ?, ?, ?, ?)";

        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                goods.getStockNum(),
                String.valueOf(DateUtil.dateToLong(goods.getLastStockDate())),
                goods.getLastStockPrice(),
                String.valueOf(System.currentTimeMillis())};

        execInsert(sql, bind);
    }

    public void update(Goods goods) {

        String sql = "UPDATE t_goods SET" +
                " name = ?, category_id = ?, stock_num = ?, last_stock_date = ?, " +
                " last_stock_price = ?, update_date = ? " +
                " WHERE id = ? ";
        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                goods.getStockNum(),
                String.valueOf(DateUtil.dateToLong(goods.getLastStockDate())),
                goods.getLastStockPrice(),
                String.valueOf(System.currentTimeMillis()),
                goods.getId()};

        execUpdate(sql, bind);
    }

    public void updateAmount(String id, int amount) {
        String sql = "UPDATE t_goods SET amount = ?, update_date = ? WHERE id = ? ";
        String[] bind = {String.valueOf(amount), String.valueOf(System.currentTimeMillis()), id};
        execUpdate(sql, bind);
    }

    public void delete(String id) {
        String sql = "DELETE FROM t_goods WHERE id = ? ";
        String[] bind = {id};
        execDelete(sql, bind);
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
        goods.setLastStockDate(getCursorDate(cursor, "last_stock_date"));
        goods.setLastStockPrice(getCursorString(cursor, "last_stock_price"));
        goods.setUpdateDate(getCursorDate(cursor, "update_date"));
        return goods;
    }
}
