package com.codenode.budgetlens.utils;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codenode.budgetlens.R;
import com.codenode.budgetlens.adapter.TypeAdapter;
import com.lxj.xpopup.core.AttachPopupView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class CustomBubbleAttachPopup extends AttachPopupView {
    RecyclerView recyclerview;
    TypeAdapter mAdapter;
    int screenWidth=600;
    int type=0;
    List<String> list = new ArrayList<>();
    public CustomBubbleAttachPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Log.e("aaa", "onEv1----11ent: "+list.size() );
        LinearLayout mainLayout =  findViewById(R.id.mainlayout);
        ViewGroup.LayoutParams lp = mainLayout.getLayoutParams();
        lp.width = screenWidth;
        mainLayout.setLayoutParams(lp);
        recyclerview = findViewById(R.id.recyclerview);
        mAdapter = new TypeAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.setNewInstance(list);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Message message=new Message();
            message.arg1=type;
            message.arg2=position+1;
            message.obj=mAdapter.getItem(position);
            EventBus.getDefault().post(message);
        });

    }

    public void addData(List<String> list,int type,int screenWidth) {
        this.screenWidth = screenWidth;
        this.list = list;
        this.type = type;
    }

}
