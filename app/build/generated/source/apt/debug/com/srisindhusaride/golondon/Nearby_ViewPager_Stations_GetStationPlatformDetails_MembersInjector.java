package com.srisindhusaride.golondon;

import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class Nearby_ViewPager_Stations_GetStationPlatformDetails_MembersInjector
    implements MembersInjector<Nearby_ViewPager_Stations.GetStationPlatformDetails> {
  private final Provider<StationArrivalPOJO> stationPojoProvider;

  public Nearby_ViewPager_Stations_GetStationPlatformDetails_MembersInjector(
      Provider<StationArrivalPOJO> stationPojoProvider) {
    assert stationPojoProvider != null;
    this.stationPojoProvider = stationPojoProvider;
  }

  public static MembersInjector<Nearby_ViewPager_Stations.GetStationPlatformDetails> create(
      Provider<StationArrivalPOJO> stationPojoProvider) {
    return new Nearby_ViewPager_Stations_GetStationPlatformDetails_MembersInjector(
        stationPojoProvider);
  }

  @Override
  public void injectMembers(Nearby_ViewPager_Stations.GetStationPlatformDetails instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.stationPojo = stationPojoProvider.get();
  }

  public static void injectStationPojo(
      Nearby_ViewPager_Stations.GetStationPlatformDetails instance,
      Provider<StationArrivalPOJO> stationPojoProvider) {
    instance.stationPojo = stationPojoProvider.get();
  }
}
