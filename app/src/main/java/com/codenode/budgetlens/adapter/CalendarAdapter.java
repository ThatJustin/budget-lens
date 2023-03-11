package com.codenode.budgetlens.adapter;

import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.CalendarBean;
import com.codenode.budgetlens.data.ReceiptSplitItem;

public class CalendarAdapter extends BaseQuickAdapter<ReceiptSplitItem, BaseViewHolder> {

    public CalendarAdapter() {
        super(R.layout.item_calendar);
        addChildClickViewIds(R.id.tv_split);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReceiptSplitItem item) {
        helper.setText(R.id.name, item.getItem_name());
        helper.setText(R.id.price,"$"+item.getItem_price());
    }
}
