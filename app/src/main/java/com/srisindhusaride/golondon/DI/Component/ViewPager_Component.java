package com.srisindhusaride.golondon.DI.Component;

/*
 * @since 14/02/17.
 */

import com.srisindhusaride.golondon.DI.Module.Viewpager_module;
import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.Nearby_Fragment;

import dagger.Component;

@ActivityScope
@Component(modules = Viewpager_module.class)
public interface ViewPager_Component {
    void inject(Nearby_Fragment nearby_fragment);
}
