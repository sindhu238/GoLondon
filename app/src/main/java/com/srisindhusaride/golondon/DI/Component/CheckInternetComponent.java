package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.CheckInternetModule;
import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.Favorites_Activity;
import com.srisindhusaride.golondon.Nearby_Station_Detail;
import com.srisindhusaride.golondon.Nearby_Stops_Detail;

import dagger.Component;

/**
 * @since 24/07/17.
 */

@ActivityScope
@Component(modules = {CheckInternetModule.class})
interface CheckInternetComponent {
    void inject(Favorites_Activity favorites_activity);
    void inject(Nearby_Station_Detail nearby_station_detail);
    void inject(Nearby_Stops_Detail nearby_stops_detail);
}
