package com.rushia.aqitracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    TextView textViewSettingsAQIIndex;
    TextView textViewSelectAQIIndex;
    int chooseAQIIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Initialize(view);

        ChooseIndexActivity chooseIndexActivity = (ChooseIndexActivity) getActivity();
        assert chooseIndexActivity != null;
        chooseAQIIndex = chooseIndexActivity.chooseID;

        if (chooseAQIIndex == 0) {
            textViewSelectAQIIndex.setText("VN AQI");
        }
        else if (chooseAQIIndex == 1) {
            textViewSelectAQIIndex.setText("US AQI");
        }
        else {
            textViewSelectAQIIndex.setText("CN AQI");
        }

        textViewSettingsAQIIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseIndexActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

        return view;
    }

    public void Initialize(View v) {

        textViewSettingsAQIIndex = v.findViewById(R.id.textViewSettingsAQIIndex);
        textViewSelectAQIIndex = v.findViewById(R.id.textViewSelectAQIIndex);

    }
}
