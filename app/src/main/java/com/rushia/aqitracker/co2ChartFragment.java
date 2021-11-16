package com.rushia.aqitracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class co2ChartFragment extends Fragment {

    ArrayList<DeviceData> arrayData;

    private List<Integer> DataCO2;
    private ArrayList<BarEntry> barEntriesGood;
    private ArrayList<BarEntry> barEntriesBad;

    BarChart barChartCO2;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_co2_chart, container, false);

        barChartCO2 = view.findViewById(R.id.barChartCO2);

        arrayData       = new ArrayList<>();
        DataCO2        = new ArrayList<>();
        barEntriesGood  = new ArrayList<>();
        barEntriesBad   = new ArrayList<>();

        sharedPreferences = this.requireActivity().getSharedPreferences("CO2-saved-data", Context.MODE_PRIVATE);
        getSavedData();

        getParentFragmentManager().setFragmentResultListener("dataFromMyAirFragmentToCO2", this, new FragmentResultListener() {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DataCO2 = getDataCO2();
                getDataBarEntries();

                DrawChart();

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DataCO2 = getDataCO2();
        getDataBarEntries();

        DrawChart();

    }

    private void DrawChart() {
        BarDataSet barDataSetGood = new BarDataSet(barEntriesGood, "Good");
        barDataSetGood.setColor(getResources().getColor(R.color.good_level, getActivity().getTheme()));

        BarDataSet barDataSetBad = new BarDataSet(barEntriesBad, "Bad");
        barDataSetBad.setColor(getResources().getColor(R.color.unhealthy_level, getActivity().getTheme()));


        BarData barData = new BarData(
                barDataSetGood,
                barDataSetBad
        );
        barData.setBarWidth(0.9f);
        barData.setValueTextSize(10f);

        barChartCO2.setData(barData);
        barChartCO2.setFitBars(false);
        barChartCO2.getDescription().setEnabled(false);
        barChartCO2.getAxisRight().setEnabled(false);
        barChartCO2.animateY(1000);

        XAxis xAxis = barChartCO2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private List<Integer> getDataCO2() {

        List<Integer> list = new ArrayList<>();
        for (int i = arrayData.size() - 1; i >= 0; i--) {
            list.add(arrayData.get(i).getCo2());
        }
        return list;
    }

    private void getDataBarEntries() {

        for (int i = 0; i < DataCO2.size(); i++) {
            int pm25_val = DataCO2.get(i);
            if (pm25_val <= 1000) {
                barEntriesGood.add(new BarEntry(i, pm25_val));
            }
            else {
                barEntriesBad.add(new BarEntry(i, pm25_val));
            }
        }
    }
}