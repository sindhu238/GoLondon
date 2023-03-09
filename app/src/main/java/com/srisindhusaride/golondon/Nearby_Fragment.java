//package com.srisindhusaride.golondon;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import androidx.core.app.Fragment;
//import androidx.core.app.FragmentActivity;
//import androidx.core.app.FragmentManager;
//import androidx.core.app.FragmentPagerAdapter;
//import androidx.core.view.ViewPager;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.astuetz.PagerSlidingTabStrip;
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
//import com.srisindhusaride.golondon.DI.Component.DaggerViewPager_Component;
//import com.srisindhusaride.golondon.DI.Component.ViewPager_Component;
//import com.srisindhusaride.golondon.DI.Module.Viewpager_module;
//import com.srisindhusaride.golondon.Utils.Constants;
//
//import javax.inject.Inject;
//
//import static android.app.Activity.RESULT_CANCELED;
//import static android.app.Activity.RESULT_OK;
//import static com.srisindhusaride.golondon.Utils.Constants.address;
//
///**
// * @since 08/02/17
// *
// * This class presents all the nearby bus stops and stations by considering the users current location
// */
//
//public class Nearby_Fragment extends Fragment {
//
//    @Inject
//    Nearby_PagerAdapter mNearby_viewpager_adapter;
//
//    private String TAG = getClass().getSimpleName();
//
//    View mRootview = null;
//    private TextView mAddress_tv;
//    private FragmentActivity mContext;
//    static ViewPager viewPager;
//    private PagerSlidingTabStrip tabStrip;
//
//    String mNearbySelected = "Stops";
//    private final String STATIONS = "Stations";
//    private final String STOPS = "Stops";
//
//    private String mAddress = "";
//    private String[] titles = {"Bus Stops", "Tube Stops"};
//    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 11;
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext = (FragmentActivity) context;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        new GetDaggerDependencies().execute();
//
//        LinearLayout addressTVLayout = null;
//        if (mRootview == null) {
//            mRootview = inflater.inflate(R.layout.nearby_fragment, container, false);
//            mAddress_tv = (TextView) mRootview.findViewById(R.id.address_tv);
//            addressTVLayout = (LinearLayout) mRootview.findViewById(R.id.address_tv_layout);
//
//            if (!mAddress.contentEquals("")) {
//                updateUiWidgets(mAddress);
//            }
//            viewPager = (ViewPager) mRootview.findViewById(R.id.viewpager);
//            tabStrip = (PagerSlidingTabStrip) mRootview.findViewById(R.id.tab_strip);
//        }
//
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//
//        final int TOOLBAR_HEIGHT = 48;
//        final int ADDRESS_TEXT_HEIGHT = 14;
//        final int PAGER_STRIP_TAB_HEIGHT = 48;
//
//        int requiredHeightInPX = height - TOOLBAR_HEIGHT - ADDRESS_TEXT_HEIGHT - PAGER_STRIP_TAB_HEIGHT - 400;
//        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
//        layoutParams.height = requiredHeightInPX;
//        viewPager.setLayoutParams(layoutParams);
//
//        if (addressTVLayout != null)
//            addressTVLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                            .setCountry("GB")
//                            .build();
//                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                            .setFilter(typeFilter)
//                            .build(mContext);
//
//                    //Gets result to onActivityResult() function
//                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//                } catch (GooglePlayServicesRepairableException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                }
//            }
//        });
//        return mRootview;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//            switch (resultCode) {
//                case RESULT_OK:
//                    Place place = PlaceAutocomplete.getPlace(mContext, data);
//
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    intent.putExtra(Constants.lat, place.getLatLng().latitude);
//                    intent.putExtra(Constants.lng, place.getLatLng().longitude);
//                    intent.putExtra(address, place.getAddress().toString());
//                    startActivity(intent);
//
//                case RESULT_CANCELED:
//                    break;
//                case PlaceAutocomplete.RESULT_ERROR:
//                    Status status = PlaceAutocomplete.getStatus(mContext, data);
//                    Log.e(TAG, status.getStatusMessage());
//            }
//        }
//    }
//
//    void updateUiWidgets(String address) {
//        mAddress = address;
//        if (mAddress_tv != null) {
//            mAddress_tv.setText(mAddress);
//        }
//
//    }
//
//
//    public class Nearby_PagerAdapter extends FragmentPagerAdapter {
//        Nearby_ViewPager_Stations nearby_viewPagerStations;
//        Nearby_Viewpager_Stops nearby_viewpager_stops;
//
//        public Nearby_PagerAdapter(FragmentManager fragmentManager, Nearby_ViewPager_Stations nearby_viewPagerRow1,
//                                   Nearby_Viewpager_Stops nearby_viewpager_stops1) {
//            super(fragmentManager);
//            nearby_viewPagerStations = nearby_viewPagerRow1;
//            nearby_viewpager_stops = nearby_viewpager_stops1;
//        }
//        @Override
//        public int getCount() {
//            return titles.length;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles[position];
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position){
//                case 0:
//                    return nearby_viewpager_stops;
//                case 1:
//                    return nearby_viewPagerStations;
//                default:
//                    return nearby_viewpager_stops;
//            }
//
//        }
//
//        @Override
//        public void finishUpdate(ViewGroup container) {
//            try{
//                super.finishUpdate(container);
//            } catch (Exception ne) {
//                Log.v(TAG, "Null pointer exception in finish update caught: "+ne.getLocalizedMessage());
//            }
//        }
//    }
//
//    private class GetDaggerDependencies extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            ViewPager_Component component = DaggerViewPager_Component.builder()
//                    .viewpager_module(new Viewpager_module(mContext.getSupportFragmentManager()))
//                    .build();
//            component.inject(Nearby_Fragment.this);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            viewPager.setAdapter(mNearby_viewpager_adapter);
//            tabStrip.setViewPager(viewPager);
//
//            //Allows tabstrip to respond to scrolls
//            tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    if (position == 0) {
//                        mNearbySelected = STOPS;
//                    } else
//                        mNearbySelected = STATIONS;
//
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//        }
//    }
//}
