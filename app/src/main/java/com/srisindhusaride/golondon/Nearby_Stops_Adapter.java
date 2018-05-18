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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.srisindhusaride.golondon.Model.BusArrivalPOJO;
import com.srisindhusaride.golondon.Utils.Constants;
import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
import com.tuyenmonkey.mkloader.MKLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @since 21/04/17.
 */

class Nearby_Stops_Adapter extends RecyclerView.Adapter {

    private List<List<BusArrivalPOJO>> mBusArrivalPOJOListFullDetails = new ArrayList<>();
    private List<BusArrivalPOJO> mBusArrivalPOJOList = new ArrayList<>();

    private Context mContext;
    private LinearLayout mLinearLayout;

    //Shared preference for saving home and office stations
    private SharedPreferences mPersonalSharedPreferences;

    //Shared preference for saving favorite stations
    private SharedPreferences mFavoriteSharedPreference;

    //Hash set that saves all the remaining times list that are opened by clicking on the arrow by user.
    private static HashSet<String> isRemainingTimesOpen = new HashSet<>();


    /**The name of the home station that is to be saved in {@link #mPersonalSharedPreferences}**/
    private String homeStationName;

    /**The name of the home station id that is to be saved in {@link #mPersonalSharedPreferences}**/
    private String homeStationID = "";

    private String workStationName;
    private String workStationID;

    private final String TAG = "NearbyStopsAda";

    private ViewHolder mHomeStationViewHolder;
    private ViewHolder mWorkStationViewholder;

    Nearby_Stops_Adapter(List<BusArrivalPOJO> busArrivalPOJOList,List<List<BusArrivalPOJO>> busArrivalPOJOList1) {
        mBusArrivalPOJOList = busArrivalPOJOList;
        mBusArrivalPOJOListFullDetails = busArrivalPOJOList1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_stops_adapter,parent,false);
        mContext = parent.getContext();

        mPersonalSharedPreferences = mContext.getSharedPreferences(Constants.personal_sp, Context.MODE_PRIVATE);
        mFavoriteSharedPreference = mContext.getSharedPreferences(Constants.favorite_sp, Context.MODE_PRIVATE);

        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
        TextView mStationName = (TextView) rootView.findViewById(R.id.stationName);
        mStationName.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        MKLoader progressBar = (MKLoader) rootView.findViewById(R.id.progressBar);

        TextView timeToWalkToStation = (TextView) rootView.findViewById(R.id.timeToTravel);

        CardView mCardView = (CardView) rootView.findViewById(R.id.cardView);

        ImageView mHomeStation = (ImageView) rootView.findViewById(R.id.home_station);
        ImageView mWorkStation = (ImageView) rootView.findViewById(R.id.office_station);
        ImageView mFavoriteStation = (ImageView) rootView.findViewById(R.id.favorite_station);

        return new Nearby_Stops_Adapter.ViewHolder(rootView, mStationName, timeToWalkToStation, mCardView, progressBar,
                mHomeStation, mWorkStation, mFavoriteStation);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        try {
            if (mBusArrivalPOJOList.size() > 0 && mBusArrivalPOJOList.size() > position) {
                ((ViewHolder) holder).stationNameTV.setText(mBusArrivalPOJOList.get(position).getStationName());
                String time = mBusArrivalPOJOList.get(position).getTimeToWalkToStation() + " min";
                if (time.contentEquals("0 min")) {
                    time = 1 + "min";
                }
                ((ViewHolder) holder).timeToWalkToStation.setText(time);

                //Appending platform name to station name
                if (mBusArrivalPOJOList.get(position).getPlatformName() != null &&
                        !(mBusArrivalPOJOList.get(position).getPlatformName()).contentEquals(""))
                    if (!((ViewHolder) holder).stationNameTV.getText().toString().contains("::"))
                        ((ViewHolder) holder).stationNameTV.append(" :: " + mBusArrivalPOJOList.get(position).getPlatformName());

            /* Checking if home station saved in {@link #mPersonalSharedPreferences} is present.
             * If present then the button {@link #mHomeStation} is highlighted */
                if (mPersonalSharedPreferences.getString(Constants.home_station_id,"")
                        .contentEquals(mBusArrivalPOJOList.get(position).getStationID())) {
                    mHomeStationViewHolder = (ViewHolder)holder;
                    ((ViewHolder)holder).homeStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.home_selected));
                }

                if (mPersonalSharedPreferences.getString(Constants.work_station_id,"")
                        .contentEquals(mBusArrivalPOJOList.get(position).getStationID())) {
                    mWorkStationViewholder = (ViewHolder)holder;
                    ((ViewHolder)holder).workStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_selected));
                }

                Gson gson = new Gson();

                String gsonSp= mFavoriteSharedPreference.getString(Constants.favorite_gson, null);
                Type type = new TypeToken<TreeMap<String, String>>() {}.getType();
                TreeMap<String, String> favStationMap = gson.fromJson(gsonSp, type);

                if (favStationMap != null)
                    for (String key : favStationMap.keySet()) {
                        Log.v(TAG, "fav refresh" + key+":" +favStationMap.get(key));
                        if (favStationMap.get(key).contentEquals(mBusArrivalPOJOList.get(position).getStationID())) {
                            Log.v(TAG, "fav refresh inside:" + key+":" +favStationMap.get(key));
                            ((ViewHolder)holder).favoriteStation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.favorites_selected));
                        }
                    }
            }

            if (mBusArrivalPOJOListFullDetails.size() > 0) {
                mLinearLayout.removeAllViews();


                for (int a=0; a< mBusArrivalPOJOListFullDetails.size() && mBusArrivalPOJOListFullDetails.get(a).size() > 0 ;a++) {

                    if (mBusArrivalPOJOListFullDetails.get(a).get(0).getStationID()
                            .contentEquals(mBusArrivalPOJOList.get(position).getStationID()) &&
                            mBusArrivalPOJOListFullDetails.get(a).get(0).getStationName()
                                    .contentEquals(mBusArrivalPOJOList.get(position).getStationName()) &&
                            mBusArrivalPOJOListFullDetails.get(a).get(0).getLineName()
                                    .contentEquals("empty")) {

                        if (mLinearLayout.getChildCount() == 0) {
                            ((ViewHolder) holder).progressBar.setVisibility(View.GONE);
                            TextView no_info_TV = new TextView(mContext);
                            no_info_TV.setText(R.string.no_info_available_bus);
                            no_info_TV.setTextColor(ContextCompat.getColor(mContext, R.color.overground));
                            mLinearLayout.addView(no_info_TV);
                            no_info_TV.setPadding(0,16,0,0);
                        }

                        final ArrayList<String> busNameList =  mBusArrivalPOJOListFullDetails.get(a).get(0).getBusNamesList();
                        ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (InternetConnectivityReceiver.isConnected()) {
                                    Intent intent = new Intent(mContext, Nearby_Stops_Detail.class);
                                    LatLng latLng = null;

//                              Get LatLng of selected bus stop
                                    for (BusArrivalPOJO busArrivalPOJO : mBusArrivalPOJOList) {
                                        if ((mBusArrivalPOJOList.get(position).getPlatformName() != null
                                                && (busArrivalPOJO.getStationName() + " :: " + busArrivalPOJO.getPlatformName())
                                                .contentEquals(((ViewHolder) holder).stationNameTV.getText()))
                                                || (mBusArrivalPOJOList.get(position).getPlatformName() == null
                                                && busArrivalPOJO.getStationName().contentEquals(((ViewHolder) holder).stationNameTV.getText()))) {
                                            latLng = busArrivalPOJO.getLatLng();
                                        }
                                    }

                                    if (latLng != null)
                                        intent.putExtra(Constants.latlng, latLng);

                                    intent.putExtra(Constants.stationName, ((ViewHolder) holder).stationNameTV.getText().toString());
                                    intent.putExtra(Constants.lineName, busNameList);
                                    mContext.startActivity(intent);
                                } else {
                                    if (Constants.mainLayout != null)
                                        InternetConnectivityReceiver.displaySnackBar(Constants.mainLayout);
                                }
                            }
                        });
                    } else {

                        if (mBusArrivalPOJOList.get(position).getStationID()
                                .contentEquals(mBusArrivalPOJOListFullDetails.get(a).get(0).getStationID())) {
                            Log.v(TAG, "destinat name pos:" +a +":" +position +":" +mBusArrivalPOJOListFullDetails.get(a).get(0).getStationName() +":"
                                    + mBusArrivalPOJOList.get(position).getPlatformName());

                            final ArrayList<String> busNameList =  mBusArrivalPOJOListFullDetails.get(a).get(0).getBusNamesList();

                            //Sort the bus details according to bus arrival timings
                            for (int j = 0; j < mBusArrivalPOJOListFullDetails.get(a).size(); j++) {
                                Collections.sort(mBusArrivalPOJOListFullDetails.get(a), new Comparator<BusArrivalPOJO>() {
                                    @Override
                                    public int compare(BusArrivalPOJO busArrivalPOJO, BusArrivalPOJO t1) {
                                        return busArrivalPOJO.getTimeToArrival() - t1.getTimeToArrival();
                                    }
                                });
                            }

                            //Group all times of those having same bus names
                            Set<String> uniqueBusNamesSet = new HashSet<>();
                            for (int m = 0; m < mBusArrivalPOJOListFullDetails.get(a).size(); m++) {
                                uniqueBusNamesSet.add(mBusArrivalPOJOListFullDetails.get(a).get(m).getLineName());
                            }

                            ArrayList<Multimap<String, Integer>> allBusesForParticularStation = new ArrayList<>();
                            for (String key : uniqueBusNamesSet) {
                                Multimap<String, Integer> busWithTimes = ArrayListMultimap.create();
                                for (int s = 0; s < mBusArrivalPOJOListFullDetails.get(a).size(); s++) {
                                    if (key.contentEquals(mBusArrivalPOJOListFullDetails.get(a).get(s).getLineName())) {
                                        busWithTimes.put(key + "::" + mBusArrivalPOJOListFullDetails.get(a).get(s).getDestinationName()
                                                , mBusArrivalPOJOListFullDetails.get(a).get(s).getTimeToArrival());
                                    }
                                }
                                allBusesForParticularStation.add(busWithTimes);
                            }

                            ((ViewHolder) holder).progressBar.setVisibility(View.GONE);
                            for (int t = 0; t < allBusesForParticularStation.size(); t++) {
                                Multimap<String, Integer> map = allBusesForParticularStation.get(t);
                                for (String key : map.keySet()) {
                                    List<Integer> values = new ArrayList<>(map.get(key));

                                    RelativeLayout relativeLayout = new RelativeLayout(mContext);
                                    relativeLayout.setPadding(16, 16, 16, 16);

                                    LinearLayout linearLayoutForBusName = new LinearLayout(mContext);
                                    linearLayoutForBusName.setOrientation(LinearLayout.VERTICAL);

                                    final TextView busNameTV = new TextView(mContext);
                                    busNameTV.setText(key.split("::")[0]);
                                    busNameTV.setTypeface(Typeface.create("sans-serif-normal", Typeface.NORMAL));
                                    busNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));

                                    final TextView destinationNameTV = new TextView(mContext);
                                    destinationNameTV.setText(key.split("::")[1]);
                                    destinationNameTV.setTextSize(12);
                                    destinationNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));

                                    linearLayoutForBusName.addView(busNameTV);
                                    linearLayoutForBusName.addView(destinationNameTV);

                                    LinearLayout linearLayoutForTimes = new LinearLayout(mContext);
                                    linearLayoutForTimes.setOrientation(LinearLayout.VERTICAL);

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
                                    TextView timeTV = new TextView(mContext);
                                    int timeSec = values.get(0);
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

                                    for (int l = 1; l < values.size() && l < 4; l++) {
                                        String tempTime;
                                        if (l == 3 || l == values.size() - 1) {
                                            tempTime = values.get(l) / 60 + " min";
                                        } else
                                            tempTime = values.get(l) / 60 + " min, ";
                                        remainingTimesTV.append(tempTime);
                                    }

                                    RelativeLayout.LayoutParams remainingTimeLayoutParams = new RelativeLayout.LayoutParams
                                            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    remainingTimeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    remainingTimeLayoutParams.addRule(RelativeLayout.BELOW, R.id.time_id);
                                    remainingTimesTV.setLayoutParams(remainingTimeLayoutParams);

                                    timeArrowLinearLayout.addView(timeTV);

                                    if (values.size() > 1) {

                                        //Down arrow button
                                        final ImageView imageButton = new ImageView(mContext);
                                        imageButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_down));

                                        if (isRemainingTimesOpen != null) {
                                            for (String openedRowKey : isRemainingTimesOpen) {
                                                if (openedRowKey.contains(busNameTV.getText().toString()) &&
                                                        openedRowKey.contains(destinationNameTV.getText().toString()) &&
                                                        openedRowKey.contains(((ViewHolder) holder).stationNameTV.getText().toString())) {
                                                    remainingTimesTV.setVisibility(View.VISIBLE);
                                                    imageButton.setImageResource(R.drawable.arrow_top);
                                                }
                                            }
                                        }

                                        timeArrowLinearLayout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                onTimeClicked(remainingTimesTV, busNameTV, destinationNameTV, (ViewHolder) holder, imageButton);
                                            }
                                        });

                                        remainingTimesTV.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                onTimeClicked(remainingTimesTV, busNameTV, destinationNameTV, (ViewHolder) holder, imageButton);
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

                                    relativeLayout.addView(linearLayoutForBusName);
                                    relativeLayout.addView(timeRelativeLayout);
                                    ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (InternetConnectivityReceiver.isConnected()) {
                                                Intent intent = new Intent(mContext, Nearby_Stops_Detail.class);
                                                LatLng latLng = null;
                                                //Get LatLng of selected bus stop
                                                for (BusArrivalPOJO busArrivalPOJO : mBusArrivalPOJOList) {

                                                    if ((busArrivalPOJO.getPlatformName() != null && (busArrivalPOJO.getStationName()+" :: "+busArrivalPOJO.getPlatformName())
                                                            .contentEquals(((ViewHolder) holder).stationNameTV.getText()))
                                                            || (busArrivalPOJO.getPlatformName() == null && (busArrivalPOJO.getStationName())
                                                            .contentEquals(((ViewHolder) holder).stationNameTV.getText()))) {
                                                        latLng = busArrivalPOJO.getLatLng();
                                                    }
                                                }
                                                if (latLng != null)
                                                    intent.putExtra(Constants.latlng, latLng);
                                                intent.putExtra(Constants.stationName, ((ViewHolder) holder).stationNameTV.getText().toString());
                                                intent.putExtra(Constants.lineName, busNameList);
                                                mContext.startActivity(intent);
                                            } else {
                                                if (Constants.mainLayout != null)
                                                    InternetConnectivityReceiver.displaySnackBar(Constants.mainLayout);
                                            }

                                        }
                                    });


                                    ((ViewHolder)holder).homeStation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onHomeStationClicked((ViewHolder)holder, mBusArrivalPOJOList.get(position).getStationID());
                                        }
                                    });

                                    ((ViewHolder)holder).workStation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onOfficeStationClicked((ViewHolder)holder, mBusArrivalPOJOList.get(position).getStationID());
                                        }
                                    });

                                    ((ViewHolder)holder).favoriteStation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onFavoritesClicked((ViewHolder)holder, mBusArrivalPOJOList.get(position).getStationID());
                                        }
                                    });
                                    mLinearLayout.addView(relativeLayout);

                                    if (t < allBusesForParticularStation.size() - 1) {
                                        mLinearLayout.addView(horLine);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onTimeClicked(TextView remainingTimesTV, TextView busNameTV, TextView destinationNameTV,
                               ViewHolder holder, ImageView imageButton) {
        if (remainingTimesTV.getVisibility() == View.GONE) {
            isRemainingTimesOpen.add(busNameTV.getText().toString()
                    + destinationNameTV.getText().toString()
                    + ((holder).stationNameTV.getText().toString()));
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
        MainActivity.vibrateFab();
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
                            if (!mPersonalSharedPreferences.getString(Constants.home_station_id, "").isEmpty()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Change Home Station").setMessage("You already have set '"
                                        + mPersonalSharedPreferences.getString(Constants.home_station_name, "")
                                        + "' as your home station. Do you want to change it?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                changeAddressInSP(editorForPersonalSP, Constants.home_station_name, homeStationName, Constants.home_station_id,
                                                        homeStationID);
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
                            if (!mPersonalSharedPreferences.getString(Constants.work_station_id, "").isEmpty()) {
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

    private void onFavoritesClicked(final ViewHolder holder, final String favStationID) {
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
        return mBusArrivalPOJOList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView stationNameTV;
        TextView timeToWalkToStation;
        CardView cardView;
        MKLoader progressBar;
        ImageView homeStation;
        ImageView workStation;
        ImageView favoriteStation;

        ViewHolder(View view, TextView stationNameTV, TextView timeToWalkToStation, CardView cardView,
                   MKLoader progressBar, ImageView homeStation, ImageView officeStation, ImageView favoriteStation) {
            super(view);
            this.stationNameTV = stationNameTV;
            this.timeToWalkToStation = timeToWalkToStation;
            this.cardView = cardView;
            this.progressBar = progressBar;
            this.homeStation = homeStation;
            this.workStation = officeStation;
            this.favoriteStation = favoriteStation;
        }

    }

}
