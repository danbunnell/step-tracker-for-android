package com.danbunnell.steptracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    /**
     * Required empty public constructor
     */
    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * This method is called when the view is created.
     *
     * @param inflater           inflates the layout
     * @param container          the layout container
     * @param savedInstanceState the saved state data
     * @return                   an inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

}
