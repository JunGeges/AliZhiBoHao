package com.zmtmt.zhibohao.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Products implements Parcelable{
    /**商品ID*/
    private String products_id;

    /**商品图片*/
    private String products_icon;

    /**商品名称*/
    private String products_name;

    /** 商品单价*/
    private String products_price;

    /**请求域名*/
    private String products_base;

    /**推荐按钮的状态*/
    private boolean isClick=false;

    public Products(){}

    public Products(Parcel dest){
        this.products_id=dest.readString();
        this.products_icon=dest.readString();
        this.products_name=dest.readString();
        this.products_price=dest.readString();
        this.products_base=dest.readString();
    }

    public String getProducts_id() {
        return products_id;
    }

    public void setProducts_id(String products_id) {
        this.products_id = products_id;
    }

    public String getProducts_icon() {
        return products_icon;
    }

    public void setProducts_icon(String products_icon) {
        this.products_icon = products_icon;
    }

    public String getProducts_name() {
        return products_name;
    }

    public void setProducts_name(String products_name) {
        this.products_name = products_name;
    }

    public String getProducts_price() {
        return products_price;
    }

    public void setProducts_price(String products_price) {
        this.products_price = products_price;
    }

    public String getProducts_base() {
        return products_base;
    }

    public void setProducts_base(String products_base) {
        this.products_base = products_base;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    @Override
    public String toString() {
        return "Products{" +
                "products_id='" + products_id + '\'' +
                ", products_icon='" + products_icon + '\'' +
                ", products_name='" + products_name + '\'' +
                ", products_price='" + products_price + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(products_id);
        dest.writeString(products_icon);
        dest.writeString(products_name);
        dest.writeString(products_price);
        dest.writeString(products_base);
    }

    public static final Parcelable.Creator<Products> CREATOR=new Creator<Products>() {
        @Override
        public Products createFromParcel(Parcel source) {
            return new Products(source);
        }

        @Override
        public Products[] newArray(int size) {
            return new Products[size];
        }
    };
}
