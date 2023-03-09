package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;

@ScopeMetadata("com.srisindhusaride.golondon.DI.Scope.ActivityScope")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory implements Factory<StationArrivalPOJO> {
  private final NearbyStationsAndStopsModule module;

  public NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory(
      NearbyStationsAndStopsModule module) {
    this.module = module;
  }

  @Override
  public StationArrivalPOJO get() {
    return providesStationArrivalPOJO(module);
  }

  public static NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory create(
      NearbyStationsAndStopsModule module) {
    return new NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory(module);
  }

  public static StationArrivalPOJO providesStationArrivalPOJO(
      NearbyStationsAndStopsModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.providesStationArrivalPOJO());
  }
}
