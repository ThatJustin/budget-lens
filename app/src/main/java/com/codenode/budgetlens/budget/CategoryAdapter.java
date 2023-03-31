package com.codenode.budgetlens.budget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.codenode.budgetlens.R;
import com.codenode.budgetlens.data.Trend;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Trend> trendList;

    String response;
    public CategoryAdapter(Context context, String response, List<Trend> trendList) {
        mContext = context;
        this.response = response;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup
            parent) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            Trend trend = trendList.get(position);
            gridView = inflater.inflate(R.layout.grid_category_item, null);
            // set value into textview
            TextView title = (TextView)
                    gridView.findViewById(R.id.grid_title);
            if (TextUtils.isEmpty(response)){
                title.setText(trend.getName());
            }else {
                title.setText(trend.getName());
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject names=new JSONObject(object.getString(trend.getName()));
                    String frequency=names.getString("category_frequency");
                    title.setText(String.format("%s  \n\nFrequency:%s", trend.getName(), frequency));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ImageView icon = (ImageView)
                    gridView.findViewById(R.id.grid_icon);
            icon.setImageResource(trend.getIcon());
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
}