package com.intelligentSoftwares.Trichain.batterymanager;

import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Awesome Intelligent Softwares on 18/11/2020. Exactly
 */

public class MyAdapter extends BaseAdapter {

    List<ActivityManager.RunningAppProcessInfo> processes;


    Context context;

    public MyAdapter(List<ActivityManager.RunningAppProcessInfo>
                             processes, Context context) {
        this.context = context;
        this.processes = processes;
    }

    @Override
    public int getCount() {
        return processes.size();
    }

    @Override
    public Object getItem(int position) {
        return processes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return processes.get(position).pid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Process pro;

        if(convertView == null)
        {
            convertView = new TextView(context);
            pro = new Process();
            pro.name = (TextView)convertView;

            convertView.setTag(pro);
        }else
        {
            pro = (Process)convertView.getTag();
        }

        pro.name.setText(processes.get(position).processName);

        return convertView;
    }

    class Process
    {
        public TextView name;
    }
}