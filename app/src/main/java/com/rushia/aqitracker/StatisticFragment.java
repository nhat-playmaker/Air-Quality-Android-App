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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StatisticFragment extends Fragment {

    TextView textView;
    ArrayList<DeviceData> arrayData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        textView = view.findViewById(R.id.test);
        arrayData = new ArrayList<>();

        getParentFragmentManager().setFragmentResultListener("dataFromMyAirFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String json = result.getString("arrayData");

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
                        arrayData.get(arrayData.size() - 1).setAqi(object.getInt("aqi"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        return view;
    }
}
