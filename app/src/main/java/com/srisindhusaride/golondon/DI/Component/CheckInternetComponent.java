package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.CheckInternetModule;
import com.srisindhusaride.golondon.DI.Scope.ActivityScope;

import dagger.Component;

/**
 * @since 24/07/17.
 */

@ActivityScope
@Component(modules = {CheckInternetModule.class})
interface CheckInternetComponent {
//    void inject(Favorites_Activity favorites_activity);
//    void inject(Nearby_Station_Detail nearby_station_detail);
//    void inject(Nearby_Stops_Detail nearby_stops_detail);
}
