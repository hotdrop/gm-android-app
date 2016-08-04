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

    public static final int AMOUNT_INCREASE_DECREASE_UNIT = 1;

    public static final String[] STOCK_NUM_LIST = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

    protected String id;
    protected String name;

    protected int categoryId;
    protected String categoryName;
    protected int amount;
    protected int stockNum;
    protected Date replenishmentDate;
    protected String note;
    protected Date registerDate;
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

    public void setStockNum(int stockNum) {
        this.stockNum = stockNum;
    }

    public int getStockNum() {
        return stockNum;
    }

    public void setReplenishmentDate(Date replenishmentDate) {
        this.replenishmentDate = replenishmentDate;
    }

    public Date getReplenishmentDate() {
        return replenishmentDate;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getRegisterDate() {
        return registerDate;
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
        this.replenishmentDate = goods.getReplenishmentDate();
        this.note = goods.getNote();
        this.registerDate = goods.getRegisterDate();
        this.updateDate = goods.getUpdateDate();
    }

    public boolean isAmountEmpty() {
        return amount == AMOUNT_EMPTY ? true : false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Goods && ((Goods) o).getId().equals(this.id);
    }
}
