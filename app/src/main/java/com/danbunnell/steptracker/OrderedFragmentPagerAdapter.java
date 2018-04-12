package com.danbunnell.steptracker;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Adapter that handles paging between ordered fragments
 */
public class OrderedFragmentPagerAdapter extends FragmentPagerAdapter {

    /**
     * the application context
     */
    private Context context;

    /**
     * the ordered fragments
     */
    private ArrayList<Fragment> orderedFragments;

    /**
     * the ordered fragment titles
     */
    private ArrayList<String> orderedTitles;

    /**
     * Initializes a new instance of the {@link OrderedFragmentPagerAdapter} class.
     *
     * @param context the application context
     * @param fm      a fragment manager
     */
    public OrderedFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.orderedFragments = new ArrayList<>();
        this.orderedTitles = new ArrayList<>();
    }

    /**
     * Adds another fragment.
     *
     * @param fragment a fragment
     * @param title    a title for the fragment
     */
    public void addFragment(Fragment fragment, String title) {
        this.orderedFragments.add(fragment);
        this.orderedTitles.add(title);
    }

    /**
     * Determines the fragment for each tab.
     *
     * @param position the tab position
     * @return         the fragment to display
     */
    @Override
    public Fragment getItem(int position) {
        return this.orderedFragments.get(position);
    }

    /**
     * Returns the tab count.
     *
     * @return the tab count
     */
    @Override
    public int getCount() {
        return this.orderedFragments.size();
    }

    /**
     * Returns the title for a given tab position.
     *
     * @param position a tab position
     * @return         a page title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return this.orderedTitles.get(position);
    }
}
