package com.rushia.aqitracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class aqiChartFragment extends Fragment {

    ArrayList<DeviceData> arrayData;

    private List<Integer> DataAQI;
    private ArrayList<BarEntry> barEntriesGood;
    private ArrayList<BarEntry> barEntriesModerate;
    private ArrayList<BarEntry> barEntriesUnhealthyForSensitiveGroups;
    private ArrayList<BarEntry> barEntriesUnhealthy;
    private ArrayList<BarEntry> barEntriesVeryUnhealthy;
    private ArrayList<BarEntry> barEntriesHazardous;

    BarChart barChartAQI;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aqi_chart, container, false);

        Initialize(view);

        sharedPreferences = this.requireActivity().getSharedPreferences("AQI-saved-data", Context.MODE_PRIVATE);
        getSavedData();

        getParentFragmentManager().setFragmentResultListener("dataFromMyAirFragmentToAQI", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String json = result.getString("arrayData");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("jsonData", json);
                editor.apply();

                arrayData = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(json);
                    int numberOfData = jsonArray.length();
                    for (int i = 0; i < numberOfData; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        arrayData.add(new DeviceData(
                                object.getInt("id"),
                                object.getInt("co2"),
                                object.getInt("hcho"),
                                object.getInt("tvoc"),
                                object.getInt("pm25"),
                                object.getInt("pm100"),
                                object.getDouble("temperature"),
                                object.getDouble("humidity"),
                                object.getString("date"),
                                object.getString("time")
                        ));
                        arrayData.get(arrayData.size() - 1).setAqi(calcAQI(object.getInt("pm25")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DataAQI = getDataAQI();
                getDataBarEntries();

                DrawChart();

//                Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    private void getSavedData() {
        String json = sharedPreferences.getString("jsonData", "[]");
//        Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();

        try {
            JSONArray jsonArray = new JSONArray(json);
            int numberOfData = jsonArray.length();
            for (int i = 0; i < numberOfData; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                arrayData.add(new DeviceData(
                        object.getInt("id"),
                        object.getInt("co2"),
                        object.getInt("hcho"),
                        object.getInt("tvoc"),
                        object.getInt("pm25"),
                        object.getInt("pm100"),
                        object.getDouble("temperature"),
                        object.getDouble("humidity"),
                        object.getString("date"),
                        object.getString("time")
                ));
                arrayData.get(arrayData.size() - 1).setAqi(calcAQI(object.getInt("pm25")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DataAQI = getDataAQI();
        getDataBarEntries();

        DrawChart();

    }

    private void DrawChart() {
        BarDataSet barDataSetGood = new BarDataSet(barEntriesGood, "Good");
        barDataSetGood.setColors(getResources().getColor(R.color.good_level, getActivity().getTheme()));

        BarDataSet barDataSetModerate = new BarDataSet(barEntriesModerate, "Moderate");
        barDataSetModerate.setColors(getResources().getColor(R.color.moderate_level, getActivity().getTheme()));

        BarDataSet barDataSetUnhealthyForSensitiveGroups = new BarDataSet(barEntriesUnhealthyForSensitiveGroups, "Unhealthy For Sensitive Groups");
        barDataSetUnhealthyForSensitiveGroups.setColors(getResources().getColor(R.color.unhealthyForSensitive_level, getActivity().getTheme()));

        BarDataSet barDataSetUnhealthy = new BarDataSet(barEntriesUnhealthy, "Unhealthy");
        barDataSetUnhealthy.setColors(getResources().getColor(R.color.unhealthy_level, getActivity().getTheme()));

        BarDataSet barDataSetVeryUnhealthy = new BarDataSet(barEntriesVeryUnhealthy, "Very Unhealthy");
        barDataSetVeryUnhealthy.setColors(getResources().getColor(R.color.veryUnhealthy_level, getActivity().getTheme()));

        BarDataSet barDataSetHazardous = new BarDataSet(barEntriesHazardous, "Hazardous");
        barDataSetHazardous.setColors(getResources().getColor(R.color.hazardous_level, getActivity().getTheme()));

        BarData barData = new BarData(
                barDataSetGood,
                barDataSetModerate,
                barDataSetUnhealthyForSensitiveGroups,
                barDataSetUnhealthy,
                barDataSetVeryUnhealthy,
                barDataSetHazardous);
        barData.setBarWidth(0.9f);
        barData.setValueTextSize(10f);

        barChartAQI.setData(barData);
        barChartAQI.setFitBars(false);
        barChartAQI.getDescription().setEnabled(false);
        barChartAQI.getAxisRight().setEnabled(false);
        barChartAQI.animateY(1000);

        XAxis xAxis = barChartAQI.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void Initialize(View v) {
        barChartAQI = v.findViewById(R.id.barChartAQI);

        arrayData                               = new ArrayList<>();
        DataAQI                                 = new ArrayList<>();
        barEntriesGood                          = new ArrayList<>();
        barEntriesModerate                      = new ArrayList<>();
        barEntriesUnhealthyForSensitiveGroups   = new ArrayList<>();
        barEntriesUnhealthy                     = new ArrayList<>();
        barEntriesVeryUnhealthy                 = new ArrayList<>();
        barEntriesHazardous                     = new ArrayList<>();
    }

    private List<Integer> getDataAQI() {

        List<Integer> list = new ArrayList<>();
        for (int i = arrayData.size() - 1; i >= 0; i--) {
            list.add(arrayData.get(i).getAqi());
        }
        return list;
    }

    private void getDataBarEntries() {

        for (int i = 0; i < DataAQI.size(); i++) {
            int val = DataAQI.get(i);
            if (val <= 50) {
                barEntriesGood.add(new BarEntry(i, val));
            }
            else if (val <= 100) {
                barEntriesModerate.add(new BarEntry(i, val));
            }
            else if (val <= 150) {
                barEntriesUnhealthyForSensitiveGroups.add(new BarEntry(i, val));
            }
            else if (val <= 200) {
                barEntriesUnhealthy.add(new BarEntry(i, val));
            }
            else if (val <= 300) {
                barEntriesVeryUnhealthy.add(new BarEntry(i, val));
            }
            else {
                barEntriesHazardous.add(new BarEntry(i, val));
            }
        }
    }

    private int calcAQI(int pm25) {
        double aqi_min, aqi_max, pm_min, pm_max, pm25_d, aqi;

        pm25_d = (double) pm25;

        if (pm25_d <= 25.0) {
            aqi_min = 0.0;
            aqi_max = 50.0;
            pm_min = 0.0;
            pm_max = 25.0;
        }
        else if (pm25_d <= 50.0) {
            aqi_min = 50.0;
            aqi_max = 100.0;
            pm_min = 25.0;
            pm_max = 50.0;
        }
        else if (pm25_d <= 80.0) {
            aqi_min = 100.0;
            aqi_max = 150.0;
            pm_min = 50.0;
            pm_max = 80.0;
        }
        else if (pm25_d <= 150.0) {
            aqi_min = 150.0;
            aqi_max = 200.0;
            pm_min = 50.0;
            pm_max = 150.0;
        }
        else if (pm25_d <= 250.0) {
            aqi_min = 200.0;
            aqi_max = 300.0;
            pm_min = 150.0;
            pm_max = 250.0;
        }
        else if (pm25_d <= 350.0) {
            aqi_min = 300.0;
            aqi_max = 400.0;
            pm_min = 250.0;
            pm_max = 350.0;
        }
        else {
            aqi_min = 400.0;
            aqi_max = 500.0;
            pm_min = 350.0;
            pm_max = 500.0;
        }

        aqi = (pm25_d - pm_min) * (aqi_max - aqi_min)/(pm_max - pm_min) + aqi_min;
        return (int) aqi;
    }
}