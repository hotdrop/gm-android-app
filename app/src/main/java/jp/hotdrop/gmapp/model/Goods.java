package jp.hotdrop.gmapp.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Goods {

    public static final int AMOUNT_FULL = 5;
    public static final int AMOUNT_MANY = 4;
    public static final int AMOUNT_HALF = 3;
    public static final int AMOUNT_A_LITTLE = 2;
    public static final int AMOUNT_EMPTY = 1;

    protected String id;
    protected String name;

    protected int categoryId;
    protected String categoryName;

    protected int amount = 0;
    // bindingを使用しておりかつEditTextにしているためintではなくStringで表現する
    // なお、空が設定された場合は0を保持する。（setterにて）
    protected String stockNum = "0";
    protected Date lastStockDate;
    protected String lastStockPrice;
    protected Date updateDate;

    public Goods() {
    }

    public void setId(String argId) {
        id = argId;
    }

    public String getId() {
        return id;
    }

    public void setName(String argName) {
        name = argName;
    }

    public String getName() {
        return name;
    }

    public void setCategoryId(int argCategoryId) {
        categoryId = argCategoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryName(String argCategoryName) {
        categoryName = argCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setAmount(int argAmount) {
        amount = argAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setStockNum(String stockNum) {
        this.stockNum = (stockNum == null || stockNum.trim().equals(""))? "0" : stockNum;
    }

    public String getStockNum() {
        return String.valueOf(stockNum);
    }

    public void setLastStockDate(Date argDate) {
        lastStockDate = argDate;
    }

    public Date getLastStockDate() {
        return lastStockDate;
    }

    public void setLastStockPrice(String lastStockPrice) {
        this.lastStockPrice = lastStockPrice;
    }

    public String getLastStockPrice() {
        return lastStockPrice;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void change(Goods goods) {
        this.id = goods.getId();
        this.name = goods.getName();
        this.categoryId = goods.getCategoryId();
        this.categoryName = goods.getCategoryName();
        this.amount = goods.getAmount();
        this.stockNum = goods.getStockNum();
        this.lastStockDate = goods.getLastStockDate();
        this.lastStockPrice = goods.getLastStockPrice();
        this.updateDate = goods.getUpdateDate();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Goods && ((Goods) o).getId().equals(this.id);
    }
}
