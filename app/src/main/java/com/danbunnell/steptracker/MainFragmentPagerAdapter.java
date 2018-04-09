package com.danbunnell.steptracker;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

final class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public MainFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MainFragment();
        } else {
            return new DebugFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return this.context.getString(R.string.main_fragment_title);
            case 1:
                return this.context.getString(R.string.debug_fragment_title);
            default:
                return null;
        }
    }
}
