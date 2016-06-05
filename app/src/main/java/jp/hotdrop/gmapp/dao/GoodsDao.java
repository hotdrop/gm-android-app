package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jp.hotdrop.gmapp.model.Goods;
import rx.Observable;

public class GoodsDao {

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

    public Observable<List<Goods>> selectAll() {

        String sql = "SELECT gs.id AS id, gs.name AS name, category_id, gc.name AS category_name, amount, stock_num, " +
                "last_stocking_date, last_stocking_price, last_update_date" +
                " FROM t_goods gs LEFT JOIN m_goods_category gc ON gs.category_id = gc.id";

        List<Goods> goodsList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            Goods goods = new Goods(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("category_id")));
            goods.setId(cursor.getString(cursor.getColumnIndex("id")));
            goods.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
            goods.setAmount(cursor.getInt(cursor.getColumnIndex("amount")));
            goods.setStockNum(cursor.getInt(cursor.getColumnIndex("stock_num")));
            goods.setLastStockingDateUnixEpoch(cursor.getLong(cursor.getColumnIndex("last_stocking_date")));
            goods.setLastStockingPrice(cursor.getInt(cursor.getColumnIndex("last_stocking_price")));
            goods.setLastUpdateDateUnixEpoch(cursor.getLong(cursor.getColumnIndex("last_update_date")));
            goodsList.add(goods);
        }

        return Observable.just(goodsList);
    }

    /**
     * 指定したIDの商品情報を取得します。
     * @param id Goodsテーブルのid
     * @return Goodsテーブルから取得した１レコード分のデータオブジェクト
     */
    public Goods select(String id) {

        Goods goods = null;

        if(db == null) {
            db = dbHelper.getReadableDatabase();
        }

        String sql = "SELECT gs.name AS name, category_id, gc.name AS category_name, amount, stock_num, " +
                "last_stocking_date, last_stocking_price, last_update_date" +
                " FROM t_goods gs LEFT JOIN m_goods_category gc ON gs.category_id = gc.id" +
                " WHERE gs.id = ?";
        String[] bind = {id};

        Cursor cursor = db.rawQuery(sql, bind);
        if (cursor.moveToNext()) {
            goods = new Goods(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("category_id")));
            goods.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
            goods.setAmount(cursor.getInt(cursor.getColumnIndex("amount")));
            goods.setStockNum(cursor.getInt(cursor.getColumnIndex("stock_num")));
            goods.setLastStockingDateUnixEpoch(cursor.getLong(cursor.getColumnIndex("last_stocking_date")));
            goods.setLastStockingPrice(cursor.getInt(cursor.getColumnIndex("last_stocking_price")));
            goods.setLastUpdateDateUnixEpoch(cursor.getLong(cursor.getColumnIndex("last_update_date")));
            goods.setId(id);
        }

        return goods;
    }

    /**
     * 商品をデータベースに登録する。
     * @param data 商品情報
     */
    public void insert(Goods data) {

        if(db == null) {
            //プログラムエラー。beginTranを実行せずにinsertを実行
        }

        long lastUpdateDate = System.currentTimeMillis();

        String sql = "INSERT INTO t_goods" +
                "  (name, category_id, stock_num, last_stocking_date, last_stocking_price, last_update_date) " +
                "VALUES" +
                "  (?, ?, ?, ?, ?, ?)";
        String[] bind = {data.getName(),
                String.valueOf(data.getCategoryId()),
                String.valueOf(data.getStockNum()),
                String.valueOf(data.getLastStockingDateUnixEpoch()),
                String.valueOf(data.getLastStockingPrice()),
                String.valueOf(lastUpdateDate)};

        db.execSQL(sql, bind);
    }

    /**
     * 既に登録されている商品情報を更新する。
     * ただしamountはこのメソッドでは更新せず、専用のメソッドでのみ更新する。
     * @param data 商品情報
     */
    public void update(Goods data) {

        if(db == null) {
            //プログラムエラー。beginTranを実行せずにupdateを実行
        }
        long lastUpdateDate = System.currentTimeMillis();

        String sql = "UPDATE t_goods SET" +
                " name = ?, category_id = ?, stock_num = ?, last_stocking_date = ?, " +
                " last_stocking_price = ?, last_update_date = ? " +
                "WHERE id = ? ";
        String[] bind = {data.getName(),
                String.valueOf(data.getCategoryId()),
                String.valueOf(data.getStockNum()),
                String.valueOf(data.getLastStockingDateUnixEpoch()),
                String.valueOf(data.getLastStockingPrice()),
                String.valueOf(lastUpdateDate),
                data.getId()};

        db.execSQL(sql, bind);
    }

    /**
     * 残量（amount）を更新します。
     * @param goodsId 商品ID
     * @param amount 残量
     */
    public void updateAmount(String goodsId, int amount) {

        if(db == null) {
            // プログラムエラー。beginTranを実行せずにupdateAmountを実行
        }

        long lastUpdateDate = System.currentTimeMillis();

        String sql = "UPDATE t_goods SET" +
                " amount = ?, last_update_date = ? " +
                "WHERE id = ? ";
        String[] bind = {String.valueOf(amount), String.valueOf(lastUpdateDate), goodsId};

        db.execSQL(sql, bind);
    }

    /**
     * 商品情報を削除します。
     * @param goodsId 商品ID
     */
    public void delete(String goodsId) {

        if(db == null) {
            // プログラムエラー。beginTranを実行せずにdeleteを実行
        }

        String sql = "DELETE FROM t_goods WHERE id = ? ";
        String[] bind = {goodsId};

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

}
