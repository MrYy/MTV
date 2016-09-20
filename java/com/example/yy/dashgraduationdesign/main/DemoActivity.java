package com.example.yy.dashgraduationdesign.main;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.yy.dashgraduationdesign.R;

import java.util.ArrayList;

public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        Spinner spinner = (Spinner) findViewById(R.id.Spinner_wifi_);
        ArrayList<String> list = new ArrayList();
        list.add("Ad-hoc");
        list.add("Push-based");
        list.add("Pull-based");
        spinner.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_item,list));
        spinner.setSelection(0);
    }
}
