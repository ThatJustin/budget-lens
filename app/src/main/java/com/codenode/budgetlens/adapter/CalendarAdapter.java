package com.codenode.budgetlens.adapter;

import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.CalendarBean;

public class CalendarAdapter extends BaseQuickAdapter<CalendarBean.DataDTO, BaseViewHolder> {

    public CalendarAdapter() {
        super(R.layout.item_calendar);
        addChildClickViewIds(R.id.tv_split);
    }

    @Override
    protected void convert(BaseViewHolder helper, CalendarBean.DataDTO bean) {
        helper.setText(R.id.name, bean.getItem_name());
        helper.setText(R.id.price,"$"+bean.getItem_price());
        CalendarItemAdapter itemAdapter=new CalendarItemAdapter();
        RecyclerView recyclerView=helper.getView(R.id.recyclerview);

        recyclerView.setAdapter(itemAdapter);
        itemAdapter.setNewInstance(bean.getSplititem());
    }

}