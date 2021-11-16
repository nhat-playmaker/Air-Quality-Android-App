package com.rushia.aqitracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StatisticFragment extends Fragment {

    ArrayList<DeviceData> arrayData;
    ViewPager2 mViewPager;
    VP_History_Adapter viewPagerAdapter;
    TabLayout tabLayout;
    private final String[] tabNames = new String[] {"AQI", "PM2.5", "CO2"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        Initialize(view);
        viewPagerAdapter = new VP_History_Adapter(getActivity().getSupportFragmentManager(), getLifecycle());
        arrayData = new ArrayList<>();

        mViewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, mViewPager, (((tab, position) -> tab.setText(tabNames[position])))).attach();

        return view;
    }

    private void Initialize(View v) {
        mViewPager = v.findViewById(R.id.viewPagerHistoricData);
        tabLayout = v.findViewById(R.id.tabLayout);
    }

}
