package com.zmtmt.zhibohao.entity;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Comment {
    /**
     * 评论者的头像
     */
    private String comment_head_url;

    /**
     * 评论者的昵称
     */
    private String comment_nick_name;

    /**
     * 评论者的内容类
     */
    private CommentContent comment_content;

    /**
     * 评论楼层
     */
    private String comment_floor;

    /**
     * 评论时间
     */
    private String comment_time;

    /**
     * 评论类型 是正常评论 还是礼物信息
     */
    private String commenttype;

    /**
     * 是否是系统消息(1:否，2:是)
     */
    private String issystem;

    public String getComment_head_url() {
        return comment_head_url;
    }

    public void setComment_head_url(String comment_head_url) {
        this.comment_head_url = comment_head_url;
    }

    public String getComment_nick_name() {
        return comment_nick_name;
    }

    public void setComment_nick_name(String comment_nick_name) {
        this.comment_nick_name = comment_nick_name;
    }

    public CommentContent getComment_content() {
        return comment_content;
    }

    public void setComment_content(CommentContent comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_floor() {
        return comment_floor;
    }

    public void setComment_floor(String comment_floor) {
        this.comment_floor = comment_floor;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getCommenttype() {
        return commenttype;
    }

    public void setCommenttype(String commenttype) {
        this.commenttype = commenttype;
    }

    public String getIssystem() {
        return issystem;
    }

    public void setIssystem(String issystem) {
        this.issystem = issystem;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_head_url='" + comment_head_url + '\'' +
                ", comment_nick_name='" + comment_nick_name + '\'' +
                ", comment_content=" + comment_content +
                ", comment_floor='" + comment_floor + '\'' +
                ", comment_time='" + comment_time + '\'' +
                ", commenttype='" + commenttype + '\'' +
                ", issystem='"+issystem+'\''+
                '}';
    }
}
