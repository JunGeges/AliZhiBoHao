package com.zmtmt.zhibohao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zmtmt.zhibohao.R;
import com.zmtmt.zhibohao.entity.Comment;
import com.zmtmt.zhibohao.entity.CommentContent;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/2.
 */
public class CommentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Comment> cList;

    public CommentAdapter(Context context, ArrayList<Comment> cList) {
        this.context = context;
        this.cList = cList;
    }

    @Override
    public int getCount() {
        return cList.size();
    }

    @Override
    public Object getItem(int i) {
        return cList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            vh = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_comment_layout, viewGroup, false);
            vh.iv_pop_comment_head = (ImageView) view.findViewById(R.id.iv_pop_comment_head);
            vh.tv_pop_comment_nickName = (TextView) view.findViewById(R.id.tv_pop_comment_nickName);
            vh.tv_pop_comment_content = (TextView) view.findViewById(R.id.tv_pop_comment_content);
            view.setTag(R.id.glide_tag, vh);
        } else {
            vh = (ViewHolder) view.getTag(R.id.glide_tag);
        }
        //获取评论对象
        Comment comment = cList.get(position);
        CommentContent comment_content = comment.getComment_content();//获取评论内容对象
        switch (comment.getCommenttype()) {
            case "1":
                vh.iv_pop_comment_head.setVisibility(View.VISIBLE);
                vh.tv_pop_comment_nickName.setVisibility(View.VISIBLE);
                //设置tag 避免加载图片的时候出现位置不对和加载多次的现象
                vh.iv_pop_comment_head.setTag(comment.getComment_head_url());
                //设置评论人的Img
                Glide.with(context).load(comment.getComment_head_url()).into(vh.iv_pop_comment_head);
                vh.tv_pop_comment_nickName.setText(comment.getComment_nick_name() + ":");//设置评论人的昵称
                vh.tv_pop_comment_nickName.setTextColor(Color.WHITE);
                vh.tv_pop_comment_content.setText(comment_content.getCommentContent());
                vh.tv_pop_comment_content.setTextColor(Color.WHITE);
                break;

            case "2":
                vh.iv_pop_comment_head.setVisibility(View.GONE);
                vh.tv_pop_comment_nickName.setVisibility(View.GONE);
                vh.tv_pop_comment_content.setText("推荐成功!您推荐的商品为:" + comment_content.getName());
                vh.tv_pop_comment_content.setTextColor(Color.WHITE);
                break;

            case "3":
                vh.iv_pop_comment_head.setVisibility(View.GONE);
                vh.tv_pop_comment_nickName.setVisibility(View.GONE);
                vh.tv_pop_comment_content.setText(comment.getComment_nick_name() + "给主播打赏了一个" + comment_content.getName());
                vh.tv_pop_comment_content.setTextColor(Color.WHITE);
                break;

            case "4":
                vh.iv_pop_comment_head.setVisibility(View.GONE);
                vh.tv_pop_comment_nickName.setVisibility(View.GONE);
                vh.tv_pop_comment_content.setText(comment.getComment_nick_name() + "购买了主播的" + comment_content.getName());
                vh.tv_pop_comment_content.setTextColor(Color.WHITE);
                break;

        }
        return view;
    }

    public class ViewHolder {
        public ImageView iv_pop_comment_head;
        public TextView tv_pop_comment_nickName, tv_pop_comment_content;
    }
}
