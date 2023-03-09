package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.NearbyStationsAndStopsModule;
import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
//import com.srisindhusaride.golondon.Nearby_ViewPager_Stations;

import dagger.Component;

/**
 * @since  17/02/17.
 */

@ActivityScope
@Component(modules = {NearbyStationsAndStopsModule.class})
public interface NearbyStationsAndStopsComponent {
//    void inject(Nearby_ViewPager_Stations.GetStationPlatformDetails intentService);
}
