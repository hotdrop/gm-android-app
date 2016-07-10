package jp.hotdrop.gmapp.model;

public class GoodsCategory {

    protected int id;
    protected String name;
    protected int viewOrder;

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
}
