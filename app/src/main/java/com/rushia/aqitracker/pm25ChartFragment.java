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

public class pm25ChartFragment extends Fragment {

    ArrayList<DeviceData> arrayData;

    private List<Integer> DataPM25;
    private ArrayList<BarEntry> barEntriesGood;
    private ArrayList<BarEntry> barEntriesBad;

    BarChart barChartPM25;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pm25_chart, container, false);

        barChartPM25 = view.findViewById(R.id.barChartPM25);

        arrayData       = new ArrayList<>();
        DataPM25        = new ArrayList<>();
        barEntriesGood  = new ArrayList<>();
        barEntriesBad   = new ArrayList<>();

        sharedPreferences = this.requireActivity().getSharedPreferences("PM25-saved-data", Context.MODE_PRIVATE);
        getSavedData();

        getParentFragmentManager().setFragmentResultListener("dataFromMyAirFragmentToPM25", this, new FragmentResultListener() {
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

                DataPM25 = getDataPM25();
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

        DataPM25 = getDataPM25();
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

        barChartPM25.setData(barData);
        barChartPM25.setFitBars(false);
        barChartPM25.getDescription().setEnabled(false);
        barChartPM25.getAxisRight().setEnabled(false);
        barChartPM25.animateY(1000);

        XAxis xAxis = barChartPM25.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private List<Integer> getDataPM25() {

        List<Integer> list = new ArrayList<>();
        for (int i = arrayData.size() - 1; i >= 0; i--) {
            list.add(arrayData.get(i).getPm25());
        }
        return list;
    }

    private void getDataBarEntries() {

        for (int i = 0; i < DataPM25.size(); i++) {
            int pm25_val = DataPM25.get(i);
            if (pm25_val <= 100) {
                barEntriesGood.add(new BarEntry(i, pm25_val));
            }
            else {
                barEntriesBad.add(new BarEntry(i, pm25_val));
            }
        }
    }

}