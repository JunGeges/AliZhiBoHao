package com.zmtmt.zhibohao.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/10/7.
 */

public class ShareInfo implements Parcelable{

    private String title;

    private String desc;

    private String link;

    private String imgUrl;

    private String liveId;

    public ShareInfo(){}

    public ShareInfo(Parcel parcel){
        this.title=parcel.readString();
        this.desc=parcel.readString();
        this.link=parcel.readString();
        this.imgUrl=parcel.readString();
        this.liveId=parcel.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLiveId(){return liveId;}

    public void setLiveId(String liveId){this.liveId=liveId;}

    @Override
    public String toString() {
        return "ShareInfo{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", link='" + link + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", liveId='" + liveId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(link);
        dest.writeString(imgUrl);
        dest.writeString(liveId);
    }

    public static final Parcelable.Creator<ShareInfo> CREATOR=new Creator<ShareInfo>(){

        @Override
        public ShareInfo createFromParcel(Parcel source) {
            return new ShareInfo(source);
        }

        @Override
        public ShareInfo[] newArray(int size) {
            return new ShareInfo[size];
        }
    };
}
