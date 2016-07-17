package jp.hotdrop.gmapp.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class GoodsCategory {

    protected int id;
    protected String name;
    protected int viewOrder;
    protected Date registerDate;
    protected Date updateDate;
    protected int goodsCount = 0;

    public GoodsCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViewOrder() {
        return viewOrder;
    }

    public void setViewOrder(int viewOrder) {
        this.viewOrder = viewOrder;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void change(GoodsCategory goodsCategory) {
        this.id = goodsCategory.getId();
        this.name = goodsCategory.getName();
        this.viewOrder = goodsCategory.getViewOrder();
        this.registerDate = goodsCategory.getRegisterDate();
        this.updateDate = goodsCategory.getUpdateDate();
        this.goodsCount = goodsCategory.getGoodsCount();
    }

    public boolean hasGoods() {
        return (goodsCount >= 1) ? true : false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GoodsCategory && (((GoodsCategory) o).getId() == this.id);
    }
}
