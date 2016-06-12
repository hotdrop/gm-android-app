package jp.hotdrop.gmapp.model;

public class Goods {

    private String id;
    private String name;
    private int categoryId;
    private String categoryName;
    private int amount = 0;
    private int stockNum = 0;
    private long lastStockingDateUnixEpoch = 0L;
    private long lastStockingPrice = 0L;
    private long lastUpdateDateUnixEpoch = 0L;

    /**
     * 商品名とカテゴリーIDを基に商品情報を生成します。
     * @param argName 商品名
     * @param argCategoryId カテゴリーID
     */
    public Goods(String argName, int argCategoryId) {
        name = argName;
        categoryId = argCategoryId;
    }

    /**
     * 商品IDを設定します。
     * @param argId 商品ID
     */
    public void setId(String argId) {
        id = argId;
    }

    /**
     * 商品IDを取得します。
     * @return 商品ID
     */
    public String getId() {
        return id;
    }

    public void setName(String argName) {
        name = argName;
    }

    /**
     * 商品名を取得します。
     * 商品名はコンストラクタで設定するためセッターは存在しません。
     * @return 商品名
     */
    public String getName() {
        return name;
    }

    public void setCategoryId(int argCategoryId) {
        categoryId = argCategoryId;
    }
    /**
     * 商品のカテゴリーIDを取得します。
     * カテゴリーIDはコンストラクタで設定するためセッターは存在しません。
     * @return カテゴリーID
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * 商品のカテゴリー名を設定します。
     * @param argCategoryName 商品のカテゴリー名
     */
    public void setCategoryName(String argCategoryName) {
        categoryName = argCategoryName;
    }

    /**
     * 商品のカテゴリー名を取得します。
     * @return 商品のカテゴリー名
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 現商品の残量情報を設定します。
     * 必ず本クラスのstaticフィールド（AMOUNT_FULL等）を使用して設定してください。
     * @param argAmount 現商品の残量
     */
    public void setAmount(int argAmount) {
        amount = argAmount;
    }

    /**
     * 現商品の残量情報を取得します。
     * @return 現商品の残量
     */
    public int getAmount() {
        return amount;
    }

    /**
     * 商品の在庫数を設定します。
     * @param argStockNum 商品の在庫数
     */
    public void setStockNum(int argStockNum) {
        stockNum = argStockNum;
    }

    /**
     * 商品の在庫数を取得します。
     * @return 商品の在庫数
     */
    public Integer getStockNum() {
        return stockNum;
    }

    /**
     * 前回仕入れ日のUNIXエポックを設定します。
     * @param argUnixEpoch 前回仕入れ日のUNIXエポック
     */
    public void setLastStockingDateUnixEpoch(long argUnixEpoch) {
        lastStockingDateUnixEpoch = argUnixEpoch;
    }

    /**
     * 前回仕入れ日のUNIXエポックを取得します。
     * @return 前回仕入れ日のUNIXエポック
     */
    public long getLastStockingDateUnixEpoch() {
        return lastStockingDateUnixEpoch;
    }

    /**
     * 前回仕入れ価格を設定します。
     * @param argLastStockingPrice 前回仕入れ価格
     */
    public void setLastStockingPrice(long argLastStockingPrice) {
        lastStockingPrice = argLastStockingPrice;
    }

    /**
     * 前回仕入れ価格を取得します。
     * @return 前回仕入れ価格
     */
    public long getLastStockingPrice() {
        return lastStockingPrice;
    }

    /**
     * 最終更新日のUNIXエポックを設定します。
     * @param argUnixEpoch 最終更新日のUNIXエポック
     */
    public void setLastUpdateDateUnixEpoch(long argUnixEpoch) {
        lastUpdateDateUnixEpoch = argUnixEpoch;
    }

    /**
     * 最終更新日のUNIXエポックを取得します。
     * @return 最終更新日のUNIXエポック
     */
    public long getLastUpdateDateUnixEpoch() {
        return lastUpdateDateUnixEpoch;
    }
}
