package com.example.yy.dashgraduationdesign.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yy.dashgraduationdesign.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DemoActivity extends Activity {

    @BindView(R.id.demo_list)
    ListView demoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_list);
        ButterKnife.bind(this);

        List<Data> list = new ArrayList<>();
        list.add(new Data.Builder().setIp("192.168.49.1").setNetmask("255.255.255.0").setMacAddr("ee:1d:7f:e6:02:1b").create());
        list.add(new Data.Builder().setIp("192.168.49.94").setNetmask("255.255.255.0").setMacAddr("8e:3a:e3:42:7b:c6").create());
        list.add(new Data.Builder().setIp("192.168.49.104").setNetmask("255.255.255.0").setMacAddr("00:8e:ac:2e:73:c0").create());
        demoList.setAdapter(new DemoAdapter(list, DemoActivity.this));
    }


    class DemoAdapter extends BaseAdapter {
        private List<Data> list;
        private Context context;

        public DemoAdapter(List<Data> list, Context context) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.demo_list_item, null);
            ViewHolder holder = new ViewHolder(view);
            Data data = list.get(position);
            holder.ipTextview.setText(data.ip);
            holder.netmaskTextview.setText(data.netMask);
            holder.netmaskTextview.setText(data.macAddr);
            if (position>0)
            holder.demoListTitle.setText("client");
            return view;
        }

         class ViewHolder {
            @BindView(R.id.ip_textview)
            TextView ipTextview;
            @BindView(R.id.netmask_textview)
            TextView netmaskTextview;
            @BindView(R.id.mac_textview)
            TextView macTextview;
             @BindView(R.id.demo_list_title)
             TextView demoListTitle;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }
}
