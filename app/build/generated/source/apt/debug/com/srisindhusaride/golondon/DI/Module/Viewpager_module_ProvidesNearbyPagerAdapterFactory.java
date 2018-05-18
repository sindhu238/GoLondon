package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Nearby_Fragment;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class Viewpager_module_ProvidesNearbyPagerAdapterFactory
    implements Factory<Nearby_Fragment.Nearby_PagerAdapter> {
  private final Viewpager_module module;

  public Viewpager_module_ProvidesNearbyPagerAdapterFactory(Viewpager_module module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Nearby_Fragment.Nearby_PagerAdapter get() {
    return Preconditions.checkNotNull(
        module.providesNearbyPagerAdapter(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Nearby_Fragment.Nearby_PagerAdapter> create(Viewpager_module module) {
    return new Viewpager_module_ProvidesNearbyPagerAdapterFactory(module);
  }

  /** Proxies {@link Viewpager_module#providesNearbyPagerAdapter()}. */
  public static Nearby_Fragment.Nearby_PagerAdapter proxyProvidesNearbyPagerAdapter(
      Viewpager_module instance) {
    return instance.providesNearbyPagerAdapter();
  }
}
