package com.codenode.budgetlens.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.CalendarBean;

public class CalendarItemAdapter extends BaseQuickAdapter<CalendarBean.DataDTO.SplititemDTO, BaseViewHolder> {

    public CalendarItemAdapter() {
        super(R.layout.item_user);
    }

    @Override
    protected void convert(BaseViewHolder helper, CalendarBean.DataDTO.SplititemDTO bean) {
        helper.setText(R.id.name, bean.getShared_user());

    }

}