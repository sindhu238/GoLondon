package com.srisindhusaride.golondon.DI.Module;

import android.support.v4.app.FragmentManager;

import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.Nearby_Fragment;
import com.srisindhusaride.golondon.Nearby_ViewPager_Stations;
import com.srisindhusaride.golondon.Nearby_Viewpager_Stops;

import dagger.Module;
import dagger.Provides;

/**
 * @since  14/02/17.
 */

@Module
public class Viewpager_module {

    private FragmentManager fragmentManager;

    public Viewpager_module (FragmentManager fm) {
        fragmentManager =fm;
    }

    @Provides
    @ActivityScope
    Nearby_Fragment.Nearby_PagerAdapter providesNearbyPagerAdapter() {
        return new Nearby_Fragment().new Nearby_PagerAdapter(fragmentManager
                , new Nearby_ViewPager_Stations(), new Nearby_Viewpager_Stops());
    }
}
