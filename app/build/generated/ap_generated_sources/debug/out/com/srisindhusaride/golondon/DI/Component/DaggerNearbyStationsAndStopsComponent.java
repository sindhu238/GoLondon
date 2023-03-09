package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.NearbyStationsAndStopsModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerNearbyStationsAndStopsComponent {
  private DaggerNearbyStationsAndStopsComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static NearbyStationsAndStopsComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder nearbyStationsAndStopsModule(
        NearbyStationsAndStopsModule nearbyStationsAndStopsModule) {
      Preconditions.checkNotNull(nearbyStationsAndStopsModule);
      return this;
    }

    public NearbyStationsAndStopsComponent build() {
      return new NearbyStationsAndStopsComponentImpl();
    }
  }

  private static final class NearbyStationsAndStopsComponentImpl implements NearbyStationsAndStopsComponent {
    private final NearbyStationsAndStopsComponentImpl nearbyStationsAndStopsComponentImpl = this;

    private NearbyStationsAndStopsComponentImpl() {


    }
  }
}
