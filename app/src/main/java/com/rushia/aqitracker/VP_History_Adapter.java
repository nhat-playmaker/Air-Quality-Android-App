package com.rushia.aqitracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VP_History_Adapter extends FragmentStateAdapter {
    public VP_History_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public VP_History_Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public VP_History_Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public static Fragment CO2ChartFragment, AQIChartFragment, PM25ChartFragment;
    private final String[] tabNames = new String[] {"AQI", "PM2.5", "CO2"};

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 2:
                CO2ChartFragment = new co2ChartFragment();
                return CO2ChartFragment;
            case 0:
                AQIChartFragment = new aqiChartFragment();
                return AQIChartFragment;
            case 1:
                PM25ChartFragment = new pm25ChartFragment();
                return PM25ChartFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return tabNames.length;
    }
}
