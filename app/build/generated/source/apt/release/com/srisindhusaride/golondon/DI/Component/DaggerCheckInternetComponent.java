package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.CheckInternetModule;
import com.srisindhusaride.golondon.Favorites_Activity;
import com.srisindhusaride.golondon.Nearby_Station_Detail;
import com.srisindhusaride.golondon.Nearby_Stops_Detail;
import dagger.internal.MembersInjectors;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerCheckInternetComponent implements CheckInternetComponent {
  private DaggerCheckInternetComponent(Builder builder) {
    assert builder != null;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static CheckInternetComponent create() {
    return builder().build();
  }

  @Override
  public void inject(Favorites_Activity favorites_activity) {
    MembersInjectors.<Favorites_Activity>noOp().injectMembers(favorites_activity);
  }

  @Override
  public void inject(Nearby_Station_Detail nearby_station_detail) {
    MembersInjectors.<Nearby_Station_Detail>noOp().injectMembers(nearby_station_detail);
  }

  @Override
  public void inject(Nearby_Stops_Detail nearby_stops_detail) {
    MembersInjectors.<Nearby_Stops_Detail>noOp().injectMembers(nearby_stops_detail);
  }

  public static final class Builder {
    private Builder() {}

    public CheckInternetComponent build() {
      return new DaggerCheckInternetComponent(this);
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This
     *     method is a no-op. For more, see https://google.github.io/dagger/unused-modules.
     */
    @Deprecated
    public Builder checkInternetModule(CheckInternetModule checkInternetModule) {
      Preconditions.checkNotNull(checkInternetModule);
      return this;
    }
  }
}
