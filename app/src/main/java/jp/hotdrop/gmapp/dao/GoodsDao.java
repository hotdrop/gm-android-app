package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import jp.hotdrop.gmapp.model.Goods;
import jp.hotdrop.gmapp.util.DateUtil;
import rx.Observable;

public class GoodsDao {

    private static final String SQL_SELECT_FROM = "SELECT " +
            "        gs.id AS id, " +
            "        gs.name AS name, " +
            "        category_id, " +
            "        gc.name AS category_name, " +
            "        amount," +
            "        stock_num, " +
            "        last_stock_date, " +
            "        last_stock_price, " +
            "        last_update_date" +
            " FROM t_goods gs " +
            "    LEFT JOIN m_goods_category gc ON gs.category_id = gc.id";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * GoodsテーブルのDAOを生成します。
     * update,insert,deleteを使う場合、beginTranメソッドでトランザクション開始し
     * 必ずcommitメソッドまたはrollbackメソッドを実行してください。
     * @param context コンテキスト
     */
    public GoodsDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 全商品を取得します。
     * @return
     */
    public Observable<List<Goods>> selectAll() {

        if(db == null) {
            db = dbHelper.getReadableDatabase();
        }

        String sql = SQL_SELECT_FROM + " ORDER BY gc.view_order, gs.id";

        List<Goods> goodsList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Goods goods = createGoods(cursor);
            goodsList.add(goods);
        }

        return Observable.just(goodsList);
    }

    /**
     * 指定したIDの商品を取得します。
     * @param id
     * @return
     */
    public Goods select(String id) {

        Goods goods = null;

        if(db == null) {
            db = dbHelper.getReadableDatabase();
        }

        String sql = SQL_SELECT_FROM + " WHERE gs.id = ?";
        String[] bind = {id};

        Cursor cursor = db.rawQuery(sql, bind);
        if (cursor.moveToNext()) {
            goods = createGoods(cursor);
        }

        return goods;
    }


    /**
     * 商品をデータベースに登録します。
     * @param goods 商品情報
     */
    public void insert(Goods goods) {

        if(db == null) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにinsertを実行しています。");
        }

        String sql = "INSERT INTO t_goods" +
                "  (name, category_id, stock_num, last_stocking_date, last_stocking_price, last_update_date) " +
                " VALUES " +
                "  (?, ?, ?, ?, ?, ?)";

        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                String.valueOf(goods.getStockNum()),
                String.valueOf(DateUtil.dateToLong(goods.getLastStockDate())),
                String.valueOf(goods.getLastStockPrice()),
                String.valueOf(System.currentTimeMillis())};

        db.execSQL(sql, bind);
    }

    /**
     * 商品情報を更新します。
     * ただし、amountは別途更新用の処理があるため、ここでは更新しません。
     * @param goods 商品情報
     */
    public void update(Goods goods) {

        if(db == null) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにupdateを実行しています。");
        }

        String sql = "UPDATE t_goods SET" +
                " name = ?, category_id = ?, stock_num = ?, last_stocking_date = ?, " +
                " last_stocking_price = ?, last_update_date = ? " +
                " WHERE id = ? ";
        String[] bind = {goods.getName(),
                String.valueOf(goods.getCategoryId()),
                String.valueOf(goods.getStockNum()),
                String.valueOf(DateUtil.dateToLong(goods.getLastStockDate())),
                String.valueOf(goods.getLastStockPrice()),
                String.valueOf(System.currentTimeMillis()),
                goods.getId()};

        db.execSQL(sql, bind);
    }

    /**
     * 残量（amount）を更新します。
     * @param id 商品ID
     * @param amount 残量
     */
    public void updateAmount(String id, int amount) {

        if(db == null) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにupdateAmountを実行しています。");
        }

        String sql = "UPDATE t_goods SET" +
                " amount = ?, last_update_date = ? " +
                "WHERE id = ? ";

        String[] bind = {String.valueOf(amount),
                String.valueOf(System.currentTimeMillis()),
                id};

        db.execSQL(sql, bind);
    }

    /**
     * 商品情報を削除します。
     * @param id 商品ID
     */
    public void delete(String id) {

        if(db == null) {
            throw new IllegalStateException("プログラムエラー。beginTranを実行せずにdeleteを実行しています。");
        }

        String sql = "DELETE FROM t_goods WHERE id = ? ";
        String[] bind = {id};

        db.execSQL(sql, bind);
    }

    /**
     * トランザクションを開始します。
     */
    public void beginTran() {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
    }

    /**
     * トランザクションをコミットします。
     */
    public void commit() {
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * トランザクションをロールバックします。
     */
    public void rollback() {
        db.endTransaction();
        db.close();
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
        goods.setLastStockDate(getCursorDate(cursor, "last_stock_date"));
        goods.setLastStockPrice(getCursorInt(cursor, "last_stock_price"));
        goods.setLastUpdateDate(getCursorDate(cursor, "last_update_date"));
        return goods;
    }

    private String getCursorString(Cursor cursor, String itemName) {
        return cursor.getString(cursor.getColumnIndex(itemName));
    }

    private int getCursorInt(Cursor cursor, String itemName) {
        return cursor.getInt(cursor.getColumnIndex(itemName));
    }

    private Date getCursorDate(Cursor cursor, String itemName) {
        long unixEpoch = cursor.getLong(cursor.getColumnIndex(itemName));
        return DateUtil.longToDate(unixEpoch);
    }
}
