package com.rushia.aqitracker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Objects;

public class MyAirFragment extends Fragment {

    private final int MAX_HISTORIC_DATA = 15;

    public TextView
        textViewLocality,
        textViewCountry,
        textViewAQIValue,
        textViewStatus,
        textViewTemperatureValue,
        textViewHumidityValue,
        textViewLastUpdatedInfo,
        textViewRecommendationContent,
        textViewPM25Value,
        textViewPM10Value,
        textViewHCHOValue,
        textViewTVOCValue,
        textViewCO2Value;

    public RelativeLayout
        relativeLayoutFrame,
        relativeLayoutReport;

    public ImageView
        imageViewIconStatus,
        imageViewCycling,
        imageViewWindow,
        imageViewFaceMask,
        imageViewAirFilter,
        imageViewPM25Info,
        imageViewPM10Info,
        imageViewHCHOInfo,
        imageViewTVOCInfo,
        imageViewCO2Info;

    public FrameLayout
        frameLayoutBackground,
        frameLayoutIconBackground,
        frameLayoutCycling,
        frameLayoutWindow,
        frameLayoutFaceMask,
        frameLayoutAirFilter,
        frameLayoutRecommend;
    
    public LinearLayout linearLayoutLink;

    public String deviceID;

    private ArrayList<DeviceData> arrayData;

    private Handler handler = new Handler();
    Runnable runnable;
    final long delay = 10*1000;

    private int aqi;

    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_air, container, false);

        Initialize(view);

        sharedPreferences = this.requireActivity().getSharedPreferences("device-data", Context.MODE_PRIVATE);
        textViewAQIValue.setText(sharedPreferences.getString("aqi", "0"));
        textViewPM25Value.setText(sharedPreferences.getString("pm25", "0"));
        textViewPM10Value.setText(sharedPreferences.getString("pm10", "0"));
        textViewHCHOValue.setText(sharedPreferences.getString("hcho", "0"));
        textViewTVOCValue.setText(sharedPreferences.getString("tvoc", "0"));
        textViewCO2Value.setText(sharedPreferences.getString("co2", "0"));
        textViewTemperatureValue.setText(sharedPreferences.getString("temp", "0°C"));
        textViewHumidityValue.setText(sharedPreferences.getString("humidity", "0%"));
        textViewLastUpdatedInfo.setText(sharedPreferences.getString("time", "Last updated"));

        changeFrameColor(Integer.parseInt(textViewAQIValue.getText().toString()));


        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        deviceID = mainActivity.deviceID;

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                process();

                // get and save data
                editor.putString("aqi", textViewAQIValue.getText().toString());
                editor.putString("co2", textViewCO2Value.getText().toString());
                editor.putString("hcho", textViewHCHOValue.getText().toString());
                editor.putString("tvoc", textViewTVOCValue.getText().toString());
                editor.putString("pm25", textViewPM25Value.getText().toString());
                editor.putString("pm10", textViewPM10Value.getText().toString());
                editor.putString("temp", textViewTemperatureValue.getText().toString());
                editor.putString("humidity", textViewHumidityValue.getText().toString());
                editor.putString("time", textViewLastUpdatedInfo.getText().toString());
                editor.apply();

                handler.postDelayed(runnable, delay);
            }
        };

        runnable.run();

        imageViewPM25Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), pm25Activity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

        imageViewPM10Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), pm10Activity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

        imageViewCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_rcm_icon(0);
            }
        });

        imageViewWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_rcm_icon(1);
            }
        });

        imageViewFaceMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_rcm_icon(2);
            }
        });

        imageViewAirFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_rcm_icon(3);
            }
        });

        relativeLayoutReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    intent.setData(Uri.parse("package:" + getContext().getPackageName()));
//                    startActivity(intent);
//                }
//                catch (ActivityNotFoundException e) {
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    startActivity(intent);
//                }
                Intent intent = new Intent(getActivity(), SendMailActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

        return view;
    }

    private void process() {
        getDataFromDevice("https://aqitrackerabc.000webhostapp.com/json.php");

        if (arrayData.size() != 0) {
//            Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
            DeviceData deviceData = arrayData.get(0);

            textViewPM25Value.setText(deviceData.getPm25()+"");
            textViewPM10Value.setText(deviceData.getPm100()+"");
            textViewHCHOValue.setText(deviceData.getHcho()+"");
            textViewTVOCValue.setText(deviceData.getTvoc()+"");
            textViewCO2Value.setText(deviceData.getCo2()+"");
            textViewTemperatureValue.setText(deviceData.getTemperature()+"°C");
            textViewHumidityValue.setText(deviceData.getHumidity()+"%");

            String time = deviceData.getTime();
            int hour = Integer.parseInt(time.substring(0, 2));
            if (hour + 7 > 24) {
                hour = hour + 7 - 24;
            }
            else {
                hour += 7;
            }
            textViewLastUpdatedInfo.setText("Last updated " + String.valueOf(hour) + time.substring(2, time.length()));

            aqi = calcAQI(deviceData.getPm25());
            arrayData.get(0).setAqi(aqi);

            Bundle result = new Bundle();
            result.putInt("aqi", aqi);

            String JSONdata = new Gson().toJson(arrayData);
            result.putString("arrayData", JSONdata);

            getParentFragmentManager().setFragmentResult("dataFromMyAirFragment", result);
            
            textViewAQIValue.setText(aqi+"");
            changeFrameColor(aqi);
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

    private void getDataFromDevice(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int i = response.length() - 1;
                        int count_correct_id = 0;
                        arrayData = new ArrayList<>();
                        while (i >= 0 && count_correct_id <= MAX_HISTORIC_DATA) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                if (String.valueOf(deviceID).equals(object.getString("id"))) {
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
                                    count_correct_id++;
                                }
                                i--;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), (CharSequence) error, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    void process_rcm_icon(int icon) {
        if (icon == 0) {
            if (aqi <= 50) {
                frameLayoutCycling.setBackgroundResource(R.drawable.bg_good_rcm);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.good_rcm_cycling);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_good_rcm);
            }
            else if (aqi <= 100) {
                frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.moderate_rcm_cycling);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else if (aqi <= 150) {
                frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthyForSensitiveGroups_rcm_cycling);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else {
                frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthy_rcm_cycling);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
        }
        else if (icon == 1) {
            if (aqi <= 50) {
                frameLayoutWindow.setBackgroundResource(R.drawable.bg_good_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.good_rcm_window);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else if (aqi <= 100) {
                frameLayoutWindow.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.moderate_rcm_window);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else if (aqi <= 150) {
                frameLayoutWindow.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthyForSensitiveGroups_rcm_window);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else {
                frameLayoutWindow.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthy_rcm_window);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
        }
        else if (icon == 2) {
            if (aqi <= 150) {
                frameLayoutFaceMask.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthyForSensitiveGroups_rcm_faceMask);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else {
                frameLayoutFaceMask.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutAirFilter.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthy_rcm_faceMask);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
        }
        else {
            if (aqi <= 150) {
                frameLayoutAirFilter.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthyForSensitiveGroups_rcm_airFilter);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
            else {
                frameLayoutAirFilter.setBackgroundResource(R.drawable.bg_bad_rcm);
                frameLayoutCycling.setBackgroundColor(0xfff);
                frameLayoutWindow.setBackgroundColor(0xfff);
                frameLayoutFaceMask.setBackgroundColor(0xfff);
                textViewRecommendationContent.setText(R.string.unhealthy_rcm_airFilter);
                textViewRecommendationContent.setBackgroundResource(R.drawable.bg_bad_rcm);
            }
        }
    }
    
    private void Initialize(View view) {
        // TODO: init TextView component
        textViewLocality = view.findViewById(R.id.textViewLocality);
        textViewCountry = view.findViewById(R.id.textViewCountry);
        textViewAQIValue = view.findViewById(R.id.textViewAQIValue);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewTemperatureValue = view.findViewById(R.id.textViewTemperatureValue);
        textViewHumidityValue = view.findViewById(R.id.textViewHumidityValue);
        textViewLastUpdatedInfo = view.findViewById(R.id.textViewLastUpdatedInfo);
        textViewRecommendationContent = view.findViewById(R.id.textViewRecommendContent);
        textViewPM25Value = view.findViewById(R.id.textViewPM25Value);
        textViewPM10Value = view.findViewById(R.id.textViewPM10Value);
        textViewHCHOValue = view.findViewById(R.id.textViewHCHOValue);
        textViewTVOCValue = view.findViewById(R.id.textViewTVOCValue);
        textViewCO2Value = view.findViewById(R.id.textViewCO2Value);

        // TODO: init RelativeLayout component
        relativeLayoutFrame = view.findViewById(R.id.relativeLayoutFrame);
        relativeLayoutReport = view.findViewById(R.id.relativeLayoutReport);

        // TODO: init ImageView component
        imageViewIconStatus = view.findViewById(R.id.imageViewIconStatus);
        imageViewCycling = view.findViewById(R.id.imageViewCycling);
        imageViewWindow = view.findViewById(R.id.imageViewWindow);
        imageViewFaceMask = view.findViewById(R.id.imageViewFaceMask);
        imageViewAirFilter = view.findViewById(R.id.imageViewAirFilter);
        imageViewPM25Info = view.findViewById(R.id.imageViewPM25Info);
        imageViewPM10Info = view.findViewById(R.id.imageViewPM10Info);
        imageViewHCHOInfo = view.findViewById(R.id.imageViewHCHOInfo);
        imageViewTVOCInfo = view.findViewById(R.id.imageViewTVOCInfo);
        imageViewCO2Info = view.findViewById(R.id.imageViewCO2Info);

        // TODO: init FrameLayout component
        frameLayoutBackground = view.findViewById(R.id.frameLayoutBackground);
        frameLayoutIconBackground = view.findViewById(R.id.frameLayoutIconBackground);
        frameLayoutCycling = view.findViewById(R.id.frameLayoutCycling);
        frameLayoutWindow = view.findViewById(R.id.frameLayoutWindow);
        frameLayoutFaceMask = view.findViewById(R.id.frameLayoutFaceMask);
        frameLayoutAirFilter = view.findViewById(R.id.frameLayoutAirFilter);
        frameLayoutRecommend = view.findViewById(R.id.frameLayoutRecommend);

        // TODO: init LinearLayout component
        linearLayoutLink = view.findViewById(R.id.linearLayoutLink);

        // TODO: init some essential variables
        arrayData = new ArrayList<>();
    }

    private void changeFrameColor(int aqi_value) {
        if (aqi_value <= 50) {
            // Change background
            relativeLayoutFrame.setBackgroundResource(R.drawable.background_good);
            frameLayoutBackground.setBackgroundResource(R.drawable.frame_good);
            frameLayoutIconBackground.setBackgroundResource(R.drawable.icon_frame_good);
            imageViewIconStatus.setImageResource(R.drawable.good);
            textViewStatus.setText(R.string.status_good);

            frameLayoutCycling.setBackgroundResource(R.drawable.bg_good_rcm);

            frameLayoutWindow.setBackgroundColor(0xfff);

            imageViewFaceMask.setEnabled(false);
            imageViewFaceMask.setVisibility(View.INVISIBLE);
            frameLayoutFaceMask.setBackgroundColor(0xfff);
            frameLayoutFaceMask.setEnabled(false);

            imageViewAirFilter.setEnabled(false);
            imageViewAirFilter.setVisibility(View.INVISIBLE);
            frameLayoutAirFilter.setBackgroundColor(0xfff);
            frameLayoutAirFilter.setEnabled(false);

            textViewRecommendationContent.setText(R.string.good_rcm_cycling);
        }
        else if (aqi_value <= 100) {
            // Change background
            relativeLayoutFrame.setBackgroundResource(R.drawable.background_moderate);
            frameLayoutBackground.setBackgroundResource(R.drawable.frame_moderate);
            frameLayoutIconBackground.setBackgroundResource(R.drawable.icon_frame_moderate);
            imageViewIconStatus.setImageResource(R.drawable.moderate);
            textViewStatus.setText(R.string.status_moderate);

            frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);

            frameLayoutWindow.setBackgroundColor(0xfff);

            imageViewFaceMask.setEnabled(false);
            imageViewFaceMask.setVisibility(View.INVISIBLE);
            frameLayoutFaceMask.setBackgroundColor(0xfff);
            frameLayoutFaceMask.setEnabled(false);

            imageViewAirFilter.setEnabled(false);
            imageViewAirFilter.setVisibility(View.INVISIBLE);
            frameLayoutAirFilter.setBackgroundColor(0xfff);
            frameLayoutAirFilter.setEnabled(false);

            textViewRecommendationContent.setText(R.string.moderate_rcm_cycling);
        }
        else if (aqi_value <= 150) {
            // Change background
            relativeLayoutFrame.setBackgroundResource(R.drawable.background_unhealthyforsensitiveskin);
            frameLayoutBackground.setBackgroundResource(R.drawable.frame_unhealthyforsensitiveskin);
            frameLayoutIconBackground.setBackgroundResource(R.drawable.icon_frame_unhealthyforsensitiveskin);
            imageViewIconStatus.setImageResource(R.drawable.unhealthyforsensitive);
            textViewStatus.setText(R.string.status_unhealthyForSensitiveGroups);

            frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);

            frameLayoutWindow.setBackgroundColor(0xfff);

            imageViewFaceMask.setEnabled(true);
            imageViewFaceMask.setVisibility(View.VISIBLE);
            frameLayoutFaceMask.setBackgroundColor(0xfff);
            frameLayoutFaceMask.setEnabled(true);

            imageViewAirFilter.setEnabled(true);
            imageViewAirFilter.setVisibility(View.VISIBLE);
            frameLayoutAirFilter.setBackgroundColor(0xfff);
            frameLayoutAirFilter.setEnabled(true);

            textViewRecommendationContent.setText(R.string.unhealthyForSensitiveGroups_rcm_cycling);
        }
        else if (aqi_value <= 200) {
            // Change background
            relativeLayoutFrame.setBackgroundResource(R.drawable.background_unhealthy);
            frameLayoutBackground.setBackgroundResource(R.drawable.frame_unhealthy);
            frameLayoutIconBackground.setBackgroundResource(R.drawable.icon_frame_unhealthy);
            imageViewIconStatus.setImageResource(R.drawable.unhealthy);
            textViewStatus.setText(R.string.status_unhealthy);

            frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);

            frameLayoutWindow.setBackgroundColor(0xfff);

            imageViewFaceMask.setEnabled(true);
            imageViewFaceMask.setVisibility(View.VISIBLE);
            frameLayoutFaceMask.setBackgroundColor(0xfff);
            frameLayoutFaceMask.setEnabled(true);

            imageViewAirFilter.setEnabled(true);
            imageViewAirFilter.setVisibility(View.VISIBLE);
            frameLayoutAirFilter.setBackgroundColor(0xfff);
            frameLayoutAirFilter.setEnabled(true);

            textViewRecommendationContent.setText(R.string.unhealthy_rcm_cycling);
        }
        else if (aqi_value <= 300) {
            // Change background
            relativeLayoutFrame.setBackgroundResource(R.drawable.background_veryunhealthy);
            frameLayoutBackground.setBackgroundResource(R.drawable.frame_veryunhealthy);
            frameLayoutIconBackground.setBackgroundResource(R.drawable.icon_frame_veryunhealthy);
            imageViewIconStatus.setImageResource(R.drawable.veryunhealthy);
            textViewStatus.setText(R.string.status_veryUnhealthy);

            frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);

            frameLayoutWindow.setBackgroundColor(0xfff);

            imageViewFaceMask.setEnabled(true);
            imageViewFaceMask.setVisibility(View.VISIBLE);
            frameLayoutFaceMask.setBackgroundColor(0xfff);
            frameLayoutFaceMask.setEnabled(true);

            imageViewAirFilter.setEnabled(true);
            imageViewAirFilter.setVisibility(View.VISIBLE);
            frameLayoutAirFilter.setBackgroundColor(0xfff);
            frameLayoutAirFilter.setEnabled(true);

            textViewRecommendationContent.setText(R.string.unhealthy_rcm_cycling);
        }
        else {
            // Change background
            relativeLayoutFrame.setBackgroundResource(R.drawable.background_hazardous);
            frameLayoutBackground.setBackgroundResource(R.drawable.frame_hazardous);
            frameLayoutIconBackground.setBackgroundResource(R.drawable.icon_frame_hazardous);
            imageViewIconStatus.setImageResource(R.drawable.hazardous);
            textViewStatus.setText(R.string.status_hazardous);

            frameLayoutCycling.setBackgroundResource(R.drawable.bg_bad_rcm);

            frameLayoutWindow.setBackgroundColor(0xfff);

            imageViewFaceMask.setEnabled(true);
            imageViewFaceMask.setVisibility(View.VISIBLE);
            frameLayoutFaceMask.setBackgroundColor(0xfff);
            frameLayoutFaceMask.setEnabled(true);

            imageViewAirFilter.setEnabled(true);
            imageViewAirFilter.setVisibility(View.VISIBLE);
            frameLayoutAirFilter.setBackgroundColor(0xfff);
            frameLayoutAirFilter.setEnabled(true);

            textViewRecommendationContent.setText(R.string.unhealthy_rcm_cycling);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}
