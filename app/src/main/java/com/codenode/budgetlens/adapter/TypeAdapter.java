package com.codenode.budgetlens.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.codenode.budgetlens.R;

public class TypeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TypeAdapter() {
        super(R.layout.item_text);
    }

    @Override
    protected void convert(BaseViewHolder helper, String str) {
        helper.setText(R.id.text, str);
        helper.setGone(R.id.iv_add,!TextUtils.equals(str,"add"));

    }

}