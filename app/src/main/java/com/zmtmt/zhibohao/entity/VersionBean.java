package com.zmtmt.zhibohao.entity;

/**
 * Created by Administrator on 2017/9/5.
 */

public class VersionBean {
    private int versionCode;

    private String url;

    private String desc;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "versionCode=" + versionCode +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
