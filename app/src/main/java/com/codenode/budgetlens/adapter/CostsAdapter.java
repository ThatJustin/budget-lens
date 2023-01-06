package com.codenode.budgetlens.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.Costs;

public class CostsAdapter extends BaseQuickAdapter<Costs.CostsBean, BaseViewHolder> {
    public String[] arcColors = new String[]{"#78CDFF", "#7E68FF", "#DC4175", "#00C69F", "#FFC300","#BAAEFE","#588035","#4B0082","#4682B4","#2F4F4F"};

    public CostsAdapter() {
        super(R.layout.item_coasts);
    }

    @Override
    protected void convert(BaseViewHolder helper, Costs.CostsBean str) {

        helper.setText(R.id.name, str.getCategory_name());
        if (getItemPosition(str)<10){
            helper.setBackgroundColor(R.id.circleview2,Color.parseColor(arcColors[getItemPosition(str)]));
        }else {
            helper.setBackgroundColor(R.id.circleview2,Color.parseColor(arcColors[0]));
        }


    }

}