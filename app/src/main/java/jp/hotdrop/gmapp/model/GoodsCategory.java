package jp.hotdrop.gmapp.model;

import org.parceler.Parcel;

@Parcel
public class GoodsCategory {

    protected int id;
    protected String name;
    protected int viewOrder;
    protected int goodsCount = 0;
    protected int checkedGoodsCount = 0;

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

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setCheckedGoodsCount(int checkedGoodsCount) {
        this.checkedGoodsCount = checkedGoodsCount;
    }

    public int getCheckedGoodsCount() {
        return checkedGoodsCount;
    }

    public void change(GoodsCategory goodsCategory) {
        this.id = goodsCategory.getId();
        this.name = goodsCategory.getName();
        this.viewOrder = goodsCategory.getViewOrder();
        this.goodsCount = goodsCategory.getGoodsCount();
        this.checkedGoodsCount = goodsCategory.getCheckedGoodsCount();
    }

    public boolean hasGoods() {
        return (goodsCount >= 1) ? true : false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GoodsCategory && (((GoodsCategory) o).getId() == this.id);
    }
}
