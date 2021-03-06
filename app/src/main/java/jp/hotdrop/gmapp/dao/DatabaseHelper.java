package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_FILE_NAME = "GMApp.db";
    private static final int DB_VERSION = 1;

    /** 複数SQL文が書かれたファイルを読み込んだ際の、各SQL文の区切りを示す文字 */
    private static final String DELIMITER_OF_SQL_STATEMENT = ";";
    private Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
        myContext = context;
    }

    /**
     * データベースの初期生成時に呼ばれ、テーブル生成と初期データ作成を行う。
     * @param db DBオブジェクト
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        List<String> createTableSqlList = getSqlList("sql/DDL/createTable.sql");

        for(String sql : createTableSqlList) {
            db.execSQL(sql);
        }

        List<String> insertList = getSqlList("sql/DML/addData.sql");
        for(String insertSql : insertList) {
            db.execSQL(insertSql);
        }
    }

    /**
     * 既存テーブルの再生成とデータ追加を行う。
     * DB_VERSIONを上げた場合に呼ばれる。
     *
     * @param db DBオブジェクト
     * @param oldVersion 旧バージョン番号
     * @param newVersion 新バージョン番号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> insertList = getSqlList("sql/DML/addData.sql");
        for(String insertSql : insertList) {
            db.execSQL(insertSql);
        }
    }

    /**
     * 指定のsqlファイルを読み込み、指定の区切り文字で分割したSQL文をリスト形式で取得する。
     *
     * @param sqlFilePath 読み込むsqlのパス
     * @return SQL文のリスト
     */
    private List<String> getSqlList(String sqlFilePath) {

        StringBuilder sb = new StringBuilder();
        AssetManager asset = myContext.getResources().getAssets();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(asset.open(sqlFilePath)))) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String readStr = sb.toString();
        List<String> sqlList = new ArrayList<>();

        for(String sql : readStr.split(DELIMITER_OF_SQL_STATEMENT)) {
            sqlList.add(sql);
        }

        return sqlList;
    }
}
