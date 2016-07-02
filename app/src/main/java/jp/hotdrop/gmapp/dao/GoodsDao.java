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

    /**
     * GoodsテーブルのDAOを生成します。
     * update,insert,deleteを使う場合、beginTranメソッドでトランザクション開始し
     * 必ずcommitメソッドまたはrollbackメソッドを実行してください。
     * @param context コンテキスト
     */
    public GoodsDao(Context context) {
        super(context);
    }

    public Observable<List<Goods>> selectAll() {

        readableDatabase();

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

        readableDatabase();

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

        if(!isBeginTransaction()) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにinsertを実行しています。");
        }

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

        if(!isBeginTransaction()) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにupdateを実行しています。");
        }

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

        if(!isBeginTransaction()) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにupdateAmountを実行しています。");
        }

        String sql = "UPDATE t_goods SET" +
                " amount = ?, update_date = ? " +
                "WHERE id = ? ";

        String[] bind = {String.valueOf(amount),
                String.valueOf(System.currentTimeMillis()),
                id};

        execUpdate(sql, bind);
    }

    public void delete(String id) {

        if(!isBeginTransaction()) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにdeleteを実行しています。");
        }

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
