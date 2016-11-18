package com.zx.shoping.bean;

import java.io.Serializable;

public class Shop implements Serializable {
    private String name;
    private int isSelect;

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