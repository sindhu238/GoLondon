package com.srisindhusaride.golondon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @since  10/03/17.
 */

public class LineDetails_Fragment extends Fragment {

    private View mRootView;
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private final String TAG = "LineDetailsFrag";
    private Context mContext;
    List<String> mDirectionList;
    private String mSpinnerSelected;
    private static String mLineName;
    private static Multimap<String, String> mLineNamesMap = ArrayListMultimap.create();

    public static LineDetails_Fragment init(Multimap<String, String> map, String currentStation, String linenName) {
        LineDetails_Fragment fragment = new LineDetails_Fragment();
        mLineNamesMap = map;
        mLineName = linenName;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CardView cardView;
        TextView lineNameTV;

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.line_details_fragment, container, false);
            mSpinner = (Spinner) mRootView.findViewById(R.id.direction_spinner);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
            cardView = (CardView) mRootView.findViewById(R.id.cardView);
            cardView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.green_yellow_gradient));
            lineNameTV = (TextView) mRootView.findViewById(R.id.lineName);
            String lineNameTemp = mLineName + " line";
            lineNameTV.setText(lineNameTemp);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ViewGroup.LayoutParams layoutParams =  mRootView.getLayoutParams();
        layoutParams.height = metrics.heightPixels - metrics.heightPixels/3;
        mRootView.setLayoutParams(layoutParams);
        Log.v(TAG,"line nme fra" );
        //Get unique direction names
        Set<String> directionSet = new HashSet<>();
        for (String key: mLineNamesMap.keySet()) {
            Log.v(TAG,"line nme fra" + key);
            if (key.toLowerCase().contains(mLineName.toLowerCase())) {
                directionSet.add(key.split("::")[1]);

            }
        }

        //Copy data from set to list
        mDirectionList = new ArrayList<>();
        for (String set: directionSet) {
            String newString = set.replace("&harr;","-") +"     ";
            mDirectionList.add(newString);
            Log.v(TAG,"line nme fra" + newString);
        }
        if (mDirectionList.size()>0)
            mSpinnerSelected = mDirectionList.get(0);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mDirectionList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(spinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                mSpinnerSelected = adapterView.getItemAtPosition(pos).toString();
                mAdapter = new LineDetails_RecyclerViewAdapter(mLineNamesMap, mSpinnerSelected);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new LineDetails_RecyclerViewAdapter(mLineNamesMap, mSpinnerSelected);
        mRecyclerView.setAdapter(mAdapter);

        return mRootView;
    }
}
