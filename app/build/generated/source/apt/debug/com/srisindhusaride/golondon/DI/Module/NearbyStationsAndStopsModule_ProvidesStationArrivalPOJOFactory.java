package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory
    implements Factory<StationArrivalPOJO> {
  private final NearbyStationsAndStopsModule module;

  public NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory(
      NearbyStationsAndStopsModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public StationArrivalPOJO get() {
    return Preconditions.checkNotNull(
        module.providesStationArrivalPOJO(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<StationArrivalPOJO> create(NearbyStationsAndStopsModule module) {
    return new NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory(module);
  }

  /** Proxies {@link NearbyStationsAndStopsModule#providesStationArrivalPOJO()}. */
  public static StationArrivalPOJO proxyProvidesStationArrivalPOJO(
      NearbyStationsAndStopsModule instance) {
    return instance.providesStationArrivalPOJO();
  }
}
