package com.zmtmt.zhibohao.tools;

import android.graphics.Bitmap;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.zmtmt.zhibohao.MyApplication;
import com.zmtmt.zhibohao.entity.ShareInfo;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/1/10.
 */

public class ShareUtils {
    //分享
    public static void shareToWX(final ShareInfo shareInfo, final int scene) {
        //构建一个WXWebpageObject对象，用于封装要分享的链接Url地址
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareInfo.getLink();

        //构建一个WXMediaMessage对象，用于封装分享链接Url信息的标题与描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareInfo.getTitle();
        msg.description = shareInfo.getDesc();

        //构建一个微信请求
        SendMessageToWX.Req reqs = new SendMessageToWX.Req();
        reqs.message = msg;
        reqs.transaction = buildTransaction("webpage");
        reqs.scene = scene == 1 ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        MyApplication.api.sendReq(reqs);
    }

    //设置请求的唯一标识 区分其他的请求
    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    //将位图转换为字节数组
    public static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
