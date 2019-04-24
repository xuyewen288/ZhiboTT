package com.xunye.zhibott.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xunye.zhibott.MyApplication;
import com.xunye.zhibott.R;
import com.xunye.zhibott.bean.HotGoods;
import com.xunye.zhibott.helper.GlideUtils;

import java.util.List;

/**
 * Created by 高磊华
 * Time  2017/8/9
 * Dscribe: 分类 二级菜单 适配器
 */

public class SecondGoodsAdapter extends BaseQuickAdapter<HotGoods.ListBean, BaseViewHolder> {

    private Context mContext;

    public SecondGoodsAdapter(Context context,List<HotGoods.ListBean> datas) {
        super(R.layout.template_category_wares, datas);
        this.mContext=context;
    }

    @Override
    protected void convert(BaseViewHolder holder, HotGoods.ListBean bean) {
        holder.setText(R.id.text_title, bean.getName())
                .setText(R.id.text_price, "￥" + bean.getPrice());
        GlideUtils.load(mContext, bean.getImgUrl(), (ImageView) holder
                .getView(R.id.iv_view));
    }
}
