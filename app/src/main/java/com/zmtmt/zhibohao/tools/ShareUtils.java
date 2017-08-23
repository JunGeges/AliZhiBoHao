package com.zmtmt.zhibohao.tools;

import android.graphics.Bitmap;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.zmtmt.zhibohao.app.MyApplication;
import com.zmtmt.zhibohao.entity.ShareInfo;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/1/10.
 */

public class ShareUtils {
    //分享
    public static void shareToWX(final ShareInfo shareInfo, final int scene, Bitmap bitmap) {
        //构建一个WXWebpageObject对象，用于封装要分享的链接Url地址
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareInfo.getLink();

        //构建一个WXMediaMessage对象，用于封装分享链接Url信息的标题与描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareInfo.getTitle();
        msg.description = shareInfo.getDesc();
        msg.thumbData = bitmap2Bytes(bitmap, 30);

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

    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        if (bitmap != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            int options = 100;
            while (output.toByteArray().length > maxkb && options != 10) {
                output.reset(); //清空output
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
                options -= 10;
            }
            return output.toByteArray();
        }
        return null;
    }
}
