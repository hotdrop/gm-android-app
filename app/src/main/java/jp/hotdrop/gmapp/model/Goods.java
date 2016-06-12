package jp.hotdrop.gmapp.model;

import java.util.Date;

/**
 * 商品情報を保持するモデルクラス
 */
public class Goods {

    private String id;
    private String name;
    private int categoryId;
    private String categoryName;
    private int amount = 0;
    private int stockNum = 0;
    private Date lastStockDate;
    private long lastStockPrice = 0L;
    private Date lastUpdateDate;

    public Goods() {
    }

    /**
     * 商品IDを設定します。
     * @param argId
     */
    public void setId(String argId) {
        id = argId;
    }

    /**
     * 商品IDを取得します。
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 商品名を設定します。
     * @param argName
     */
    public void setName(String argName) {
        name = argName;
    }

    /**
     * 商品名を取得します。
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 商品のカテゴリーIDを設定します。
     * @param argCategoryId
     */
    public void setCategoryId(int argCategoryId) {
        categoryId = argCategoryId;
    }
    /**
     * 商品のカテゴリーIDを取得します。
     * @return
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * 商品のカテゴリー名を設定します。
     * @param argCategoryName
     */
    public void setCategoryName(String argCategoryName) {
        categoryName = argCategoryName;
    }

    /**
     * 商品のカテゴリー名を取得します。
     * @return
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 現商品の残量情報を設定します。
     * 必ず本クラスのstaticフィールド（AMOUNT_FULL等）を使用して設定してください。
     * @param argAmount
     */
    public void setAmount(int argAmount) {
        amount = argAmount;
    }

    /**
     * 現商品の残量情報を取得します。
     * @return
     */
    public int getAmount() {
        return amount;
    }

    /**
     * 商品の在庫数を設定します。
     * @param argStockNum
     */
    public void setStockNum(int argStockNum) {
        stockNum = argStockNum;
    }

    /**
     * 商品の在庫数を取得します。
     * @return
     */
    public Integer getStockNum() {
        return stockNum;
    }

    /**
     * 前回仕入れ日を設定します。
     * @param argDate
     */
    public void setLastStockDate(Date argDate) {
        lastStockDate = argDate;
    }

    /**
     * 前回仕入れ日を取得します。
     * @return
     */
    public Date getLastStockDate() {
        return lastStockDate;
    }

    /**
     * 前回仕入れ価格を設定します。
     * @param argLastStockingPrice
     */
    public void setLastStockPrice(long argLastStockingPrice) {
        lastStockPrice = argLastStockingPrice;
    }

    /**
     * 前回仕入れ価格を取得します。
     * @return
     */
    public long getLastStockPrice() {
        return lastStockPrice;
    }

    /**
     * 最終更新日を設定します。
     * @param argDate
     */
    public void setLastUpdateDate(Date argDate) {
        lastUpdateDate = argDate;
    }

    /**
     * 最終更新日を取得します。
     * @return
     */
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }
}
