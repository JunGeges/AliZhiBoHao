package com.zmtmt.zhibohao.entity;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/8/24.
 */
public class CommentContent {
    /**礼物的名字 或者商品的名字*/
    private String name;

    /**评论的内容*/
    private String commentContent;

    /**购买商品的图片  或者礼物的图片*/
    private Bitmap bitmap;

    /**购买商品的数量*/
    private String amount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CommentContent{" +
                "name='" + name + '\'' +
                ", commentContent='" + commentContent + '\'' +
                ", bitmap=" + bitmap +
                ", amount='" + amount + '\'' +
                '}';
    }
}
