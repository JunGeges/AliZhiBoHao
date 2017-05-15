package com.zmtmt.zhibohao.tools;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zmtmt.zhibohao.R;
import com.zmtmt.zhibohao.entity.Products;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/8.
 */
public class ProductsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Products> pList;
    private String url;
    private Map<String, String> params;

    public ProductsAdapter(ArrayList<Products> pList, Context context, String url, Map<String, String> params) {
        this.pList = pList;
        this.context = context;
        this.url = url;
        this.params = params;
    }

    @Override
    public int getCount() {
        return pList.size();
    }

    @Override
    public Object getItem(int position) {
        return pList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_products_layout, null);
            vh.iv_pop_products_img = (ImageView) convertView.findViewById(R.id.iv_pop_products_img);
            vh.tv_pop_products_name = (TextView) convertView.findViewById(R.id.tv_pop_products_name);
            vh.tv_pop_products_price = (TextView) convertView.findViewById(R.id.tv_pop_products_price);
            vh.btn_pop_products_isRecommend = (Button) convertView.findViewById(R.id.btn_pop_products_isRecommend);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Products products = pList.get(position);
        Glide.with(context).load(products.getProducts_icon()).into(vh.iv_pop_products_img);
        vh.tv_pop_products_price.setText("RMB:" + products.getProducts_price());
        vh.tv_pop_products_name.setText(products.getProducts_name());
        if (products.isClick()) {
            vh.btn_pop_products_isRecommend.setText("已推荐");
            vh.btn_pop_products_isRecommend.setBackgroundColor(Color.parseColor("#F68912"));
        } else {
            vh.btn_pop_products_isRecommend.setText("点击推荐");
            vh.btn_pop_products_isRecommend.setBackgroundColor(Color.parseColor("#60C8f5"));
        }
        vh.btn_pop_products_isRecommend.setTag(position);

        //给推荐按钮设置点击事件
        vh.btn_pop_products_isRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//liveid op products_id  参数  post(eventUrl + "applogin", param);
                int index = Integer.parseInt(v.getTag().toString());
                params.put("goodid", pList.get(index).getProducts_id());
                if (!pList.get(index).isClick()) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            String post = HttpUtils.post(url, params);
                        }
                    }.start();
                    vh.btn_pop_products_isRecommend.setText("已推荐");
                    vh.btn_pop_products_isRecommend.setBackgroundColor(Color.parseColor("#F68912"));
                } else {
                    Toast.makeText(context, "商品已推荐，无需多次推荐", Toast.LENGTH_SHORT).show();
                }
                pList.get(index).setClick(true);
            }
        });
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_pop_products_img;
        public TextView tv_pop_products_name, tv_pop_products_price;
        public Button btn_pop_products_isRecommend;
    }
}
