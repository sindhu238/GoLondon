package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.Viewpager_module;
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
public final class DaggerViewPager_Component {
  private DaggerViewPager_Component() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static ViewPager_Component create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder viewpager_module(Viewpager_module viewpager_module) {
      Preconditions.checkNotNull(viewpager_module);
      return this;
    }

    public ViewPager_Component build() {
      return new ViewPager_ComponentImpl();
    }
  }

  private static final class ViewPager_ComponentImpl implements ViewPager_Component {
    private final ViewPager_ComponentImpl viewPager_ComponentImpl = this;

    private ViewPager_ComponentImpl() {


    }
  }
}
