package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Nearby_Fragment;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ObjectFactoryActivityScopeModule_ProvidesNearByFragmentFactory
    implements Factory<Nearby_Fragment> {
  private final ObjectFactoryActivityScopeModule module;

  public ObjectFactoryActivityScopeModule_ProvidesNearByFragmentFactory(
      ObjectFactoryActivityScopeModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Nearby_Fragment get() {
    return Preconditions.checkNotNull(
        module.providesNearByFragment(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Nearby_Fragment> create(ObjectFactoryActivityScopeModule module) {
    return new ObjectFactoryActivityScopeModule_ProvidesNearByFragmentFactory(module);
  }

  /** Proxies {@link ObjectFactoryActivityScopeModule#providesNearByFragment()}. */
  public static Nearby_Fragment proxyProvidesNearByFragment(
      ObjectFactoryActivityScopeModule instance) {
    return instance.providesNearByFragment();
  }
}
