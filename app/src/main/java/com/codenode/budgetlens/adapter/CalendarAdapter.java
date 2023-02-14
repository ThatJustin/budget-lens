package com.codenode.budgetlens.adapter;

import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.CalendarBean;
import java.math.BigDecimal;

public class CalendarAdapter extends BaseQuickAdapter<CalendarBean.DataDTO, BaseViewHolder> {

    public CalendarAdapter() {
        super(R.layout.item_calendar);
        addChildClickViewIds(R.id.tv_split);
    }

    @Override
    protected void convert(BaseViewHolder helper, CalendarBean.DataDTO bean) {
        helper.setText(R.id.name, bean.getItem_name());
        helper.setText(R.id.price,"$"+bean.getItem_price());
        helper.setVisible(R.id.ybprice,bean.getSplititem().size()>0);
        if (bean.getSplititem().size()>0){
            String num=divides(bean.getItem_price(),(bean.getSplititem().size()+1)+"",2);
            helper.setText(R.id.ybprice,"Your bill:"+num);

        }
        CalendarItemAdapter itemAdapter=new CalendarItemAdapter();
        RecyclerView recyclerView=helper.getView(R.id.recyclerview);

        recyclerView.setAdapter(itemAdapter);
        itemAdapter.setNewInstance(bean.getSplititem());
    }
    String divides(String v1, String v2, int scale) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale).toString();
    }
}