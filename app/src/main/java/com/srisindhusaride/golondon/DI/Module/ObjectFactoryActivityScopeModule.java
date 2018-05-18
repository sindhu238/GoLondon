package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.Nearby_Fragment;
import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;

import dagger.Module;
import dagger.Provides;

/**
 * @since  14/02/17.
 */

@Module
public class ObjectFactoryActivityScopeModule {

    @Provides
    @ActivityScope
    Nearby_Fragment providesNearByFragment() {
        return new Nearby_Fragment();
    }

    @ActivityScope
    @Provides
    InternetConnectivityReceiver providesInternetConnectivityReceiver() {
        return new InternetConnectivityReceiver();
    }

}
