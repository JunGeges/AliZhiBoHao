package com.zmtmt.zhibohao.entity;

/**
 * Created by Administrator on 2016/8/4.
 */
public class LiveRoomInfo {
    private String uId;
    private String acId;
    private String openId;
    private String nickName;
    private String roomImgUrl;
    private String eventUrl;
    private String memberlevelid;
    /**
     * 推流地址
     */
    private String pushUrl;

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getAcId() {
        return acId;
    }

    public void setAcId(String acId) {
        this.acId = acId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRoomImgUrl() {
        return roomImgUrl;
    }

    public void setRoomImgUrl(String roomImgUrl) {
        this.roomImgUrl = roomImgUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getMemberlevelid() {
        return memberlevelid;
    }

    public void setMemberlevelid(String memberlevelid) {
        this.memberlevelid = memberlevelid;
    }

    @Override
    public String toString() {
        return "LiveRoomInfo{" +
                "uId='" + uId + '\'' +
                ", acId='" + acId + '\'' +
                ", openId='" + openId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", roomImgUrl='" + roomImgUrl + '\'' +
                ", eventUrl='" + eventUrl + '\'' +
                ", memberlevelid='" + memberlevelid + '\'' +
                ", pushUrl='" + pushUrl + '\'' +
                '}';
    }
}
