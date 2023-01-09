package com.codenode.budgetlens.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.Trend;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<Trend> trendList;

    public CategoryAdapter(Context context, List<Trend> trendList) {
        mContext = context;
        this.trendList = trendList;
    }

    @Override
    public int getCount() {
        return trendList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup
            parent) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = new View(mContext);
            Trend trend = trendList.get(position);
            gridView = inflater.inflate(R.layout.grid_category_item, null);
            // set value into textview
            TextView title = (TextView)
                    gridView.findViewById(R.id.grid_title);
            title.setText(trend.getName());

            ImageView icon = (ImageView)
                    gridView.findViewById(R.id.grid_icon);
            icon.setImageResource(trend.getIcon());
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
}
