package com.rushia.aqitracker;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public static Fragment myAirFragment, statisticFragment, settingsFragment;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                myAirFragment = new MyAirFragment();
                return myAirFragment;
            case 1:
                statisticFragment = new StatisticFragment();
                return statisticFragment;
            case 2:
                settingsFragment = new SettingsFragment();
                return settingsFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
