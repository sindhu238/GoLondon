package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.NearbyStationsAndStopsModule;
import com.srisindhusaride.golondon.DI.Module.NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory;
import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
import com.srisindhusaride.golondon.Nearby_ViewPager_Stations;
import com.srisindhusaride.golondon.Nearby_ViewPager_Stations_GetStationPlatformDetails_MembersInjector;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerNearbyStationsAndStopsComponent
    implements NearbyStationsAndStopsComponent {
  private Provider<StationArrivalPOJO> providesStationArrivalPOJOProvider;

  private MembersInjector<Nearby_ViewPager_Stations.GetStationPlatformDetails>
      getStationPlatformDetailsMembersInjector;

  private DaggerNearbyStationsAndStopsComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.providesStationArrivalPOJOProvider =
        DoubleCheck.provider(
            NearbyStationsAndStopsModule_ProvidesStationArrivalPOJOFactory.create(
                builder.nearbyStationsAndStopsModule));

    this.getStationPlatformDetailsMembersInjector =
        Nearby_ViewPager_Stations_GetStationPlatformDetails_MembersInjector.create(
            providesStationArrivalPOJOProvider);
  }

  @Override
  public void inject(Nearby_ViewPager_Stations.GetStationPlatformDetails intentService) {
    getStationPlatformDetailsMembersInjector.injectMembers(intentService);
  }

  public static final class Builder {
    private NearbyStationsAndStopsModule nearbyStationsAndStopsModule;

    private Builder() {}

    public NearbyStationsAndStopsComponent build() {
      if (nearbyStationsAndStopsModule == null) {
        throw new IllegalStateException(
            NearbyStationsAndStopsModule.class.getCanonicalName() + " must be set");
      }
      return new DaggerNearbyStationsAndStopsComponent(this);
    }

    public Builder nearbyStationsAndStopsModule(
        NearbyStationsAndStopsModule nearbyStationsAndStopsModule) {
      this.nearbyStationsAndStopsModule = Preconditions.checkNotNull(nearbyStationsAndStopsModule);
      return this;
    }
  }
}
