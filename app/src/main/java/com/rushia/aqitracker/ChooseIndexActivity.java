package com.rushia.aqitracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChooseIndexActivity extends AppCompatActivity {

    Toolbar toolbar;
    RadioGroup radioGroupAQIIndex;
    RadioButton radioButtonVNAQI, radioButtonUSAQI, radioButtonCNAQI;
    public int chooseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_index);

        toolbar = findViewById(R.id.toolbarAQIIndex);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseIndexActivity.this.finish();
            }
        });

        radioGroupAQIIndex = findViewById(R.id.radioGroupAQIIndex);
        radioButtonVNAQI = findViewById(R.id.radioButtonVNAQI);
        radioButtonUSAQI = findViewById(R.id.radioButtonUSAQI);
        radioButtonCNAQI = findViewById(R.id.radioButtonCNAQI);

        radioGroupAQIIndex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonVNAQI:
                        chooseID = 0;
                        break;
                    case R.id.radioButtonUSAQI:
                        chooseID = 1;
                        break;
                    case R.id.radioButtonCNAQI:
                        chooseID = 2;
                        break;
                }
            }
        });

    }
}