package com.srisindhusaride.golondon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
import com.srisindhusaride.golondon.Utils.Constants;
import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
import com.tuyenmonkey.mkloader.MKLoader;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * @since 15/02/17.
 */

class Nearby_Stations_Adapter extends RecyclerView.Adapter {

    private ArrayList<ArrayList<StationArrivalPOJO>> mStationPojoDetailsList;
    private ArrayList<StationArrivalPOJO> mStationPojoList;
    private LinearLayout mLinearLayout;
    private MKLoader mProgressBar;

    private Context mContext;

    private static HashSet<String> isRemainingTimesOpen = new HashSet<>();
    private static final String TAG = "AdapterRecyclerView";
    private HashMap<String, JSONArray> mStationProperties;

    //Shared preference for saving home and office stations
    private SharedPreferences mPersonalSharedPreferences;

    //Shared preference for saving favorite stations
    private SharedPreferences mFavoriteSharedPreference;


    /**The name of the home station that is to be saved in {@link #mPersonalSharedPreferences}**/
    private String homeStationName;

    /**The name of the home station id that is to be saved in {@link #mPersonalSharedPreferences}**/
    private String homeStationID = "";

    private String workStationName;
    private String workStationID;

    private ViewHolder mHomeStationViewHolder;
    private ViewHolder mWorkStationViewholder;

    Nearby_Stations_Adapter(ArrayList<StationArrivalPOJO> data1, ArrayList<ArrayList<StationArrivalPOJO>> data) {
        mStationPojoDetailsList = data;
        mStationPojoList = data1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_station_adapter,parent,false);
        mContext = parent.getContext();

        mPersonalSharedPreferences = mContext.getSharedPreferences(Constants.personal_sp, Context.MODE_PRIVATE);
        mFavoriteSharedPreference = mContext.getSharedPreferences(Constants.favorite_sp, Context.MODE_PRIVATE);

        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
        TextView mStationName = (TextView) rootView.findViewById(R.id.stationName);
        mProgressBar = (MKLoader) rootView.findViewById(R.id.progressBar);
        mStationName.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        ImageView mHomeStation = (ImageView) rootView.findViewById(R.id.home_station);
        ImageView mWorkStation = (ImageView) rootView.findViewById(R.id.office_station);
        ImageView mFavoriteStation = (ImageView) rootView.findViewById(R.id.favorite_station);


        TextView timeToWalkToStation = (TextView) rootView.findViewById(R.id.timeToTravel);
        CardView cardView = (CardView) rootView.findViewById(R.id.cardView);

        return new ViewHolder(rootView, mStationName, timeToWalkToStation, cardView
                , mHomeStation, mWorkStation, mFavoriteStation);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        try {
            if (mStationPojoList.size() > 0){

                ((ViewHolder)holder).stationNameTV.setText(mStationPojoList.get(position).getStationName());
                String timeToWalkText = String.valueOf(Math.round(mStationPojoList.get(position).getTimeToWalkToStation()/60))+" min";
                if (timeToWalkText.contentEquals("0 min")) {
                    timeToWalkText = 1 + "min";
                }
                ((ViewHolder)holder).timeToWalkToStation.setText(timeToWalkText);

            /* Checking if home station saved in {@link #mPersonalSharedPreferences} is present.
             * If present then the button {@link #mHomeStation} is highlighted */
                if (mPersonalSharedPreferences.getString(Constants.home_station_id,"")
                        .contentEquals(mStationPojoList.get(position).getStationID())) {
                    ((ViewHolder)holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_selected));
                    mHomeStationViewHolder = (ViewHolder)holder;
                }

                if (mPersonalSharedPreferences.getString(Constants.work_station_id,"")
                        .contentEquals(mStationPojoList.get(position).getStationID())) {
                    ((ViewHolder)holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_selected));
                    mWorkStationViewholder = (ViewHolder) holder;
                }

                Gson gson = new Gson();

                String gsonSp= mFavoriteSharedPreference.getString(Constants.favorite_gson, null);
                Type type = new TypeToken<TreeMap<String, String>>() {}.getType();
                TreeMap<String, String> favStationMap = gson.fromJson(gsonSp, type);

                if (favStationMap != null)
                    for (String key : favStationMap.keySet()) {
                        if (favStationMap.get(key).contentEquals(mStationPojoList.get(position).getStationID())) {
                            ((ViewHolder)holder).favoriteStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.favorites_selected));
                        }
                    }
            }


            if (mStationPojoDetailsList.size()>0 && mStationPojoList.size()>0) {
                mLinearLayout.removeAllViews();


                for (int a = 0; a < mStationPojoDetailsList.size() && mStationPojoDetailsList.get(a).size() > 0; a++) {

                    if (mStationPojoDetailsList.get(a).get(0).getLineName() == null
                            && mStationPojoDetailsList.get(a).get(0).getPlatformName() == null
                            && mStationPojoList.get(position).getStationID()
                            .contentEquals(mStationPojoDetailsList.get(a).get(0).getStationID())) {

                        mStationProperties = mStationPojoDetailsList.get(a).get(0).getCommonProperties();

                        mProgressBar.setVisibility(View.GONE);
                        TextView no_info_TV = new TextView(mContext);
                        no_info_TV.setText(R.string.no_info_available_trains);
                        no_info_TV.setTextColor(ContextCompat.getColor(mContext, R.color.overground));
                        mLinearLayout.addView(no_info_TV);
                        no_info_TV.setPadding(0, 16, 0, 0);

                        final LatLng latLngTemp = new LatLng(mStationPojoList.get(position).getLatLng().latitude,
                                mStationPojoList.get(position).getLatLng().longitude);
                        ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (InternetConnectivityReceiver.isConnected()) {
                                    Intent intent = new Intent(mContext, Nearby_Station_Detail.class);
                                    intent.putExtra(Constants.stationName, ((ViewHolder) holder).stationNameTV.getText());
                                    intent.putExtra(Constants.lineName, mStationPojoList.get(position).getLineNameList());
                                    intent.putExtra("LatLng", latLngTemp);
                                    mContext.startActivity(intent);
                                } else {
                                    if (Constants.mainLayout != null)
                                        InternetConnectivityReceiver.displaySnackBar(Constants.mainLayout);
                                }

                            }
                        });

                    } else {
                        if (mStationPojoList.get(position).getStationID()
                                .contentEquals(mStationPojoDetailsList.get(a).get(0).getStationID())) {

                            mStationProperties = mStationPojoDetailsList.get(a).get(0).getCommonProperties();

                            final ArrayList<String> mUniqueStationName = new ArrayList<>();  //Unique platform names of a station got from hash set 'uniqStatNames'
                            HashSet<String> uniqStatNames = new HashSet<>(); //Unique platform names of a station

                            final ArrayList<String> mUniqueLineNames = mStationPojoList.get(position).getLineNameList();

                            for (int i = 0; i < mStationPojoDetailsList.get(a).size(); i++) {
                                uniqStatNames.add(mStationPojoDetailsList.get(a).get(i).getPlatformName()
                                        + mStationPojoDetailsList.get(a).get(i).getTowards()
                                        + mStationPojoDetailsList.get(a).get(i).getLineName());
                            }

                            for (String it : uniqStatNames) {
                                mUniqueStationName.add(it);
                            }

                            mProgressBar.setVisibility(View.GONE);

                            for (int j = 0; j < mUniqueStationName.size(); j++) {
                                final ArrayList<StationArrivalPOJO> arrivalPOJOs = new ArrayList<>();   //Platform wise categorized train details
                                for (int i = 0; i < mStationPojoDetailsList.get(a).size(); i++) {
                                    if (mUniqueStationName.get(j).contains(mStationPojoDetailsList.get(a).get(i).getPlatformName())
                                            && mUniqueStationName.get(j).contains(mStationPojoDetailsList.get(a).get(i).getTowards())
                                            && mUniqueStationName.get(j).contains(mStationPojoDetailsList.get(a).get(i).getLineName())) {
                                        arrivalPOJOs.add(mStationPojoDetailsList.get(a).get(i));
                                    }
                                }

                                Collections.sort(arrivalPOJOs, new Comparator<StationArrivalPOJO>() {
                                    @Override
                                    public int compare(StationArrivalPOJO stationArrivalPOJO, StationArrivalPOJO t1) {
                                        return stationArrivalPOJO.getTimeToStation() - t1.getTimeToStation();
                                    }
                                });

                                RelativeLayout relativeLayout = new RelativeLayout(mContext);
                                relativeLayout.setPadding(16, 16, 16, 16);
                                LinearLayout platformLinearLayout = new LinearLayout(mContext);
                                platformLinearLayout.setOrientation(LinearLayout.VERTICAL);

                                final TextView platformName = new TextView(mContext);
                                platformName.setText(arrivalPOJOs.get(0).getPlatformName());
                                platformName.setTypeface(Typeface.create("sans-serif-normal", Typeface.NORMAL));
                                platformName.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));


                                final TextView towards = new TextView(mContext);
                                final String towardsText = arrivalPOJOs.get(0).getTowards() + " - "
                                        + arrivalPOJOs.get(0).getLineName();
                                towards.setText(towardsText);
                                towards.setTextSize(12);
                                towards.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));

                                platformLinearLayout.addView(platformName);
                                platformLinearLayout.addView(towards);

                                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                platformLinearLayout.setLayoutParams(layoutParams1);


                                RelativeLayout timeRelativeLayout = new RelativeLayout(mContext);
                                timeRelativeLayout.setId(R.id.time_relativeLayout_id);

                                LinearLayout timeArrowLinearLayout = new LinearLayout(mContext);
                                timeArrowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                timeArrowLinearLayout.setId(R.id.time_id);
                                timeArrowLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
                                timeArrowLinearLayout.setPadding(6, 6, 6, 6);

                                RelativeLayout.LayoutParams timeArrowLP = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                timeArrowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                timeArrowLinearLayout.setLayoutParams(timeArrowLP);

                                //Recent Time text view
                                final TextView timeTV = new TextView(mContext);
                                int timeSec = arrivalPOJOs.get(0).getTimeToStation();
                                int timeInMin = Math.round(timeSec / 60);
                                if (timeInMin == 0) {
                                    String timeText = "due";
                                    timeTV.setText(timeText);
                                } else {
                                    String timeInMinText = timeInMin + " min";
                                    timeTV.setText(timeInMinText);
                                }
                                timeTV.setTextColor(ContextCompat.getColor(mContext, R.color.textColorAccent));
                                timeTV.setPadding(0, 0, 16, 0);

                                //Remaining times text view
                                final TextView remainingTimesTV = new TextView(mContext);
                                remainingTimesTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
                                remainingTimesTV.setTextSize(11);
                                remainingTimesTV.setVisibility(View.GONE);

                                for (int i = 1; i < arrivalPOJOs.size() && i < 4; i++) {
                                    String tempTime;
                                    if (i == 3 || i == arrivalPOJOs.size() - 1) {
                                        tempTime = arrivalPOJOs.get(i).getTimeToStation() / 60 + " min";
                                    } else
                                        tempTime = arrivalPOJOs.get(i).getTimeToStation() / 60 + " min, ";
                                    remainingTimesTV.append(tempTime);
                                }
                                RelativeLayout.LayoutParams remainingTimeLayoutParams = new RelativeLayout.LayoutParams
                                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                remainingTimeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                remainingTimeLayoutParams.addRule(RelativeLayout.BELOW, R.id.time_id);
                                remainingTimesTV.setLayoutParams(remainingTimeLayoutParams);

                                timeArrowLinearLayout.addView(timeTV);

                                if (arrivalPOJOs.size() > 1) {

                                    //Down arrow button
                                    final ImageView imageButton = new ImageView(mContext);
                                    imageButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_down));

                                    if (isRemainingTimesOpen != null) {
                                        for (String openedRowKey : isRemainingTimesOpen) {
                                            if (openedRowKey.contains(platformName.getText().toString()) &&
                                                    openedRowKey.contains(towards.getText().toString()) &&
                                                    openedRowKey.contains(((ViewHolder) holder).stationNameTV.getText().toString())) {
                                                remainingTimesTV.setVisibility(View.VISIBLE);
                                                imageButton.setImageResource(R.drawable.arrow_top);
                                            }
                                        }
                                    }

                                    timeArrowLinearLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onTimeClicked(remainingTimesTV, platformName, towards, (ViewHolder) holder, imageButton);
                                        }
                                    });
                                    remainingTimesTV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onTimeClicked(remainingTimesTV, platformName, towards, (ViewHolder) holder, imageButton);
                                        }
                                    });

                                    timeArrowLinearLayout.addView(imageButton);
                                }

                                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
                                timeRelativeLayout.setLayoutParams(layoutParams2);

                                timeRelativeLayout.addView(timeArrowLinearLayout);
                                timeRelativeLayout.addView(remainingTimesTV);


                                View horLine = new View(mContext);
                                RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        1);
                                horLine.setLayoutParams(layoutParams3);
                                horLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.horizontal_line));
                                horLine.setPadding(0, 5, 0, 5);

                                relativeLayout.addView(platformLinearLayout);
                                relativeLayout.addView(timeRelativeLayout);

                                ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (InternetConnectivityReceiver.isConnected()) {
                                            Intent intent = new Intent(mContext, Nearby_Station_Detail.class);
                                            intent.putExtra(Constants.stationName, ((ViewHolder) holder).stationNameTV.getText());
                                            intent.putExtra(Constants.lineName, mUniqueLineNames);
                                            intent.putExtra(Constants.stationProperties, mStationProperties
                                                    .get(((ViewHolder) holder).stationNameTV.getText().toString()).toString());
                                            intent.putExtra("LatLng", arrivalPOJOs.get(0).getStationLatLng().get(((ViewHolder) holder).stationNameTV.getText()));
                                            mContext.startActivity(intent);
                                        } else {
                                            if (Constants.mainLayout != null)
                                                InternetConnectivityReceiver.displaySnackBar(Constants.mainLayout);
                                        }
                                    }
                                });

                                mLinearLayout.addView(relativeLayout);

                                if (j != mUniqueStationName.size() - 1) {
                                    mLinearLayout.addView(horLine);
                                }

                            }

                            ((ViewHolder) holder).homeStation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onHomeStationClicked((ViewHolder) holder, mStationPojoList.get(position).getStationID());
                                }
                            });

                            ((ViewHolder) holder).workStation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onOfficeStationClicked((ViewHolder) holder, mStationPojoList.get(position).getStationID());
                                }
                            });

                            ((ViewHolder) holder).favoriteStation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onFavoritesClicked((ViewHolder) holder, mStationPojoList.get(position).getStationID());
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onTimeClicked(TextView remainingTimesTV, TextView platformName, TextView towards,
            ViewHolder holder, ImageView imageButton) {
        if (remainingTimesTV.getVisibility() == View.GONE) {
            isRemainingTimesOpen.add(platformName.getText().toString()
                    + towards.getText().toString() + (((ViewHolder) holder).stationNameTV.getText().toString()));
            remainingTimesTV.setVisibility(View.VISIBLE);
            imageButton.setImageResource(R.drawable.arrow_down);
            imageButton.animate().rotation(180).start();

        } else {
            remainingTimesTV.setVisibility(View.GONE);
            imageButton.setImageResource(R.drawable.arrow_top);
            imageButton.animate().rotation(-180).start();
        }
    }

    //Change values in shared preference
    private void changeAddressInSP(SharedPreferences.Editor editor, String nameConstant, String stationName, String idConstant, String idValue) {
        editor.putString(nameConstant, stationName);
        editor.putString(idConstant, idValue).apply();
    }

    private void onHomeStationClicked(final ViewHolder holder, String stationID) {

        final SharedPreferences.Editor editorForPersonalSP = mPersonalSharedPreferences.edit();

        homeStationName = (holder).stationNameTV.getText().toString();
        homeStationID = stationID;

        //Check if homeStation and workStation are selected as same stations
        if (homeStationID.contentEquals(mPersonalSharedPreferences
                .getString(Constants.work_station_id,""))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Work station and Home station cannot be same. Do you want to change this as your Home Station?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            changeAddressInSP(editorForPersonalSP, Constants.work_station_name, "", Constants.work_station_id,
                                    "");
                            changeAddressInSP(editorForPersonalSP, Constants.home_station_name, homeStationName, Constants.home_station_id,
                                    homeStationID);
                            MainActivity.vibrateFab();

                            (holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_selected));
                            (holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_unselected));

                            if (mHomeStationViewHolder != null && mHomeStationViewHolder != holder)
                                mHomeStationViewHolder.homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_unselected));

                            mHomeStationViewHolder = holder;
                        }}).setNegativeButton("Cancel", null).show();
        }
        //Check if station name is already set as home station
        else if (mPersonalSharedPreferences.getString(Constants.home_station_id, "").contentEquals(homeStationID)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Do you want to remove '" + (holder).stationNameTV.getText().toString() + "' as your Home Station?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            changeAddressInSP(editorForPersonalSP, Constants.home_station_name, "", Constants.home_station_id,
                                    "");
                            MainActivity.vibrateFab();
                            (holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_unselected));
                            mHomeStationViewHolder = null;
                        }
                    }).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Do you want to set this as your Home Station?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!mPersonalSharedPreferences.getString(Constants.home_station_name, "").isEmpty()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Change Home Station").setMessage("You already have set '"
                                        + mPersonalSharedPreferences.getString(Constants.home_station_name, "")
                                        + "' as your home station. Do you want to change it?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                changeAddressInSP(editorForPersonalSP, Constants.home_station_name, homeStationName, Constants.home_station_id,
                                                        homeStationID);
                                                MainActivity.vibrateFab();
                                                (holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_selected));

                                                if (mHomeStationViewHolder != null) {
                                                    mHomeStationViewHolder.homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_unselected));
                                                }
                                                mHomeStationViewHolder = holder;
                                            }
                                        }).setNegativeButton("Cancel", null).show();
                            } else {
                                changeAddressInSP(editorForPersonalSP, Constants.home_station_name, homeStationName, Constants.home_station_id,
                                        homeStationID);
                                MainActivity.vibrateFab();
                                mHomeStationViewHolder = holder;
                                (holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_selected));
                            }
                        }
                    }).setNegativeButton("Cancel", null).show();
        }
    }

    private void onOfficeStationClicked(final ViewHolder holder, String stationID) {

        final SharedPreferences.Editor editorForPersonalSP = mPersonalSharedPreferences.edit();

        workStationName = (holder).stationNameTV.getText().toString();
        workStationID = stationID;

        //Check if workStation and homeStation are selected as same stations
        if (workStationID.contentEquals(mPersonalSharedPreferences
                .getString(Constants.home_station_id,""))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Work station and Home station cannot be same. Do you want to change this as your Home Station?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            changeAddressInSP(editorForPersonalSP, Constants.work_station_name, workStationName, Constants.work_station_id,
                                    workStationID);
                            changeAddressInSP(editorForPersonalSP, Constants.home_station_name, "", Constants.home_station_id,
                                    "");
                            MainActivity.vibrateFab();

                            (holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_unselected));
                            (holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_selected));

                            if (mWorkStationViewholder != null && mWorkStationViewholder != holder)
                                mWorkStationViewholder.workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_unselected));

                            mWorkStationViewholder = holder;
                        }}).setNegativeButton("Cancel", null).show();
        }
        //Check if station name is already set as work station
        else
        if (mPersonalSharedPreferences.getString(Constants.work_station_id, "").contentEquals(workStationID)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Do you want to remove '" + (holder).stationNameTV.getText().toString() + "' as your Work Station?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            changeAddressInSP(editorForPersonalSP, Constants.work_station_name, "", Constants.work_station_id,
                                    "");
                            MainActivity.vibrateFab();
                            (holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_unselected));
                            mWorkStationViewholder = null;
                        }
                    }).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Do you want to set this as your Work Station?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!mPersonalSharedPreferences.getString(Constants.work_station_name, "").isEmpty()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Change Work Station").setMessage("You already have set '"
                                        + mPersonalSharedPreferences.getString(Constants.work_station_name, "")
                                        + "' as your work station. Do you want to change it?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                changeAddressInSP(editorForPersonalSP, Constants.work_station_name, workStationName, Constants.work_station_id,
                                                        workStationID);
                                                MainActivity.vibrateFab();
                                                if (mWorkStationViewholder != null) {
                                                    mWorkStationViewholder.workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_unselected));
                                                }
                                                mWorkStationViewholder = holder;
                                                (holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_selected));
                                            }
                                        }).setNegativeButton("Cancel", null).show();
                            } else {
                                changeAddressInSP(editorForPersonalSP, Constants.work_station_name, workStationName, Constants.work_station_id,
                                        workStationID);
                                mWorkStationViewholder = holder;
                                MainActivity.vibrateFab();
                                (holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_selected));
                            }
                        }
                    }).setNegativeButton("Cancel", null).show();
        }
    }

    private void onFavoritesClicked(final ViewHolder holder,final String favStationID) {
        final String favStationName = holder.stationNameTV.getText().toString();

        final Gson gson = new Gson();
        final String gsonSp = mFavoriteSharedPreference.getString(Constants.favorite_gson, null);
        final Type type = new TypeToken<TreeMap<String, String>>() { }.getType();

        if (holder.favoriteStation.getDrawable().getConstantState() !=
                ContextCompat.getDrawable(mContext,R.drawable.favorites_selected).getConstantState()) {
            holder.favoriteStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.favorites_selected));
            MainActivity.vibrateFab();

            TreeMap<String, String> favStationMap = gson.fromJson(gsonSp, type);

            if (favStationMap == null)
                favStationMap = new TreeMap<>();
            //Add station name and station id to MultiMapSet and save it in a ArrayList
            favStationMap.put(favStationName, favStationID);

            String gsonString = gson.toJson(favStationMap);

            SharedPreferences.Editor editorForFavoritesSP = mFavoriteSharedPreference.edit();
            editorForFavoritesSP.putString(Constants.favorite_gson, gsonString).apply();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Do you want to remove '" + favStationName + "' from your favorites list?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TreeMap<String, String> favStationMap = gson.fromJson(gsonSp, type);

                            if (favStationMap != null)

                                for (Iterator<Map.Entry<String, String>> iterator = favStationMap.entrySet().iterator();
                                        iterator.hasNext(); ) {
                                    Map.Entry<String, String> entry = iterator.next();
                                    String key = entry.getKey();
                                    if (favStationMap.get(key).contentEquals(favStationID)) {
                                        holder.favoriteStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.favorites_unselected));
                                        iterator.remove();
                                        String gsonString = gson.toJson(favStationMap);
                                        SharedPreferences.Editor editorForFavoritesSP = mFavoriteSharedPreference.edit();
                                        editorForFavoritesSP.putString(Constants.favorite_gson, gsonString).apply();
                                    }
                                }
                        }
                    }).show();
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mStationPojoList.size();
    }

      private class ViewHolder extends RecyclerView.ViewHolder {
          TextView stationNameTV;
          TextView timeToWalkToStation;
          CardView cardView;
          ImageView homeStation;
          ImageView workStation;
          ImageView favoriteStation;

        ViewHolder(View view, TextView stationNameTV, TextView timeToWalkToStation, CardView cardView
                , ImageView homeStation, ImageView officeStation, ImageView favoriteStation) {
            super(view);
            this.stationNameTV = stationNameTV;
            this.timeToWalkToStation = timeToWalkToStation;
            this.cardView = cardView;
            this.homeStation = homeStation;
            this.workStation = officeStation;
            this.favoriteStation = favoriteStation;
        }
    }
}
