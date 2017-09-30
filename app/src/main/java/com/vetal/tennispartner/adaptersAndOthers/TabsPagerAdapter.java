package com.vetal.tennispartner.adaptersAndOthers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vetal.tennispartner.searchFragments.*;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;

    public TabsPagerAdapter( FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;


    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:

                return new ByProfileFragment();

            case 1:
                // SearchByLevel fragment activity
                return new ByLevelFragment();
            case 2:
                // SearchByLocation fragment activity
                return new ByLocationFragment();
            case 3:
                // SearchByName fragment activity
                return new ByNameFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return mNumOfTabs;
    }
}
