package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.CheckInternetModule;
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
final class DaggerCheckInternetComponent {
  private DaggerCheckInternetComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static CheckInternetComponent create() {
    return new Builder().build();
  }

  static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder checkInternetModule(CheckInternetModule checkInternetModule) {
      Preconditions.checkNotNull(checkInternetModule);
      return this;
    }

    public CheckInternetComponent build() {
      return new CheckInternetComponentImpl();
    }
  }

  private static final class CheckInternetComponentImpl implements CheckInternetComponent {
    private final CheckInternetComponentImpl checkInternetComponentImpl = this;

    private CheckInternetComponentImpl() {


    }
  }
}
