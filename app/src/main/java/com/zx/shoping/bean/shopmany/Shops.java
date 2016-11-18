package com.zx.shoping.bean.shopmany;

import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */
public class Shops {
    private String name;
    private int isSelect;
    private List<Goods> goodsList;

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(int isSelect) {
        this.isSelect = isSelect;
    }
}
