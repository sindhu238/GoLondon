package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;

import dagger.Module;
import dagger.Provides;

/**
 *@since  24/07/17.
 */

@Module
public class CheckInternetModule {

    @ActivityScope
    @Provides
    public InternetConnectivityReceiver providesInternetConnectivityReceiver() {
        return new InternetConnectivityReceiver();
    }
}
