package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.ObjectFactoryActivityScopeModule;
import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.MainActivity1;

import dagger.Component;

/**
 * @since  14/02/17.
 */

@ActivityScope
@Component(modules = ObjectFactoryActivityScopeModule.class)
public interface ObjectFactoryActivityScopeComponent {
    void inject(MainActivity1 mainActivity1);
}
